package org.sentinel.instrumentationserver.instrumentation;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.sentinel.instrumentationserver.Main;
import org.sentinel.instrumentationserver.metadata.MetadataDAO;

import java.io.*;
import java.security.MessageDigest;

/**
 * The instrumentation runner builds the command to execute the instrumentation.sh bash script, executes it and outputs information
 * while running the instrumentation.
 */
public class InstrumentationRunner implements Runnable {

    /**
     * The process builder for the instrumentation process calling the bash script.
     */
    private final ProcessBuilder processBuilder;

    /**
     * The sha512sum of the APK before instrumentation and signing.
     */
    private String sha512Hash;


    /**
     * APK file that should be instrumented.
     */
    private byte[] apkFile;

    /**
     * The logo of the app.
     */
    private final byte[] logo;

    /**
     * The name of the app.
     */
    private final String appName;

    /**
     * The package name of the app.
     */
    private final String packageName;

    /**
     * Determines whether or not the app will appear in the App store for everybody
     */
    private final boolean saveMetadata;

    /**
     * The path where the signed, instrumented and aligned APK gets saved.
     */
    private String alignedApkPath;

    public InstrumentationRunner(String alignedApkPath, ProcessBuilder processBuilder,
                                 byte[] apkFile, String sha512Hash, byte[] logo, String appName, String packageName, boolean saveMetadata) {
        this.alignedApkPath = alignedApkPath;
        this.processBuilder = processBuilder;
        this.apkFile = apkFile;
        this.sha512Hash = sha512Hash;
        this.logo = logo;
        this.appName = appName;
        this.packageName = packageName;
        this.saveMetadata = saveMetadata;
    }

    public InstrumentationRunner(String alignedApkPath, ProcessBuilder processBuilder,
                                 byte[] apkFile, String sha512Hash, boolean saveMetadata) {
        this.alignedApkPath = alignedApkPath;
        this.processBuilder = processBuilder;
        this.apkFile = apkFile;
        this.sha512Hash = sha512Hash;
        this.logo = null;
        this.appName = null;
        this.packageName = null;
        this.saveMetadata = saveMetadata;
    }

    /**
     * The Runnable interface gets implemented in order to return an OK response to the instrumentation POST request
     * immediately. The instrumentation gets executed through this method in a new thread.
     */
    @Override
    public void run() {
        try {
            Process process = processBuilder.start();
            printLines(" STDOUT:", process.getInputStream());
            printLines(" STDERR:", process.getErrorStream());
            System.out.println(" EXITVALUE " + process.exitValue());


            InstrumentationDAO instrumentationDAO = new InstrumentationDAO();
            MetadataDAO metadataDAO = new MetadataDAO();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            String sha256hash = String.valueOf(Hex.encodeHex(messageDigest.digest(apkFile)));
            InputStream instrumentedApkInputstream = new FileInputStream((new File(alignedApkPath)));
            byte[] apkBytes = IOUtils.toByteArray(instrumentedApkInputstream);

            instrumentationDAO.saveInstrumentedApkToDatabase(apkBytes, sha512Hash, sha256hash);
            if (saveMetadata) {
                metadataDAO.saveMetadataForInstrumentedApk(logo, appName, packageName, sha512Hash, sha256hash);
            }
            if (Main.DELETE_INSTRUMENTATION_JOB_DIRECTORY) {
                FileUtils.deleteDirectory(new File(Main.INSTRUMENTATION_JOB_DIRECTORY + "/" + sha512Hash));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Print the STDOUT and STDERR of the instrumentation process.
     */
    private static void printLines(String name, InputStream inputStream) throws Exception {
        String line;
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(name + " " + line);
        }
    }


}
