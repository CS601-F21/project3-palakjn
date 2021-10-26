package server;

import utils.Strings;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HTTPServer {

    private HashMap<String, Handler> handlers;
    private volatile boolean running = true;
    private int port;
    private ExecutorService threadPool;

    public HTTPServer(int port) {
        this.port = port;
        handlers = new HashMap<String, Handler>();
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
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);

            while (running) {
                //accept a new connection
                Socket socket = server.accept();
                System.out.println("New connection from " + socket.getInetAddress());

                //threadPool.execute(() -> handleRequest(socket));
                handleRequest(socket);
            }
        } catch (IOException ioException) {
            StringWriter writer = new StringWriter();
            ioException.printStackTrace(new PrintWriter(writer));
            System.out.println("Error while handling a request from client. Exception: "+ writer);
        }
    }

    private void handleRequest(Socket socket) {
        try (socket;
             BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream())
        ) {
            String requestLine = inStream.readLine();
            System.out.println("Request: " + requestLine);

            boolean isValidRequest = validateRequest(requestLine, writer);

            if(isValidRequest) {
                //Reading method, path, version
                String[] requestLineParts = requestLine.split("\\s");
                String method = requestLineParts[0];
                String path = requestLineParts[1];
                String version = requestLineParts[2];

                System.out.println("HTTP method: "+ method);
                System.out.println("Path: " + path);
                System.out.println("Version: " + version);

                //Read headers
                Map<String, String> headers = getHeader(inStream);

                if(method.equals(HttpConstants.GET)) {
                    handleRequest(path, writer, null);
                }
                else if(method.equals(HttpConstants.POST)) {
                    int contentLength = Integer.parseInt(headers.getOrDefault(HttpConstants.CONTENT_LENGTH, "0"));

                    if(contentLength != 0) {
                        String content = extractContent(inStream, contentLength);
                        handleRequest(path, writer, content);
                    }
                    else {
                        ServerUtils.send411(writer);
                    }
                }
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private boolean validateRequest(String request, PrintWriter writer) {
        boolean isValid = false;

        if(Strings.isNullOrEmpty(request)) {
            ServerUtils.send400(writer);
        }
        else {
            String[] requestLineParts = request.split("\\s");

            if(requestLineParts.length != 3) {
                //Bad Request
                ServerUtils.send400(writer);
            }
            else if(!requestLineParts[0].equals(HttpConstants.GET) && !requestLineParts[0].equals(HttpConstants.POST)) {
                //Method not supported
                ServerUtils.send405(writer);
            }
            else if(!handlers.containsKey(requestLineParts[1])) {
                //Page not found
                ServerUtils.send404(writer);
            }
            else if(!requestLineParts[2].equals(HttpConstants.VERSION)) {
                //version not supported
                ServerUtils.send505(writer);
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

    private String extractContent(BufferedReader inStream, int contentLength) throws IOException {
        char[] bodyArr = new char[contentLength];
        inStream.read(bodyArr, 0, bodyArr.length);

        String encodedBody = new String(bodyArr);
        return URLDecoder.decode(encodedBody.substring(encodedBody.indexOf("=") + 1), StandardCharsets.UTF_8.toString());
    }

    private void handleRequest(String path, PrintWriter writer, String body) {
        String page = handlers.get(path).handle(body);
        if(!Strings.isNullOrEmpty(page)) {
            ServerUtils.send200(writer);
            writer.println(page);
        }
        else {
            //No content to send to client
            ServerUtils.send204(writer);
        }
    }
}
