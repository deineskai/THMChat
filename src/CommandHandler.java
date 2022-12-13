import de.thm.oop.chat.base.server.BasicTHMChatServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class CommandHandler {

    private final BasicTHMChatServer s; //create BasicTHMChat s (will be initialized in the constructor)
    private String user, pwd; //create credential vars
    private final ArrayList<Broadcast> broadcasts = new ArrayList<>(); //initialize an arraylist for broadcasts (empty)
    int lastMsg = 0; //initialize counter for message IDs with 0

    private static boolean running = true;


    //constructor
    public CommandHandler(BasicTHMChatServer server){
        s = server; //initialize s
    }

    //method for executing commands
     public void execute(String command) throws IOException { //but practically it doesn't because we check the credentials at line 52
        if (command.isEmpty() || command.isBlank()){ return; } //return if line is empty or blank

        ArrayList<String> args = new ArrayList<>(); //initialize arraylist 'args' to save arguments (empty)
        String keyword; //create keyword var (will later be the actual command without arguments)

        //gather keyword from command
        command += " "; //add space to avoid errors
        keyword = command.substring(0, command.indexOf(" ")); //save keyword
        command = command.substring(command.indexOf(" ")+1); //shorten leftover command

        //gather arguments //adds each argument (divided by single spaces) to string array 'args'
        while (!command.isEmpty()){
            if (command.charAt(0) == '\"'){
                try {
                    command = command.substring(1);
                    args.add(command.substring(0, command.indexOf("\"")));
                    command = command.substring(command.indexOf("\"") + 1);
                } catch (Exception e) {
                    System.out.println(ANSIColors.RED.get() + "Syntax error" + ANSIColors.RESET.get());
                    return;
                }
            } else {
                args.add(command.substring(0,command.indexOf(" ")));
                command = command.substring(command.indexOf(" ")+1);
            }
        }

        //if the command requires credentials and at least one hasn't been set, print warning and return
        if(!(keyword.equals("user") || keyword.equals("pwd") || keyword.equals("help")) && (user == null || pwd == null || !credentialsOK())){
            InfoCodes.CREDENTIALS_INCORRECT.print();
            return;
        }

        //check for missing arguments
        if (((keyword.equals("bc") || keyword.equals("bcdel") || keyword.equals("user") || keyword.equals("pwd"))&& args.size() < 1) ||
                ((keyword.equals("bcadd") || keyword.equals("bcrem") || keyword.equals("msg") || keyword.equals("img"))&& args.size() < 2)){
            InfoCodes.ARGUMENTS_MISSING.print(); //print warning
            return;
        }

        //procedures for each keyword //where the  magic  happens
        switch (keyword) {
            case "user" -> user = args.get(0); //set variable 'user' to specified username
            case "pwd" -> pwd = args.get(0); //set variable 'pwd' to specified password
            case "list" -> {
                String[] users = s.getUsers(user, pwd); //get an array of all usernames
                //print all usernames
                System.out.println("All users in this chat:");
                for (int i = 0; i < users.length; i++) { //iterate through all username array
                    System.out.print(ANSIColors.BLUE.get() + users[i] + ANSIColors.RESET.get()); //print username a current position to console
                    if (i != users.length - 1) { //every username except for the last one is followed by a comma
                        //every third comma will also have a line  break so that there will be three usernames per line
                        if (i % 3 == 2) { System.out.println(", "); }
                        else { System.out.print(", "); }
                    }
                }
                System.out.println(); //an empty line for separation
            }
            case "bc" -> {
                /*
                * if neither a broadcast nor a user shares the specified name, create a broadcast and print information
                * otherwise print warnings
                * */


                if (!isBroadcast(args.get(0))) {
                    if (!isUser(args.get(0))) {
                        broadcasts.add(new Broadcast(args.get(0)));
                        InfoCodes.BROADCAST_CREATED.print(args.get(0));
                    } else { InfoCodes.ILLEGAL_NAME.print(args.get(0)); }
                } else { InfoCodes.EXISTS.print(args.get(0)); }
            }
            case "bcadd" -> {
                Broadcast bc = getBroadcast(args.get(0)); //select broadcast by specified name from broadcast arraylist (null if it doesn't exist)

                if (isUser(args.get(1)) && isBroadcast(args.get(0)) && bc.getUsers().stream().noneMatch(
                        u -> u.getName().equals(args.get(1)))) { //if specified user and broadcast exist and user is not member of broadcast
                    bc.add(new User(args.get(1))); //add user to broadcast and
                    InfoCodes.ADDED.print(args.get(1), args.get(0)); //print info
                } else { //otherwise print warning
                    if (!isUser(args.get(1))) { InfoCodes.USER_NOT_FOUND.print(args.get(1)); }
                    else if(!isBroadcast(args.get(0))) { InfoCodes.BROADCAST_NOT_FOUND.print(args.get(0)); }
                    else { InfoCodes.ALREADY_MEMBER.print(args.get(1), args.get(0)); }
                }
            }
            case "bcrem" -> {
                Broadcast bc = getBroadcast(args.get(0)); //select broadcast by specified name from broadcast arraylist (null it doesn't exist)
                if (bc != null && bc.getUsers().stream().anyMatch(u -> u.getName().equals(args.get(1)))) { //if specified user and broadcast exist
                    bc.rem(args.get(1)); //remove user from broadcast and
                    InfoCodes.REMOVED.print(args.get(1), args.get(0)); //print info
                }
                //otherwise print warning
                else if (bc == null) { InfoCodes.BROADCAST_NOT_FOUND.print(args.get(0)); }
                else { InfoCodes.NOT_A_MEMBER.print(args.get(1), args.get(0)); }
            }
            case "bcdel" -> {
                Broadcast bc = getBroadcast(args.get(0)); //select broadcast by specified name from broadcast arraylist (null if it doesn't exist)
                if (bc == null){ //if broadcast doesn't exist
                    InfoCodes.NOT_FOUND.print(args.get(0)); //print warning
                } else { //if it exists
                    broadcasts.removeIf(broadcast -> broadcast.getName().equals(args.get(0))); //delete it and
                    InfoCodes.DELETED.print(args.get(0)); //print info
                }
            }
            case "msg" -> {
                Broadcast bc = getBroadcast(args.get(0)); //select broadcast by specified receiver name from broadcast arraylist (null if it doesn't exist)
                String u = getUser(args.get(0)); //select user by specified receiver name (null if it doesn't exist)
                /*
                * if neither a broadcast nor a user shares the specified name of the receiver, print warning
                * otherwise send message and refresh, so it gets displayed (automatically displays 100 most recent)
                * */
                if (bc==null && u==null) { InfoCodes.NOT_FOUND.print(args.get(0)); }
                else if (bc!=null && bc.getUsers().size()==0){
                    InfoCodes.EMPTY.print(args.get(0));
                } else {
                    TextMsg msg = new TextMsg(s, bc==null ? new User(u) : bc, user, pwd, args.get(1));
                    msg.send();
                    execute("refresh");
                }
            }
            case "img" -> {
                Broadcast bc = getBroadcast(args.get(0)); //select broadcast by specified receiver name from broadcast arraylist (null if it doesn't exist)
                String u = getUser(args.get(0)); //select user by specified receiver name (null if it doesn't exist)
                /*
                 * if neither a broadcast nor a user shares the specified name of the receiver, print warning
                 * otherwise send message and refresh, so it gets displayed (automatically displays 100 most recent)
                 * */
                if (bc==null  && u==null) { InfoCodes.NOT_FOUND.print(args.get(0)); }
                else if (bc!=null && bc.getUsers().size()==0) {
                    InfoCodes.EMPTY.print(args.get(0));
                    break;
                } else {
                    ImageMsg msg = new ImageMsg(s, bc==null ? new User(u) : bc, user, pwd, args.get(1));
                    msg.send();
                }
                execute("refresh");
            }
            case "le" -> {
                //either get 100 most recent or all messages depending on argument and save in string array 'messages'
                String[] messages = (args.size() != 0 && args.get(0).equals("100")) ? s.getMostRecentMessages(user, pwd) : s.getMessages(user, pwd, 0);
                //iterate through 'messages' and print each
                for (String message : messages) {
                    printMsg(message);
                }
                //update message counter
                lastMsg = Integer.parseInt(messages[messages.length-1].substring(0, 4));
            }
            case "refresh" -> {
                //if no messages are displayed yet, show the 100 most recent
                if (lastMsg == 0) { execute("le 100"); }
                //get all new and save in string array 'messages'
                String[] messages = s.getMessages(user, pwd, lastMsg);
                //iterate through 'messages', print each and update message counter
                for (String message : messages) {
                    printMsg(message);
                    lastMsg++;
                }
            }
            case "exit" -> running = false;
            case "help" -> {
                //TO-DO: add le again
                System.out.println(ANSIColors.YELLOW.get());
                System.out.println("Command usage and meaning:");
                System.out.println("user [username]: set username");
                System.out.println("pwd [password]: set password");
                System.out.println("list: shows a list of all users in this chat");
                System.out.println("le (100): shows all messages (optional: the 100 most recent)");
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
            default -> InfoCodes.UNKNOWN_COMMAND.print(); //print warning if command is unknown
        }
    }
    //end of execute

    public static boolean isRunning(){
        return running;
    }

    //return broadcast that has the specified name
    public Broadcast getBroadcast(String name){
        for (Broadcast broadcast:broadcasts) {
            if (broadcast.getName().equals(name)){
                return broadcast;
            }
        }
        return null;
    }

    //return specified username if that user exists
    public String getUser(String name)  {
        try {
            String[] users = s.getUsers(user, pwd);
            for (String user : users) {
                if (user.equals(name)) {
                    return user;
                }
            }
        } catch (Exception e){
            credentialsOK();
            if (credentialsOK()) {
                InfoCodes.UNKNOWN_ERROR.print();
            }
        }
        return null;
    }

    //print formatted message to console
    private void printMsg(String m){
        m += "|";
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
                (Objects.equals(msg[2], "in") ? msg[3] + " -> you" : "you -> " + msg[3])
                + ANSIColors.RESET.get() + ": " + ANSIColors.YELLOW_BOLD.get() + msg[5] + ANSIColors.RESET.get());
    }

    //check if there's a broadcast that has the specified name
    private boolean isBroadcast(String name)  {
        return broadcasts.stream().anyMatch(broadcast -> broadcast.getName().equals(name));
    }

    //check if there's a user that has the specified name
    private boolean isUser(String name)  {
        return getUser(name) != null;
    }

    //check credentials
    private boolean credentialsOK(){
        try {
            s.getUsers(user, pwd);
            return true;
        } catch (IOException e){
            if (e.getMessage().equals("turing.iem.thm.de")){
                InfoCodes.CONNECTION_FAILED.print();
            } else {
                InfoCodes.CREDENTIALS_INCORRECT.print();
            }
            return false;
        }
    }

}
