package com.feupsplaza.chat.client.network;

import com.feupsplaza.chat.client.util.SynchronousResponseQueue;
import com.feupsplaza.chat.shared.protocol.Operation;
import com.feupsplaza.chat.shared.protocol.Response;
import com.feupsplaza.chat.shared.protocol.ResponseParser;
import com.feupsplaza.chat.shared.protocol.Status;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.Socket;
import java.security.KeyStore;
import java.util.List;

public class ServerConnection {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String currentRoom = null;
    private String token;
    private final SynchronousResponseQueue responseQueue = new SynchronousResponseQueue();
    private String host;
    private int port;

    public boolean connect(String host, int port) {
        this.host = host;
        this.port = port;
        return establishConnection();
    }

    private boolean establishConnection() {
        try {
            // technically this is not fine, but for our project is fine. ideally the truststore and the keystore are not the same
            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            trustStore.load(new FileInputStream("server_keystore.p12"), "password".toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            SSLSocketFactory factory = sslContext.getSocketFactory();
            socket = factory.createSocket(this.host, this.port);

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread.ofVirtual().start(() -> {
                try {
                    String incomingLine;
                    while ((incomingLine = in.readLine()) != null) {
                        Response response = ResponseParser.parse(incomingLine);

                        if (response.getOperation().equals(Operation.CHAT_BROADCAST)) {
                            handleBroadcast(response);
                        } else {
                            responseQueue.put(response);
                        }
                    }
                    System.out.println("\n[SYSTEM] Server closed connection. Reconnecting transparently...");
                    attemptRecovery();

                } catch (IOException e) {
                    System.out.println("\n[SYSTEM] Connection to server lost. Reconnecting transparently...");
                    attemptRecovery();
                }
            });

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void attemptRecovery() {
        while (true) {
            try {
                Thread.sleep(2000);
                if (establishConnection()) {
                    System.out.println("\n[SYSTEM] Network link restored! Restoring chat session...");
                    if (this.token != null) {
                        out.println("RECONNECT|" + this.token);
                        Response reconnectResponse = responseQueue.take();

                        if (reconnectResponse.hasSucceeded()) {
                            if (this.currentRoom != null) {
                                out.println("JOIN_ROOM|" + this.currentRoom);
                                responseQueue.take();
                            }
                        } else {
                            // failed due to an expired token (most likely)
                            System.out.println("\n[SYSTEM] Your session has expired. Please press Enter to return the Welcome Menu.");
                            this.token = null;
                            this.currentRoom = null;
                        }
                    }
                    break;
                }
            } catch (Exception ex) {
            }
        }
    }

    public Response send(String data) {
        if (out == null || in == null) {
            return new Response(Operation.ERROR, Status.ERROR, List.of("Not connected"));
        }

        out.println(data);
        return responseQueue.take();
    }

    public void setCurrentRoom(String room) {
        this.currentRoom = room;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void close() {
        try {
            if (socket != null) socket.close();
        }  catch (IOException e) { }
    }

    public void handleBroadcast(Response response) {
        List<String> data = response.getData();
        if (data != null && data.size() >= 3) {
            String room = data.get(0);
            String sender = data.get(1);
            String message = data.get(2);

            System.out.println("\r" + sender + ": " + message);
            System.out.print("> ");
        }
    }
}