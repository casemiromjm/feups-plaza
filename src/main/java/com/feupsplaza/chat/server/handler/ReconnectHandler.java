package com.feupsplaza.chat.server.handler;

import com.feupsplaza.chat.server.ClientConnection;
import com.feupsplaza.chat.server.controller.AuthController;
import com.feupsplaza.chat.server.controller.SessionController;
import com.feupsplaza.chat.shared.model.User;
import com.feupsplaza.chat.shared.protocol.Operation;
import com.feupsplaza.chat.shared.protocol.Request;
import com.feupsplaza.chat.shared.protocol.Response;
import com.feupsplaza.chat.shared.protocol.Status;

import java.util.List;

public class ReconnectHandler implements CommandHandler {
    private final AuthController authController;
    private final SessionController sessionController;

    public ReconnectHandler(AuthController authController, SessionController sessionController) {

        this.authController = authController;
        this.sessionController = sessionController;
    }

    @Override
    public Response execute(Request request, ClientConnection client) {
        List<String> args = request.getArguments();
        if (args.isEmpty() || args == null) {
            return new Response(Operation.RECONNECT, Status.ERROR, List.of("Missing session token"));
        }
        String token = args.getFirst();
        User reconnectedUser = authController.authenticateUserByToken(token);
        if (reconnectedUser != null) {
            sessionController.loginClient(client, reconnectedUser);
            return new Response(Operation.RECONNECT, Status.OK, List.of(reconnectedUser.getUsername(), String.valueOf(reconnectedUser.getId()), token));
        }
        return new Response(Operation.RECONNECT, Status.ERROR, List.of("Invalid or expired token"));
    }
}
