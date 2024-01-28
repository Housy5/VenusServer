package venus.server;

import venus.server.event.Event;
import venus.server.event.EventObserver;
import venus.server.message.Message;

public class EventManager implements EventObserver {

    @Override
    public void observe(Event e) {
        if (e.type() == Event.USERS_CHANGED) {
            onUsersChanged();
            return;
        }
        
        if (e.type() == Event.LOBBY_USERS_CHANGED) {
            VenusServer.broadcast(new Message(Requests.UPDATE_INFO));
            return;
        }
        
        if (e.type() == Event.CHALLENGES_CHANGED) {
            VenusServer.broadcast(new Message(Requests.UPDATE_INFO));
            return;
        }
    }

    public void onUsersChanged() {
        try {
            VenusServer.getUserManager().saveToFile();
        } catch (Exception ex) {
            ex.printStackTrace();
            VenusServer.log(ex.getMessage());
            VenusServer.log("Fail to save the users.");
        }
    }
    
    private static EventManager instance;
    
    public static EventManager getInstance() {
        if (instance == null)
            instance = new EventManager();
        return instance;
    }
}
