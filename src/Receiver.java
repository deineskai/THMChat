import de.thm.oop.chat.base.server.BasicTHMChatServer;

public abstract class Receiver {

    private final String name;

    public Receiver(String name){
        this.name = name;
    }

    public String getName(){ return name; }

}
