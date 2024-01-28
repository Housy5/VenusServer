package venus.server;

import venus.server.message.ServerMessage;
import java.util.Objects;

public class Request {
    
    private final ServerMessage message;
    private volatile ServerMessage response = null;
    
    private long timestamp;
    
    private static final long TRESHOLD = 5_000; //5 second
    
    public Request(ServerMessage msg) {
        this.message = msg;
        timestamp = System.currentTimeMillis();
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() - timestamp >= TRESHOLD;
    }
    
    public boolean hasResponse() {
        return response != null;
    }

    public ServerMessage getMessage() {
        return message;
    }
    
    public synchronized void setResponse(ServerMessage msg) {
        if (response != null) {
            return;
        }    
        response = msg;
    }
    
    public String getId() {
        return message.getId();
    }
    
    public ServerMessage awaitResponse() {
        await();
        return response;
    }

    private void await() {
        while (!hasResponse() && !isExpired()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.message);
        hash = 53 * hash + Objects.hashCode(this.response);
        hash = 53 * hash + (int) (this.timestamp ^ (this.timestamp >>> 32));
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
        final Request other = (Request) obj;
        if (this.timestamp != other.timestamp) {
            return false;
        }
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        return Objects.equals(this.response, other.response);
    }
    
}
