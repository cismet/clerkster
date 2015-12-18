package controllers;

import java.io.File;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.SignJar;

/**
 *
 * @author Gilles Baatz
 */
public class JarSigner extends SignJar {

    public JarSigner() {
        project = new Project();
        project.init();
        taskType = "signJar";
        taskName = "signJar";
        target = new Target();
    }

    public static void signJar(final File jarToSign, final String alias, final String keypass, final String keystore, final String storepass, final String tsaurl) {
        JarSigner signer = new JarSigner();
        signer.setJar(jarToSign);
        signer.setAlias(alias);
        signer.setKeypass(keypass);
        signer.setKeystore(keystore);
        signer.setStorepass(storepass);
        signer.setSignedjar(jarToSign);        
        signer.setTsaurl(tsaurl);
        signer.execute();
    }
}
