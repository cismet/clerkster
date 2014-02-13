package controllers;

import com.typesafe.config.ConfigFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import play.Logger;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;

import views.html.*;

@Security.Authenticated(CheckDigestAuthAction.class)
public class Application extends Controller {
    
    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }
    
    public static Result upload() {
        Logger.info("Upload");
        MultipartFormData body = request().body().asMultipartFormData();
        FilePart uploadedFile = body.getFile("upload");
        if (uploadedFile != null) {
            File file = uploadedFile.getFile();
            if (isValidatedFile(file)) {
                signJar(file);                
                return sendResignedJarBack(file);
            } else {
                Logger.info("Not Validated File");
                return badRequest();
            }
        } else {
            flash("error", "Missing file");
            return redirect(routes.Application.index());
        }
    }
    
    private static boolean isValidatedFile(File file) {
        String keystorePath = ConfigFactory.load("certificate.conf").getString("de.cismet.check.keystore.path");
        String keystorePW = ConfigFactory.load("certificate.conf").getString("de.cismet.check.keystore.pass");
        String keystoreAlias = ConfigFactory.load("certificate.conf").getString("de.cismet.check.keystore.alias");
        Logger.info("validating file: " + keystorePath + " - " + keystorePW + " - " + keystoreAlias);        
        return JarVerifier.isSigned(file, keystorePath, keystorePW, keystoreAlias, true, true);
    }
    
    private static void signJar(File jarToSign){
        String keystorePath = ConfigFactory.load("certificate.conf").getString("de.cismet.ca.keystore.path");
        String keystorePW = ConfigFactory.load("certificate.conf").getString("de.cismet.ca.keystore.pass");
        String keystoreAlias = ConfigFactory.load("certificate.conf").getString("de.cismet.ca.keystore.alias");
        String keypass = ConfigFactory.load("certificate.conf").getString("de.cismet.ca.keystore.keypass");        
        Logger.info("signing file: " + keystorePath + " - " + keystorePW + " - " + keystoreAlias);  
        JarSigner.signJar(jarToSign, keystoreAlias, keypass, keystorePath, keystorePW);
    }

    private static Result sendResignedJarBack(File file) {
        Logger.info("Send File back");
        try {
            return ok(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            return badRequest();
        }
    }
}
