package com.feupsplaza.chat.shared.protocol;

public class ResponseFormatter {
    public static String format(Response response) {
        StringBuilder builder = new StringBuilder(response.getOperation().name());
        builder.append("|").append(response.getStatus().name());

        if (response.getData() != null) {
            for (String d : response.getData()) {
                builder.append("|").append(d);
            }
        }

        return builder.toString();
    }
}
