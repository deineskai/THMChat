package de.thm.chat.msg;

import de.thm.chat.util.ANSIColors;
import de.thm.oop.chat.base.server.BasicTHMChatServer;

import java.io.FileInputStream;
import java.io.InputStream;

public class ImageMsg extends Msg {
    private final String mime, path;


    public ImageMsg(BasicTHMChatServer s, String user, String pwd, String path) {
        super(s, user, pwd);
        this.path = path;
        mime = "image/" + path.substring(path.lastIndexOf(".") + 1);
    }

    /* methods */
    public void send(String u) {
        try {
            InputStream image = new FileInputStream(path);
            s.sendImageMessage(user, pwd, u, mime, image);
        } catch (Exception e) {
            System.out.println(ANSIColors.RED.get() + e.getMessage() + ANSIColors.RESET.get());
        }
    }
}
