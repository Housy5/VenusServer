package venus.server.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class StringHasher {
    
    private static MessageDigest messageDigest;

    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static String hash(String input) {
        if (messageDigest == null) {
            throw new IllegalStateException("MessageDigest not initialized.");
        }

        byte[] hashBytes = messageDigest.digest(input.getBytes());
        
        return Base64.getEncoder().encodeToString(hashBytes);
    }
}
