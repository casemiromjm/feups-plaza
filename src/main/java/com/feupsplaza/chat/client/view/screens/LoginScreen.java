package com.feupsplaza.chat.client.view.screens;

import com.feupsplaza.chat.client.ClientApp;
import com.feupsplaza.chat.client.util.Command;
import com.feupsplaza.chat.client.util.CommandParser;
import com.feupsplaza.chat.shared.util.Pair;
import com.feupsplaza.chat.shared.model.User;
import com.feupsplaza.chat.client.network.ChatClientService;
import com.feupsplaza.chat.client.view.Screen;

import java.util.List;
import java.util.Scanner;

public class LoginScreen extends Screen {
    private String username = "";
    private String password = "";

    public LoginScreen(ClientApp clientApp) {
        super(clientApp);

        addScreenCommands(List.of(
                new Pair<>("/back, /b", "Goes to back to Welcome Menu"),
                new Pair<>("/username USERNAME, /u USERNAME", "Sets your username for registration"),
                new Pair<>("/password PASSWORD, /p PASSWORD", "Sets your password for registration")
        ));
    }

    @Override
    public void draw() {
        super.clearScreen();

        System.out.println("=============================================");
        System.out.println("=                   LOGIN                   =");
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
                    System.out.println("Logging in as: " + username);
                    break;

                case "/p":
                case "/password":
                    if (command.getArgs().isEmpty()) {
                        setFeedbackMessage("Usage: " + command.getName() + " USERNAME", false);
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
        User loggedUser = chatClientService.login(username, password);

        if (loggedUser != null) {

            this.clientApp.setCurrentUser(loggedUser);
            
            this.clientApp.getConnection().setToken(loggedUser.getToken());

            System.out.println("\nLogin successful! Welcome back " + clientApp.getCurrentUser().getUsername() + "!");
            System.out.println("Press Enter to go to see the available chat rooms...");
            scanner.nextLine();

            return new AvailableRoomsScreen(this.clientApp);
        } else {
            setFeedbackMessage("Invalid username/password", false);
            
            this.username = "";
            this.password = "";

            return this;
        }
    }
}