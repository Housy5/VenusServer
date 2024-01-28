package venus.server.event;

public record Event(int type) {
    
    public final static int USERS_CHANGED = 1;
    public final static int LOBBY_USERS_CHANGED = 2;
    public final static int CHALLENGES_CHANGED = 3;
}
