package venus.server.encryption;

import java.security.Key;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class AESDecryptor implements Decryptor {

    private Cipher cipher;
    
    public AESDecryptor(final Key key) throws Exception {
        cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
    }
    
    @Override
    public String decrypt(String msg) {
        try {
            var bytes = Base64.getDecoder().decode(msg);
            return new String(cipher.doFinal(bytes), EncryptionManager.CHARACTER_SET);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }
    
}
