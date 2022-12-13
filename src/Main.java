import de.thm.oop.chat.base.server.BasicTHMChatServer;

import java.io.IOException;
import java.util.Scanner;



public class Main {

    static boolean running = true;

    static BasicTHMChatServer s = new BasicTHMChatServer();
    static CommandHandler ch = new CommandHandler(s);
    static Scanner in  = new Scanner(System.in);



    //main method
    public static void main(String[] args) throws IOException {

        System.out.println("Start chatting. Type in 'help' to see a list of commands."); //welcome message

        while (running) {
            ch.execute(in.nextLine()); //execute command from console input
            running = CommandHandler.isRunning(); //update 'running' boolean
        }
    }


}