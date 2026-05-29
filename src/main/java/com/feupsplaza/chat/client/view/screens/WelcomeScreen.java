package com.feupsplaza.chat.client.view.screens;

import com.feupsplaza.chat.client.ClientApp;
import com.feupsplaza.chat.client.util.Command;
import com.feupsplaza.chat.client.util.CommandParser;
import com.feupsplaza.chat.shared.util.Pair;
import com.feupsplaza.chat.client.view.Screen;

import java.util.List;
import java.util.Scanner;

public class WelcomeScreen extends Screen {

    public WelcomeScreen(ClientApp clientApp)
    {
        super(clientApp);

        addScreenCommands(List.of(
            new Pair<>("/login, /l", "Log into your account"),
            new Pair<>("/register, /r", "Create a new account"),
            new Pair<>("/exit, /e", "Close the application")
        ));
    }

    @Override
    public void draw() {
        super.clearScreen();

        System.out.println("=============================================");
        System.out.println("=          WELCOME TO FEUP'S PLAZA          =");
        System.out.println("=              use /help or /h              =");
        System.out.println("=============================================");

        super.drawFeedback();
    }

    @Override
    public Screen handleInput(Scanner scanner) {
        //System.out.print("\033[5m> \033[0m");
        System.out.print("> ");
        String input = scanner.nextLine().trim();

        if (!input.startsWith("/"))
        {
            setFeedbackMessage("You can't send messages here.", false);
            return this;
        }

        Command command = CommandParser.parse(input);

        switch (command.getName())
        {
            case "/l":
            case "/login":
                return new LoginScreen(clientApp);

            case "/r":
            case "/register":
                return new RegisterScreen(clientApp);

            case "/h":
            case "/help":
                super.clearScreen();
                showCommands();
                System.out.println("\nPress Enter to reactivate command line...");
                scanner.nextLine();
                return this;

            case "/e":
            case "/exit":
                System.out.println("\nPress Enter to close the application...");
                scanner.nextLine();
                return null;

            default:
                setFeedbackMessage("Unknown command: " + command.getName(), false);
                return this;
        }

    }
}
