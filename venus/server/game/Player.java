package venus.server.game;

import java.awt.Color;
import java.io.Serializable;
import venus.server.ClientService;

public class Player implements Serializable {

    private String name;
    private Color color;

    private int wins = 0;
    private int games = 0;
        
    private transient ClientService cs;

    public Player(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public ClientService getClientService() {
        return cs;
    }
    
    public void setClientService(ClientService cs) {
        this.cs = cs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getWinCount() {
        return wins;
    }

    public int getGameCount() {
        return games;
    }

    public void incrementGameCount() {
        games++;
    }

    public void incrementWinCount() {
        wins++;
    }
}
