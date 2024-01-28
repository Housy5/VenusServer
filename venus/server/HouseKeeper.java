package venus.server;

import java.util.*;

public class HouseKeeper extends Thread {
    
    private final int cph = 60 / 5; // clicks per hour
    private final int HOUR_MILLIS = 60 * 60 * 1_000;
    
    private List<Runnable> jobs = new Vector<>();
    
    public void addTask(Runnable runnable) {
        jobs.add(runnable);
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                jobs.forEach(x -> new Thread(x).start());
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static HouseKeeper instance;
    
    public static HouseKeeper instance() {
        if (instance == null) {
            instance = new HouseKeeper();
            instance.start();
        }
        return instance;
    }
}
