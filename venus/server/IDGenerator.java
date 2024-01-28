package venus.server;

import java.util.*;

public final class IDGenerator {
    
    private static final Random random = new Random();
    private static final List<String> used = new ArrayList<>();
    
    private static final int ID_BIT_LENGTH = 256;
    private static final int ID_BYTE_LENGTH = ID_BIT_LENGTH / Byte.SIZE;
    
    private IDGenerator() {}
    
    private static String generateId() {
        byte[] bytes = new byte[ID_BYTE_LENGTH];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
    
    public static String genUniqueId() {
        String id = null;
        
        do {
            id = generateId();
        } while (used.contains(id));
        
        used.add(id);
        
        return id;
    }
    
}
