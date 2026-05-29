package com.feupsplaza.chat.client;

import com.feupsplaza.chat.shared.model.User;
import com.feupsplaza.chat.client.view.CLIView;
import com.feupsplaza.chat.client.network.ServerConnection;

import java.util.Scanner;

public class ClientApp {

    public CLIView view;
    private final Scanner scanner;
    private User currentUser = null;
    private final ServerConnection connection;
    private final String host;
    private final int port;

    public ClientApp(String host, int port)
    {
        this.scanner = new Scanner(System.in);
        this.connection = new ServerConnection();
        this.view = new CLIView(this.scanner, this);
        this.host = host;
        this.port = port;
    }

    public void run()
    {
        if (!connection.connect(this.host, this.port)) {
            System.out.println("Failed to connect to the server. Try again later.");
            return;
        }

        boolean isRunning = true;
        while (isRunning)
        {
            isRunning = this.view.update();
        }

        System.out.println("The plaza is closing... See ya!");
        connection.close();
        scanner.close();
    }

    public ServerConnection getConnection() {
        return connection; 
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
