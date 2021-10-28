package server.controller;

import server.models.WebRequest;
import server.models.WebResponse;

import java.io.IOException;

public interface Handler {

    public void handle(WebRequest request, WebResponse response) throws IOException;
}
