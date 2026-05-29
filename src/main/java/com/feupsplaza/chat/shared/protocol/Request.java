package com.feupsplaza.chat.shared.protocol;

import java.util.List;

public class Request {
    private final Operation operation;
    private final List<String> arguments;

    public Request(Operation op, List<String> arguments) {
        this.operation = op;
        this.arguments = arguments;
    }

    public Operation getOperation() {
        return operation;
    }

    public List<String> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        StringBuilder requestString = new StringBuilder();

        requestString.append("OP=").append(operation);
        requestString.append(" ARGS=[");
        requestString.append(String.join(",", arguments));
        requestString.append("]");

        return requestString.toString();
    }
}
