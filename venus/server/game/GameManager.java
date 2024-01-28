package venus.server.game;

import java.util.*;
import venus.server.nch.ChallengeInfo;

public class GameManager {
    
    private static List<Game> games = Collections.synchronizedList(new ArrayList<>());
    
    private static int count;
    
    private static Object lock = new Object();

    public static boolean add(Game e) {
        synchronized(lock) {
            if (games.contains(e)) {
                return false;
            }
            return games.add(e);
        }
    }
    
    public static void closeGame(Game game) {
        synchronized(lock) {
            game.close();
            games.remove(game);
        }
    }

    public static boolean remove(Object o) {
        return games.remove(o);
    }
    
    public static Game findGameWith(String username) {
        synchronized (lock) {
            Game game = games.stream().filter(x -> x.getChallenger().equals(username)).findFirst().orElse(null);
            if (game == null) {
                game = games.stream().filter(x -> x.getChallenged().equals(username)).findFirst().orElse(null);
            }
            return game;
        }
    }
    
    public static Game startGame(ChallengeInfo ci) {
        synchronized (lock) {
            Game game = new Game(ci);
            games.add(game);
            return game;
        }
    }
}
