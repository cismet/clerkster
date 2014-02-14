package controllers;

import play.Play;

/**
 *
 * @author Gilles Baatz
 */
public class LoadConfig {
    
    public static String loadOwnConf(String key) {
        return Play.application().configuration().getString(key);
    }
}
