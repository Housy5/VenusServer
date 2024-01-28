package venus.server.challenge;

import java.util.*;
import venus.server.Requests;
import venus.server.message.Message;

public class ChallengeManager {
    
    private List<Challenge> challenges;
    
    public ChallengeManager() {
        challenges = new ArrayList<>();
    }
    
    public synchronized boolean add(Challenge c) {
        if (challenges.contains(c)) {
            return false;
        }
        return challenges.add(c);
    }
    
    public synchronized boolean remove(Challenge c) {
        return challenges.remove(c);
    }
    
    public synchronized boolean remove(String id) {
        final var deleteObject = challenges.stream().filter(x -> x.id().equals(id)).findFirst().orElse(null);
        if (deleteObject == null) {
            return false;
        }
        return challenges.remove(deleteObject);
    }
    
    public synchronized Challenge fromId(String id) {
        return challenges.stream().filter(x -> x.id().equals(id)).findFirst().orElse(null);
    }
    
    private synchronized void notifyUsers(Challenge c) {
        var p1 = c.player1();
        var p2 = c.player2();
        
        if (p1 != null) {
            p1.getClientService().send(new Message(Requests.UPDATE_INFO));
        }
        
        if (p2 != null) {
            p2.getClientService().send(new Message(Requests.UPDATE_INFO));
        }
    }
    
    private static ChallengeManager instance;
    
    public static ChallengeManager instance() {
        if (instance == null)
            instance = new ChallengeManager();
        return instance;
    }
}
