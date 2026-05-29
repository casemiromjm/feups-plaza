package com.feupsplaza.chat.shared.protocol;

/**
 * Converts a Request object into a formatted string
 */
public class RequestFormatter {
    public static String format(Request request) {
        StringBuilder builder = new StringBuilder(request.getOperation().name());

        for (String arg : request.getArguments()) {
            builder.append("|").append(arg);
        }

        return builder.toString();
    }
}
