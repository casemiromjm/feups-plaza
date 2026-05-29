package com.feupsplaza.chat.server.handler;

import com.feupsplaza.chat.server.ClientConnection;
import com.feupsplaza.chat.server.controller.RoomController;
import com.feupsplaza.chat.shared.protocol.Operation;
import com.feupsplaza.chat.shared.protocol.Request;
import com.feupsplaza.chat.shared.protocol.Response;
import com.feupsplaza.chat.shared.protocol.Status;

import java.util.List;

public class ListRoomUsersHandler implements CommandHandler {
    private final RoomController roomController;

    public ListRoomUsersHandler(RoomController roomController) {
        this.roomController = roomController;
    }

    @Override
    public Response execute(Request request, ClientConnection client) {
        List<String> args = request.getArguments();
        if (args.isEmpty()) {
            return new Response(Operation.LIST_ROOM_USERS, Status.ERROR, List.of("Bad syntax"));
        }
        String roomName = args.get(0);
        String users = roomController.getUsersInRoom(roomName);
        return new Response(Operation.LIST_ROOM_USERS, Status.OK, List.of(users));
    }
}
