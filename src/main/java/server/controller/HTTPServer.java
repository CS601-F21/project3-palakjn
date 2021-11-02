package server.controller;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
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

/**
 * HTTP server which opens a connection with a client and accepts GET or POST requests.
 *
 * @author Palak Jain
 */
public class HTTPServer {
    private Map<String, Handler> handlers;
    private volatile boolean running = true;
    private int port;
    private ExecutorService threadPool;
    private ServerSocket serverSocket;
    private Socket socket;
    private static final Logger logger = (Logger) LogManager.getLogger(HTTPServer.class);

    public HTTPServer(int port) {
        this.port = port;
        handlers = new HashMap<>();
        this.threadPool = Executors.newFixedThreadPool(30);
    }

    /**
     * Add the mapping of a page requested by a server to the handler class which will handle the request
     * @param path Requested page
     * @param handler Handler class which will handle the request
     */
    public void addMapping(String path, Handler handler) {
        if(!handlers.containsKey(path)) {
            handlers.put(path, handler);
        }
    }

    /**
     * Starting the server at the specified port number.
     */
    public void startup() {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ioException) {
            System.out.println("Fail to create server socket object. " + ioException.getMessage());

            return;
        }

        logger.printf(Level.INFO,"Server listening on port %s.", port);

        while (running) {
            try {
                //accept a new connection
                this.socket = serverSocket.accept();
                System.out.println("New connection from " + socket.getInetAddress());

                threadPool.execute(() -> handleRequest(socket));
            } catch(IOException ioException) {
                logger.printf(Level.ERROR,"Error while handling a request from client. Exception: %s", ioException);
            }
        }
    }

    /**
     * Handles the request.
     *
     * @param socket A connection socket with one client.
     */
    private void handleRequest(Socket socket) {
        try (socket;
             BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream())
        ) {
            String requestLine = inStream.readLine();
            logger.printf(Level.INFO,"Request: %s", requestLine);

            WebResponse webResponse = new WebResponse(writer);

            boolean isValidRequest = validateRequest(requestLine, webResponse);

            if(isValidRequest) {
                logger.printf(Level.INFO, "Request is valid");
                WebRequest webRequest = new WebRequest(inStream);

                //Reading method, path, version
                String[] requestLineParts = requestLine.split("\\s");
                webRequest.setMethod(requestLineParts[0]);
                webRequest.setPath(requestLineParts[1]);
                webRequest.setVersion(requestLineParts[2]);

                logger.printf(Level.INFO,"HTTP method: %s", webRequest.getMethod());
                logger.printf(Level.INFO,"Path: %s", webRequest.getPath());
                logger.printf(Level.INFO,"Version: %s", webRequest.getVersion());

                //Read headers
                webRequest.setHeaders(getHeader(inStream));

                if(webRequest.getPath().equals(HttpConstants.SHUTDOWN_PATH)) {
                    shutdown(webRequest, webResponse);
                } else {
                    handlers.get(webRequest.getPath()).handle(webRequest, webResponse);
                }
            }
        } catch(IOException ioe) {
            logger.printf(Level.ERROR, "Error while handling request: %s", ioe);
        }
    }

    /**
     * Shutting down server if the client requests a page /shutdown.
     * Passcode is needed for verifying the person who is shutting down.
     * Only admins who know the passcode can shut down the server.
     *
     * @param request Web request
     * @param response Web response
     * @throws IOException
     */
    private void shutdown(WebRequest request, WebResponse response) throws IOException {
        if(request.isGET()) {
            logger.printf(Level.INFO, "A request came for shutting down a server");
            response.setStatus(200);
            response.send(HttpConstants.SHUTDOWN_PAGE);
        }

        if(request.isPOST()) {
            int contentLength;
            String header = request.getHeader(HttpConstants.CONTENT_LENGTH);

            if(!Strings.isNullOrEmpty(header)) {
                contentLength = Integer.parseInt(header);

                String passcode = request.read(contentLength);
                if(passcode.equals(HttpConstants.PASSCODE)) {
                    response.setStatus(200);
                    response.send(String.format(HttpConstants.SHUTDOWN_RESPONSE, "Shutting down the server..."));
                    response.flush();

                    shutdown();
                }
                else {
                    logger.printf(Level.WARN, "Wrong passcode. Asking user to try again.");
                    response.setStatus(200);
                    response.send(String.format(HttpConstants.SHUTDOWN_ERROR_PAGE, "Wrong passcode. Try Again..!"));
                    response.flush();
                }
            }
            else {
                logger.printf(Level.WARN, "No content length information found. Sending status 411 Length Required");
                response.setStatus(411);
            }
        }
    }

    /**
     * Closing the connection with the client.
     */
    private void shutdown() {
        logger.printf(Level.INFO, "Shutting down server");

        running = false;

        threadPool.shutdown();

        try {
            if (!threadPool.awaitTermination(15, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.printf(Level.ERROR, "Error while terminating threadpool: %s." + e);
        }

        logger.printf(Level.DEBUG, "All tasks completed. Closing socket");

        try {
            //After closing the socket, thread will wake up from the socket.accept() call and then will exit the loop
            this.serverSocket.close();
            if (this.socket != null) {
                //When there is no remote host, then socket will be null.
                this.socket.close();
            }

            logger.printf(Level.DEBUG, "Server is down successfully");
        } catch (IOException ioException) {
            logger.printf(Level.ERROR, "Error while closing the remote connection. %s.", ioException);
        }
    }

    /**
     * Validates whether the request from client is valid.
     *
     * @param request Request string
     * @param response WebResponse
     * @return true if valid else false
     */
    private boolean validateRequest(String request, WebResponse response) {
        boolean isValid = false;

        if(Strings.isNullOrEmpty(request)) {
            logger.printf(Level.WARN, "Bad Request. Request empty");
            response.setStatus(400);
        }
        else {
            String[] requestLineParts = request.split("\\s");

            if(requestLineParts.length != 3) {
                //Bad Request
                logger.printf(Level.WARN, "Bad Request");
                response.setStatus(400);
            }
            else if(!requestLineParts[0].equals(HttpConstants.GET) && !requestLineParts[0].equals(HttpConstants.POST)) {
                //Method not supported
                logger.printf(Level.WARN, "Method not supported");
                response.setStatus(405);
            }
            else if(!handlers.containsKey(requestLineParts[1]) && !requestLineParts[1].equals(HttpConstants.SHUTDOWN_PATH)) {
                //Page not found
                logger.printf(Level.WARN, "Page not found");
                response.setStatus(404);
            }
            else if(!requestLineParts[2].equals(HttpConstants.VERSION)) {
                //version not supported
                logger.printf(Level.WARN, "Version not supported");
                response.setStatus(505);
            }
            else {
                isValid = true;
            }
        }

        return isValid;
    }

    /**
     * Reads all headers from input stream.
     *
     * @param inStream Input stream from client
     * @return Headers
     * @throws IOException
     */
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
