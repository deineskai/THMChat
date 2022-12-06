import de.thm.oop.chat.base.server.BasicTHMChatServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class CommandHandler {

    BasicTHMChatServer s; //create BasicTHMChat s (will be initialized in the constructor)
    String user, pwd; //create credential vars
    ArrayList<Broadcast> broadcasts = new ArrayList<>(); //initialize an arraylist for broadcasts (empty)

    int lastMsg = 0; //initialize counter for message IDs with 0

    //constructor
    public CommandHandler(BasicTHMChatServer server){
        s = server; //initialize s
    }

    //method for executing commands
    void execute(String command) throws IOException {
        ArrayList<String> args = new ArrayList<>(); //initialize arraylist 'args' to save arguments (empty)
        String keyword; //create keyword var (will later be the actual command without arguments)

        //select keyword from command
        if (!command.contains(" ")){ //if there are no spaces, there are no arguments
            keyword = command;
            command = "";
        } else { //extract keyword from command with arguments
            keyword = command.substring(0,command.indexOf(" "));
            command = command.substring(command.indexOf(" ")+1);
        }

        //collect arguments
        while (!command.equals("")){
            if (!command.contains(" ")){ //if there's no space, there's just one argument left
                args.add(command);
                command = "";
            } else {
                if (args.size() == 1){ //if there already is one argument, there can only be one more as there
                    args.add(command); //is no command that requires more than two arguments. so the remaining
                    command = "";      //command is saved as the last argument
                } else {
                    args.add(command.substring(0, command.indexOf(" "))); //add first argument to ArrayList
                    command = command.substring(command.indexOf(" ") + 1); //shorten remaining command
                }
            }
        }

        //if the command requires credentials though at least one hasn't been set, print warning and return
        if((user == null || pwd == null || !credentialsOK()) && !(keyword.equals("user") || keyword.equals("pwd") || keyword.equals("help"))){
            printInformation(Information.CREDENTIALS_INCORRECT);
            return;
        }

        //check for missing arguments
        if ((keyword.equals("bc") || keyword.equals("bcdel") || keyword.equals("user") || keyword.equals("pwd"))&& args.size() < 1){
            printInformation(Information.ARGUMENTS_MISSING); //print warning
            return;
        }

        if ((keyword.equals("bcadd") || keyword.equals("bcrem") || keyword.equals("msg") || keyword.equals("img"))&& args.size() < 2){
            printInformation(Information.ARGUMENTS_MISSING); //print warning
            return;
        }

        //if the command doesn't require credentials and/or the credentials are correct, continue
        //procedures for each keyword
        switch (keyword) {
            case "user" -> {
                user = args.get(0); //set variable 'user' to specified username
            }
            case "pwd" -> {
                pwd = args.get(0); //set variable 'pwd' to specified password
            }
            case "list" -> {
                String[] users; //create a string array for all usernames

                users = s.getUsers(user, pwd); //get an array of all usernames

                //print all usernames
                System.out.println("List of all users:");
                for (int i = 0; i < users.length; i++) { //iterate through all usernames
                    System.out.print(ANSIColors.BLUE.get() + users[i] + ANSIColors.RESET.get()); //print username to console
                    if (i != users.length - 1) { //every username except for the last one needs a comma
                        if (i % 5 == 4) {
                            //for every fifth username a comma with line break is printed, so that there won't be more
                            //than five usernames in each line
                            System.out.println(", ");
                        } else {
                            //for all others there won't be a line break
                            System.out.print(", ");
                        }
                    }
                }
                System.out.println(""); //an empty line for separation
            }
            case "bc" -> {
                /*
                * if neither a broadcast nor a user shares the specified name, create a broadcast and print information.
                * otherwise print warnings
                * */
                if (getBroadcast(args.get(0)) == null) {
                    if (getUser(args.get(0)) == null) {
                        broadcasts.add(new Broadcast(args.get(0)));
                        printInformation(Information.BROADCAST_CREATED, args.get(0));
                    } else { printInformation(Information.ILLEGAL_NAME); }
                } else { printInformation(Information.EXISTS, args.get(0)); }
            }
            case "bcadd" -> {
                Broadcast bc = getBroadcast(args.get(0)); //select broadcast by name from broadcast arraylist (can be null)

                if (getUser(args.get(1)) != null && bc != null) { //if specified user and broadcast exist
                    bc.add(new User(args.get(1))); //add user to broadcast and
                    printInformation(Information.ADDED, args.get(1), args.get(0)); //print info
                } else {
                    printInformation(Information.NOT_FOUND); //otherwise print warning
                }
            }
            case "bcrem" -> {
                Broadcast bc = getBroadcast(args.get(0)); //select broadcast by name from broadcast arraylist (can be null)

                if (bc != null && bc.getUsers().stream().anyMatch(u -> u.getName().equals(args.get(1)))) { //if specified user and broadcast exist
                    bc.rem(args.get(1)); //remove user from broadcast and
                    printInformation(Information.REMOVED, args.get(1), args.get(0)); //print info
                }
                else if (bc == null) { printInformation(Information.BROADCAST_NOT_FOUND, args.get(0)); } //otherwise print warning
                else { printInformation(Information.NOT_A_MEMBER, args.get(1), args.get(0)); }
            }
            case "bcdel" -> {
                Broadcast bc = getBroadcast(args.get(0));
                if (bc == null){
                    printInformation(Information.NOT_FOUND, args.get(0));

                } else {
                    broadcasts.removeIf(broadcast -> broadcast.getName().equals(args.get(0)));
                    printInformation(Information.DELETED, args.get(0));
                }
            } //add procedure and add to help page
            case "msg" -> {
                if (lastMsg == 0) { execute("le"); } //if no messages have been displayed yet, print the most recent 100 messages

                Broadcast bc = getBroadcast(args.get(0));
                String u = getUser(args.get(0));
                TextMsg msg;

                if (bc != null) {
                    msg = new TextMsg(s, bc, user, pwd, args.get(1));
                    msg.send();
                } else if (u != null) {
                    msg = new TextMsg(s, new User(u), user, pwd, args.get(1));
                    msg.send();
                } else {
                    printInformation(Information.NOT_FOUND, args.get(0));
                }
                execute("refresh");
            }
            case "img" -> {
                if (lastMsg == 0) { execute("le"); }

                Broadcast bc = getBroadcast(args.get(0));
                String u = getUser(args.get(0));
                ImageMsg img;

                if (bc != null) {
                    img = new ImageMsg(s, bc, user, pwd, args.get(1));
                    img.send();
                } else if (u != null) {
                    img = new ImageMsg(s, new User(u), user, pwd, args.get(1));
                    img.send();
                } else {
                    printInformation(Information.NOT_FOUND, args.get(0));
                }
                execute("refresh");
            }
            case "le" -> {
                String[] messages = s.getMostRecentMessages(user, pwd);
                for (String message : messages) {
                    printMsg(message);
                }
                lastMsg = Integer.parseInt(messages[messages.length - 1].substring(0, 4));

            }
            case "refresh" -> {
                if (lastMsg == 0) { execute("le"); }

                String[] messages = s.getMessages(user, pwd, lastMsg);
                for (String message : messages) {
                    printMsg(message);
                    lastMsg++;
                }

            }
            case "exit" -> {
                //set running false
                //set running false
            }
            case "help" -> {
                System.out.println(ANSIColors.YELLOW.get());
                System.out.println("user [username]: set username");
                System.out.println("pwd [password]: set password");
                System.out.println("list: shows a list of all users in this chat");
                //System.out.println("le: shows the 100 most recent messages");
                System.out.println("bc [name]: creates a broadcast");
                System.out.println("bcadd [name] [username]: adds user to broadcast");
                System.out.println("bcrem [name] [username]: removes user from broadcast");
                System.out.println("bcdel [broadcast]: deletes broadcast");
                System.out.println("msg [username/broadcast] [message]: sends text message to user or broadcast");
                System.out.println("img [username/broadcast] [path_to_file]: sends an image to user or broadcast");
                System.out.println("refresh: shows new messages");
                System.out.println("exit: exits the chat");
                System.out.println(ANSIColors.RESET.get());
            }
            default -> {
                printInformation(Information.UNKNOWN);
            }
        }
    }
    //end of execute

    //method to quickly select broadcast by its name attribute
    Broadcast getBroadcast(String name){
        for (Broadcast broadcast:broadcasts) {
            if (broadcast.getName().equals(name)){
                return broadcast;
            }
        }
        return null;
    }

    //check if there is a user with given name
    String getUser(String name) throws IOException {
        String[] users = s.getUsers(user, pwd);
        for (String user:users) {
            if (user.equals(name)){
                return user;
            }
        }
        return null;
    }

    private boolean credentialsOK(){
        try {
            s.getUsers(user, pwd);
            return true;
        } catch (IOException e){
            if (e.getMessage().equals("turing.iem.thm.de")){
                printInformation(Information.CONNECTION_FAILED);
            } else {
                printInformation(Information.CREDENTIALS_INCORRECT);
            }
            return false;
        }
    }

    private void printMsg(String m){
        m = m + "|";
        String[] msg = new String[6];
        for (int i = 0; i < 6; i++){
            msg[i] = m.substring(0, m.indexOf("|"));
            m = m.substring(m.indexOf("|")+1);

            if (i == 4 && msg[i].equals("img")) {
                m = m.substring(m.indexOf("|")+1);
                m = m.substring(m.indexOf("|")+1);
            }
        }

        System.out.println(msg[1] + " | " + ANSIColors.BLUE.get() +
                (Objects.equals(msg[2], "in") ? msg[3] + " -> you" : ("you -> " + msg[3]))
                + ANSIColors.RESET.get() + ": " + ANSIColors.YELLOW_BOLD.get() + msg[5] + ANSIColors.RESET.get());

    }

    //methods for printing warnings and information
    private void printInformation(Information info_code){
        switch (info_code){
            case USER_INVALID -> { System.out.println(ANSIColors.RED.get() + "Username invalid" + ANSIColors.RESET.get()); }
            case PWD_INVALID -> { System.out.println(ANSIColors.RED.get() + "Password invalid" + ANSIColors.RESET.get()); }
            case CREDENTIALS_INCORRECT -> { System.out.println(ANSIColors.RED.get() + "Username or password wrong" + ANSIColors.RESET.get()); }
            case ARGUMENTS_MISSING -> { System.out.println(ANSIColors.RED.get() + "Argument(s) missing" + ANSIColors.RESET.get()); }
            case ILLEGAL_NAME -> { System.out.println(ANSIColors.RED.get() + "Illegal name" + ANSIColors.RESET.get()); }
            case CONNECTION_FAILED -> { System.out.println(ANSIColors.RED.get() + "Conncetion failed" + ANSIColors.RESET.get()); }
            case UNKNOWN -> { System.out.println(ANSIColors.RED.get() + "Unknown command" + ANSIColors.RESET.get()) ;}
        }
    }
    private void printInformation(Information info_code, String user_or_broadcast){
        switch (info_code){
            case EXISTS -> { System.out.println("Broadcast '" + ANSIColors.RED.get() + user_or_broadcast + ANSIColors.RESET.get() + "' already exists."); }
            case USER_NOT_FOUND -> { System.out.println("User '" + ANSIColors.RED.get() + user_or_broadcast + ANSIColors.RESET.get() + "' could not be found."); }
            case BROADCAST_NOT_FOUND -> { System.out.println("Broadcast '" + ANSIColors.RED.get() + user_or_broadcast + ANSIColors.RESET.get() + "' could not be found."); }
            case NOT_FOUND -> { System.out.println("User or broadcast '" + ANSIColors.RED.get() + user_or_broadcast + ANSIColors.RESET.get() + "' could not be found."); }
            case BROADCAST_CREATED -> { System.out.println("Broadcast '" + ANSIColors.BLUE.get() + user_or_broadcast + ANSIColors.RESET.get() + "' has been created."); }
            case DELETED -> { System.out.println("Broadcast '" + ANSIColors.BLUE.get() + user_or_broadcast + ANSIColors.RESET.get() + "' has been deleted.");}
        }
    }
    private void printInformation(Information info_code, String username, String broadcast){
        switch (info_code){
            case ADDED -> { System.out.println("User '" + ANSIColors.BLUE.get() + username + ANSIColors.RESET.get() + "' has been added to '" + ANSIColors.BLUE.get() + broadcast + ANSIColors.RESET.get() + "'."); }
            case REMOVED -> { System.out.println("User '" + ANSIColors.BLUE.get() + username + ANSIColors.RESET.get() + "' has been removed from '" + ANSIColors.BLUE.get() + broadcast + ANSIColors.RESET.get() + "'."); }
            case NOT_A_MEMBER -> { System.out.println("User '" + ANSIColors.RED.get() + username + ANSIColors.RESET.get() + "' is not a member of '" +
                    ANSIColors.BLUE.get() + broadcast + ANSIColors.RESET.get() + "'."); }
        }
    }

}
