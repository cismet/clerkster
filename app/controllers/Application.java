package controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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
        Logger.info("Got a new upload request.");
        MultipartFormData body = request().body().asMultipartFormData();
        FilePart uploadedFile = body.getFile("upload");
        if (uploadedFile != null) {
            File file = uploadedFile.getFile();
            if (isValidatedFile(file)) {
                archiveJar(request().username(), file, uploadedFile.getFilename());
                signJar(file);
                return sendResignedJarBack(file);
            } else {
                Logger.info("Uploaded file is not properly validated.");
                return badRequest("Your file is not properly validated.");
            }
        } else {
            Logger.info("There was no file in the upload.");
            return badRequest("Missing file.");
        }
    }

    private static boolean isValidatedFile(File file) {
        String keystorePath = LoadConfig.loadOwnConf("de.cismet.check.keystore.path");
        String keystorePW = LoadConfig.loadOwnConf("de.cismet.check.keystore.pass");
        String keystoreAlias = LoadConfig.loadOwnConf("de.cismet.check.keystore.alias");
        Logger.debug("validating file: " + keystorePath + " - " + keystorePW + " - " + keystoreAlias);
        return JarVerifier.isSigned(file, keystorePath, keystorePW, keystoreAlias, true, true);
    }

    private static void signJar(File jarToSign) {
        String keystorePath = LoadConfig.loadOwnConf("de.cismet.ca.keystore.path");
        String keystorePW = LoadConfig.loadOwnConf("de.cismet.ca.keystore.pass");
        String keystoreAlias = LoadConfig.loadOwnConf("de.cismet.ca.keystore.alias");
        String keypass = LoadConfig.loadOwnConf("de.cismet.ca.keystore.keypass");
        Logger.debug("signing file: " + keystorePath + " - " + keystorePW + " - " + keystoreAlias);
        JarSigner.signJar(jarToSign, keystoreAlias, keypass, keystorePath, keystorePW);
    }

    private static void archiveJar(String username, File file, String filename) {
        String pathArchivedFile = LoadConfig.loadOwnConf("de.cismet.archive.jar-folder") + username + "/";
        String baseName = FilenameUtils.getBaseName(filename);
        String extension = FilenameUtils.getExtension(filename);

        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date now = new Date();
        String uniqueString = "-" + DATE_FORMAT.format(now);

        String destinationFileName = pathArchivedFile + baseName + uniqueString + "." + extension;
        Logger.debug("Destination of the archived file: " + destinationFileName);

        File destinationFile = new File(destinationFileName);
        try {
            FileUtils.copyFile(file, destinationFile);
        } catch (IOException ex) {
            Logger.error("Problem while writing archive file.", ex);
        }

        PrintWriter out = null;
        String archiveFile = LoadConfig.loadOwnConf("de.cismet.archive.csv-file");
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(archiveFile, true)));
            out.println(username + "," + DATE_FORMAT.format(now) + "," + destinationFile.getAbsolutePath());
        } catch (IOException e) {
            Logger.error("Problem while writing to the archive file.", e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private static Result sendResignedJarBack(File file) {
        Logger.info("Sending signed file back");
        try {
            return ok(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            return badRequest();
        }
    }
}
