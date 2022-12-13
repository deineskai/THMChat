import de.thm.oop.chat.base.server.BasicTHMChatServer;

import java.io.FileInputStream;
import java.io.InputStream;

public class ImageMsg extends Msg {

    private static InputStream image;
    private static String mime;

    private final String path;


    public ImageMsg(BasicTHMChatServer s, Receiver r, String user, String pwd, String path) {
        super(s, r, user, pwd);
        this.path = path;
        //get image as stream, print warning if not found
        try {
            image = new FileInputStream(path);
        } catch (Exception e) {
            System.out.println(ANSIColors.RED.get() + e.getMessage() + ANSIColors.RESET.get());
        }
        mime = "image/" + path.substring(path.lastIndexOf(".")+1); //get mime type from filetype (which can be found in path)
    }

    public void send() {
        //receiver can be user or broadcast
        if (r instanceof User) {
            //procedure for user
            try {
                s.sendImageMessage(user, pwd, r.getName(), mime, image); //try to send to server
            } catch (Exception e) {
                System.out.println(ANSIColors.RED.get() + e.getMessage() + ANSIColors.RESET.get()); //print warning
            }

        } else  {
            //procedure for broadcast
            for (User u:((Broadcast) r).getUsers()) { //for each user in a broadcast
                //create new message and send it to the USER (because each message needs its own stream. otherwise only the first one could be sent)
                ImageMsg msg = new ImageMsg(s, u, user, pwd, path);
                msg.send();
            }
        }
    }
}
