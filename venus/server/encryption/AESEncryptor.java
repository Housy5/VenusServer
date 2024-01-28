package venus.server.encryption;

import java.security.Key;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class AESEncryptor implements Encryptor {
    
    private final Cipher cipher;
    
    public AESEncryptor(final Key key) throws Exception {
        cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
    } 

    @Override
    public String encrypt(String data) {
        try {
            var bytes = data.getBytes(EncryptionManager.CHARACTER_SET);
            return Base64.getEncoder().encodeToString(cipher.doFinal(bytes));
        } catch (BadPaddingException | IllegalBlockSizeException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
        return null;
    }
}
