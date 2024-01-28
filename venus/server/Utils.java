package venus.server;

import java.awt.Color;
import java.util.List;

public class Utils {
    
    public static String formatForTransmission(List<String> strings) {
        if (strings.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder(strings.size() * 2 - 1);
        
        for (String str : strings) {
            if (!sb.isEmpty()) {
                sb.append(ServerConstants.SEPARATOR);
            }
            
            sb.append(str);
        }
        
        return sb.toString();
    }
    
    public static String timeToString(final int time) {
        var result = "";
        var minutes = time / 60;
        var seconds = time - 60 * minutes;
        
        if (minutes > 0) {
            result += minutes + " min "; 
        }
        
        if (seconds > 0) {
            result += seconds + " sec ";
        }
        
        return result;
    }
    
    public static int getColorCode(Color color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        
        return (red << 16) | (green << 8) | blue;
    }
    
    public static String encodeColor(Color color) {
        return String.valueOf(getColorCode(color));
    }
    
    public static Color decodeColor(String encodedColor) {
        return new Color(Integer.parseInt(encodedColor));
    }
}
