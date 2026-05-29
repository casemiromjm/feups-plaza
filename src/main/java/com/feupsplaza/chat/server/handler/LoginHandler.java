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

public class LoginHandler implements CommandHandler {
    private final AuthController authController;
    private final SessionController sessionController;

    public LoginHandler(AuthController authController, SessionController sessionController) {
        this.authController = authController;
        this.sessionController = sessionController;
    }

    @Override
    public Response execute(Request request, ClientConnection client) {
        List<String> args = request.getArguments();

        if (args.size() == 2) {
            User user = authController.authenticateUser(args.get(0), args.get(1));
            if (sessionController.isUserOnline(user.getUsername())){ 
                return new Response(Operation.LOGIN, Status.FAIL, List.of("This account is already logged in"));
            }
            if (user != null) {
                if(sessionController.loginClient(client, user)){
                    return new Response(Operation.LOGIN, Status.OK, List.of(user.getUsername(), String.valueOf(user.getId()), user.getToken()));
                }
                return new Response(Operation.LOGIN,Status.FAIL,List.of("This account is already logged in"));
            }
        }
        return new Response(Operation.LOGIN, Status.ERROR, List.of("Bad syntax"));
    }
}
