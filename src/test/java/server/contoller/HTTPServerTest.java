package server.contoller;

import applications.search.controller.FindHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.controller.HTTPServer;
import server.models.WebResponse;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class HTTPServerTest {
    private static HTTPServer httpServer;

    @BeforeAll
    public static void init() {
        httpServer = new HTTPServer(3000);
    }

    @Test
    public void validateRequest_nullRequest_send400() {
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));

        try {
            Method processMethod = HTTPServer.class.getDeclaredMethod("validateRequest", String.class, WebResponse.class);
            processMethod.setAccessible(true);
            processMethod.invoke(httpServer, null, webResponse);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }

        StringWriter expectedStringWriter = new StringWriter();
        PrintWriter expectedPrintWriter = new PrintWriter(expectedStringWriter);
        expectedPrintWriter.printf("HTTP/1.1 400 Bad Request\r\n");
        expectedPrintWriter.printf("Connection: close \r\n\r\n");

        Assertions.assertEquals(expectedStringWriter.toString(), actualStringWriter.toString());
    }

    @Test
    public void validateRequest_badRequest_send400() {
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));

        try {
            Method processMethod = HTTPServer.class.getDeclaredMethod("validateRequest", String.class, WebResponse.class);
            processMethod.setAccessible(true);
            processMethod.invoke(httpServer, "INVALID REQUEST", webResponse);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }

        StringWriter expectedStringWriter = new StringWriter();
        PrintWriter expectedPrintWriter = new PrintWriter(expectedStringWriter);
        expectedPrintWriter.printf("HTTP/1.1 400 Bad Request\r\n");
        expectedPrintWriter.printf("Connection: close \r\n\r\n");

        Assertions.assertEquals(expectedStringWriter.toString(), actualStringWriter.toString());
    }

    @Test
    public void validateRequest_putMethod_send405() {
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));

        try {
            Method processMethod = HTTPServer.class.getDeclaredMethod("validateRequest", String.class, WebResponse.class);
            processMethod.setAccessible(true);
            processMethod.invoke(httpServer, "PUT /find HTTP/1.1", webResponse);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }

        StringWriter expectedStringWriter = new StringWriter();
        PrintWriter expectedPrintWriter = new PrintWriter(expectedStringWriter);
        expectedPrintWriter.printf("HTTP/1.1 405 Method Not Allowed\r\n");
        expectedPrintWriter.printf("Connection: close \r\n\r\n");

        Assertions.assertEquals(expectedStringWriter.toString(), actualStringWriter.toString());
    }

    @Test
    public void validateRequest_pageNotFound_send404() {
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));

        try {
            Method processMethod = HTTPServer.class.getDeclaredMethod("validateRequest", String.class, WebResponse.class);
            processMethod.setAccessible(true);
            processMethod.invoke(httpServer, "GET /invalid HTTP/1.1", webResponse);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }

        StringWriter expectedStringWriter = new StringWriter();
        PrintWriter expectedPrintWriter = new PrintWriter(expectedStringWriter);
        expectedPrintWriter.printf("HTTP/1.1 404 Not Found\r\n");
        expectedPrintWriter.printf("Connection: close \r\n\r\n");
        expectedPrintWriter.println("<!DOCTYPE html>\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "  <title>Resource not found</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "  <p>The resource you are looking for was not found.</p>\n" +
                "\n" +
                "</body>\n" +
                "</html>");

        Assertions.assertEquals(expectedStringWriter.toString(), actualStringWriter.toString());
    }

    @Test
    public void validateRequest_versionIncorrect_send505() {
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));

        try {
            Field configField = HTTPServer.class.getDeclaredField("handlers");
            configField.setAccessible(true);
            configField.set(httpServer, Map.of("/find", new FindHandler()));
        }
        catch (NoSuchFieldException | IllegalAccessException exception) {
            System.err.println(exception.getMessage());
        }

        try {
            Method processMethod = HTTPServer.class.getDeclaredMethod("validateRequest", String.class, WebResponse.class);
            processMethod.setAccessible(true);
            processMethod.invoke(httpServer, "GET /find HTTP/1.0", webResponse);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }

        StringWriter expectedStringWriter = new StringWriter();
        PrintWriter expectedPrintWriter = new PrintWriter(expectedStringWriter);
        expectedPrintWriter.printf("HTTP/1.1 505 HTTP Version Not Supported\r\n");
        expectedPrintWriter.printf("Connection: close \r\n\r\n");

        Assertions.assertEquals(expectedStringWriter.toString(), actualStringWriter.toString());
    }

    @Test
    public void validateRequest_validRequest_returnTrue() {
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));

        try {
            Field configField = HTTPServer.class.getDeclaredField("handlers");
            configField.setAccessible(true);
            configField.set(httpServer, Map.of("/find", new FindHandler()));
        }
        catch (NoSuchFieldException | IllegalAccessException exception) {
            System.err.println(exception.getMessage());
        }

        try {
            Method processMethod = HTTPServer.class.getDeclaredMethod("validateRequest", String.class, WebResponse.class);
            processMethod.setAccessible(true);
            boolean actual = (boolean) processMethod.invoke(httpServer, "GET /find HTTP/1.1", webResponse);

            Assertions.assertTrue(actual);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }
    }
}
