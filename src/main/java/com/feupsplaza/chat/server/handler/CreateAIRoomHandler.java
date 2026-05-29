package com.feupsplaza.chat.server.handler;

import com.feupsplaza.chat.server.ClientConnection;
import com.feupsplaza.chat.server.controller.RoomController;
import com.feupsplaza.chat.shared.protocol.Operation;
import com.feupsplaza.chat.shared.protocol.Request;
import com.feupsplaza.chat.shared.protocol.Response;
import com.feupsplaza.chat.shared.protocol.Status;

import java.util.List;

public class CreateAIRoomHandler implements CommandHandler {

    private final RoomController roomController;

    public CreateAIRoomHandler(RoomController roomController) {
        this.roomController = roomController;
    }

    @Override
    public Response execute(Request request, ClientConnection client) {
        List<String> args = request.getArguments();
        if (args.size() < 2) {
            return new Response(Operation.CREATE_AI_ROOM, Status.ERROR, List.of("Bad syntax"));
        }

        String roomName = args.get(0);
        String prompt = args.get(1);
        boolean created = roomController.createAiRoom(roomName, prompt);

        if (!created) {
            return new Response(Operation.CREATE_AI_ROOM, Status.FAIL, List.of("Room already exists"));
        }

        return new Response(Operation.CREATE_AI_ROOM, Status.OK, List.of(roomName));
    }
}