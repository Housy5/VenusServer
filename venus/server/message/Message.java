package venus.server.message;

import java.util.*;
import venus.server.IDGenerator;
import venus.server.ServerConstants;
import venus.server.encryption.EncryptionManager;

public final class Message implements ServerMessage {

    private final String message;
    private String id;

    private final List<String> arguments = new ArrayList<>();

    private Message(String msg, List<String> args, String id) {
        this.message = msg;
        arguments.addAll(args);
        this.id = id;
    }

    public Message(String msg, String... args) {
        this(IDGenerator.genUniqueId(), msg, args);
    }

    private Message(String id, String msg, String... args) {
        this.id = id;
        this.message = msg;
        arguments.addAll(List.of(args));
    }

    public List<String> getArguments() {
        return List.copyOf(arguments);
    }

    @Override
    public String getArgument(int index) {
        return arguments.get(index);
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int getArgCount() {
        return arguments.size();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.message);
        hash = 29 * hash + Objects.hashCode(this.id);
        hash = 29 * hash + Objects.hashCode(this.arguments);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Message other = (Message) obj;
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return Objects.equals(this.arguments, other.arguments);
    }

    private String argsToStr() {
        StringBuilder sb = new StringBuilder(arguments.size() * 2 + 1);

        for (String arg : arguments) {
            if (!sb.isEmpty()) {
                sb.append(ServerConstants.SEP_STR);
            }
            sb.append(arg);
        }

        return sb.toString();
    }

    String toPlainString() {
        String sep = ServerConstants.SEP_STR;
        return message + sep + argsToStr() + sep + id;
    }

    @Override
    public String toString() {
        return EncryptionManager.encrypt(toPlainString());
    }

    /**
     * Parses a Message object from a line of text. Warning: may return a
     * message of type PlainTextMessage when the line of text starts with the
     * plain text tag defined in ServerConstants.
     *
     * @param data
     * @return
     */
    public static ServerMessage from(String data) {
        if (data.startsWith(ServerConstants.PLAIN_TEXT_TAG)) {
            return PlainTextMessage.from(data);
        }
        
        String decrypted = EncryptionManager.decrypt(data);
        
        String[] tokens = decrypted.split(ServerConstants.SEP_STR);
        String message = tokens[0];
        String id = tokens[tokens.length - 1];

        String[] args = new String[tokens.length - 2];
        int offset = 1;

        for (int i = 0; i < args.length; i++) {
            args[i] = tokens[i + offset];
        }

        Message msg = new Message(message, List.of(args), id);
        return msg;
    }
    
    static ServerMessage from(String data, boolean decrypt) {
        String decrypted = data;
        
        if (decrypt) {
            decrypted = EncryptionManager.decrypt(data);
        }
        
        String[] tokens = decrypted.split(ServerConstants.SEP_STR);
        String message = tokens[0];
        String id = tokens[tokens.length - 1];

        String[] args = new String[tokens.length - 2];
        int offset = 1;

        for (int i = 0; i < args.length; i++) {
            args[i] = tokens[i + offset];
        }

        Message msg = new Message(message, List.of(args), id);
        return msg;
    }
}
