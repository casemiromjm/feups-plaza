package com.feupsplaza.chat.server.controller;

import com.feupsplaza.chat.server.ClientConnection;
import com.feupsplaza.chat.server.ai.LLMClient;
import com.feupsplaza.chat.server.handler.*;
import com.feupsplaza.chat.shared.protocol.Operation;
import com.feupsplaza.chat.shared.protocol.Request;
import com.feupsplaza.chat.shared.protocol.Response;
import com.feupsplaza.chat.shared.protocol.Status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestRouter {

    private final Map<Operation, CommandHandler> handlers = new HashMap<>();

    public RequestRouter(AuthController authController, SessionController sessionController, RoomController roomController, LLMClient llmClient) {
        handlers.put(Operation.REGISTER, new RegisterHandler(authController));
        handlers.put(Operation.LOGIN, new LoginHandler(authController, sessionController));
        handlers.put(Operation.LIST_ROOMS, new ListRoomsHandler(roomController));
        handlers.put(Operation.JOIN_ROOM, new JoinRoomHandler(roomController,sessionController));
        handlers.put(Operation.SEND_MESSAGE, new SendMessageHandler(roomController,sessionController, llmClient));
        handlers.put(Operation.LEAVE_ROOM, new LeaveRoomHandler(roomController,sessionController));
        handlers.put(Operation.LIST_ROOM_USERS, new ListRoomUsersHandler(roomController));
        handlers.put(Operation.RECONNECT, new ReconnectHandler(authController, sessionController));
        handlers.put(Operation.GET_HISTORY, new GetHistoryHandler(roomController));
        handlers.put(Operation.CREATE_AI_ROOM, new CreateAIRoomHandler(roomController));
    }

    public Response route(Request request, ClientConnection client) {
        try {
            CommandHandler handler = handlers.get(request.getOperation());
            
            if (handler != null) {
                return handler.execute(request, client);
            }
            
            return new Response(Operation.ERROR, Status.ERROR, List.of("Unknown command: " + request.getOperation()));
            
        } catch (Exception e) {
            return new Response(Operation.ERROR, Status.ERROR, List.of("Malformed request"));
        }
    }
}
