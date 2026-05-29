package com.feupsplaza.chat.server.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.feupsplaza.chat.server.ClientConnection;
import com.feupsplaza.chat.shared.model.User;

public class SessionController {
    private final Map<ClientConnection, User> activeUsers = new HashMap<>();
    private final Map<String, ClientConnection> activeClientsByUsername = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public boolean isUserOnline(String username) {
        lock.readLock().lock();
        try {
            return activeClientsByUsername.containsKey(username);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean loginClient(ClientConnection client, User user) {
        lock.writeLock().lock();
        try {
            String username = user.getUsername();

            ClientConnection existingClient = activeClientsByUsername.get(username);

            if (existingClient != null && existingClient != client){ return false;}

            activeUsers.put(client, user);
            activeClientsByUsername.put(username, client);
            System.out.println("[SERVER] " + user.getUsername() + " has logged in.");
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeClient(ClientConnection client) {
        lock.writeLock().lock();
        try {
            User user = activeUsers.remove(client);
            if (user != null) {
                if (activeClientsByUsername.get(user.getUsername()) == client){activeClientsByUsername.remove(user.getUsername());}
                System.out.println("[LOG] " + user.getUsername() + " disconnected.");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public User getUser(ClientConnection client) {
        lock.readLock().lock();
        try {
            return activeUsers.get(client);
        } finally {
            lock.readLock().unlock();
        }
    }
}
