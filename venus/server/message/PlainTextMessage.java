package venus.server.message;

import java.util.List;
import venus.server.ServerConstants;

public final class PlainTextMessage implements ServerMessage {
    
    private final Message msg;
    
    private PlainTextMessage(Message msg) {
        this.msg = msg;
    }
    
    public PlainTextMessage(String msg, String... args) {
        this.msg = new Message(msg, args);
    }

    @Override
    public String getMessage() {
        return msg.getMessage();
    }

    @Override
    public String getArgument(int i) {
        return msg.getArgument(i);
    }
    
    @Override
    public List<String> getArguments() {
        return msg.getArguments();
    }

    @Override
    public String getId() {
        return msg.getId();
    }    

    public void setId(String id) {
        msg.setId(id);
    }

    @Override
    public int getArgCount() {
        return msg.getArgCount();
    }
    
    @Override
    public String toString() {
        return PLAIN_TEXT_TAG + ServerConstants.SEP_STR + msg.toPlainString();
    }
    
    private static final String PLAIN_TEXT_TAG = ServerConstants.PLAIN_TEXT_TAG;

    public static PlainTextMessage from(String s) {
        Message msg = (Message) Message.from(s.substring(PLAIN_TEXT_TAG.length() + 1), false);
        return new PlainTextMessage(msg);
    }
}
