package venus.server.user;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import venus.server.NameValidator;
import venus.server.event.Event;
import venus.server.event.EventCenter;

public class UserManager {
    
    private Map<String, User> users;
    private List<String> online;

    public UserManager() {
        users = new HashMap<>();
        online = new ArrayList<>();
    }

    public synchronized boolean addUser(User newUser) {
        if (users.containsKey(newUser.getUsername())) {
            return false;
        }

        users.put(newUser.getUsername(), newUser);
        EventCenter.getInstance().raiseEvent(new Event(Event.USERS_CHANGED));
        return true;
    }

    public synchronized User getUser(String username) {
        return users.get(username);
    }

    public synchronized boolean authenticateUser(String username, String password) {
        User user = users.get(username);
        return user != null && user.getPassword().unlock(password);
    }
    
    public synchronized boolean markAsOnline(String username) {
        if (online.contains(username)) {
            return false;
        }
        return online.add(username);
    }
    
    public synchronized boolean markAsOffline(String username) {
        return online.remove(username);
    }
    
    public synchronized boolean isOnline(String username) {
        return online.contains(username);
    }
    
    public synchronized boolean changePassword(String username, String newPass, String oldPass) {
        User user = users.get(username);
        if (user == null){
            return false;
        }
        
        if (!user.getPassword().unlock(oldPass)) {
            return false;
        }
        
        user.changePassword(newPass);
        return true;
    }
    
    public synchronized boolean changeUsername(String oldUsername, String newUsername) {
        if (users.containsKey(newUsername)) {
            return false;
        }
        
        User user = users.get(oldUsername);
        
        if (user == null) {
            return false;
        }
        
        user.setUsername(newUsername);
        users.remove(oldUsername);
        users.put(newUsername, user);
        return true;
    }
    
    public synchronized List<String> getUsernames() {
        return users.keySet().stream().toList();
    }
    
    public synchronized NameValidator getNameValidator() {
        NameValidator nv = new NameValidator();
        nv.setUsedNames(getUsernames());
        return nv;
    }
    
    public synchronized void saveToFile() throws Exception {
        File file = new File("users.bin");
        
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(users);
        }
    }
    
    public synchronized void loadFromFile() throws Exception {
        File file = new File("users.bin");
        
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            users = (HashMap<String, User>) in.readObject();
        }
    }
}
