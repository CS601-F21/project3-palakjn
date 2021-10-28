package server.models;

import server.HttpConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class WebResponse {

    private PrintWriter writer;
    private BufferedReader reader;

    public WebResponse(PrintWriter writer, BufferedReader reader) {
        this.writer = writer;
        this.reader = reader;
    }

    public void setStatus(int statusCode) {
        if(HttpConstants.STATUS_CODE.containsKey(statusCode)) {
            writer.printf("%s %s\r\n", HttpConstants.VERSION, HttpConstants.STATUS_CODE.get(statusCode));
            writer.printf("%s \r\n\r\n", HttpConstants.CONNECTION_CLOSE);

            if(statusCode == 404) {
                writer.println(HttpConstants.NOT_FOUND_PAGE);
            }
        }
    }

    public String read(int contentLength) throws IOException {
        char[] bodyArr = new char[contentLength];
        reader.read(bodyArr, 0, bodyArr.length);

        String encodedBody = new String(bodyArr);
        return URLDecoder.decode(encodedBody.substring(encodedBody.indexOf("=") + 1), StandardCharsets.UTF_8.toString());
    }

    public void send(String response) {
        writer.println(response);
    }
}
