package server.controller;

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

    public void shutdown() {
        running = false;

        threadPool.shutdown();

        try {
            if(!threadPool.awaitTermination(2, TimeUnit.MINUTES)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.println("Error while terminating threadpool: " + e.getMessage());
        }

        try {
            //After closing the socket, thread will wake up from the socket.accept() call and then will exit the loop
            this.serverSocket.close();
            if(this.socket != null) {
                //When there is no remote host, then socket will be null.
                this.socket.close();
            }
        } catch (IOException ioException) {
            System.out.printf("Error while closing the remote connection. %s.\n", ioException);
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
                WebRequest httpRequest = new WebRequest();

                //Reading method, path, version
                String[] requestLineParts = requestLine.split("\\s");
                httpRequest.setMethod(requestLineParts[0]);
                httpRequest.setPath(requestLineParts[1]);
                httpRequest.setVersion(requestLineParts[2]);

                System.out.println("HTTP method: "+ httpRequest.getMethod());
                System.out.println("Path: " + httpRequest.getPath());
                System.out.println("Version: " + httpRequest.getVersion());

                //Read headers
                httpRequest.setHeaders(getHeader(inStream));

                handlers.get(httpRequest.getPath()).handle(httpRequest, webResponse);
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
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
            else if(!handlers.containsKey(requestLineParts[1])) {
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
