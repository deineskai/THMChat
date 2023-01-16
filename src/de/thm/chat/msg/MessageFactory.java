package de.thm.chat.msg;

import java.util.ArrayList;

public class MessageFactory {

    public MessageFactory(){}

    /* methods */
    public ArrayList<IncomingMsg> wrapMessages(String[] rawdata) {
        ArrayList<IncomingMsg> a = new ArrayList<>();
        for (String[] msg : collectMessages(rawdata)) {
            a.add(new IncomingMsg(msg));
        }
        return a;
    }

    public ArrayList<String[]> collectMessages(String[] rawdata) {
        ArrayList<String[]> messages = new ArrayList<>();
        for (String s : rawdata) {
            messages.add(disassembleMsg(s));
        }
        return messages;
    }

    private String[] disassembleMsg(String s) {
        String[] msg = new String[6];
        s += "|";
        for (int i = 0; i < 6; i++) {
            msg[i] = s.substring(0, s.indexOf("|"));
            s = s.substring(s.indexOf("|") + 1);

            if (i == 4 && msg[i].equals("img")) {
                s = s.substring(s.indexOf("|") + 1);
                s = s.substring(s.indexOf("|") + 1);
            }
        }
        return msg;
    }

}
