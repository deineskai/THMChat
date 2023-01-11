package de.thm.chat.msg;

import de.thm.oop.chat.base.server.BasicTHMChatServer;

public abstract class Msg {

    protected final BasicTHMChatServer s;
    protected final String user, pwd;

    public Msg(BasicTHMChatServer s, String user, String pwd) {
        this.s = s;
        this.user = user;
        this.pwd = pwd;
    }
}
