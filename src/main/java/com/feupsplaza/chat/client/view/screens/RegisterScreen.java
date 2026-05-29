package com.feupsplaza.chat.client.view.screens;

import com.feupsplaza.chat.client.ClientApp;
import com.feupsplaza.chat.client.network.ChatClientService;
import com.feupsplaza.chat.client.util.Command;
import com.feupsplaza.chat.client.util.CommandParser;
import com.feupsplaza.chat.shared.util.Pair;
import com.feupsplaza.chat.client.view.Screen;

import java.util.List;
import java.util.Scanner;

public class RegisterScreen extends Screen {

    private String username = "";
    private String password = "";

    public RegisterScreen(ClientApp clientApp) {
        super(clientApp);

        addScreenCommands(List.of(
                new Pair<>("/back, /b", "Goes to back to Welcome Menu"),
                new Pair<>("/username USERNAME, /u USERNAME", "Sets your username for registration"),
                new Pair<>("/password PASSWORD, /p PASSWORD", "Sets your password for registration")
        ));
    }

    @Override
    public void draw() {
        clearScreen();

        System.out.println("=============================================");
        System.out.println("=                 REGISTER                  =");
        System.out.println("=              use /help or /h              =");
        System.out.println("=============================================");

        drawFeedback();
    }

    @Override
    public Screen handleInput(Scanner scanner) {

        do {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (!input.startsWith("/"))
            {
                setFeedbackMessage("You can't send messages here.", false);
                return this;
            }

            Command command = CommandParser.parse(input);

            switch (command.getName()) {
                case "/h":
                case "/help":
                    super.clearScreen();
                    showCommands();
                    System.out.println("\nPress Enter to reactivate command line...");
                    scanner.nextLine();
                    return this;

                case "/b":
                case "/back":
                    System.out.println("\nPress Enter to go back to the Welcome menu...");
                    scanner.nextLine();
                    return new WelcomeScreen(clientApp);

                case "/u":
                case "/username":
                    if (command.getArgs().isEmpty()) {
                        setFeedbackMessage("Usage: " + command.getName() + " USERNAME", false);
                        return this;
                    }

                    username = command.getArgs().getFirst().trim();
                    System.out.println("Registering as: " + username);
                    break;

                case "/p":
                case "/password":
                    if (command.getArgs().isEmpty()) {
                        setFeedbackMessage("Usage: " + command.getName() + " PASSWORD", false);
                        return this;
                    }

                    password = command.getArgs().getFirst().trim();
                    break;

                default:
                    setFeedbackMessage("Unknown command: " + command.getName(), false);
                    return this;

            }

        } while (username.isEmpty() || password.isEmpty());

//        used to be useful, but now with the command based approach it is not
//        if (System.console() != null) {
//            password = Arrays.toString(System.console().readPassword());
//        } else {
//            password = scanner.nextLine().trim();
//        }

        ChatClientService chatClientService = new ChatClientService(clientApp.getConnection());
        boolean hasRegistered = chatClientService.register(username, password);

        if (hasRegistered) {
            System.out.println("\nRegistration successful! Welcome to the plaza, " + username + ".");
            System.out.println("Press Enter to return to the Welcome Menu...");
            scanner.nextLine();

            return new WelcomeScreen(clientApp);
        } else {
            setFeedbackMessage("Problem occurred while registering", false);
            return this;
        }
    }

    private static Scanner getScanner(Scanner scanner) {
        return scanner;
    }
}
