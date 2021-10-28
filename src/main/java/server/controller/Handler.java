package server.controller;

import server.models.WebRequest;
import server.models.WebResponse;

import java.io.IOException;

public abstract class Handler {

    public void handle(WebRequest request, WebResponse response) throws IOException {
        if(request.isGET()) {
            doGet(request, response);
        }
        else if(request.isPOST()) {
            doPost(request, response);
        }
    }

    protected abstract void doGet(WebRequest request, WebResponse response);

    protected abstract void doPost(WebRequest request, WebResponse response) throws IOException;
}
