package venus.server.game;

import java.util.*;
import venus.server.event.Event;
import venus.server.event.EventCenter;

public class Lobby {
    
    private final List<Player> players;
    private final Random random = new Random();
    
    public Lobby() {
        players = new ArrayList<>();
    }
    
    public synchronized boolean contains(final String nm) {
        return players.stream()
                .map(Player::getName)
                .anyMatch(x -> x.equals(nm));
    }
    
    public synchronized void addPlayer(Player player) {
        players.add(player);
        EventCenter.getInstance().raiseEvent(new Event(Event.LOBBY_USERS_CHANGED));
    }

    public synchronized void removePlayer(Player player) {
        players.remove(player);
        EventCenter.getInstance().raiseEvent(new Event(Event.LOBBY_USERS_CHANGED));
    }
    
    public synchronized List<Player> getPlayers() {
        return List.copyOf(players);
    }
    
    public synchronized List<String> getPlayerNames() {
        return players.stream()
                .parallel()
                .map(x -> x.getName())
                .toList();
    }
    
    public synchronized Player findPlayerByName(String n) {
        return players.stream().filter(x -> x.getName().equals(n)).findFirst().orElse(null);
    } 
    
    private volatile static Lobby instance;
    
    public synchronized static Lobby instance() {
        if (instance == null) {
            instance = new Lobby();
        }
        return instance;
    }
}
