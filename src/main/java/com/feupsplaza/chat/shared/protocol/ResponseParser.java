package com.feupsplaza.chat.shared.protocol;

import java.util.ArrayList;
import java.util.List;

public class ResponseParser {
    public static Response parse(String rawResponse) {
        if (rawResponse == null || rawResponse.trim().isEmpty()) {
            return new Response(Operation.ERROR, Status.ERROR, List.of("Empty or null response from the server"));
        }

        // using -1 to capture a potential empty data payload
        String[] parts = rawResponse.split("\\|", -1);

        if (parts.length < 2) {
            return new Response(Operation.ERROR, Status.ERROR, List.of("Malformed response: " + rawResponse));
        }

        try {
            Operation op = Operation.valueOf(parts[0].trim().toUpperCase());
            Status st = Status.valueOf(parts[1].trim().toUpperCase());

            List<String> data = new ArrayList<>();
            if (parts.length > 2) {
                for (int i = 2; i < parts.length; i++) {
                    data.add(parts[i]);
                }
            }

            return new Response(op, st, data);
        } catch (IllegalArgumentException e) {
            return new Response(Operation.ERROR, Status.ERROR, List.of("Unrecognized protocol response: " + rawResponse));
        }
    }
}
