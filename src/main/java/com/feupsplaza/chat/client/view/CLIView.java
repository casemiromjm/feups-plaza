package com.feupsplaza.chat.client.view;

import com.feupsplaza.chat.client.ClientApp;
import com.feupsplaza.chat.client.view.screens.WelcomeScreen;

import java.util.Scanner;

public class CLIView {
    Screen currentScreen;
    Scanner scanner;

    public CLIView(Scanner scanner, ClientApp clientApp)
    {
        this.currentScreen = new WelcomeScreen(clientApp);
        this.scanner = scanner;
    }

    public void changeScreen(Screen other)
    {
        this.currentScreen = other;
    }

    /**
     * Update the view (e.g. which screen the user is seeing)
     * @return false if the app should terminate, true otherwise
     */
    public boolean update()
    {
        this.currentScreen.draw();

        var newScreen = this.currentScreen.handleInput(this.scanner);

        if (newScreen != null)
        {
            changeScreen(newScreen);
        }
        else
        {
            return false;
        }

        return true;
    }
}
