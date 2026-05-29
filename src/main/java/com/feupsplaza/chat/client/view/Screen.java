package com.feupsplaza.chat.client.view;

import com.feupsplaza.chat.client.ClientApp;
import com.feupsplaza.chat.shared.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public abstract class Screen {
    protected Map<String, String> availableCommands;
    protected String feedbackMessage = "";
    protected ClientApp clientApp;

    public Screen(ClientApp clientApp)
    {
        this.clientApp = clientApp;

        this.availableCommands = new TreeMap<>();
        addScreenCommands(List.of(
            new Pair<>("/help, /h", "List all available commands")
        ));
    }

    public abstract void draw();

    /**
     * Handle the input in a certain screen
     * @param scanner Input reader
     * @return The next screen to be displayed
     */
    public abstract Screen handleInput(Scanner scanner);

    protected void showCommands()
    {
        for (var cmd : this.availableCommands.keySet())
        {
            System.out.println("|> " + cmd + " :: " + this.availableCommands.get(cmd));
        }
    }

    protected void drawFeedback()
    {
        if (!this.feedbackMessage.isEmpty())
        {
            System.out.println(this.feedbackMessage);
            this.feedbackMessage = "";
        }
    }

    protected void setFeedbackMessage(String feedbackMessage, boolean isPositive)
    {
        this.feedbackMessage = (isPositive ? "\033[0;32m[:)] " : "\033[0;31m[:(] ") + feedbackMessage + "\033[0m";
    }

    public void clearScreen()
    {
        System.out.println("\033[2J");
    }

    protected void addScreenCommand(String cmd, String desc)
    {
        this.availableCommands.put(cmd, desc);
    }

    protected void addScreenCommands(List<Pair<String, String>> cmds)
    {
        for (var cmd : cmds)
        {
            addScreenCommand(cmd.getFirst(), cmd.getSecond());
        }
    }

}
