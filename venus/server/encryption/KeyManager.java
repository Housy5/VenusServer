package venus.server.encryption;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public final class KeyManager {

    private PrivateKey privKey;
    private PublicKey pubKey;
    private SecretKey aesKey;

    public void init() throws Exception {
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(4096);
        var pair = generator.genKeyPair();
        privKey = pair.getPrivate();
        pubKey = pair.getPublic();
        
        var aesGenerator = KeyGenerator.getInstance("AES");
        aesGenerator.init(256);
        aesKey = aesGenerator.generateKey();
        
        save();
    }

    public PrivateKey getPrivateKey() {
        return privKey;
    }

    public PublicKey getPublicKey() {
        return pubKey;
    }

    public SecretKey getAESKey() {
        return aesKey;
    }
    
    private void savePublicKey() throws IOException {
        var key = pubKey;

        try (var out = new FileOutputStream("public.key")) {
            out.write(key.getEncoded());
        }
    }

    private void savePrivateKey() throws IOException {
        var key = privKey;

        try (var out = new FileOutputStream("private.key")) {
            out.write(key.getEncoded());
        }
    }
    
    private void saveAESKey() throws IOException {
        var key = aesKey;
        
        try (var out = new FileOutputStream("aes.key")) {
            out.write(key.getEncoded());
        }
    }

    private void loadPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        File keyFile = new File("private.key");
        byte[] bytes = Files.readAllBytes(keyFile.toPath());

        privKey = decodePrivateKey(bytes);
    }

    private void loadPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        File keyFile = new File("public.key");
        byte[] bytes = Files.readAllBytes(keyFile.toPath());

        pubKey = decodePublicKey(bytes);
    }

    private void loadAESKey() throws NoSuchAlgorithmException, IOException {
        File keyFile = new File("aes.key");
        byte[] bytes = Files.readAllBytes(keyFile.toPath());
        
        aesKey = decodeAESKey(bytes);
    }
    
    public void save() throws Exception {
        savePrivateKey();
        savePublicKey();
        saveAESKey();
    }

    public void load() throws Exception, IOException {
        loadPrivateKey();
        loadPublicKey();
        loadAESKey();
    }

    public static PublicKey decodePublicKey(byte[] b) throws NoSuchAlgorithmException, InvalidKeySpecException {
        var factory = KeyFactory.getInstance("RSA");
        var keySpec = new X509EncodedKeySpec(b);
        return factory.generatePublic(keySpec);
    }

    public static PrivateKey decodePrivateKey(byte[] b) throws NoSuchAlgorithmException, InvalidKeySpecException  {
        var factory = KeyFactory.getInstance("RSA");
        var keySpec = new PKCS8EncodedKeySpec(b);
        return factory.generatePrivate(keySpec);
    }
    
    public static SecretKey decodeAESKey(byte[] b) throws NoSuchAlgorithmException {
        return new SecretKeySpec(b, "AES");
    }
}
