package server.controller;

import server.models.HttpRequest;
import server.models.HttpResponse;

import java.io.IOException;

public interface Handler {

    public void handle(HttpRequest request, HttpResponse response) throws IOException;
}
