package controllers;

import de.cismet.commons.utils.JarUtils;
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
import java.util.List;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public static Result upload() {
        try {
            Logger.info("Got a new upload request.");
            final MultipartFormData body = request().body().asMultipartFormData();
            final FilePart uploadedFile = body.getFile("upload");
            if (uploadedFile != null) {
                final File file = uploadedFile.getFile();
                if (JarUtils.checkIfJarWithManifest(file)) {
                    if (isValidatedFile(file)) {
                        archiveJar(file, uploadedFile.getFilename());
                        JarUtils.unsignJar(file);
                        signJar(file);
                        return sendResignedJarBack(file);
                    } else {
                        Logger.info("Uploaded file is not properly signed.");
                        return badRequest("Your file is not properly signed.");
                    }
                } else {
                    Logger.info("The uploaded file is not a proper Jar.");
                    return badRequest("Not a proper Jar");
                }
            } else {
                Logger.info("There was no file in the upload.");
                return badRequest("Missing file.");
            }
        } catch (Exception ex) {
            Logger.error(ex.getMessage(), ex);
            return internalServerError(ex.getMessage());
        }
    }

    private static boolean isValidatedFile(File file) {
        String keystorePath = LoadConfig.loadStringFromConfig("de.cismet.check.keystore.path");
        String keystorePW = LoadConfig.loadStringFromConfig("de.cismet.check.keystore.pass");
        List<String> keystoreAlias = LoadConfig.loadStringListFromConfig("de.cismet.check.keystore.alias");
        Logger.debug("validating file: " + keystorePath + " - " + keystoreAlias);
        for (String alias : keystoreAlias) {
            boolean isSigned = JarUtils.isSigned(file, keystorePath, keystorePW, alias);
            if (isSigned) {
                Logger.debug("the file was validated with: " + alias);
                return true;
            }
        }
        return false;
    }

    private static void signJar(File jarToSign) {
        final String keystorePath = LoadConfig.loadStringFromConfig("de.cismet.ca.keystore.path");
        final String keystorePW = LoadConfig.loadStringFromConfig("de.cismet.ca.keystore.pass");
        final String keystoreAlias = LoadConfig.loadStringFromConfig("de.cismet.ca.keystore.alias");
        final String keypass = LoadConfig.loadStringFromConfig("de.cismet.ca.keystore.keypass");
        final String tsaurl =  LoadConfig.loadStringFromConfig("de.cismet.ca.tsaurl");
        Logger.debug("signing file: " + keystorePath + " - " + keystoreAlias);
        JarSigner.signJar(jarToSign, keystoreAlias, keypass, keystorePath, keystorePW, tsaurl);
    }

    private static void archiveJar(File file, String filename) {
        final String pathArchivedFile = LoadConfig.loadStringFromConfig("de.cismet.archive.jar-folder") + "/";
        final String baseName = FilenameUtils.getBaseName(filename);
        final String extension = FilenameUtils.getExtension(filename);

        final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        final Date now = new Date();
        final String uniqueString = "-" + DATE_FORMAT.format(now);

        final String destinationFileName = pathArchivedFile + baseName + uniqueString + "." + extension;
        Logger.debug("Destination of the archived file: " + destinationFileName);

        final File destinationFile = new File(destinationFileName);
        try {
            FileUtils.copyFile(file, destinationFile);
        } catch (IOException ex) {
            Logger.error("Problem while writing archive file.", ex);
        }

        PrintWriter out = null;
        final String archiveFile = LoadConfig.loadStringFromConfig("de.cismet.archive.csv-file");
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(archiveFile, true)));
            out.println(DATE_FORMAT.format(now) + "," + destinationFile.getAbsolutePath());
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
            return internalServerError(ex.getMessage());
        }
    }
}
