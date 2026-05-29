package com.feupsplaza.chat.client.view.screens;

import com.feupsplaza.chat.client.ClientApp;
import com.feupsplaza.chat.client.network.ChatClientService;
import com.feupsplaza.chat.client.util.Command;
import com.feupsplaza.chat.client.util.CommandParser;
import com.feupsplaza.chat.shared.util.Pair;
import com.feupsplaza.chat.client.view.Screen;

import java.util.List;
import java.util.Scanner;

public class AvailableRoomsScreen extends Screen {

    public AvailableRoomsScreen(ClientApp clientApp) {
        super(clientApp);
        addScreenCommands(List.of(
                new Pair<>("/logout, /l", "Logout back to Welcome Menu"),
                new Pair<>("/chat CHAT_NAME, /c CHAT_NAME", "Enter the name of the chat room you want to enter or create"),
                new Pair<>("/ai_chat CHAT_AI_NAME PROMPT, /ai CHAT_AI_NAME PROMPT", "Enter the name of the AI chat room you want to enter or create")
        ));
    }

    @Override
    public void draw() {
        super.clearScreen();

        System.out.println("=============================================");
        System.out.println("=              AVAILABLE ROOMS              =");
        System.out.println("=              use /help or /h              =");
        System.out.println("=============================================");

        ChatClientService chatClientService = new ChatClientService(this.clientApp.getConnection());
        List<String> availableRooms = chatClientService.listRooms();

        if (availableRooms == null) {
            System.out.println("ERROR | Could not fetch rooms from server");
        } else if (availableRooms.isEmpty()) {
            System.out.println("No active rooms yet. Be the first to create one!");
        } else {
            for (String availableRoom : availableRooms) {
                System.out.println("# " + availableRoom);
            }
        }

        // System.out.println("Please select a room or type /chat [NAME], or /c [NAME], or /ai_chat [CHAT_AI_NAME], or /ai [CHAT_AI_NAME] to create a new one");

        super.drawFeedback();
    }

    @Override
    public Screen handleInput(Scanner scanner) {
        String roomName = "";
        System.out.print("> ");
        String input = scanner.nextLine().trim();

        if (!input.startsWith("/")) {
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

                case "/l":
                case "/logout":
                    System.out.println("\nPress Enter to go back to the Welcome menu...");
                    scanner.nextLine();
                    return new WelcomeScreen(clientApp);
                case "/c":
                case "/chat":
                    if (command.getArgs().isEmpty()) {
                        setFeedbackMessage("Usage: " + command.getName() + " CHAT_NAME", false);
                        return this;
                    }

                    roomName = command.getArgs().getFirst().trim();
                    ChatClientService chatClientService = new ChatClientService(this.clientApp.getConnection());
                    boolean res = chatClientService.joinRoom(roomName);
                    if (res) {
                        System.out.println("Successfully joined: " + roomName);
                        System.out.println("Press Enter to go to the chat...");
                        scanner.nextLine();
                        return new ChatRoomScreen(clientApp, roomName, false);
                    } else {
                        setFeedbackMessage("Failed to join room: " + roomName, false);
                        return this;
                    }
                case "/ai":
                case "/ai_chat":
                    if (command.getArgs().size() < 2) {
                        setFeedbackMessage("Usage: " + command.getName() + " AI_CHAT_NAME PROMPT", false);
                        return this;
                    }

                    roomName = command.getArgs().getFirst().trim();
                    ChatClientService service = new ChatClientService(this.clientApp.getConnection());
                    String prompt = command.combineArgs(1);
                    boolean created = service.createAiRoom(roomName, prompt);
                    if (!created) {
                        System.out.println("AI room may already exist. Trying to join it...");
                    }
                    boolean joined = service.joinRoom(roomName);
                    if (joined) {
                        System.out.println("Successfully joined: " + roomName);
                        System.out.println("Press Enter to go to the AI chat...");
                        scanner.nextLine();
                        return new ChatRoomScreen(clientApp, roomName, true);
                    } else {
                        setFeedbackMessage("Failed to join AI room: " + roomName, false);
                        return this;
                    } 
                default:
                    setFeedbackMessage("Unknown command: " + command.getName(), false);
                    return this;
            }
    }
}
