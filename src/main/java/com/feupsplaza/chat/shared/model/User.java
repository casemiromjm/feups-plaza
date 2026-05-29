package com.feupsplaza.chat.shared.model;

public class User {
    private int id;
    private String username;
    private String hashedPassword;
    private String token;
    /**
     * value in ms
     */
    private long tokenExpiresAt;

    // constructor for creating new user
    public User(String username, String password) {
        this.username = username;
        this.hashedPassword = password;
    }

    // constructor for fetching existing user
    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.hashedPassword = password;
    }

    public int getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getHashedPassword() {
        return this.hashedPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getTokenExpiresAt() {
        return tokenExpiresAt;
    }

    public void setTokenExpiresAt(long tokenExpiresAt) {
        this.tokenExpiresAt = tokenExpiresAt;
    }
}
