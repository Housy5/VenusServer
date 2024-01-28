package venus.server.encryption;

import java.security.Key;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public final class RSADecryptor implements Decryptor {

    private final Cipher cipher;

    public RSADecryptor(final Key k) throws Exception {
        cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, k);
    }

    public String decrypt(final String s) {
        try {
            var ebytes = cipher.doFinal(Base64.getDecoder().decode(s));
            return Base64.getEncoder().encodeToString(ebytes);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }
}
