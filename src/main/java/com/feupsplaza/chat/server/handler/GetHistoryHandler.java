package com.feupsplaza.chat.server.handler;

import com.feupsplaza.chat.server.ClientConnection;
import com.feupsplaza.chat.server.controller.RoomController;
import com.feupsplaza.chat.server.controller.SessionController;
import com.feupsplaza.chat.shared.model.User;
import com.feupsplaza.chat.shared.protocol.Operation;
import com.feupsplaza.chat.shared.protocol.Request;
import com.feupsplaza.chat.shared.protocol.Response;
import com.feupsplaza.chat.shared.protocol.Status;

import java.util.ArrayList;
import java.util.List;

public class GetHistoryHandler implements CommandHandler {
    private final RoomController roomController;

    public GetHistoryHandler(RoomController roomController) {
        this.roomController = roomController;
    }

    @Override
    public Response execute(Request request, ClientConnection client) {
        List<String> args = request.getArguments();
        if (args.isEmpty()) {
            return new Response(Operation.GET_HISTORY, Status.ERROR, List.of("Bad syntax"));
        }
        String roomName = args.get(0);
        List<List<String>> history = roomController.getHistory(roomName);
        List<String> formattedHistory = new ArrayList<>();
        for (List<String> entry : history) {
            if (entry.size() < 2) {
                continue;
            }
            String sender = entry.get(0);
            String message = entry.get(1);
            formattedHistory.add(sender + ": " + message);
            
        }
        return new Response(Operation.GET_HISTORY, Status.OK, formattedHistory);
    }
}