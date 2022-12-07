import de.thm.oop.chat.base.server.BasicTHMChatServer;

public abstract class Msg {

    protected BasicTHMChatServer s;
    protected Receiver r;
    protected String user, pwd;

    public Msg(BasicTHMChatServer s, Receiver r, String user, String  pwd){
        this.s = s;
        this.r = r;
        this.user = user;
        this.pwd = pwd;
    }
}
