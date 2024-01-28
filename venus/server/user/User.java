package venus.server.user;

import java.io.Serializable;
import java.util.Objects;
import venus.server.game.Player;

public class User implements Serializable {
    
    private String username;
    private Password password;
    private Player player;
        
    public User(String username, String password) {
        this.username = username;
        this.password = new Password(password);
        player = new Player(username);
    }

    public String getUsername() {
        return username;
    }
    
    void setUsername(String newUser) {
        username = newUser;
    }

    public Password getPassword() {
        return password;
    }
    
    void changePassword(String newPass) {
        password = new Password(newPass);
    }

    public Player getPlayer() {
        return player;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.username);
        hash = 71 * hash + Objects.hashCode(this.password);
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
        final User other = (User) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        return Objects.equals(this.password, other.password);
    }
}
