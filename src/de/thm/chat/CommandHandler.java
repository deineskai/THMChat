package de.thm.chat;

import de.thm.chat.msg.ImageMsg;
import de.thm.chat.msg.TextMsg;
import de.thm.chat.receiver.Broadcast;
import de.thm.chat.util.ANSIColors;
import de.thm.chat.util.InfoCodes;
import de.thm.oop.chat.base.server.BasicTHMChatServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class CommandHandler {

    private final BasicTHMChatServer s; //create BasicTHMChat s (will be initialized in the constructor)
    private static String user, pwd; //create credential vars
    private static ArrayList<Broadcast> broadcasts = new ArrayList<>(); //initialize an arraylist for broadcasts (empty)
    private static int lastMsg = 0; //initialize counter for message IDs with 0

    private static boolean running = true;


    //constructor
    public CommandHandler(BasicTHMChatServer server){
        s = server; //initialize s
    }

    //method for executing commands
    /** TO-DO: split up*/
    public void execute(String command) throws IOException { //but practically it doesn't because we check the credentials at line 52
         if (command.isEmpty() || command.isBlank()){ return; } //return if line is empty or blank
         ArrayList<String> args = new ArrayList<>(); //initialize arraylist 'args' to save arguments (empty)
         String keyword; //create keyword var (will later be the actual command without arguments)

         //gather keyword from command
         command += " ";
         keyword = command.substring(0, command.indexOf(" ")); //save keyword
         command = command.substring(command.indexOf(" ")+1); //shorten leftover command

         //gather arguments //adds each argument (divided by single spaces) to string array 'args'
         while (!command.isEmpty()){
             if (command.charAt(0) == '\"'){
                 try {
                     command = command.substring(1);
                     args.add(command.substring(0, command.indexOf("\"")));
                     command = command.substring(command.indexOf("\"") + 1);
                 }
                 catch (Exception e) {
                     System.out.println(ANSIColors.RED.get() + "Syntax error" + ANSIColors.RESET.get());
                     return;
                 }
             }
             else {
                 args.add(command.substring(0,command.indexOf(" ")));
                 command = command.substring(command.indexOf(" ")+1);
             }
         }

         //if the command requires credentials and at least one hasn't been set, print warning and return
         if(!(keyword.equals("user") || keyword.equals("pwd") || keyword.equals("help") || keyword.equals("exit")) && (user == null || pwd == null || !credentialsOK())){
             InfoCodes.CREDENTIALS_INCORRECT.print();
             return;
         }

         //check for missing arguments
         if (((keyword.equals("bc") || keyword.equals("bcdel") || keyword.equals("user") || keyword.equals("pwd"))&& args.size() < 1) ||
                 ((keyword.equals("bcadd") || keyword.equals("bcrem") || keyword.equals("msg") || keyword.equals("img"))&& args.size() < 2)){
             InfoCodes.ARGUMENTS_MISSING.print(); //print warning
             return;
         }

        switch (keyword) {
            case "user" -> setUser(args.get(0)); //set variable 'user' to specified username
            case "pwd" -> setPwd(args.get(0)); //set variable 'pwd' to specified password
            case "list" -> listUsers();
            case "bc" -> createBroadcast(args.get(0));
            case "bcadd" -> addToBroadcast(args.get(1), args.get(0));
            case "bcrem" -> removeFromBroadcast(args.get(1), args.get(0));
            case "bcdel" -> deleteBroadcast(args.get(0));
            case "msg" -> sendText(args.get(1), args.get(0));
            case "img" -> sendImage(args.get(1), args.get(0));
            case "le" -> listEveryting(args);
            case "refresh" -> refresh();
            case "exit" -> exit();
            case "help" -> help();
            default -> unknown(); //print warning if command is unknown
        }
    }

    /**
    * start of command methods
    * */

    private void setUser(String name){
        user = name;
    }

    private void setPwd(String password){
        pwd = password;
    }

    private void listUsers() throws IOException {
        String[] users = s.getUsers(user, pwd); //get an array of all usernames
        //print all usernames
        System.out.println("All users in this chat:");
        for (int i = 0; i < users.length; i++) { //iterate through username array
            System.out.print(ANSIColors.BLUE.get() + users[i] + ANSIColors.RESET.get()); //print username a current position to console
            if (i != users.length - 1) { //every username except for the last one is followed by a comma
                //every third comma will also have a line  break so that there will be three usernames per line
                if (i % 3 == 2) { System.out.println(", "); }
                else { System.out.print(", "); }
            }
        }
        System.out.println(); //an empty line for separation
    }

    private void createBroadcast(String name){
        if (!isBroadcast(name)) {
            if (!isUser(name)) {
                broadcasts.add(new Broadcast(name));
                InfoCodes.BROADCAST_CREATED.print(name);
            }
            else { InfoCodes.ILLEGAL_NAME.print(name); }
        }
        else { InfoCodes.EXISTS.print(name); }
    }

    private void addToBroadcast(String user, String name){
        Broadcast bc = getBroadcast(name); //select broadcast by specified name from broadcast arraylist (null if it doesn't exist)

        if (isUser(user) && isBroadcast(name) && bc.getUsers().stream().noneMatch(
                u -> u.equals(user))) { //if specified user and broadcast exist and user is not member of broadcast
            bc.add(user); //add user to broadcast and
            InfoCodes.ADDED.print(user, name); //print info
        }
        else { //otherwise print warning
            if (!isUser(user)) { InfoCodes.USER_NOT_FOUND.print(user); }
            else if(!isBroadcast(name)) { InfoCodes.BROADCAST_NOT_FOUND.print(name); }
            else { InfoCodes.ALREADY_MEMBER.print(user, name); }
        }
    }

    private void removeFromBroadcast(String user, String name){
        Broadcast bc = getBroadcast(name); //select broadcast by specified name from broadcast arraylist (null it doesn't exist)
        if (bc != null && bc.getUsers().stream().anyMatch(u -> u.equals(user))) { //if specified user and broadcast exist
            bc.rem(user); //remove user from broadcast and
            InfoCodes.REMOVED.print(user, name); //print info
        }
        //otherwise print warning
        else if (bc == null) { InfoCodes.BROADCAST_NOT_FOUND.print(name); }
        else { InfoCodes.NOT_A_MEMBER.print(user, name); }
    }

    private void deleteBroadcast(String name){
        Broadcast bc = getBroadcast(name); //select broadcast by specified name from broadcast arraylist (null if it doesn't exist)
        if (bc == null){ //if broadcast doesn't exist
            InfoCodes.NOT_FOUND.print(name); //print warning
        }
        else { //if it exists
            bc.clear();
            broadcasts.removeIf(broadcast -> broadcast == bc); //delete it and
            InfoCodes.DELETED.print(name); //print info
        }
    }

    private void sendText(String message, String receiver) throws IOException {
        TextMsg msg = new TextMsg(s, user, pwd, message);
        for (String u:Objects.requireNonNull(gatherUsers(receiver))) {
            msg.send(u);
        }
        refresh();
    }

    private void sendImage(String path, String receiver) throws IOException {
        ImageMsg msg = new ImageMsg(s, user, pwd, path);
        System.out.println("path: " + path);
        for (String u: Objects.requireNonNull(gatherUsers(receiver))) {
            msg.send(u);
        }
        refresh();
    }

    private void listEveryting(ArrayList<String> args) throws IOException {
        //either get 100 most recent or all messages depending on argument and save in string array 'messages'
        String[] messages = (args.size() != 0 && args.get(0).equals("100")) ? s.getMostRecentMessages(user, pwd) : s.getMessages(user, pwd, 0);
        //iterate through 'messages' and print each
        for (String message : messages) {
            printMsg(message);
        }
        //update message counter
        String lmg = messages[messages.length-1];
        lmg = lmg.substring(0, lmg.indexOf("|"));
        lastMsg = Integer.parseInt(lmg);
    }

    private void refresh() throws IOException {
        //if no messages are displayed yet, show the 100 most recent
        if (lastMsg == 0) { execute("le 100"); }
        //get all new and save in string array 'messages'
        else {
            String[] messages = s.getMessages(user, pwd, lastMsg);
            //iterate through 'messages', print each and update message counter
            for (String message : messages) {
                printMsg(message);
                lastMsg++;
            }
        }
    }

    private void exit(){
        running = false;
    }

    private void help(){
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

    private void unknown(){
        InfoCodes.UNKNOWN_COMMAND.print(); //print warning if command is unknown
    }

    /**
    * end of command methods
    * */

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
        System.out.println(msg[0] + " " + msg[1] + " | " + ANSIColors.BLUE.get() +
                (Objects.equals(msg[2], "in") ? msg[3] + " -> you" : "you -> " + msg[3])
                + ANSIColors.RESET.get() + ": " + ANSIColors.YELLOW_BOLD.get() + msg[5] + ANSIColors.RESET.get());
    }

    /**
    * start of getter methods
    * */
    private ArrayList<String> gatherUsers(String receiver){
        ArrayList<String> users = new ArrayList<>();
        if (isUser(receiver)){
            users.add(receiver);
        }
        else if (isBroadcast(receiver)) {
            users = getBroadcast(receiver).getUsers();
            if (users.isEmpty()) {
                InfoCodes.EMPTY.print(receiver);
                return null;
            }
        }
        else {
            InfoCodes.NOT_FOUND.print(receiver);
            return null;
        }
        return users;
    }

    //return broadcast with the specified name
    public Broadcast getBroadcast(String name){
        for (Broadcast broadcast:broadcasts) {
            if (broadcast.getName().equals(name)){
                return broadcast;
            }
        }
        return null;
    }
    /**
    * end of getter methods
    * */

    /**
    * start of checker methods
    * */

    //check if there's a broadcast that has the specified name
    private boolean isBroadcast(String name)  {
        return broadcasts.stream().anyMatch(broadcast -> broadcast.getName().equals(name));
    }

    //check if there's a user that has the specified name
    private boolean isUser(String name) {
        try {
            String[] users = s.getUsers(user, pwd);
            for (String user : users) {
                if (user.equals(name)) {
                    return true;
                }
            }
        }
        catch (Exception e){
            credentialsOK();
            if (credentialsOK()) {
                InfoCodes.UNKNOWN_ERROR.print();
            }
        }
        return false;
    }

    //check credentials
    private boolean credentialsOK(){
        try {
            s.getUsers(user, pwd);
            return true;
        }
        catch (IOException e){
            if (e.getMessage().equals("turing.iem.thm.de")){
                InfoCodes.CONNECTION_FAILED.print();
            } else {
                InfoCodes.CREDENTIALS_INCORRECT.print();
            }
            return false;
        }
    }

    //returns running
    public static boolean isRunning(){
        return running;
    }
    /**
    * end of checker methods
    * */
}
