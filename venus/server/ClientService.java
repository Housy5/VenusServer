package venus.server;

import java.awt.Color;
import venus.server.message.ServerMessage;
import venus.server.message.PlainTextMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Base64;
import venus.server.encryption.EncryptionManager;
import venus.server.game.Game;
import venus.server.game.GameManager;
import venus.server.game.Lobby;
import venus.server.message.Message;
import venus.server.nch.ChallengeBook;
import venus.server.user.User;
import venus.server.user.UserManager;

public class ClientService extends Thread {

    private final Socket s;
    private final InputStreamReader input;
    private final PrintStream output;

    private User user = null;
    private volatile int id = -1;
    private volatile boolean greeted = false;
    
    private Game cg;

    public ClientService(final Socket s) throws IOException {
        this.s = s;
        input = new InputStreamReader(s.getInputStream());
        output = new PrintStream(s.getOutputStream());
    }

    public boolean isOpen() {
        return !s.isClosed();
    }

    public void close() throws IOException {
        if (user != null) {
            logout();
        }
        
        input.close();
        output.close();
        s.close();
    }

    private void logout() {
        freeLobby();
        closeGame();
        
        ChallengeBook.instance().removeEverythingFrom(user.getUsername());
        VenusServer.getUserManager().markAsOffline(user.getUsername());
        user = null;
    }

    private void closeGame() {    
        if (cg == null) {
            return;
        }
        
        GameManager.closeGame(cg);
    }
    
    private void freeLobby() {
        Lobby lobby = VenusServer.getLobby();
        if (lobby.contains(user.getUsername())) {
            lobby.removePlayer(user.getPlayer());
        }
    }

    public synchronized void send(ServerMessage msg) {
        output.println(msg);
    }

    public void setServiceId(int id) {
        this.id = id;
    }

    public int getServiceId() {
        return id;
    }

    public User getUser() {
        return user;
    }
    
    public boolean isGreeted() {
        return greeted;
    }

    private String prefix = null;

    public void log(String msg) {
        if (prefix == null) {
            prefix = "[Service: " + id + "]";
        }
        System.out.println(prefix + " " + msg);
    }    

    public void clearCG() {
        GameManager.closeGame(cg);
        cg = null;
    }
    
    public void ensureCG() {
        if (cg == null) {
            cg = GameManager.findGameWith(user.getUsername());
        }
    }
    
