import de.thm.oop.chat.base.server.BasicTHMChatServer;

import java.io.IOException;
import java.util.Scanner;



public class Main {

    boolean running = true;

    static BasicTHMChatServer s = new BasicTHMChatServer();
    static CommandHandler ch = new CommandHandler(s);
    static Scanner in  = new Scanner(System.in);



    //main method
    public static void main(String[] args) throws IOException {

        System.out.println("Start chatting. Type in 'help' to see a list of commands.");

        while (true){ //to-do: create some exiting command or listener
            ch.execute(in.nextLine());
        }
    }

    public void setRunning(boolean b){ running=b; }

}