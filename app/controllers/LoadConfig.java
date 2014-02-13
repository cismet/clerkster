package controllers;

import com.typesafe.config.ConfigFactory;

/**
 *
 * @author Gilles Baatz
 */
public class LoadConfig {

    public static String loadOwnConf(String key) {
        return ConfigFactory.load("certificate.conf").getString(key);
    }
}
