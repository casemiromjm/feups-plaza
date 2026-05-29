package com.feupsplaza.chat.server.util;

import java.util.UUID;

public class UserTokenGenerator {

    public static String generate() {
        return UUID.randomUUID().toString();
    }

}
