package controllers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import play.Logger;

/**
 *
 * @author Gilles Baatz
 */
public class HtdigestFileParser {

    public static String getHA1(String filepath, String username, String realm) {
        BufferedReader br = null;
        String HA1ofUser = null;
        try {
            br = new BufferedReader(new FileReader(filepath));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splittedLine = line.split(":");
                if (splittedLine[0].equals(username) && splittedLine[1].equals(realm)) {
                    HA1ofUser = splittedLine[2];
                    break;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.error("File not Found.", ex);
        } catch (IOException ex) {
            Logger.error("Problems while reading the file", ex);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                Logger.error("Problems while closing the file", ex);
            }
        }
        return HA1ofUser;
    }
}
