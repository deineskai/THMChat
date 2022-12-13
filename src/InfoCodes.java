public enum InfoCodes {

    //this enum makes it easier to print warnings to the console

    USER_INVALID,
    PWD_INVALID,
    CREDENTIALS_INCORRECT,
    USER_NOT_FOUND,
    BROADCAST_NOT_FOUND,
    NOT_FOUND,
    ARGUMENTS_MISSING,
    BROADCAST_CREATED,
    ILLEGAL_NAME,
    EXISTS,
    ADDED,
    REMOVED,
    ALREADY_MEMBER,
    NOT_A_MEMBER,
    CONNECTION_FAILED,
    UNKNOWN_COMMAND,
    UNKNOWN_ERROR,
    DELETED,
    EMPTY;



    //printing methods
    public void print(){
        switch (this){
            case USER_INVALID
                    -> System.out.println(ANSIColors.RED.get() + "Username invalid" + ANSIColors.RESET.get());
            case PWD_INVALID
                    -> System.out.println(ANSIColors.RED.get() + "Password invalid" + ANSIColors.RESET.get());
            case CREDENTIALS_INCORRECT
                    -> System.out.println(ANSIColors.RED.get() + "Username or password wrong" + ANSIColors.RESET.get());
            case ARGUMENTS_MISSING
                    -> System.out.println(ANSIColors.RED.get() + "Argument(s) missing" + ANSIColors.RESET.get());
            case ILLEGAL_NAME
                    -> System.out.println(ANSIColors.RED.get() + "Illegal name" + ANSIColors.RESET.get());
            case CONNECTION_FAILED
                    -> System.out.println(ANSIColors.RED.get() + "Connection failed" + ANSIColors.RESET.get());
            case UNKNOWN_COMMAND
                    -> System.out.println(ANSIColors.RED.get() + "Unknown command" + ANSIColors.RESET.get());
            case UNKNOWN_ERROR
                    -> System.out.println(ANSIColors.RED.get() + "An unknown error occurred" + ANSIColors.RESET.get());
        }
    }

    public void print(String user_or_broadcast){
        switch (this){
            case EXISTS
                    -> System.out.println("Broadcast '" + ANSIColors.RED.get() + user_or_broadcast + ANSIColors.RESET.get() + "' already exists.");
            case USER_NOT_FOUND
                    -> System.out.println("User '" + ANSIColors.RED.get() + user_or_broadcast + ANSIColors.RESET.get() + "' could not be found.");
            case BROADCAST_NOT_FOUND
                    -> System.out.println("Broadcast '" + ANSIColors.RED.get() + user_or_broadcast + ANSIColors.RESET.get() + "' could not be found.");
            case NOT_FOUND
                    -> System.out.println("User or broadcast '" + ANSIColors.RED.get() + user_or_broadcast + ANSIColors.RESET.get() + "' could not be found.");
            case BROADCAST_CREATED
                    -> System.out.println("Broadcast '" + ANSIColors.BLUE.get() + user_or_broadcast + ANSIColors.RESET.get() + "' has been created.");
            case DELETED
                    -> System.out.println("Broadcast '" + ANSIColors.BLUE.get() + user_or_broadcast + ANSIColors.RESET.get() + "' has been deleted.");
            case EMPTY
                    -> System.out.println(ANSIColors.YELLOW.get() + "Broadcast '" +  user_or_broadcast +  "' is empty." + ANSIColors.RESET.get());
        }
    }

    public void print(String username, String broadcast){
        switch (this){
            case ADDED
                    -> System.out.println("User '" + ANSIColors.BLUE.get() + username + ANSIColors.RESET.get() + "' has been added to '" +
                    ANSIColors.BLUE.get() + broadcast + ANSIColors.RESET.get() + "'.");
            case REMOVED
                    -> System.out.println("User '" + ANSIColors.BLUE.get() + username + ANSIColors.RESET.get() + "' has been removed from '" +
                    ANSIColors.BLUE.get() + broadcast + ANSIColors.RESET.get() + "'.");
            case ALREADY_MEMBER
                    -> System.out.println("User '" + ANSIColors.RED.get() + username + ANSIColors.RESET.get() + "' is already a member of '" +
                    ANSIColors.BLUE.get() + broadcast + ANSIColors.RESET.get() + "'.");
            case NOT_A_MEMBER
                    -> System.out.println("User '" + ANSIColors.RED.get() + username + ANSIColors.RESET.get() + "' is not a member of '" +
                    ANSIColors.BLUE.get() + broadcast + ANSIColors.RESET.get() + "'.");
        }
    }
}
