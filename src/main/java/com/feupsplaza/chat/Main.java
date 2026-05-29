package com.feupsplaza.chat;

import com.feupsplaza.chat.client.ClientApp;
import com.feupsplaza.chat.server.Server;
import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) {

        Options options = setupOptionsCLI();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("help") || args.length == 0) {
                formatter.printHelp("feupsplaza-chat", options);
                return;
            }

            if (line.hasOption("server") && line.hasOption("client")) {
                System.err.println("Error: Cannot run as both server and client.");
                formatter.printHelp("feupsplaza-chat", options);
                return;
            }

            int port = Integer.parseInt(line.getOptionValue("port", "8080"));
            String hostAddress = line.getOptionValue("address", "127.0.0.1");

            if (line.hasOption("server")) {
                // server option
                Server server = new Server(hostAddress, port);
                server.run();
            }
            else if (line.hasOption("client")) {
                // client option
                ClientApp client = new ClientApp(hostAddress, port);
                client.run();
            }
            else {
                System.err.println("Error: You must specify either -s (server) or -c (client).");
                formatter.printHelp("feupsplaza-chat", options);
            }

        } catch (ParseException e) {
            System.err.println("Parsing failed. Reason: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error: Port must be a valid number.");
        }
    }

    private static Options setupOptionsCLI() {
        Options options = new Options();

        // general flags
        options.addOption("h", "help", false, "print this message");
        options.addOption("s", "server", false, "runs the server side");
        options.addOption("c", "client",false, "runs a client");
        
        options.addOption("p", "port", true, "in which port the server is running. default: 8080");
        options.addOption("a", "address", true, "server IP address to (the client) connect to. default: 127.0.0.1");

        return options;
    }
}
