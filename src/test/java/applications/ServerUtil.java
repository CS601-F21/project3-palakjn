package applications;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Make a GET or POST call to Http Server
 *
 * @author Palak Jain
 */
public class ServerUtil {

    /**
     * Make GET call
     * @param port Port the server is listening to
     * @param host Host like localhost
     * @param path Page to request like /find
     * @return response from the request
     */
    public static String doGet(int port, String host, String path) {
        StringBuilder builder = new StringBuilder();
        Socket socket = null;

        //Waiting for server to be up
        do {
            try {
                socket = new Socket(host, port);
            }
            catch (IOException ioException) {
                sleep(1000);
            }
        } while (socket == null);

        try (
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ){
            String request = getGETRequest(host, path);
            writer.println(request);
            writer.flush();

            String line = reader.readLine();
            while (line != null) {
                builder.append(line).append("\n");
                line = reader.readLine();
            }
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ioException) {
                System.err.println("Exception while closing the socket " + ioException.getMessage());
            }
        }

        return builder.toString();
    }

    /**
     * Make POST call
     * @param port Port the server is listening to
     * @param host Host like localhost
     * @param path Page to request like /find
     * @param body Request body
     * @return response from the request
     */
    public static String doPost(int port, String host, String path, String body) {
        StringBuilder builder = new StringBuilder();

        try (
                Socket socket = new Socket(host, port);
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ){
            String request = getPOSTRequest(host, path, body.length());
            writer.println(request);
            writer.println(body);
            writer.flush();

            String line = reader.readLine();
            while (line != null) {
                builder.append(line + "\n");
                line = reader.readLine();
            }
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }

        return builder.toString();
    }

    /**
     * Get the request string for making GET call
     * @param host
     * @param path
     * @return Request string
     */
    private static String getGETRequest(String host, String path) {
        String request = "GET " + path + " HTTP/1.1" + "\n" //GET request
                + "Host: " + host + "\n" //Host header required for HTTP/1.1
                + "Connection: close\n" //Closing connection
                + "\r\n";
        return request;
    }

    /**
     * Get the request string for making POST call
     * @param host
     * @param path
     * @param contentLength length of the body to send to the server
     * @return Request string
     */
    private static String getPOSTRequest(String host, String path, int contentLength) {
        String request = "POST " + path + " HTTP/1.1" + "\n" //Post request
                + "Host: " + host + "\n" //Host header required for HTTP/1.1
                + "Content-Length: " + contentLength + "\n" //Content Length
                + "Connection: close\n"; //Closing connection
        return request;
    }

    /**
     * Format the body and return it.
     */
    public static String getBody(String key, String value) {
        return String.format("%s=%s", key, value);
    }

    /**
     * Sleep for certain amount of time.
     * @param milliseconds The value in milliseconds
     */
    private static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException exception) {
            System.out.printf("Fail to sleep for %d time. %s.\n", milliseconds, exception);
        }
    }
}
