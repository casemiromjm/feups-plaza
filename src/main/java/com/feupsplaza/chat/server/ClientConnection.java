package com.feupsplaza.chat.server;

import com.feupsplaza.chat.server.ai.LLMClient;
import com.feupsplaza.chat.server.controller.AuthController;
import com.feupsplaza.chat.server.controller.RoomController;
import com.feupsplaza.chat.server.controller.SessionController;
import com.feupsplaza.chat.server.controller.RequestRouter;
import com.feupsplaza.chat.shared.protocol.Request;
import com.feupsplaza.chat.shared.protocol.RequestParser;
import com.feupsplaza.chat.shared.protocol.Response;
import com.feupsplaza.chat.shared.protocol.ResponseFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnection implements Runnable {

    private final Socket socket;
    private final SessionController sessionController;
    private final RoomController roomController;
    private final RequestRouter router;
    private String currentRoom = null;
    private PrintWriter out;
    private BufferedReader in;

    public ClientConnection(Socket socket, AuthController authController, SessionController sessionController, RoomController roomController, LLMClient llmClient) {
        this.socket = socket;
        this.sessionController = sessionController;
        this.roomController = roomController;
        this.router = new RequestRouter(authController, sessionController, roomController, llmClient);
    }

    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String rawRequest;
            while ((rawRequest = in.readLine()) != null) {
                Request request = RequestParser.parse(rawRequest);
                Response response = router.route(request, this);

                System.out.println("[LOG] Request Received: " + request);
                System.out.println("[LOG] Response Sent: " + response);

                if (response != null) {
                    out.println(ResponseFormatter.format(response));
                }
            }
        } catch (IOException e) {
            System.out.println("[SERVER] A client connection dropped.");
        } finally {
            if (currentRoom != null) {
                roomController.leaveRoom(currentRoom, this);
            }
            sessionController.removeClient(this);
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setCurrentRoom(String currentRoom) {
        this.currentRoom = currentRoom;
    }

    public String getCurrentRoom() {
        return currentRoom;
    }

    public void sendAsyncData(String data) {
        if (out != null) {
            out.println(data);
        }
    }
}
