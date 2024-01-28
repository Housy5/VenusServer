package venus.server.nch;

import java.awt.Color;
import java.util.Objects;
import venus.server.IDGenerator;
import venus.server.Utils;

public class ChallengeInfo {

    private static final int EXPIRE_TIME = 5 * 1000 * 60; // 5 min experation time. 

    private String challenger;
    private String challenged;
    private Color challengerColor;
    private Color challengedColor;
    private String id;
    private long timestamp;
    private int turnTime;

    private ChallengeInfo() {}
    
    public ChallengeInfo(String challenger, Color chColor, int t) {
        this.challenger = challenger;
        this.challengerColor = chColor;
        this.id = IDGenerator.genUniqueId();
        this.timestamp = System.currentTimeMillis();
        this.turnTime = t;
    }

    public int getTurnTime() {
        return turnTime;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - timestamp >= EXPIRE_TIME;
    }

    public String getChallenged() {
        return challenged;
    }

    public void setChallenged(String challenged) {
        this.challenged = challenged;
    }

    public Color getChallengedColor() {
        return challengedColor;
    }

    public void setChallengedColor(Color challengedColor) {
        this.challengedColor = challengedColor;
    }

    public String getChallenger() {
        return challenger;
    }

    public Color getChallengerColor() {
        return challengerColor;
    }

    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.challenger);
        hash = 59 * hash + Objects.hashCode(this.challenged);
        hash = 59 * hash + Objects.hashCode(this.challengerColor);
        hash = 59 * hash + Objects.hashCode(this.challengedColor);
        hash = 59 * hash + Objects.hashCode(this.id);
        hash = 59 * hash + (int) (this.timestamp ^ (this.timestamp >>> 32));
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
        final ChallengeInfo other = (ChallengeInfo) obj;

        if (Objects.equals(this.id, other.id)) {
            return true;
        }
        if (!Objects.equals(this.challenger, other.challenger)) {
            return false;
        }
        if (!Objects.equals(this.challenged, other.challenged)) {
            return false;
        }
        if (!Objects.equals(this.challengerColor, other.challengerColor)) {
            return false;
        }
        if (this.turnTime != other.turnTime) {
            return false;
        }
        return Objects.equals(this.challengedColor, other.challengedColor);
    }

    //challenger name, challenger color, id, time.
    public String getTransitData() {
        return challenger
                + ","
                + Utils.encodeColor(challengerColor)
                + ","
                + id
                + ","
                + String.valueOf(this.turnTime);
    }

    public static ChallengeInfo from(String td) {
        var tokens = td.split(",");

        if (tokens.length != 4) {
            throw new IllegalArgumentException("Can't parse challenge info from: " + td + ".");
        }

        ChallengeInfo info = new ChallengeInfo();
        info.challenger = tokens[0];
        info.challengerColor = Utils.decodeColor(tokens[1]);
        info.id = tokens[2];
        info.turnTime = Integer.parseInt(tokens[3]);
        
        return info;
    }
}
