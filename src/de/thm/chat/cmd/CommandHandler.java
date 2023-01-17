package de.thm.chat.cmd;

import de.thm.chat.util.InfoCodes;
import de.thm.oop.chat.base.server.BasicTHMChatServer;

import java.io.IOException;
import java.util.ArrayList;


public class CommandHandler {

    private static String keyword;
    private static ArrayList<String> args;
    private static Command c;


    public CommandHandler(BasicTHMChatServer server) {
        c = new Command(server);
    }

    /* methods */
    /**
     * Calls the corresponding method for given command.
     * */
    public void execute(String command) throws IOException {
        translateCommand(command);
        if (!commandOK()) { return; }
        switch (keyword) {
            case "user" -> c.setUser(args.get(0));
            case "pwd" -> c.setPwd(args.get(0));
            case "lu" -> c.listUsers();
            case "bc" -> c.createBroadcast(args.get(0));
            case "bcadd" -> c.addToBroadcast(args.get(1), args.get(0));
            case "bcrem" -> c.removeFromBroadcast(args.get(1), args.get(0));
            case "bcdel" -> c.deleteBroadcast(args.get(0));
            case "msg" -> c.sendText(args.get(1), args.get(0));
            case "img" -> c.sendImage(args.get(1), args.get(0));
            case "le" -> c.listEverything();
            case "refresh" -> c.refresh();
            case "exit" -> c.exit();
            case "help" -> c.help();
            case "hamster" -> c.searchForSeed();
            default -> c.unknown();
        }
    }

    /**
     * Reads keyword and arguments from given command.
     * */
    private void translateCommand(String cmd) {
        if (cmd.isEmpty() || cmd.isBlank()) {
            return;
        }
        cmd += " ";
        keyword = cmd.substring(0, cmd.indexOf(" "));
        cmd = cmd.substring(cmd.indexOf(" ") + 1);
        fillArgs(cmd);
    }

    /**
     * Reads each argument from command and adds it to {@link ArrayList<> args}.
     * Quotation marks will also be considered to ensure image file paths may contain spaces.
     * */
    private void fillArgs(String cmd) {
        args = new ArrayList<>();
        while (!cmd.isEmpty()) {
            if (cmd.charAt(0) == '\"') {
                try {
                    // extract content between quotation marks (also containing spaces)
                    cmd = cmd.substring(1);
                    args.add(cmd.substring(0, cmd.indexOf("\"")));
                    cmd = cmd.substring(cmd.indexOf("\"") + 1);
                } catch (Exception e) {
                    InfoCodes.SYNTAX.print();
                    return;
                }
            } else {
                args.add(cmd.substring(0, cmd.indexOf(" ")));
                cmd = cmd.substring(cmd.indexOf(" ") + 1);
            }
        }
    }

    /**
     * Checks 1. if credentials are correct (not needed if the {@link String keyword} is "user", "pwd", "help" or "exit")
     * and 2. if the amount of arguments is sufficient depending on the keyword.
     * */
    private boolean commandOK() {
        // check if credentials are set and correct in case they're needed for the command
        if (!(keyword.equals("user") || keyword.equals("pwd") || keyword.equals("help") || keyword.equals("exit")) &&
                (c.getUser() == null || c.getPwd() == null || !c.credentialsOK())) {
            InfoCodes.CREDENTIALS_INCORRECT.print();
            return false;
        }
        // check for missing arguments
        if (((keyword.equals("bc") || keyword.equals("bcdel") || keyword.equals("user") || keyword.equals("pwd")) &&
                args.size() < 1) || ((keyword.equals("bcadd") || keyword.equals("bcrem") || keyword.equals("msg") ||
                keyword.equals("img")) && args.size() < 2)) {
            InfoCodes.ARGUMENTS_MISSING.print();
            return false;
        }
        return true;
    }

    /* getters */
    /**
     * Returns whether the application should still be running.
     * */
    public boolean isRunning() {
        return c.isRunning();
    }

}
