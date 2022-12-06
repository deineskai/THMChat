import de.thm.oop.chat.base.server.BasicTHMChatServer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ImageMsg extends Msg {

    private final InputStream image;
    private final String mime;

    public ImageMsg(BasicTHMChatServer s, Receiver r, String user, String pwd, String path) throws FileNotFoundException {
        super(s, r, user, pwd);
        image = new FileInputStream(path);
        mime = "image/" + path.substring(path.indexOf("."));
    }

    public void send() throws IOException {
        if (r instanceof User) {
            s.sendImageMessage(user, pwd, r.getName(), "image/png", image);
        } else  {
            for (User u:((Broadcast) r).getUsers()) {
                s.sendImageMessage(user, pwd, u.getName(), mime, image);
            }
        }
    }

    @Override
    public Receiver getReceiver() {
        return null;
    }
}
