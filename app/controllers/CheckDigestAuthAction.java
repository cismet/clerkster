/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class CheckDigestAuthAction extends Security.Authenticator{

    private static String realm = "real_test";
    
    @Override
    public String getUsername(Http.Context cntxt) {
        Logger.info("CheckDigestAuthAction - getUsername");
        if (DigestRequest.isAuthorized(cntxt.request())) {
            return "some dummy value";                      
        }        
        return null;
    }

    @Override
    public Result onUnauthorized(Http.Context cntxt) {
        Http.Response response = cntxt.response();
        String auth = "Digest realm=" + realm + ", nonce=" + UUID.randomUUID();
        response.setHeader("WWW-Authenticate", auth);                
        Logger.info("CheckDigestAuthAction - onUnauthorized");
        return status(Http.Status.UNAUTHORIZED, "No permissions");
    }
    
}
