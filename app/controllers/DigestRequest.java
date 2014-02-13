package controllers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import models.User;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.api.libs.Codecs;


import play.mvc.Http;
import play.mvc.Http.Request;

public class DigestRequest {
    
    private Map<String, String> params = new HashMap<String, String>();
    private Request request;
    
    public DigestRequest(Request request) {
        this.request = request;
    }
    
    public boolean isValid() {
        if (!request.headers().containsKey("Authorization")) {
            return false;
        }
        String authString = request.headers().get("Authorization")[0];
        if (StringUtils.isEmpty(authString) || !authString.startsWith("Digest ")) {
            return false;
        }
        for (String keyValuePair : authString.replaceFirst("Digest ", "").split(",")) {
            String data[] = keyValuePair.trim().split("=", 2);
            String key = data[0];
            String value = data[1].replaceAll("\"", "");
            if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
                params.put(key, value);
            }
        }
        return params.containsKey("username") && params.containsKey("realm")
                && params.containsKey("uri") && params.containsKey("nonce")
                && params.containsKey("response");
    }
    
    public boolean isAuthorized() {
        User user = User.findByName(params.get("username"));
        if (user == null) {
            return false;
            //throw new UnauthorizedDigest(params.get("realm"));
        }
        
        String digest = createDigest(user.password);
        return digest.equals(params.get("response"));
    }
    
    private String createDigest(String pass) {
        String username = params.get("username");
        String realm = params.get("realm");
        String digest1 = Codecs.md5((username + ":" + realm + ":" + pass).getBytes());
        String digest2 = Codecs.md5((request.method() + ":" + params.get("uri")).getBytes());
        String digest3 = Codecs.md5((digest1 + ":" + params.get("nonce") + ":" + digest2).getBytes());
        Logger.info("digest1: " + digest1);
        Logger.info("digest2: " + digest2);
        Logger.info("digest3: " + digest3);
        return digest3;
    }
    
    public static boolean isAuthorized(Http.Request request) {
        DigestRequest req = new DigestRequest(request);
        boolean isValid = req.isValid();
        Logger.info("The request is valid: " + isValid);
        
        boolean isAuthorized = req.isAuthorized();
        Logger.info("The request is authorized: " + isAuthorized);
        
        return isValid && isAuthorized;
    }
}
