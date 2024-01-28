package venus.server.message;

import java.util.List;

public interface ServerMessage {
    
    public String getMessage();
    public List<String> getArguments();
    public String getArgument(int i);
    public String getId();
    public int getArgCount();
}
