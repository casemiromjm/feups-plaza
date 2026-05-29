package com.feupsplaza.chat.server;

import com.feupsplaza.chat.server.ai.LLMClient;
import com.feupsplaza.chat.server.ai.OllamaClient;
import com.feupsplaza.chat.server.controller.AuthController;
import com.feupsplaza.chat.server.controller.SessionController;
import com.feupsplaza.chat.server.controller.RoomController;
import com.feupsplaza.chat.server.repository.SqlUserRepository;
import com.feupsplaza.chat.server.repository.UserRepository;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.security.KeyStore;

public class Server {

    private final String hostAddress;
    private final int port;

    public Server(String host, int port) {
        this.hostAddress = host;
        this.port = port;
    }

    public void run() {

        UserRepository userRepository = new SqlUserRepository();
        AuthController authController = new AuthController(userRepository);
        SessionController sessionController = new SessionController();
        RoomController roomController = new RoomController(sessionController);
        LLMClient llmClient = new OllamaClient();

        System.out.println("Feup's Plaza Server starting on address " + this.hostAddress + " on port " + this.port + "...");

        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream("server_keystore.p12"), "password".toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, "password".toCharArray());

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            SSLServerSocketFactory factory = sslContext.getServerSocketFactory();

            InetAddress address = InetAddress.getByName(this.hostAddress);

            try (SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(this.port, 50, address)) {

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("[SERVER] New raw connection from: " + clientSocket.getInetAddress());
                    ClientConnection client = new ClientConnection(clientSocket, authController, sessionController, roomController, llmClient);
                    Thread.ofVirtual().start(client);
                }

            }
        } catch (Exception ex) {
            System.out.println("Server Error: " + ex.getMessage());
        }
    }
}
        
