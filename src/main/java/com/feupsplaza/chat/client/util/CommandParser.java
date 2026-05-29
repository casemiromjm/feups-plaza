package com.feupsplaza.chat.client.util;

import java.util.ArrayList;
import java.util.List;

public class CommandParser {
    public static Command parse(String input)
    {
        if (input == null || input.trim().isEmpty())
        {
            return new Command("");
        }

        String[] tokens = input.split(" ",2);
        String cmd = tokens[0].toLowerCase();

        List<String> args = new ArrayList<>();
        if (tokens.length > 1)
        {
            for (var arg : tokens[1].split(" "))
            {
                args.add(arg);
            }
        }

        return new Command(cmd, args);
    }

}
