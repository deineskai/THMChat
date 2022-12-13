import de.thm.oop.chat.base.server.BasicTHMChatServer;

import java.io.IOException;

public class TextMsg extends Msg {

    private final String content; //the actual message that should be sent

    public TextMsg(BasicTHMChatServer s, Receiver r, String user, String pwd, String content) {
        super(s, r, user, pwd);
        this.content =  content;
    }

    public void send() {
        try { //try to send message to server
            if (r instanceof User) {
                    s.sendTextMessage(user, pwd, r.getName(), content);

            } else  {
                for (User u:((Broadcast) r).getUsers()) { //for each user in a broadcast
                    s.sendTextMessage(user, pwd, u.getName(), content);
                }
            }
        } catch (IOException e) {
        System.out.println(ANSIColors.RED.get() + e.getMessage() + ANSIColors.RESET.get()); //print warning
        }
    }
}
