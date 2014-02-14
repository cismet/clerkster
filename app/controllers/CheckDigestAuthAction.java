package controllers;

import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import java.util.UUID;

/**
 *
 * @author Gilles Baatz
 */
public class CheckDigestAuthAction extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context cntxt) {
        Logger.info("performing Authentification on request");
        DigestRequest req = new DigestRequest(cntxt.request());
        if (req.isAuthorized()) {
            return req.getUsername();
        }
        return null;
    }

    @Override
    public Result onUnauthorized(Http.Context cntxt) {
        Logger.info("Authentification was not successful, sending digest information back");
        Http.Response response = cntxt.response();
        String realm = LoadConfig.loadOwnConf("de.cismet.realm.sign_jar");
        String auth = "Digest realm=" + realm + ", nonce=" + UUID.randomUUID();
        response.setHeader("WWW-Authenticate", auth);
        Logger.info("CheckDigestAuthAction - onUnauthorized");
        return status(Http.Status.UNAUTHORIZED, "No permissions");
    }
}
