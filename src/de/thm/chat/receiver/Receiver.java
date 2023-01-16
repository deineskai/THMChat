package de.thm.chat.receiver;

public abstract class Receiver {

    private final String NAME;

    public Receiver(String name) {
        this.NAME = name;
    }

    /* getters */
    public String getName() {
        return NAME;
    }

}
