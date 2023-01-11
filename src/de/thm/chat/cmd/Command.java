package de.thm.chat.cmd;

import de.thm.chat.msg.ImageMsg;
import de.thm.chat.msg.IncomingMsg;
import de.thm.chat.msg.MessageFactory;
import de.thm.chat.msg.TextMsg;
import de.thm.chat.receiver.Broadcast;
import de.thm.chat.util.ANSIColors;
import de.thm.chat.util.InfoCodes;
import de.thm.oop.chat.base.server.BasicTHMChatServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Command {


    private final BasicTHMChatServer s;
    private final MessageFactory mf = new MessageFactory();
    private final ArrayList<Broadcast> broadcasts = new ArrayList<>();

    private String user, pwd;
    private int lastMsgId;
    private boolean running = true;


    public Command(BasicTHMChatServer s) {
        this.s = s;
    }

    /* command methods */
    public void setUser(String name) {
        user = name;
    }

    public void setPwd(String password) {
        pwd = password;
    }

    public void listUsers() {
        String[] users = new String[0]; //get an array of all usernames
        try {
            users = s.getUsers(user, pwd);
        } catch (IOException e) {
            System.out.println(ANSIColors.RED.get() + e + ANSIColors.RESET.get());
        }
        //print all usernames
        System.out.println("All users in this chat:");
        for (int i = 0; i < users.length; i++) { //iterate through username array
            System.out.print(ANSIColors.BLUE.get() + users[i] + ANSIColors.RESET.get()); //print username to console
            if (i != users.length - 1) { //every username except for the last one is followed by a comma
                //every third comma will also have a line  break so that there will be three usernames per line
                if (i % 3 == 2) {
                    System.out.println(", ");
                } else {
                    System.out.print(", ");
                }
            }
        }
        System.out.println(); //an empty line for separation
    }

    public void createBroadcast(String name) {
        if (!isBroadcast(name)) {
            if (!isUser(name)) {
                getBroadcasts().add(new Broadcast(name));
                InfoCodes.BROADCAST_CREATED.print(name);
            } else {
                InfoCodes.ILLEGAL_NAME.print(name);
            }
        } else {
            InfoCodes.EXISTS.print(name);
        }
    }

    public void addToBroadcast(String user, String name) {
        Broadcast bc = getBroadcast(name); //select broadcast by specified name from broadcast arraylist (null if it doesn't exist)

        if (isUser(user) && isBroadcast(name) && Objects.requireNonNull(bc).getUsers().stream().noneMatch(
                u -> u.equals(user))) { //if specified user and broadcast exist and user is not member of broadcast
            bc.add(user); //add user to broadcast and
            InfoCodes.ADDED.print(user, name); //print info
        } else { //otherwise print warning
            if (!isUser(user)) {
                InfoCodes.USER_NOT_FOUND.print(user);
            } else if (!isBroadcast(name)) {
                InfoCodes.BROADCAST_NOT_FOUND.print(name);
            } else {
                InfoCodes.ALREADY_MEMBER.print(user, name);
            }
        }
    }

    public void removeFromBroadcast(String user, String name) {
        Broadcast bc = getBroadcast(name); //select broadcast by specified name from broadcast arraylist (null it doesn't exist)
        if (bc != null && bc.getUsers().stream().anyMatch(u -> u.equals(user))) { //if specified user and broadcast exist
            bc.rem(user); //remove user from broadcast and
            InfoCodes.REMOVED.print(user, name); //print info
        }
        //otherwise print warning
        else if (bc == null) {
            InfoCodes.BROADCAST_NOT_FOUND.print(name);
        } else {
            InfoCodes.NOT_A_MEMBER.print(user, name);
        }
    }

    public void deleteBroadcast(String name) {
        if (isBroadcast(name)) {
            Broadcast bc = getBroadcast(name);
            assert bc != null;
            bc.clear();
            getBroadcasts().removeIf(broadcast -> broadcast == bc);
            InfoCodes.DELETED.print(name);
        } else {
            InfoCodes.NOT_FOUND.print(name);
        }
    }

    public void sendText(String message, String receiver) {
        TextMsg msg = new TextMsg(s, user, pwd, message);
        for (String u : Objects.requireNonNull(gatherUsers(receiver))) {
            msg.send(u);
        }
        refresh();
    }

    public void sendImage(String path, String receiver) {
        ImageMsg msg = new ImageMsg(s, user, pwd, path);
        for (String u : Objects.requireNonNull(gatherUsers(receiver))) {
            msg.send(u);
        }
        refresh();
    }

    public void listEverything(ArrayList<String> args) throws IOException {
        //either get 100 most recent or all messages depending on argument and save in string array 'messages'
        ArrayList<IncomingMsg> messages = mf.wrapMessages((args.size() != 0 && args.get(0).equals("100")) ?
                s.getMostRecentMessages(user, pwd) :
                s.getMessages(user, pwd, 0));
        for (IncomingMsg msg : messages) {
            msg.print();
        }
        updateMsgCounter(messages);
    }

    public void refresh() {
        ArrayList<IncomingMsg> messages;
        try {
            messages = mf.wrapMessages(s.getMessages(user, pwd, lastMsgId));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (IncomingMsg msg : messages) {
            msg.print();
        }
        updateMsgCounter(messages);
    }

    public void exit() {
        running = false;
    }

    public void help() {
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

    public void unknown() {
        InfoCodes.UNKNOWN_COMMAND.print();
    }


    /* helper methods */
    private void updateMsgCounter(ArrayList<IncomingMsg> messages) {
        lastMsgId = messages.isEmpty() ? lastMsgId : messages.get(messages.size() - 1).getId();
    }

    private ArrayList<String> gatherUsers(String receiver) {
        ArrayList<String> users = new ArrayList<>();
        if (isUser(receiver)) {
            users.add(receiver);
        } else if (isBroadcast(receiver)) {
            users = Objects.requireNonNull(getBroadcast(receiver)).getUsers();
            if (users.isEmpty()) {
                InfoCodes.EMPTY.print(receiver);
                return users;
            }
        } else {
            InfoCodes.NOT_FOUND.print(receiver);
            return users;
        }
        return users;
    }


    /* getter methods */
    public String getUser() {
        return user;
    }

    public String getPwd() {
        return pwd;
    }

    public boolean credentialsOK() {
        try {
            s.getUsers(user, pwd);
        } catch (IOException e) {
            if (e.getMessage().equals("turing.iem.thm.de")) {
                InfoCodes.CONNECTION_FAILED.print();
            } else {
                InfoCodes.CREDENTIALS_INCORRECT.print();
            }
            return false;
        }
        return true;
    }

    public boolean isRunning() {
        return running;
    }

    private boolean isBroadcast(String name) {
        return broadcasts.stream().anyMatch(broadcast -> broadcast.getName().equals(name));
    }

    private boolean isUser(String name) {
        String[] users = new String[0];
        try {
            users = s.getUsers(user, pwd);
        } catch (Exception e) {
            if (credentialsOK()) {
                InfoCodes.UNKNOWN_ERROR.print();
            }
        }
        for (String user : users) {
            if (user.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<Broadcast> getBroadcasts() {
        return broadcasts;
    }

    private Broadcast getBroadcast(String name) {
        for (Broadcast broadcast : broadcasts) {
            if (broadcast.getName().equals(name)) {
                return broadcast;
            }
        }
        return null;
    }

}
