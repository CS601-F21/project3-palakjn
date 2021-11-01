package server.controller;

import applications.slack.configuration.SlackConstants;
import server.HttpConstants;
import server.models.WebRequest;
import server.models.WebResponse;
import utils.Strings;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HTTPServer {

    private Map<String, Handler> handlers;
    private volatile boolean running = true;
    private int port;
    private ExecutorService threadPool;
    private ServerSocket serverSocket;
    private Socket socket;

    public HTTPServer(int port) {
        this.port = port;
        handlers = new HashMap<>();
        this.threadPool = Executors.newFixedThreadPool(30);
    }

    public boolean addMapping(String path, Handler handler) {
        boolean isAdded = false;

        if(!handlers.containsKey(path)) {
            handlers.put(path, handler);
            isAdded = true;
        }

        return isAdded;
    }

    public void startup() {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ioException) {
            System.out.println("Fail to create server socket object. " + ioException.getMessage());

            return;
        }

        System.out.println("Server listening on port " + port);

        while (running) {
            try {
                //accept a new connection
                this.socket = serverSocket.accept();
                System.out.println("New connection from " + socket.getInetAddress());

                threadPool.execute(() -> handleRequest(socket));
            } catch(IOException ioException){
                StringWriter writer = new StringWriter();
                ioException.printStackTrace(new PrintWriter(writer));
                System.out.println("Error while handling a request from client. Exception: " + writer);
            }
        }
    }

    private void handleRequest(Socket socket) {
        try (socket;
             BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream())
        ) {
            String requestLine = inStream.readLine();
            System.out.println("Request: " + requestLine);

            WebResponse webResponse = new WebResponse(writer, inStream);

            boolean isValidRequest = validateRequest(requestLine, webResponse);

            if(isValidRequest) {
                WebRequest webRequest = new WebRequest();

                //Reading method, path, version
                String[] requestLineParts = requestLine.split("\\s");
                webRequest.setMethod(requestLineParts[0]);
                webRequest.setPath(requestLineParts[1]);
                webRequest.setVersion(requestLineParts[2]);

                System.out.println("HTTP method: "+ webRequest.getMethod());
                System.out.println("Path: " + webRequest.getPath());
                System.out.println("Version: " + webRequest.getVersion());

                //Read headers
                webRequest.setHeaders(getHeader(inStream));

                if(webRequest.getPath().equals(HttpConstants.SHUTDOWN_PATH)) {
                    shutdown(webRequest, webResponse);
                } else {
                    handlers.get(webRequest.getPath()).handle(webRequest, webResponse);
                }
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void shutdown(WebRequest request, WebResponse response) throws IOException {
        if(request.isGET()) {
            response.setStatus(200);
            response.send(HttpConstants.SHUTDOWN_PAGE);
        }

        if(request.isPOST()) {
            int contentLength;
            String header = request.getHeader(HttpConstants.CONTENT_LENGTH);

            if(!Strings.isNullOrEmpty(header)) {
                contentLength = Integer.parseInt(header);

                String passcode = response.read(contentLength);
                if(passcode.equals(HttpConstants.PASSCODE)) {
                    response.setStatus(200);
                    response.send(String.format(HttpConstants.SHUTDOWN_RESPONSE, "Shutting down the server..."));
                    response.flush();

                    shutdown();
                }
                else {
                    //No content to display
                    response.setStatus(204);
                    response.send(String.format(HttpConstants.SHUTDOWN_ERROR_PAGE, "Wrong passcode. Try Again..!"));
                    response.flush();
                }
            }
            else {
                response.setStatus(411);
            }

        }
    }

    private void shutdown() {
        System.out.println("Shutting down server");

        running = false;

        threadPool.shutdown();

        try {
            if (!threadPool.awaitTermination(15, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.println("Error while terminating threadpool: " + e.getMessage());
        }

        System.out.println("Threadpool done. Closing socket");

        try {
            //After closing the socket, thread will wake up from the socket.accept() call and then will exit the loop
            this.serverSocket.close();
            if (this.socket != null) {
                //When there is no remote host, then socket will be null.
                this.socket.close();
            }

            System.out.println("Server is down successfully");
        } catch (IOException ioException) {
            System.out.printf("Error while closing the remote connection. %s.\n", ioException);
        }
    }

    private boolean validateRequest(String request, WebResponse response) {
        boolean isValid = false;

        if(Strings.isNullOrEmpty(request)) {
            System.out.println("Bad Request. Request empty");
            response.setStatus(400);
        }
        else {
            String[] requestLineParts = request.split("\\s");

            if(requestLineParts.length != 3) {
                //Bad Request
                System.out.println("Bad Request");
                response.setStatus(400);
            }
            else if(!requestLineParts[0].equals(HttpConstants.GET) && !requestLineParts[0].equals(HttpConstants.POST)) {
                //Method not supported
                System.out.println("Method not supported");
                response.setStatus(405);
            }
            else if(!handlers.containsKey(requestLineParts[1]) && !requestLineParts[1].equals(HttpConstants.SHUTDOWN_PATH)) {
                //Page not found
                System.out.println("Page not found");
                response.setStatus(404);
            }
            else if(!requestLineParts[2].equals(HttpConstants.VERSION)) {
                //version not supported
                System.out.println("Version not supported");
                response.setStatus(505);
            }
            else {
                isValid = true;
            }
        }

        return isValid;
    }

    private Map<String, String> getHeader(BufferedReader inStream) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String header;

        while(!(header = inStream.readLine()).isEmpty()) {
            String[] headerParts = header.split("\\s");
            headers.put(headerParts[0], headerParts[1]);
        }

        return headers;
    }
}
