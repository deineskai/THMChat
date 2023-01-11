package de.thm.chat;

import de.thm.chat.cmd.CommandHandler;
import de.thm.oop.chat.base.server.BasicTHMChatServer;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static final BasicTHMChatServer s = new BasicTHMChatServer();
    private static final CommandHandler ch = new CommandHandler(s);
    private static final Scanner in = new Scanner(System.in);


    //main method
    public static void main(String[] args) throws IOException {
        System.out.println("Start chatting. Type in 'help' to see a list of commands.");
        while (ch.isRunning()) {
            ch.execute(in.nextLine());
        }
    }

}