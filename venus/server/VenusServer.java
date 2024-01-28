package venus.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import venus.server.encryption.AESDecryptor;
import venus.server.encryption.AESEncryptor;
import venus.server.encryption.Decryptor;
import venus.server.encryption.RSADecryptor;
import venus.server.encryption.EncryptionManager;
import venus.server.encryption.Encryptor;
import venus.server.encryption.RSAEncryptor;
import venus.server.encryption.KeyManager;
import venus.server.event.EventCenter;
import venus.server.game.Lobby;
import venus.server.message.ServerMessage;
import venus.server.user.UserManager;

public class VenusServer {

    private static final String sep = ServerConstants.SEP_STR;

    private static final int MAX_CONNECTIONS = 10;
    private static final ClientService[] services = new ClientService[MAX_CONNECTIONS];

    private static final int NO_FREE_SLOTS = -1;
    public final static int PORT = 2_500;

    private static KeyManager keyManager;
    
    private static UserManager users; 
    
    private static AutoSaver autoSaver;
    
    private static Lobby lobby;
    
    static {
        lobby = new Lobby();
    }
    
    private static int findFreeSlot() {
        synchronized (services) {
            for (int i = 0; i < services.length; i++) {
                if (services[i] == null) {
                    return i;
                }
            }
            
            return NO_FREE_SLOTS;
        }
    }

    private static boolean freeSlot(final int index) {
        synchronized (services) {
            try {
                if (services[index] == null) {
                    return true;
                }
                
                services[index].close();
                services[index] = null;
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }
    
    public static UserManager getUserManager() {
        return users;
    }
    
    public static Lobby getLobby() {
        return lobby;
    }
    
    public static void broadcast(final ServerMessage message) {
        synchronized (services) {
            Arrays.stream(services)
                    .filter(x -> x != null)
                    .forEach(x -> x.send(message));
        }
    }
    
    public static ClientService findServiceByUsername(String username) {
        synchronized (services) {
            return Arrays.stream(services)
                    .parallel()
                    .filter(x -> x != null)
                    .filter(x -> x.getUser().getUsername().equals(username))
                    .findFirst()
                    .orElse(null);
        }
    }
    
    public static boolean sendTo(final String username, final ServerMessage msg) {
        synchronized (services) {
            var client = findServiceByUsername(username);
            
            if (client == null) {
                return false;
            }
            
            client.send(msg);
            return true;
        }
    }

    public static void end(int id) {
        log("Stopping service: " + id + ".");
        if (!freeSlot(id)) {
            log("Failed to stop service: " + id + ".");
            return;
        }
        log("Stopped and freed service: " + id + ".");
    }

    public static synchronized void log(String msg) {
        System.out.println("[Server] " + msg);
    }

    public static boolean addClient(ClientService cs) {
        synchronized (services) {
            int id = findFreeSlot();
            
            if (id == -1) {
                log("Server full, connection refused.");
                //cs.send("refused");
                return false;
            }
            
            services[id] = cs;
            cs.setServiceId(id);
            
            log("New services created with id: " + id + ".");
            
            log("Service: " + id + ", started.");
            return true;
        }
    }

    private static void doGreeting(ClientService cs) {
        long millis = System.currentTimeMillis();

        while (!cs.isGreeted()) {
            long current = System.currentTimeMillis() - millis;
            if (current >= 5_000) {
                log("Connection timed out.");
                return;
            }
        }

        addClient(cs);
    }
    
    public static KeyManager getKeyManager() {
        return keyManager;
    }
    
    private static void regenKeyManager() {
        try {
            keyManager = new KeyManager();
            keyManager.init();
        } catch (Exception ex) {
            log("Something went wrong during the regeneration of the keys!");
            log("Shutting down...");
            System.exit(-1);
        }
    }

    private static void initKeyManager() {
        try {
            keyManager = new KeyManager();
            keyManager.load();
        } catch (IOException ex) {
            regenKeyManager();
        } catch (Exception e) {
            e.printStackTrace();
            log("Something went wrong during the construction of the keys!");
            log("Shutting down...");
            System.exit(-1);
        }
    }
    
    private static void initUserManager() {
        try {
            users = new UserManager();
            users.loadFromFile();
        } catch (Exception ex) {
            ex.printStackTrace();
            log("Failed to initialize the User manager from a file.");
            log("Starting with an empty user manager.");
        }
    }
    
    private static void initSerialManager() {
        EventCenter.getInstance().addObserver(EventManager.getInstance());
    }
    
    private static void initAutoSaver() {
        autoSaver = new AutoSaver(users);
        autoSaver.start();
    }
    
    private static void initEncryptionManager() {
        try {
            Encryptor keyEncryptor = new RSAEncryptor(keyManager.getPrivateKey());
            Decryptor keyDecryptor = new RSADecryptor(keyManager.getPrivateKey());
            
            Encryptor generalEncryptor = new AESEncryptor(keyManager.getAESKey());
            Decryptor generalDecryptor = new AESDecryptor(keyManager.getAESKey());
            
            EncryptionManager.setKeyEncryptor(keyEncryptor);
            EncryptionManager.setKeyDecryptor(keyDecryptor);
            
            EncryptionManager.setGeneralDecryptor(generalDecryptor);
            EncryptionManager.setGeneralEncryptor(generalEncryptor);
        } catch (Exception ex) {
            log("Couldn't initialize the encryptors.");
            log("Shutting down...");
            System.exit(-1);
        }
    }
    
    public static void main(String[] args) throws IOException {
        log("Initializing the user manager...");
        initUserManager();
        initSerialManager();
        initAutoSaver();
        
        log("Initializing the key manager...");
        initKeyManager();

        log("Initializing the encryptors...");
        initEncryptionManager();
                
        log("Starting...");
        ServerSocket socket = new ServerSocket(PORT);

        log("Listening for connections...");
        while (true) {
            Socket client = socket.accept();
            
            ClientService cs = new ClientService(client);
            cs.start();
            log("Connection from: " + client.getInetAddress().getHostAddress() + ".");

            new Thread(() -> {
                doGreeting(cs);
            }).start();
        }
    }

}
