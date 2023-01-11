package de.thm.chat.msg;

import de.thm.chat.util.ANSIColors;
import de.thm.oop.chat.base.server.BasicTHMChatServer;

import java.io.IOException;

public class TextMsg extends Msg {

    private final String content; //the actual message that should be sent

    public TextMsg(BasicTHMChatServer s, String user, String pwd, String content) {
        super(s, user, pwd);
        this.content =  content;
    }

    public void send(String u) {
        try { //try to send message to server
            s.sendTextMessage(user, pwd, u, content);
        } catch (IOException e) {
        System.out.println(ANSIColors.RED.get() + e.getMessage() + ANSIColors.RESET.get()); //print warning
        }
    }
}
