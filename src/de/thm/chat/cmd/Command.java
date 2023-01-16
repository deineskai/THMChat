package de.thm.chat.cmd;

import de.thm.chat.hamster.Hamster;
import de.thm.chat.hamster.Map;
import de.thm.chat.hamster.Suche;
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
import java.util.concurrent.TimeUnit;

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

    /**
     * Prints all chat users to console divided by commas. Each line contains up to three usernames.
     * */
    public void listUsers() {
        String[] users = new String[0]; // get an array of all usernames
        try {
            users = s.getUsers(user, pwd);
        } catch (IOException e) {
            System.out.println(ANSIColors.RED.get() + e + ANSIColors.RESET.get());
        }
        //print all usernames
        System.out.println("All users in this chat:");
        for (int i = 0; i < users.length; i++) {
            System.out.print(ANSIColors.BLUE.get() + users[i] + ANSIColors.RESET.get()); //print username to console
            if (i < users.length - 1) { // every username except for the last one is followed by a comma
                if (i % 3 == 2) {
                    System.out.println(", ");
                } else {
                    System.out.print(", ");
                }
            }
        }
        System.out.println(); // an empty line for separation
    }

    /**
     * Adds a new broadcast with the given name to {@link ArrayList<> broadcasts}.
     * Won't add broadcast if there's another broadcast or user with the given name.
     * */
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

    /**
     * Adds user to broadcast if both exist and the user isn't already a member of that broadcast.
     * */
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

    /**
     * Removes user from broadcast if both exist and the user is a member of the broadcast.
     * */
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

    /**
     * Deletes broadcast with given name if it exists.
     * */
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

    /**
     * Sends given {@code String} as text message to given receiver.
     * If the receiver is a broadcast it will be sent to every user of that broadcast.
     * This method also calls refresh(); to print the new message to console.
     * */
    public void sendText(String message, String receiver) {
        TextMsg msg = new TextMsg(s, user, pwd, message);
        for (String u : Objects.requireNonNull(gatherUsers(receiver))) {
            msg.send(u);
        }
        refresh();
    }

    /**
     * Reads image file from given file path and sends it to given receiver.
     * If the receiver is a broadcast it will be sent to every user of that broadcast.
     * This method also calls refresh(); to print the new message to console.
     * */
    public void sendImage(String path, String receiver) {
        ImageMsg msg = new ImageMsg(s, user, pwd, path);
        for (String u : Objects.requireNonNull(gatherUsers(receiver))) {
            msg.send(u);
        }
        refresh();
    }

    /**
     * Prints all messages to console.
     * */
    public void listEverything() {
        ArrayList<IncomingMsg> messages;
        try {
            messages = mf.wrapMessages(s.getMessages(user, pwd, 0));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (IncomingMsg msg : messages) {
            msg.print();
        }
        updateMsgCounter(messages);
    }

    /**
     * Prints all new messages to console.
     * If no messages have been printed yet the 100 most recent will be.
     * */
    public void refresh() {
        ArrayList<IncomingMsg> messages;
        try {
            messages = mf.wrapMessages(lastMsgId==0?s.getMostRecentMessages(user, pwd):s.getMessages(user, pwd, lastMsgId));
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

    /**
     * Prints help page to console.
     * */
    public void help() {
        System.out.println(ANSIColors.YELLOW.get());
        System.out.println("Command usage and meaning:");
        System.out.println("user [username]: set username");
        System.out.println("pwd [password]: set password");
        System.out.println("lu: shows a list of all users in this chat");
        System.out.println("le: shows all messages");
        System.out.println("bc [name]: creates a broadcast");
        System.out.println("bcadd [name] [username]: adds user to broadcast");
        System.out.println("bcrem [name] [username]: removes user from broadcast");
        System.out.println("bcdel [name]: deletes broadcast");
        System.out.println("msg [username/broadcast] [message]: sends text message to user or broadcast");
        System.out.println("img [username/broadcast] [\"path to file\"]: sends an image to user or broadcast");
        System.out.println("refresh: shows new or the 100 most recent messages");
        System.out.println("eat: let hamster walk to seeds and eat them");
        System.out.println("exit: exits the chat");
        System.out.println(ANSIColors.RESET.get());
    }

    public void unknown() {
        InfoCodes.UNKNOWN_COMMAND.print();
    }


    /* helper methods */
    /**
     * Updates {@link Integer lastMsgId} to the id of the last message.
     * */
    private void updateMsgCounter(ArrayList<IncomingMsg> messages) {
        lastMsgId = messages.isEmpty() ? lastMsgId : messages.get(messages.size() - 1).getId();
    }

    /**
     * If the receiver is a broadcast, an ArrayList containing all of its users will be returned.
     * Otherwise, a single user will be in the ArrayList.
     * If it's neither a broadcast nor a single user, an empty ArrayList will be returned.
     * */
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

    /**
     * Checks the credentials by sending a user request to the server.
     * */
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

    /**
     * Returns {@code true} if there's a broadcast with the given name.
     * */
    private boolean isBroadcast(String name) {
        return broadcasts.stream().anyMatch(broadcast -> broadcast.getName().equals(name));
    }

    /**
     * Returns {@code true} if there's a user with the given name.
     * */
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

    /**
     * Returns the broadcast with the given name.
     * Returns {@code null} if it doesn't exist.
     * */
    private Broadcast getBroadcast(String name) {
        for (Broadcast broadcast : broadcasts) {
            if (broadcast.getName().equals(name)) {
                return broadcast;
            }
        }
        return null;
    }

    /**
     * Initializes the hamster, and lets it find and walk to seeds.
     * */
    public void searchForSeed() throws IOException {
        sendText("init", "hamster22ws");
        try { TimeUnit.SECONDS.sleep(3); } // wait for incoming messages
        catch (Exception e) { throw new RuntimeException(e); }
        refresh();
        try {
            String rawMapData = mf.wrapMessages(s.getMessages(user, pwd, lastMsgId - 1)).get(0).getMessage().substring(13) + " ";
            Map map = new Map(rawMapData);
            Hamster h = new Hamster(this);
            Suche suche = new Suche(map);
            suche.suchePfad(h);
        } catch (StringIndexOutOfBoundsException e){ InfoCodes.NO_RESPONSE.print(); }

    }

}
