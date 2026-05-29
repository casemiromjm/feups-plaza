package com.feupsplaza.chat.client.view.screens;

import com.feupsplaza.chat.client.ClientApp;
import com.feupsplaza.chat.client.network.ChatClientService;
import com.feupsplaza.chat.client.util.Command;
import com.feupsplaza.chat.client.util.CommandParser;
import com.feupsplaza.chat.client.view.Screen;
import com.feupsplaza.chat.shared.util.Pair;
import java.util.List;
import java.util.Scanner;

public class ChatRoomScreen extends Screen {
    private String roomName;
    private ChatClientService chatClientService = new ChatClientService(clientApp.getConnection());    
    private boolean isAiRoom;

    public ChatRoomScreen(ClientApp clientApp, String roomName) {
        this(clientApp, roomName, false);
    }

    public ChatRoomScreen(ClientApp clientApp, String roomName, boolean isAiRoom) {
        super(clientApp);
        this.roomName = roomName;
        this.isAiRoom = isAiRoom;

        List<Pair<String, String>> commands = List.of(
            new Pair<>("/quit, /q", "Quit the Chat Room and goes to back to Available Rooms"),
            new Pair<>("/online, /o", "Lists all online chat participants"),
            new Pair<>("/room, /r", "Shows in which room you are")
        );
        addScreenCommands(commands);
        if (isAiRoom) {
            addScreenCommand("/ai <text>", "Ask the AI Ollama");
        }
    }

    @Override
    public void draw() {
        super.clearScreen();

        int innerWidth = 43;
        int leftPad = (innerWidth - roomName.length()) / 2;
        String centeredName = " ".repeat(leftPad) + roomName;

        System.out.println("=============================================");
        System.out.printf("=%-43s=%n", centeredName);
        System.out.println("=              use /help or /h              =");
        System.out.println("=============================================");
    
        for (String line : chatClientService.getHistory(roomName) ){
            System.out.println(line);
        }
    }

    @Override
    public Screen handleInput(Scanner scanner) {

        // useful inside the loop and avoid always reinstantiating the object
        ChatClientService chatClientService = new ChatClientService(clientApp.getConnection());
        boolean printPrompt = true;

        while (true) {

            if (clientApp.getConnection().getToken() == null) {
                // guest user having unauthorized access, need to kick out
                clientApp.setCurrentUser(null);

                return new WelcomeScreen(clientApp);
            }

            if (printPrompt) {
                System.out.print("> ");
            }
            printPrompt = true;
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            if (!input.startsWith("/")) {
                // a message!
                boolean res = chatClientService.sendMessage(roomName, input);

                if (!res) {
                    System.out.println("\033[0;31mYOUR MESSAGE WAS NOT SENT. TRY AGAIN.\033[0m");
                } else {
                    // a message was sent, so no need to print the prompt (the background listener / broadcast does that)
                    printPrompt = false;
                }

                continue;
            }

            // a command!
            Command command = CommandParser.parse(input);

            switch (command.getName()) {
                case "/h":
                case "/help":
                    showCommands();
                    System.out.println("\nPress Enter to reactivate command line...");
                    scanner.nextLine();
                    break;

                case "/q":
                case "/quit":
                    chatClientService.leaveRoom(roomName);

                    System.out.println("\nPress Enter to exit the room " + roomName + "...");
                    scanner.nextLine();

                    return new AvailableRoomsScreen(clientApp);

                case "/r":
                case "/room":
                    System.out.println("SERVER: You are in " + roomName);
                    continue;

                case "/o":
                case "/online":
                    List<String> users = chatClientService.listUsersInRoom(roomName);

                    if (users != null && !users.isEmpty()) {
                        System.out.println("--- Online in " + roomName + " ---");

                        for (var user : users) {
                            System.out.println("* " + user);
                        }

                        System.out.println("-------------------------");
                    } else {
                        System.out.println("\033[0;31mCould not fetch online users.\033[0m");
                    }

                    continue;

                case "/ai":
                    if (!isAiRoom) {
                        System.out.println("\033[0;31mThis is not an AI room.\033[0m");
                        continue;
                    }
                    if (command.getArgs().isEmpty()) {
                        System.out.println("\033[0;31mUsage: /ai <your message>\033[0m");
                        continue;
                    }
                    String aiMsg = String.join(" ", command.getArgs()).trim();
                    boolean aiRes = chatClientService.sendMessage(roomName, "/ai " + aiMsg);
                    if (!aiRes) {
                        System.out.println("\033[0;31mYOUR AI MESSAGE WAS NOT SENT. TRY AGAIN.\033[0m");
                    }
                    continue;

                default:
                    System.out.println("\033[0;31mUnknown command: " + command.getName() + "\033[0m");
                    break;
            }
        }
    }
}
