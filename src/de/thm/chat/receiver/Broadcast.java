package de.thm.chat.receiver;

import java.util.*;

public class Broadcast extends Receiver {

    private static ArrayList<String> users = new ArrayList<>();

    //constructor
    public Broadcast(String name) {
        super(name);
    }

    //add user to broadcast
    public void add(String user){
        users.add(user);
    }

    //remove user from broadcast by name
    public void rem(String user){
        users.removeIf(u -> Objects.equals(u, user));
    }

    //return all users of broadcast as an ArrayList
    public ArrayList<String> getUsers(){
        return users;
    }

    public void clear(){
        users = new ArrayList<>();
    }

}
