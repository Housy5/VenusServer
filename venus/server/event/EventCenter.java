package venus.server.event;

import java.util.*;
import java.util.Queue;
import venus.server.VenusServer;

public class EventCenter {
    
    private final List<EventObserver> observers = new ArrayList<>();
    private final Queue<Event> events = new LinkedList<>();
    
    private class EventThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    sendEvents();
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    VenusServer.log("the event queue crashed!");
                    System.exit(-1);
                }
            }
        }
        
        private void sendEvents() {
            synchronized(instance) {
                while (!events.isEmpty()) {
                    Event event = events.poll();
                    observers.forEach(x -> x.observe(event));
                }
            }
        }
    }
    
    private EventCenter() {
    }
    
    private void start() {
        new EventThread().start();
    }

    public synchronized boolean addObserver(EventObserver e) {
        return observers.add(e);
    }
    
    public synchronized void raiseEvent(Event evt) {
        if (events.contains(evt))
            return;
        events.add(evt);
    }
    
    private static EventCenter instance;
    
    public static EventCenter getInstance() {
        if (instance == null) {
            instance = new EventCenter();
            instance.start();
        }
        
        return instance;
    }
}
