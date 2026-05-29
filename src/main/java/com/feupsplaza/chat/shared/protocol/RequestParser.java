package com.feupsplaza.chat.shared.protocol;

import java.util.ArrayList;
import java.util.List;

public class RequestParser {
    public static Request parse(String rawRequest) {
        if (rawRequest == null || rawRequest.trim().isEmpty()) {
            return new Request(Operation.ERROR, List.of("Empty or null request from client"));
        }

        String[] parts = rawRequest.split("\\|", -1);
        if (parts.length < 1) {
             return new Request(Operation.ERROR, List.of("Malformed request: " + rawRequest));
        }

        try {
            Operation op = Operation.valueOf(parts[0].trim().toUpperCase());
            List<String> args = new ArrayList<>();

            for (int i = 1; i < parts.length; i++) {
                args.add(parts[i]);
            }

            return new Request(op, args);
        } catch (IllegalArgumentException e) {
            return new Request(Operation.ERROR, List.of("Unrecognized protocol request: " + rawRequest));
        }
    }
}
