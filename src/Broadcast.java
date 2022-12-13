import java.util.*;

public class Broadcast extends Receiver {

    private static ArrayList<User> users = new ArrayList<>();

    //constructor
    public Broadcast(String name) {
        super(name);
    }

    //add user to broadcast
    public void add(User u){
        users.add(u);
    }

    //remove user from broadcast by name
    public void rem(String name){
        users.removeIf(user -> Objects.equals(user.getName(), name));
    }

    //return all users of broadcast as an ArrayList
    public ArrayList<User> getUsers(){
        return users;
    }

    public void clear(){
        users = new ArrayList<>();
    }

}
