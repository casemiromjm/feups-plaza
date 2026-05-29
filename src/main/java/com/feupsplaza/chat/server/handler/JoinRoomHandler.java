package com.feupsplaza.chat.server.handler;

import com.feupsplaza.chat.server.ClientConnection;
import com.feupsplaza.chat.server.controller.RoomController;
import com.feupsplaza.chat.server.controller.SessionController;
import com.feupsplaza.chat.shared.model.User;
import com.feupsplaza.chat.shared.protocol.Operation;
import com.feupsplaza.chat.shared.protocol.Request;
import com.feupsplaza.chat.shared.protocol.Response;
import com.feupsplaza.chat.shared.protocol.Status;

import java.util.List;

public class JoinRoomHandler implements CommandHandler {
    private final RoomController roomController;
    private final SessionController sessionController;

    public JoinRoomHandler(RoomController roomController, SessionController sessionController) {
        this.roomController = roomController;
        this.sessionController = sessionController;
    }

    @Override
    public Response execute(Request request, ClientConnection client) {
        List<String> args = request.getArguments();
        if (args.isEmpty()) {
            return new Response(Operation.JOIN_ROOM, Status.ERROR, List.of("Bad syntax"));
        }
        User user =sessionController.getUser(client);
        String roomName = args.get(0);
        if (roomController.joinOrCreateRoom(roomName, client)) {
            client.setCurrentRoom(roomName);
            roomController.broadcastMessage(roomName, "SYSTEM", user.getUsername() + " entered the room" );
            return new Response(Operation.JOIN_ROOM, Status.OK, List.of(roomName));
        }
        return new Response(Operation.JOIN_ROOM, Status.FAIL, List.of("Failed to join room"));
    }
}
