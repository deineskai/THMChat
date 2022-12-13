import de.thm.oop.chat.base.server.BasicTHMChatServer;

public abstract class Msg {

    protected BasicTHMChatServer s; 
    protected Receiver r; //can either be a user or broadcast
    protected String user, pwd; //username and password of the sender

    public Msg(BasicTHMChatServer s, Receiver r, String user, String  pwd){
        this.s = s;
        this.r = r;
        this.user = user;
        this.pwd = pwd;
    }
}
