package venus.server;

import venus.server.user.UserManager;

public class AutoSaver implements Runnable {

    private boolean running = false;
    
    private int saveInterval = 5 * 60 * 60;
    
    private UserManager um;
    
    public AutoSaver(UserManager userm) {
        um = userm;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(saveInterval);
                um.saveToFile();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(-1);
            }
        }
    }
    
    public void stop() {
        running = false;
    }
    
    public void start() {
        if (running) {
            return;
        }
        Thread thread = new Thread(this);
        thread.start();
    }
    
}
