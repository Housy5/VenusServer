package venus.server;

import java.util.*;

public class NameValidator {
    
    public List<String> usedNames = Collections.synchronizedList(new ArrayList<>());

    private final String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 _-";
    
    public List<String> getUsedNames() {
        return usedNames;
    }

    public void setUsedNames(List<String> usedNames) {
        this.usedNames = usedNames;
    }

    public boolean add(String e) {
        if (!isUnique(e))
            return false;
        return usedNames.add(e);
    }

    public boolean remove(Object o) {
        return usedNames.remove(o);
    }

    public void clear() {
        usedNames.clear();
    }
    
    //Note alphabetic not to THE alphabet but to the alphabet defined above.
    private boolean isAlphabetic(String alphabet, char target) {
        return alphabet.chars().anyMatch(x -> x == target);
    }
    
    private boolean isClean(String n) {
        return !n.isBlank() && !n.chars().parallel().mapToObj(x -> (boolean) isAlphabetic(alphabet, (char) x)).anyMatch(x -> x == false);
    }
    
    private boolean isUnique(String n) {
        return !usedNames.parallelStream().anyMatch(x -> x.equalsIgnoreCase(n));
    }
    
    public boolean isValidName(String name) {
        boolean result = isClean(name);
        result &= isUnique(name);
        return result;
    }
}
