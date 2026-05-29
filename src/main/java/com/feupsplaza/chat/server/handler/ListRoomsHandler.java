package com.feupsplaza.chat.server.handler;

import com.feupsplaza.chat.server.ClientConnection;
import com.feupsplaza.chat.server.controller.RoomController;
import com.feupsplaza.chat.shared.protocol.Operation;
import com.feupsplaza.chat.shared.protocol.Request;
import com.feupsplaza.chat.shared.protocol.Response;
import com.feupsplaza.chat.shared.protocol.Status;

import java.util.List;

public class ListRoomsHandler implements CommandHandler {
    private final RoomController roomController;

    public ListRoomsHandler(RoomController roomController) {
        this.roomController = roomController;
    }

    @Override
    public Response execute(Request request, ClientConnection client) {
        return new Response(Operation.LIST_ROOMS, Status.OK, List.of(roomController.getExistingRooms()));
    }
}
