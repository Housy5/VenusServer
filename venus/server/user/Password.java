package venus.server.user;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Base64;
import venus.server.encryption.StringHasher;

public class Password implements Serializable {

    private final int SALT_LENGTH = 20;
    
    private String hash;
    private String salt;
    
    public Password(String pass) {
        salt = generateSalt(SALT_LENGTH);
        hash = hash(pass);
    }
    
    private String hash(String pass) {
        String ihash = StringHasher.hash(pass);
        return StringHasher.hash(ihash + salt);
    }
    
    private String generateSalt(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Salt length must be a positive integer.");
        }

        int byteLength = (int) Math.ceil((length * 3.0) / 4.0);
        byte[] salt = new byte[byteLength];
        new SecureRandom().nextBytes(salt);

        return Base64.getEncoder().encodeToString(salt);
    }
    
    public boolean unlock(String pass) {
        String other = hash(pass);
        return other.equals(hash);
    }
}
