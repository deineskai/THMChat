package de.thm.chat.cmd;

import de.thm.chat.util.ANSIColors;
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

    public void execute(String command) throws IOException {

        c.setPwd("eTb52HSS");
        c.setUser("deineskai");

        translateCommand(command);
        if (!commandOK()) {
            return;
        }
        switch (keyword) {
            case "user" -> c.setUser(args.get(0));
            case "pwd" -> c.setPwd(args.get(0));
            case "list" -> c.listUsers();
            case "bc" -> c.createBroadcast(args.get(0));
            case "bcadd" -> c.addToBroadcast(args.get(1), args.get(0));
            case "bcrem" -> c.removeFromBroadcast(args.get(1), args.get(0));
            case "bcdel" -> c.deleteBroadcast(args.get(0));
            case "msg" -> c.sendText(args.get(1), args.get(0));
            case "img" -> c.sendImage(args.get(1), args.get(0));
            case "le" -> c.listEverything(args);
            case "refresh" -> c.refresh();
            case "exit" -> c.exit();
            case "help" -> c.help();
            case "eat" -> c.searchForSeed();
            default -> c.unknown();
        }
    }

    private void translateCommand(String cmd) {
        if (cmd.isEmpty() || cmd.isBlank()) {
            return;
        }
        cmd += " ";
        keyword = cmd.substring(0, cmd.indexOf(" "));
        cmd = cmd.substring(cmd.indexOf(" ") + 1);
        fillArgs(cmd);
    }

    private void fillArgs(String cmd) {
        args = new ArrayList<>();
        while (!cmd.isEmpty()) {
            if (cmd.charAt(0) == '\"') {
                try {
                    cmd = cmd.substring(1);
                    args.add(cmd.substring(0, cmd.indexOf("\"")));
                    cmd = cmd.substring(cmd.indexOf("\"") + 1);
                } catch (Exception e) {
                    System.out.println(ANSIColors.RED.get() + "Syntax error" + ANSIColors.RESET.get());
                    return;
                }
            } else {
                args.add(cmd.substring(0, cmd.indexOf(" ")));
                cmd = cmd.substring(cmd.indexOf(" ") + 1);
            }
        }
    }


    //check command
    private boolean commandOK() {
        c.setPwd("eTb52HSS");
        c.setUser("deineskai");


        //check if credentials are set and correct in case they're needed for the command
        if (!(keyword.equals("user") || keyword.equals("pwd") || keyword.equals("help") || keyword.equals("exit")) &&
                (c.getUser() == null || c.getPwd() == null || !c.credentialsOK())) {
            InfoCodes.CREDENTIALS_INCORRECT.print();
            return false;
        }
        //check for missing arguments
        if (((keyword.equals("bc") || keyword.equals("bcdel") || keyword.equals("user") || keyword.equals("pwd")) &&
                args.size() < 1) || ((keyword.equals("bcadd") || keyword.equals("bcrem") || keyword.equals("msg") ||
                keyword.equals("img")) && args.size() < 2)) {
            InfoCodes.ARGUMENTS_MISSING.print();
            return false;
        }
        return true;
    }

    public boolean isRunning() {
        return c.isRunning();
    }

}
