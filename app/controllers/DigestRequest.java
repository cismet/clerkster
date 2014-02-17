package controllers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.api.libs.Codecs;


import play.mvc.Http.Request;

public class DigestRequest {

    private Map<String, String> params = new HashMap<String, String>();
    private Request request;

    public DigestRequest(Request request) {
        this.request = request;
    }

    public boolean isAuthorized() {
        return this.isValid() && this.compareResponse();
    }

    public String getUsername() {
        if (params.containsKey("username")) {
            return params.get("username");
        } else {
            return null;
        }
    }

    private boolean isValid() {
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

    private boolean compareResponse() {
        String htdigestLocation = LoadConfig.loadStringFromConfig("de.cismet.users.htdigest-file");
        String ha1 = HtdigestFileParser.getHA1(htdigestLocation, params.get("username"), params.get("realm"));
        if (ha1 == null) {
            return false;
            //throw new UnauthorizedDigest(params.get("realm"));
        }

        String digest = createDigest(ha1);
        return digest.equals(params.get("response"));
    }

    private String createDigest(String ha1) {
        String digest1 = ha1; //md5(username + ":" + realm + ":" + pass)
        String digest2 = Codecs.md5((request.method() + ":" + params.get("uri")).getBytes());
        String digest3 = Codecs.md5((digest1 + ":" + params.get("nonce") + ":" + digest2).getBytes());
        Logger.debug("digest1: " + digest1);
        Logger.debug("digest2: " + digest2);
        Logger.debug("digest3: " + digest3);
        return digest3;
    }
}
