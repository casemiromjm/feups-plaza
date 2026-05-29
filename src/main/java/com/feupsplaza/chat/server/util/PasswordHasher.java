package com.feupsplaza.chat.server.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordHasher {

    private static final int hashingCost = 12;

    public static String hash(String rawPassword) {
        return BCrypt.withDefaults().hashToString(hashingCost, rawPassword.toCharArray());
    }
    public static boolean checkPasswordHash(String rawPassword, String hashedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toCharArray(), hashedPassword);
        
        return result.verified;
    }

}
