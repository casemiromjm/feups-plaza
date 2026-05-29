package com.feupsplaza.chat.shared.protocol;

import java.util.List;

public class Response {
    private final Operation operation;
    private final Status status;
    private final List<String> data;

    public Response(Operation operation, Status status, List<String> data) {
        this.operation = operation;
        this.status = status;
        this.data = data;
    }

    public Operation getOperation() {
        return operation;
    }

    public Status getStatus() {
        return status;
    }

    public List<String> getData() {
        return data;
    }

    public boolean isError() {
        return status == Status.ERROR;
    }

    public boolean hasFailed() {
        return status == Status.FAIL;
    }

    public boolean hasSucceeded() {
        return status == Status.OK;
    }

    public String toString() {
        StringBuilder responseString = new StringBuilder();

        responseString.append("OP=").append(operation);
        responseString.append(" STATUS=").append(status);
        responseString.append(" DATA=[");
        responseString.append(String.join(",", data));
        responseString.append("]");

        return responseString.toString();
    }
}
