package venus.server.encryption;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

public class EncryptionManager {
    
    public static final Charset CHARACTER_SET = StandardCharsets.UTF_16;
    
    private static Encryptor keyEncryptor;
    private static Decryptor keyDecryptor;

    private static Encryptor encryptor;
    private static Decryptor decryptor;
    
    public static void setKeyEncryptor(Encryptor encryptor) {
        keyEncryptor = encryptor;
    }
    
    public static void setKeyDecryptor(Decryptor decryptor) {
        keyDecryptor = decryptor; 
    }
    
    public static void setGeneralEncryptor(Encryptor encry) {
        encryptor = encry;
    }
    
    public static void setGeneralDecryptor(Decryptor decry) {
        decryptor = decry;
    }
    
    public static String encryptKey(Key key) {
        var encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        return keyEncryptor.encrypt(encodedKey);
    }
    
    public static String decryptKey(String msg) {
        return keyDecryptor.decrypt(msg);
    }
    
    public static String encrypt(String data) {
        return encryptor.encrypt(data);
    }
    
    public static String decrypt(String data) {
        return decryptor.decrypt(data);
    }
}
