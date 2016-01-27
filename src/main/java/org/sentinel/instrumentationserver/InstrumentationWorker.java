package org.sentinel.instrumentationserver;

import org.apache.commons.codec.binary.Hex;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;

/**
 * The instrumentation runner builds the command to execute the instrumentation.sh bash script, executes it and outputs information
 * while running the instrumentation.
 */
public class InstrumentationWorker extends Thread {

    private final ProcessBuilder processBuilder;

    public Integer exit;

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

    public InstrumentationWorker(String alignedApkPath, ProcessBuilder processBuilder,
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

    public InstrumentationWorker(String alignedApkPath, ProcessBuilder processBuilder,
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
            exit = process.waitFor();
            System.out.println(" EXITVALUE " + process.exitValue());


            InstrumentationDAO instrumentationDAO = InstrumentationDAO.getInstance();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            String sha256hash = String.valueOf(Hex.encodeHex(messageDigest.digest(apkFile)));
            instrumentationDAO.saveInstrumentedApkToDatabase(alignedApkPath, sha512Hash, sha256hash);
            if (saveMetadata) {
                instrumentationDAO.saveMetadataForInstrumentedApk(logo, appName, packageName, sha512Hash, sha256hash);
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
