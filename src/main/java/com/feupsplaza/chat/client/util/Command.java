package com.feupsplaza.chat.client.util;

import java.util.ArrayList;
import java.util.List;

/**
A Command is something the used in the client-side. By using it the user can navigate screens, set username/password, etc.
 */
public class Command {
    protected String name;
    protected List<String> args;

    public Command(String name)
    {
        this.name = name;
        this.args = new ArrayList<>();
    }

    public Command(String name, List<String> args)
    {
        this.name = name;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public List<String> getArgs() {
        return args;
    }

    public String toString() {
        return "command name: " + getName() + "\nargs: " + getArgs();
    }

    /**
     * Combines arguments from a starting index to the end into a single space-separated string.
     *
     * @param start The index of the first argument to include (inclusive).
     * @return The combined string, or an empty string if start is out of bounds.
     */
    public String combineArgs(int start) {
        return combineArgs(start, args.size());
    }

    /**
     * Combines arguments from a starting index to the end into a single space-separated string.
     *
     * @param start The index of the first argument to include (inclusive).
     * @param end The index of the argument to stop at (exclusive).
     * @return The combined string, or and empty string if any index is out of bound.
     */
    public String combineArgs(int start, int end) {
        if (start < 0 || start >= args.size() || start >= end || end > args.size()) {
            return "";
        }

        return String.join(" ", args.subList(start, end));
    }
}
