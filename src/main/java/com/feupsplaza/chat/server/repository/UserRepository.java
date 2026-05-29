package com.feupsplaza.chat.server.repository;

import com.feupsplaza.chat.shared.model.User;

public interface UserRepository {
    boolean userExists(String username);
    boolean storeUser(User user);
    User getUserByUsername(String username);
    User getUserByToken(String token);
    boolean updateUserToken(User user, String token, long expiresAt);
}
