package venus.server.challenge;

import java.awt.Color;
import java.util.Objects;
import venus.server.IDGenerator;
import venus.server.game.Player;

public class Challenge {
    
    private Player p1, p2;
    private Color c1, c2;
    private int time;
    private final String id;
    
    public Challenge() {
        id = IDGenerator.genUniqueId();
    }
    
    public void player1(Player p1) {
        this.p1 = p1;
    }
    
    public Player player1() {
        return p1;
    }
    
    public void player2(Player p2) {
        this.p2 = p2;
    }
    
    public Player player2() {
        return p2;
    }
    
    public void color1(Color c) {
        c1 = c;
    }
    
    public Color color1() {
        return c1;
    }
    
    public void color2(Color c) {
        c2 = c;
    }
    
    public Color color2() {
        return c2;
    }
    
    public String id() {
        return id;
    }
    
    public int time() {
        return time;
    }
    
    public void time(int time) {
        this.time = time;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.p1);
        hash = 97 * hash + Objects.hashCode(this.p2);
        hash = 97 * hash + Objects.hashCode(this.c1);
        hash = 97 * hash + Objects.hashCode(this.c2);
        hash = 97 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Challenge other = (Challenge) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.p1, other.p1)) {
            return false;
        }
        if (!Objects.equals(this.p2, other.p2)) {
            return false;
        }
        if (!Objects.equals(this.c1, other.c1)) {
            return false;
        }
        return Objects.equals(this.c2, other.c2);
    }

    
} 
