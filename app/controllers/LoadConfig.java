package controllers;

import java.util.List;
import play.Configuration;
import play.Play;

/**
 *
 * @author Gilles Baatz
 */
public class LoadConfig {
    
    private static Configuration config = Play.application().configuration();
    
    public static String loadStringFromConfig(String key) {
        return config.getString(key);
    }
    
    public static List<String> loadStringListFromConfig(String key){
        return config.getStringList(key);
    }
}
