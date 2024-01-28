package venus.server.nch;

import java.awt.Color;
import java.util.*;
import venus.server.HouseKeeper;
import venus.server.event.Event;
import venus.server.event.EventCenter;

public class ChallengeBook {

    private List<ChallengeInfo> challenges;

    private ChallengeBook() {
        challenges = Collections.synchronizedList(new ArrayList<>());
        initHouseKeeping();
    }

    private void initHouseKeeping() {
        HouseKeeper.instance().addTask(() -> {
            synchronized (instance) {
                var expired = challenges.stream().filter(x -> x.isExpired()).toList();
                if (expired.isEmpty()) {
                    return;
                }
                challenges.removeAll(expired);
                EventCenter.getInstance().raiseEvent(new Event(Event.CHALLENGES_CHANGED));
            };
        });
    }

    public synchronized ChallengeInfo createChallenge(String p1, String p2, Color color1, int time) {
        ChallengeInfo info = new ChallengeInfo(p1, color1, time);
        info.setChallenged(p2);

        if (challenges.contains(info)) {
            return null;
        }

        challenges.add(info);
        EventCenter.getInstance().raiseEvent(new Event(Event.CHALLENGES_CHANGED));
        return info;
    }

    public synchronized boolean removeChallenge(String id) {
        var c = getChallenge(id);
        if (c == null) {
            return false;
        }
        challenges.remove(c);
        EventCenter.getInstance().raiseEvent(new Event(Event.CHALLENGES_CHANGED));
        return true;
    }

    public synchronized void removeEverythingFrom(String username) {
        var buffer = new ArrayList<ChallengeInfo>();
        buffer.addAll(challenges.stream().filter(x -> x.getChallenger().equals(username)).toList());
        buffer.addAll(challenges.stream().filter(x -> x.getChallenged().equals(username)).toList());
        challenges.removeAll(buffer);
        
        EventCenter.getInstance().raiseEvent(new Event(Event.CHALLENGES_CHANGED));
    }

    public synchronized ChallengeInfo getChallenge(String id) {
        return challenges.stream()
                .parallel()
                .filter(x -> x.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public synchronized List<ChallengeInfo> getReceivedChallenges(String username) {
        return challenges.stream()
                .parallel()
                .filter(x -> x.getChallenged().equals(username))
                .toList();
    }

    public synchronized List<ChallengeInfo> getSentChallenges(String username) {
        return challenges.stream()
                .parallel()
                .filter(x -> x.getChallenger().equals(username))
                .toList();
    }

    private static ChallengeBook instance;

    public static ChallengeBook instance() {
        if (instance == null) {
            instance = new ChallengeBook();
        }
        return instance;
    }
}
