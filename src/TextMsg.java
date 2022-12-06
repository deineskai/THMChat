import de.thm.oop.chat.base.server.BasicTHMChatServer;

import java.io.IOException;

public class TextMsg extends Msg {

    private final String content;

    public TextMsg(BasicTHMChatServer s, Receiver r, String user, String pwd, String content) {
        super(s, r, user, pwd);
        this.content =  content;
    }

    public void send() throws IOException {
        if (r instanceof User) {
            s.sendTextMessage(user, pwd, r.getName(), content);
        } else  {
            for (User u:((Broadcast) r).getUsers()) {
                s.sendTextMessage(user, pwd, u.getName(), content);
            }
        }
    }

    @Override
    public Receiver getReceiver() {
        return null;
    }
}
