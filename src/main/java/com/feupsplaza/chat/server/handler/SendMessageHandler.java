package com.feupsplaza.chat.server.handler;

import com.feupsplaza.chat.server.ClientConnection;
import com.feupsplaza.chat.server.ai.LLMClient;
import com.feupsplaza.chat.server.controller.RoomController;
import com.feupsplaza.chat.server.controller.SessionController;
import com.feupsplaza.chat.shared.model.User;
import com.feupsplaza.chat.shared.protocol.Operation;
import com.feupsplaza.chat.shared.protocol.Request;
import com.feupsplaza.chat.shared.protocol.Response;
import com.feupsplaza.chat.shared.protocol.Status;

import java.util.List;

public class SendMessageHandler implements CommandHandler {
    
    private final RoomController roomController;
    private final SessionController sessionController;
    private final LLMClient llmClient;

    public SendMessageHandler(RoomController roomController,SessionController sessionController, LLMClient llmClient) {
        this.roomController = roomController;
        this.sessionController = sessionController;
        this.llmClient = llmClient;
    }

    @Override
    public Response execute(Request request, ClientConnection client) {
        List<String> args = request.getArguments();
        if (args.size() != 2) {
            return new Response(Operation.ERROR, Status.ERROR, List.of("Bad syntax"));
        }

        String room = args.get(0);
        User user = sessionController.getUser(client);
        String sender = user.getUsername();
        String message = args.get(1);

        roomController.broadcastMessage(room, sender, message);

        // For AI rooms, only respond if message is an AI command
        if (roomController.isAiRoom(room)) {
            if (isAiMessage(message)) {
                String prompt = roomController.buildAiContext(room);
                String answer = llmClient.ask(prompt);
            
                if (answer.startsWith("SYSTEM:")) {
                    answer = answer.substring(7).trim();
                }
                roomController.broadcastMessage(room, "Bot", answer);
            }
        }

        return new Response(Operation.SEND_MESSAGE, Status.OK, List.of());
    }

    private boolean isAiMessage(String message) {
            return message != null && message.startsWith("/ai ");
    }
}
