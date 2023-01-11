package de.thm.chat.msg;

import de.thm.chat.util.ANSIColors;
import de.thm.oop.chat.base.server.BasicTHMChatServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageMsg extends Msg {

    private static InputStream image;
    private final String mime, path;


    public ImageMsg(BasicTHMChatServer s, String user, String pwd, String path) {
        super(s, user, pwd);
        this.path = path;
        mime = "image/" + path.substring(path.lastIndexOf(".")+1); //get mime type from filetype (which can be found in path)
    }

    public void send(String u) {
        //get image as stream, print warning if not found
        try {
            image = new FileInputStream(path);
            s.sendImageMessage(user, pwd, u, mime, image); //try to send to server
        } catch (Exception e) {
            System.out.println(ANSIColors.RED.get() + e.getMessage() + ANSIColors.RESET.get());
        }


    }
}
