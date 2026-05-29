package com.feupsplaza.chat.server.controller;

import com.feupsplaza.chat.shared.model.User;
import com.feupsplaza.chat.server.repository.SqlUserRepository;
import com.feupsplaza.chat.server.repository.UserRepository;
import com.feupsplaza.chat.server.util.PasswordHasher;
import com.feupsplaza.chat.server.util.UserTokenGenerator;

public class AuthController {
    private final UserRepository repo;

    public AuthController(UserRepository repo) {
        this.repo = repo;
    }

    public boolean registerUser(String rawUsername, String rawPassword) {

        // check if exists
        if (repo.userExists(rawUsername)) {
            return false;
        }

        // hash pass
        String hashedPassword = PasswordHasher.hash(rawPassword);

        User user = new User(rawUsername, hashedPassword);

        return repo.storeUser(user);
    }

    public User authenticateUser(String rawUsername, String rawPassword) {
        if (!repo.userExists(rawUsername)) return null;

        User user = repo.getUserByUsername(rawUsername);

        if (user.equals(null)) {
            return null;
        }

        if (PasswordHasher.checkPasswordHash(rawPassword, user.getHashedPassword())) {
            String token = UserTokenGenerator.generate();

            // expiration time of 1 hour
            long expiresAt = System.currentTimeMillis() + (60 * 60 * 1000);

            boolean success_token = repo.updateUserToken(user, token, expiresAt);

            if (success_token) {
                user.setToken(token);
                user.setTokenExpiresAt(expiresAt);

                return user;
            }
        }

        return null;
    }

    public User authenticateUserByToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }

        User user = repo.getUserByToken(token);

        if (user != null) {
            if (System.currentTimeMillis() > user.getTokenExpiresAt()) {
                System.out.println("[SYSTEM] Reconnection cancelled. Token has expired for user: " + user.getUsername());
                return null;
            }

            return user;
        }

        return null;
    }
}
