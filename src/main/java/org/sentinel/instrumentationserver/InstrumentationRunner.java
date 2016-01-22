package org.sentinel.instrumentationserver;

import org.apache.commons.io.IOUtils;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

import java.io.*;

/**
 * The instrumentation runner builds the command to execute the instrumentation.sh bash script, executes it and outputs information
 * while running the instrumentation.
 */
public class InstrumentationRunner implements Runnable {

    /**
     * The path where the signed and instrumented APK gets saved.
     */
    private String signedApkPath;

    /**
     * The sha512sum of the APK before instrumentation and signing.
     */
    private String sha512Hash;

    /**
     * Source file containing the android's source methods.
     */
    private InputStream sourceFile;

    /**
     * Sink file containing the android's sink methods.
     */
    private InputStream sinkFile;

    /**
     * Taint wrapper file containing the android's package names that
     * should be considered during the instrumentation phase.
     */
    private InputStream easyTaintWrapperSource;

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
    private final boolean makeAppPublic;

    public InstrumentationRunner(InputStream sourceFile, InputStream sinkFile, InputStream easyTaintWrapperSource,
                                 byte[] apkFile, String sha512Hash, byte[] logo, String appName, String packageName, boolean makeAppPublic) {
        this.sourceFile = sourceFile;
        this.sinkFile = sinkFile;
        this.easyTaintWrapperSource = easyTaintWrapperSource;
        this.apkFile = apkFile;
        this.sha512Hash = sha512Hash;
        this.logo = logo;
        this.appName = appName;
        this.packageName = packageName;
        this.makeAppPublic = makeAppPublic;
    }

    /**
     * The Runnable interface gets implemented in order to return an OK response to the instrumentation POST request
     * immediately. The instrumentation gets executed through this method in a new thread.
     */
    @Override
    public void run() {
        try {
            ProcessBuilder processBuilder = createInstrumentationProcessBuilder(sourceFile, sinkFile, easyTaintWrapperSource, apkFile, sha512Hash);
            Process process = processBuilder.start();
            printLines(" STDOUT:", process.getInputStream());
            printLines(" STDERR:", process.getErrorStream());
            process.waitFor();
            System.out.println(" EXITVALUE " + process.exitValue());

            InstrumentationDAO instrumentationDAO = InstrumentationDAO.getInstance();
            instrumentationDAO.saveInstrumentedApkToDatabase(signedApkPath, sha512Hash);
            if(makeAppPublic) {
                instrumentationDAO.saveMetadataForInstrumentedApk(logo, appName, packageName, sha512Hash);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO do something better than returning null

    /**
     * Build the process builder for the instrumentation and set all necessary variables.
     */
    private ProcessBuilder createInstrumentationProcessBuilder(InputStream sourceFile, InputStream sinkFile, InputStream easyTaintWrapperSource, byte[] apkFile, String sha512Hash) {

        File sourceFileTemp = getTmpFile(sourceFile, "catSources_Short", ".txt");
        File sinkFileTemp = getTmpFile(sinkFile, "catSinks_Short", ".txt");
        File easyTaintWrapperSourceTemp = getTmpFile(easyTaintWrapperSource, "EasyTaintWrapperSource", ".txt");

        try {
            final File fileToInstrumentTemp = File.createTempFile("fileToInstrument", ".apk");
            fileToInstrumentTemp.deleteOnExit();
            FileOutputStream fileOutputStream = new FileOutputStream(fileToInstrumentTemp);
            fileOutputStream.write(apkFile);

            Ini ini = new Ini(new File("config.ini"));
            String keystoreDirectory = ini.get("Keystore", "keyStorePath", String.class);
            String keystoreAlias = ini.get("Keystore", "alias", String.class);
            String keystorePass = ini.get("Keystore", "storePass", String.class);
            String androidJarDirectory = ini.get("Android Jar", "androidJarPath", String.class);

            String currentDirectory = System.getProperty("user.dir");
            String instrumentationJobsDirectory = currentDirectory + "/InstrumentationPEP/instrumentation-server-jobs";
            String outputDirectoryAbsolutePath = instrumentationJobsDirectory + "/" + sha512Hash;
            String instrumentedApkPath = instrumentationJobsDirectory + "/" + sha512Hash + "/" + fileToInstrumentTemp.getName();

            signedApkPath = instrumentationJobsDirectory + "/" + sha512Hash + "/signedApk.jar";


            ProcessBuilder processBuilder = new ProcessBuilder(currentDirectory + "/instrumentation.sh", sourceFileTemp.getAbsolutePath(), sinkFileTemp.getAbsolutePath(),
                    fileToInstrumentTemp.getAbsolutePath(), easyTaintWrapperSourceTemp.getAbsolutePath(), outputDirectoryAbsolutePath, androidJarDirectory, instrumentedApkPath, keystoreDirectory,
                    keystoreAlias, keystorePass, signedApkPath);

            return processBuilder;

        } catch (InvalidFileFormatException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

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

    /**
     * Create temporary files that the instrumentation request needs.
     */
    private File getTmpFile(InputStream fileData, String prefix, String suffix) {
        try {
            final File tempFile = File.createTempFile(prefix, suffix);
            tempFile.deleteOnExit();
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            IOUtils.copy(fileData, fileOutputStream);
            return tempFile;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
