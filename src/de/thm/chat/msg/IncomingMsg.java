package de.thm.chat.msg;

import de.thm.chat.util.ANSIColors;

import java.util.Objects;

public class IncomingMsg {

    private final String message, sender, receiver, datetime;
    private final int id;

    public IncomingMsg(String[] msg) {
        this.id = Integer.parseInt(msg[0]);
        this.datetime = msg[1];
        this.sender = Objects.equals(msg[2], "in") ? msg[3] : "you";
        this.receiver = Objects.equals(msg[2], "in") ? "you" : msg[3];
        this.message = msg[5];
    }

    public void print() {
        System.out.println(datetime + " | " + ANSIColors.BLUE.get() + sender + " -> " + receiver +
                ANSIColors.RESET.get() + ": " + ANSIColors.YELLOW_BOLD.get() + message + ANSIColors.RESET.get());
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
