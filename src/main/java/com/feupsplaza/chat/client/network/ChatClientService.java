package com.feupsplaza.chat.client.network;

import com.feupsplaza.chat.shared.protocol.Operation;
import com.feupsplaza.chat.shared.protocol.Request;
import com.feupsplaza.chat.shared.protocol.Response;
import com.feupsplaza.chat.shared.model.User;
import com.feupsplaza.chat.shared.protocol.RequestFormatter;

import java.util.List;

public class ChatClientService {
    private final ServerConnection connection;

    public ChatClientService(ServerConnection connection) {
        this.connection = connection;
    }

    private Response sendRequest(Request request) {
        String rawRequest = RequestFormatter.format(request);

        return connection.send(rawRequest);
    }

    public User login(String username, String password) {
        Request req = new Request(Operation.LOGIN, List.of(username, password));

        Response res = sendRequest(req);

        if (res.getOperation().equals(Operation.LOGIN) && res.hasSucceeded()) {
            String returnedUsername = res.getData().get(0);
            int id = Integer.parseInt(res.getData().get(1));
            String token = res.getData().get(2);

            User user = new User(id, returnedUsername, null);
            user.setToken(token);
            return user;
        }

        return null;
    }

    public boolean register(String username, String password) {
        Request req = new Request(Operation.REGISTER, List.of(username, password));
        Response res = sendRequest(req);

        return res.getOperation().equals(Operation.REGISTER) && res.hasSucceeded();
    }

    public List<String> listRooms() {
        Request req = new Request(Operation.LIST_ROOMS, List.of());
        Response res = sendRequest(req);

        if (res.getOperation().equals(Operation.LIST_ROOMS) && res.hasSucceeded()) {
            // room1,room2,room3...
            String rooms = res.getData().getFirst().trim();

            if (rooms.isEmpty()) {
                // no room to retrieve
                return List.of();
            }

            return List.of(rooms.split(","));
        }

        // failed to retrieve rooms
        return null;
    }

    public List<String> listUsersInRoom(String roomName) {
        Request req = new Request(Operation.LIST_ROOM_USERS, List.of(roomName));
        Response res = sendRequest(req);

        if (res.getOperation().equals(Operation.LIST_ROOM_USERS) && res.hasSucceeded()) {
            String usersInRoom = res.getData().getFirst().trim();

            if (usersInRoom.isEmpty()) {
                return List.of();
            }

            return List.of(usersInRoom.split(","));
        }

        return null;
    }

    public List<String> getHistory(String roomName) {
        Request req = new Request(Operation.GET_HISTORY, List.of(roomName));
        Response res = sendRequest(req);

        if (!res.getOperation().equals(Operation.GET_HISTORY) || !res.hasSucceeded()) {
            return List.of();
        }

        return res.getData();
    }

    public boolean joinRoom(String roomName) {
        Request req = new Request(Operation.JOIN_ROOM, List.of(roomName));
        Response res = sendRequest(req);
        if (res.getOperation().equals(Operation.JOIN_ROOM) && res.hasSucceeded()) {
            connection.setCurrentRoom(roomName);
            return true;
        }
        return false;
    }

    public boolean leaveRoom(String roomName) {
        Request req = new Request(Operation.LEAVE_ROOM, List.of(roomName));
        Response res = sendRequest(req);
        if (res.getOperation().equals(Operation.LEAVE_ROOM) && res.hasSucceeded()) {
            connection.setCurrentRoom(null);
            return true;
        }
        return false;
    }

    public boolean sendMessage(String room,String message) {
        Request req = new Request(Operation.SEND_MESSAGE, List.of(room,message));
        Response res = sendRequest(req);

        return res.getOperation().equals(Operation.SEND_MESSAGE) && res.hasSucceeded();
    }

    public User reconnect(String token) {
        Request request = new Request(Operation.RECONNECT, List.of(token));
        Response response = sendRequest(request);
        if (response.getOperation().equals(Operation.RECONNECT) && response.hasSucceeded()) {
            String returnedUsername = response.getData().get(0);
            int id = Integer.parseInt(response.getData().get(1));
            User user = new User(id, returnedUsername, null);
            user.setToken(token);
            return user;
        }
        return null;
    }

    public boolean createAiRoom(String roomName, String prompt) {
        Request req = new Request(Operation.CREATE_AI_ROOM, List.of(roomName, prompt));
        Response res = sendRequest(req);

        return res.getOperation().equals(Operation.CREATE_AI_ROOM) && res.hasSucceeded();
    }

}
