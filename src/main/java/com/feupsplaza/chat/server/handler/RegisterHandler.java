package com.feupsplaza.chat.server.handler;

import com.feupsplaza.chat.server.ClientConnection;
import com.feupsplaza.chat.server.controller.AuthController;
import com.feupsplaza.chat.shared.protocol.Operation;
import com.feupsplaza.chat.shared.protocol.Request;
import com.feupsplaza.chat.shared.protocol.Response;
import com.feupsplaza.chat.shared.protocol.Status;

import java.util.List;

public class RegisterHandler implements CommandHandler {
    private final AuthController authController;

    public RegisterHandler(AuthController authController) {
        this.authController = authController;
    }

    @Override
    public Response execute(Request request, ClientConnection client) {
        List<String> args = request.getArguments();
        if (args.size() == 2) {
            if (authController.registerUser(args.get(0), args.get(1))) {
                return new Response(Operation.REGISTER, Status.OK, List.of());
            }
            return new Response(Operation.REGISTER, Status.FAIL, List.of("Username already exists"));
        }
        return new Response(Operation.REGISTER, Status.ERROR, List.of("Bad syntax"));
    }
}
