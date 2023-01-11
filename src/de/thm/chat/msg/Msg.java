package de.thm.chat.msg;

import de.thm.chat.receiver.Receiver;
import de.thm.oop.chat.base.server.BasicTHMChatServer;

public abstract class Msg {

    protected BasicTHMChatServer s; 
    protected Receiver r; //can either be a user or broadcast
    protected String user, pwd; //username and password of the sender

    public Msg(BasicTHMChatServer s, String user, String  pwd){
        this.s = s;
        this.user = user;
        this.pwd = pwd;
    }
}
