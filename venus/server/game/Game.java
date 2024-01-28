package venus.server.game;

import java.awt.Color;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import javax.swing.Timer;
import venus.server.ClientService;
import venus.server.Requests;
import venus.server.Utils;
import venus.server.VenusServer;
import venus.server.message.Message;
import venus.server.message.ServerMessage;
import venus.server.nch.ChallengeInfo;

public class Game {
    
    private final ChallengeInfo info;
    private int currentPlayer;
    
    private int timeRemaining;
    private Timer timer;
    
    private MatrixAnalyser analyser;
    
    private static final int width = 7;
    private static final int height = 6;
    private final int[][] matrix = new int[height][width];
    
    private ClientService[] services = new ClientService[2];
    
    private boolean end = false;
    
    private boolean closed = false;
    
    public Game(ChallengeInfo info) {
        this.info = info;
        currentPlayer = selectPlayer();
        
        analyser = new MatrixAnalyser(matrix);
        
        services[0] = VenusServer.findServiceByUsername(info.getChallenger());
        services[1] = VenusServer.findServiceByUsername(info.getChallenged());
        
        timeRemaining = info.getTurnTime();
        
        timer = new Timer(1_000, (e) -> tick());
        timer.start();
    }
    
    private void tick() {
        timeRemaining--;
        
        if (timeRemaining <= 0) {
            makeRandomMove();
        }
    }
    
    private void makeRandomMove() {
        Random random = new Random();
        
        int col = -1;
        int row = -1;
        
        while (row == -1) {
            col = random.nextInt(width);
            row = findEmptySpot(col);
        }
        
        matrix[row][col] = currentPlayer;
    }
    
    private synchronized void endGame(ServerMessage msg) {
        incrementGameCount();
        send(msg);
        end = true;
    }
    
    private synchronized void checkForWinner() {
        if (analyser.hasWinner()) {
            String winner = translatePlayerName(analyser.getWinner());
            incrementWinCount(winner);
            endGame(new Message(Requests.WINNER, winner));
            return;
        }
        
        if (analyser.hasTie()) {
            incrementWinCount();
            endGame(new Message(Requests.TIE));
        }
    }
    
    public synchronized void quit(String quitter) {
        String winner = findOppositePlayer(quitter, getPlayers());
        incrementWinCount(winner);
        endGame(new Message(Requests.WINNER, winner));
    }
    
    private String findOppositePlayer(String name, String[] players, int index) {
        if (index == 2) {
            throw new IllegalArgumentException("There should only be 2 names!");
        }
        
        if (players[index].equals(name)) {
            return findOppositePlayer(name, players, index + 1);
        }
        
        return players[index];
    }
    
    private String findOppositePlayer(String name, String[] players) {
        return findOppositePlayer(name, players, 0);
    }
    
    private void incrementGameCount() {
       Arrays.stream(getPlayers()).forEach(x -> incrementGameCount(x));
    }
    
    private void incrementWinCount() {
        Arrays.stream(getPlayers()).forEach(x -> incrementWinCount(x));
    }
    
    private void incrementWinCount(String winner) {
        VenusServer.getUserManager().getUser(winner).getPlayer().incrementWinCount();
    }
    
    private void incrementGameCount(String user) {
        VenusServer.getUserManager().getUser(user).getPlayer().incrementGameCount();
    }
    
    private synchronized void resetTimer() {
        timeRemaining = info.getTurnTime();
    }
    
    private synchronized void togglePlayer() {
        resetTimer();
        if (currentPlayer == 1) {
            currentPlayer = 2;
            return;
        }
        currentPlayer = 1;
    }
    
    private void end() {
        Arrays.stream(services).forEach(x -> x.clearCG());
    }
    
    public boolean isClosed() {
        return closed;
    }
    
    private int selectPlayer() {
        return new Random().nextBoolean() ? 1 : 2;
    }
    
    private String translatePlayerName(int x) {
        if (x == 1) {
            return info.getChallenger();
        }
        return info.getChallenged();
    }
    
    private int findEmptySpot(int column, int y) {
        if (y == -1 || matrix[y][column] == 0)
            return y;
        return findEmptySpot(column, y - 1);
    }
    
    private int findEmptySpot(int column) {
        return findEmptySpot(column, matrix.length - 1);
    }
    
    public Color colorFor(int player) {
        return player == 1 ? info.getChallengerColor() : info.getChallengedColor();
    }
    
    public boolean placeCoin(int column) {
        int y = findEmptySpot(column);
        
        if (y == -1)
            return false;
        
        
        makeMove(y, column);
        return true;
    }
    
    private synchronized void makeMove(int i, int j) {
        matrix[i][j] = currentPlayer;
        togglePlayer();
        resetTimer();
        analyser.analyse();
        checkForWinner();
    }
    
    public void send(ServerMessage msg) {
        services[0].send(msg);
        services[1].send(msg);
    }
    
    public String getChallenger() {
        return info.getChallenger();
    }
    
    public String getChallenged() {
        return info.getChallenged();
    }
    
    public String[] getPlayers() {
        return new String[] {info.getChallenger(), info.getChallenged()};
    }
    
    void errclose() {
        send(new Message(Requests.CLOSE_GAME));
        closed = true;
    }
    
    void close() {
        if (!end) {
            errclose();
            return;
        }
        closed = true;
    }
    
    private synchronized String getMatrixData() {
        byte[] b = new byte[width * height];
        int count = 0;
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                b[count++] = (byte) matrix[i][j];
            }
        }
        
        return Base64.getEncoder().encodeToString(b);
    }
    
    //format: p1 name / p2 name / current p name / remaining turn time / turn time / board data / p1 color / p2 color
    public synchronized ServerMessage getGameData(String username) {
        ServerMessage msg = new Message(Requests.GAME_DATA,
                info.getChallenger(),
                info.getChallenged(),
                translatePlayerName(currentPlayer), 
                String.valueOf(timeRemaining),
                String.valueOf(info.getTurnTime()),
                getMatrixData(),
                Utils.encodeColor(info.getChallengerColor()),
                Utils.encodeColor(info.getChallengedColor()),
                username);
        return msg;
    }
    
    public static int[][] decodeMatrixData(String s) {
        int[][] m = new int[height][width];
        byte[] barr = Base64.getDecoder().decode(s);
        
        for (int i = 0; i < barr.length; i++) {
            int y = i / width;
            int x = i % width;
            
            m[y][x] = barr[i];
        }
        
        return m;
    }
    
}
