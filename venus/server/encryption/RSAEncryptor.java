package venus.server.encryption;

import java.security.Key;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class RSAEncryptor implements Encryptor {
    
    private final Cipher cipher;
    
    public RSAEncryptor(final Key k) throws Exception {
       cipher = Cipher.getInstance("RSA");
       cipher.init(Cipher.ENCRYPT_MODE, k);
    }
    
    public String encrypt(String msg) {
        try {
            var bytes = Base64.getDecoder().decode(msg);
            return Base64.getEncoder().encodeToString(cipher.doFinal(bytes));
        } catch (BadPaddingException | IllegalBlockSizeException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
        return null;
    }
    
    
}

