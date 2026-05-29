package com.feupsplaza.chat.server.controller;
import com.feupsplaza.chat.server.ClientConnection;
import com.feupsplaza.chat.shared.model.User;
import com.feupsplaza.chat.shared.protocol.Operation;
import com.feupsplaza.chat.shared.protocol.Response;
import com.feupsplaza.chat.shared.protocol.ResponseFormatter;
import com.feupsplaza.chat.shared.protocol.Status;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.*;

public class RoomController {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, List<ClientConnection>> existingRooms = new HashMap<>();
    private final Map<String, List<List<String>>> roomHistory = new HashMap<>();
    private final Map<String, String> aiRooms = new HashMap<>();
    private static final int MAX_HISTORY_SIZE = 100;
    private final SessionController sessionController;

    public RoomController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public String getExistingRooms() {
        lock.readLock().lock();
        try {
            if (existingRooms.isEmpty()) {
                return "";
            }
            return String.join(",", existingRooms.keySet());
        } finally {
            lock.readLock().unlock();
        }
    }

    public String getUsersInRoom(String roomName) {
        lock.readLock().lock();

        try {
            List<ClientConnection> clientsInRoom = existingRooms.get(roomName);
            if (clientsInRoom == null || clientsInRoom.isEmpty()) {
                return "";
            }

            List<String> usernames = new ArrayList<>();
            for (var client : clientsInRoom) {
                User user = sessionController.getUser(client);
                if (user != null) {
                    usernames.add(user.getUsername());
                }
            }
            return String.join(",", usernames);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean joinOrCreateRoom(String roomName, ClientConnection client) {
        lock.writeLock().lock();
        try {
            existingRooms.putIfAbsent(roomName, new ArrayList<>());
            roomHistory.putIfAbsent(roomName, new ArrayList<>());
            List<ClientConnection> clientsInRoom = existingRooms.get(roomName);
            if (!clientsInRoom.contains(client)) {
                clientsInRoom.add(client);
            }
            System.out.println("[LOG] Joined room '" + roomName + "'. Users now: " + clientsInRoom.size());
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void leaveRoom(String roomName, ClientConnection client) {
        lock.writeLock().lock();
        try {
            List<ClientConnection> clientsInRoom = existingRooms.get(roomName);
            if (clientsInRoom != null) {
                clientsInRoom.remove(client);

                if (clientsInRoom.isEmpty()) {
                    existingRooms.remove(roomName);
                    roomHistory.remove(roomName);
                    aiRooms.remove(roomName);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void broadcastMessage(String room, String sender, String message) {
        lock.writeLock().lock();

        try {
            List<ClientConnection> clientsInRoom = existingRooms.get(room);
            if (clientsInRoom != null) {
                Response pushMessage = new Response(Operation.CHAT_BROADCAST, Status.OK, List.of(room, sender, message));
                String formattedPush = ResponseFormatter.format(pushMessage);
                saveMessage(room, sender, message);
                System.out.println("[LOG] Broadcasting to " + clientsInRoom.size() + " users in room '"
                        + room + "': " + formattedPush);

                for (var client : clientsInRoom) {
                    client.sendAsyncData(formattedPush);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<List<String>> getHistory(String roomName) {
        lock.readLock().lock();
        try {
            List<List<String>> history = roomHistory.get(roomName);
            if (history == null) {
                return List.of();
            }
            return history;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void saveMessage(String roomName, String sender, String message){
        roomHistory.putIfAbsent(roomName, new ArrayList<>());

        List<List<String>> history = roomHistory.get(roomName);
        history.add(List.of(sender,message));
        if (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0);
        }
    }

    public boolean createAiRoom(String roomName, String prompt) {
        lock.writeLock().lock();
        try {
            if (existingRooms.containsKey(roomName)) {
                return false;
            }
            existingRooms.put(roomName, new ArrayList<>());
            roomHistory.put(roomName, new ArrayList<>());
            aiRooms.put(roomName, prompt);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean isAiRoom(String roomName) {
        return aiRooms.containsKey(roomName);
    }

    public String buildAiContext(String roomName) {
        lock.readLock().lock();
        try {
            List<List<String>> history = roomHistory.get(roomName);
            String prompt = aiRooms.get(roomName);

            StringBuilder builder = new StringBuilder();

            builder.append("Prompt: ").append(prompt).append("\n\n");

            if (history != null) {
                for (List<String> msg : history) {
                    if (msg.size() >= 2) {
                        String sender = msg.get(0);
                        String message = msg.get(1);
                        builder.append(sender).append(": ").append(message).append("\n");
                    }
            }
        }

            return builder.toString();
        } finally {
            lock.readLock().unlock();
        }
    }

}