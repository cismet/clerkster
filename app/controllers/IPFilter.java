/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.List;
import play.Logger;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.SimpleResult;

/**
 *
 * @author Gilles Baatz
 */
public class IPFilter extends Action.Simple {
    
    @Override
    public F.Promise<SimpleResult> call(Http.Context cntxt) throws Throwable {
        List<String> ips = LoadConfig.loadStringListFromConfig("de.cismet.ips.allowed");
        String requestIP = cntxt.request().remoteAddress();
        Logger.debug("Got a request from: " + requestIP);
        for (String ip : ips) {
            if (ip.equals(requestIP)) {
                return delegate.call(cntxt);
            }
        }
        return F.Promise.pure((SimpleResult) forbidden("IP is not allowed"));
    }
}
