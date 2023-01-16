package de.thm.chat.receiver;

import java.util.ArrayList;
import java.util.Objects;

public class Broadcast extends Receiver {

    private static ArrayList<String> users = new ArrayList<>();

    public Broadcast(String name) {
        super(name);
    }

    /* methods */
    public void add(String user) {
        users.add(user);
    }

    public void rem(String user) {
        users.removeIf(u -> Objects.equals(u, user));
    }

    public void clear() {
        users = new ArrayList<>();
    }

    /* getters */
    public ArrayList<String> getUsers() {
        return users;
    }
}