    @Override
    public void run() {
        try {
            BufferedReader buffer = new BufferedReader(input);

            String line;

            while ((line = buffer.readLine()) != null) {
                ServerMessage msg = Message.from(line);
                
                if (msg.getMessage().equals(Requests.VALIDATE_USER_NAME)) {
                    String n = msg.getArgument(0);
                    boolean result = VenusServer.getUserManager().getNameValidator().isValidName(n);
                    if (!result) {
                        Message response = new Message(Requests.USER_NAME_INVALID);
                        response.setId(msg.getId());
                        send(response);
                        continue;
                    }
                    
                    Message response = new Message(Requests.USER_NAME_VALID);
                    response.setId(msg.getId());
                    
                    send(response);
                    continue;
                }
                
                if (msg.getMessage().equals(Requests.CREATE_USER)) {
                    String name = msg.getArgument(0);
                    String pass = msg.getArgument(1);
                    
                    UserManager manager = VenusServer.getUserManager();
                    User user = new User(name, pass);
                    manager.addUser(user);
                    
                    Message response = new Message(Requests.USER_CREATED);
                    response.setId(msg.getId());
                    
                    send(response);
                    continue;
                }
                
                if (msg.getMessage().equals(Requests.LOGIN_USER)) {
                    String name = msg.getArgument(0);
                    String pass = msg.getArgument(1);
                                        
                    UserManager manager = VenusServer.getUserManager();
                    
                    if (manager.isOnline(name)) {
                        Message err = new Message(Requests.LOGIN_ALREADY_ONLINE);
                        err.setId(msg.getId());
                        
                        send(err);
                        continue;
                    }
                    
                    String res = Requests.LOGIN_FAILED;
                    
                    if (manager.authenticateUser(name, pass)) {
                        res = Requests.LOGIN_SUCCESS;
                        manager.markAsOnline(name);
                        user = manager.getUser(name);
                        var player = user.getPlayer();
                        player.setClientService(this);
                        VenusServer.getLobby().addPlayer(player);
                    }
                    
                    Message response = new Message(res);
                    response.setId(msg.getId());
                    
                    send(response);
                    continue;
                }

                if (msg.getMessage().equals(Requests.GREET)) {
                    greeted = true;
                    continue;
                }
                
                if (msg.getMessage().equals(Requests.PUBLIC_KEY_REQUEST)) {
                    String key = Base64.getEncoder().encodeToString(VenusServer.getKeyManager().getPublicKey().getEncoded());
                    var response = new PlainTextMessage(Requests.PUBLIC_KEY, key);
                    response.setId(msg.getId());
                    send(response);
                    continue;
                }
                
                if (msg.getMessage().equals(Requests.REQUEST_ONLINE_LIST)) {
                    var names = VenusServer.getLobby().getPlayerNames();
                    String formattedList = Utils.formatForTransmission(names);
                    
                    Message response = new Message(Requests.UPDATE_ONLINE_LIST, formattedList);
                    response.setId(msg.getId());
                    
                    send(response);
                    continue;
                }
                
                if (msg.getMessage().equals(Requests.USER_NAME_REQUEST)) {
                    if (user == null) {
                        log("Not logged in.");
                        continue;
                    }
                    
                    Message response = new Message(Requests.USER_NAME_REQUEST, user.getUsername());
                    response.setId(msg.getId());
                    
                    send(response);
                    continue;
                }
                
                if(msg.getMessage().equals(Requests.GAME_COUNT_REQUEST)) {
                    if(user == null) {
                        log("Not logged in.");
                        continue;
                    }
                    
                    String gameCount = String.valueOf(user.getPlayer().getGameCount());
                    Message response = new Message(Requests.GAME_COUNT_REQUEST, gameCount);
                    response.setId(msg.getId());
                    
                    send(response);
                    continue;
                }
                
                if (msg.getMessage().equals(Requests.WIN_COUNT_REQUEST)) {
                    if (user == null) {
                        log("Not logged in.");
                        continue;
                    }
                    
                    String winCount = String.valueOf(user.getPlayer().getWinCount());
                    Message response = new Message(Requests.WIN_COUNT_REQUEST, winCount);
                    response.setId(msg.getId());
                    
                    send(response);
                    continue;
                }
                
                if (msg.getMessage().equals(Requests.LOGOUT)) {
                    logout();
                    continue;
                }
                
                if (msg.getMessage().equals(Requests.CHALLENGE_USER)) {
                    String otherName = msg.getArgument(0);
                    Color color = Utils.decodeColor(msg.getArgument(1));
                    int time = Integer.parseInt(msg.getArgument(2));
                    
                    var userManager = VenusServer.getUserManager();
                    
                    if (!userManager.isOnline(otherName)) {
                        Message err = new Message(Requests.USER_OFFLINE);
                        err.setId(msg.getId());
                        
                        send(err);
                        continue;
                    }
                    
                    var info = ChallengeBook.instance().createChallenge(user.getUsername(), otherName, color, time);
                    
                    if (info == null) {
                        continue;
                    }
                    
                    continue;
                }
                
                if (msg.getMessage().equals(Requests.CHALLENGES_UPDATE)) {
                    var challenges = ChallengeBook.instance().getReceivedChallenges(user.getUsername());
                    if (challenges.isEmpty()) {
                        var err = new Message(Requests.NO_CHALLENGES);
                        err.setId(msg.getId());
                        send(err);
                        continue;
                    }
                    var formats = challenges.stream().map(x -> x.getTransitData()).toArray(String[]::new);
                    var response = new Message(Requests.CHALLENGES_UPDATE, formats);
                    response.setId(msg.getId());
                    send(response);
                    continue;
                }
                
                if (msg.getMessage().equals(Requests.AES_KEY_REQUEST)) {
                    var key = VenusServer.getKeyManager().getAESKey();
                    var encryptedKey = EncryptionManager.encryptKey(key);
                    PlainTextMessage response = new PlainTextMessage(Requests.AES_KEY, encryptedKey);
                    response.setId(msg.getId());
                    send(response);
                    continue;
                }
                
                if (msg.getMessage().equals(Requests.CHALLENGE_DECLINED)) {
                    var manager = ChallengeBook.instance();
                    manager.removeChallenge(msg.getArgument(0));
                    continue;
                }
                
                
                if (msg.getMessage().equals(Requests.CHALLENGE_ACCEPT)) {
                    var manager = ChallengeBook.instance();
                    var info = manager.getChallenge(msg.getArgument(1));
                    
                    if (info == null) {
                        var err = new Message(Requests.USER_UNAVAILABLE);
                        err.setId(msg.getId());
                        send(err);
                        continue;
                    }
                    
                    info.setChallengedColor(Utils.decodeColor(msg.getArgument(0)));
                    
                    manager.removeEverythingFrom(info.getChallenged());
                    manager.removeEverythingFrom(info.getChallenger());
                    
                    cg = GameManager.startGame(info);
                    
                    //Unlocking the pending status request.
                    var res = new Message(Requests.CHALLENGE_ACCEPT);
                    res.setId(msg.getId());
                    send(res);
                    
                    //starting the game
                    var interrupt = new Message(Requests.START_GAME, info.getTransitData());
                    send(interrupt);
                    
                    VenusServer.sendTo(info.getChallenger(), interrupt);
                }
                
                if (msg.getMessage().equals(Requests.GAME_DATA)) {
                    ensureCG();
                    
                    if (cg == null) {
                        System.out.println("No active game!");
                        return;
                    }
                    
                    ServerMessage response = cg.getGameData(user.getUsername());
                    ((Message) response).setId(msg.getId());
                    send(response);
                }
                
                if (msg.getMessage().equals(Requests.PLACE_COIN)) {
                    ensureCG();
                    cg.placeCoin(Integer.parseInt(msg.getArgument(0)));
                }
                
                if (msg.getMessage().equals(Requests.QUIT_GAME)) {
                    
                }
                
            }

            VenusServer.end(id);
        } catch (Exception ex) {
            ex.printStackTrace();
            log("Error!");
            VenusServer.end(id);
        }
    }

    public Socket getSocket() {
        return s;
    }
}
