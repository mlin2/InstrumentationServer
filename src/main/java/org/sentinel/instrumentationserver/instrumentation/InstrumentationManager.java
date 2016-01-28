package org.sentinel.instrumentationserver.instrumentation;


import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.sentinel.instrumentationserver.Main;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class handles the creation of an instrumentation process and the calling of the instrumentation runner.
 */
public class InstrumentationManager {

    /**
     * Instrument an APK without saving metadata in the database.
     */
    public void instrument(InputStream sourceFile, InputStream sinkFile, InputStream easyTaintWrapperSource, byte[] apkFile, String sha512Hash) {
        String alignedApkPath = Main.DATA_DIRECTORY + "/" + sha512Hash + "/alignedApk.apk";
        ProcessBuilder processBuilder = createInstrumentationProcessBuilder(sourceFile,
                sinkFile, easyTaintWrapperSource, apkFile, sha512Hash);

        InstrumentationRunner instrumentationRunner = new InstrumentationRunner(alignedApkPath, processBuilder, apkFile, sha512Hash);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(instrumentationRunner);
        executorService.shutdown();
    }

    /**
     * Instrument an APK with saving metadata in the database.
     */
    public void instrumentWithMetadata(InputStream sourceFile, InputStream sinkFile, InputStream easyTaintWrapperSource, byte[] apkFile, String sha512Hash,
                                       byte[] logo, String appName, String packageName, boolean makeAppPublic) {


        String alignedApkPath = Main.DATA_DIRECTORY + "/" + sha512Hash + "/alignedApk.apk";
        ProcessBuilder processBuilder = createInstrumentationProcessBuilder(sourceFile,
                sinkFile, easyTaintWrapperSource, apkFile, sha512Hash);

        InstrumentationRunner instrumentationRunner = new InstrumentationRunner(alignedApkPath, processBuilder, apkFile, sha512Hash, logo, appName, packageName, makeAppPublic);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(instrumentationRunner);
        executorService.shutdown();
    }

    /**
     * Fetch and instrument all APKs from the link and save them in the database.
     */
    public void instrumentFromLinks(List<String> repositoryApkLinks) {
        {
            Iterator<String> repositoryApkLinkIterator = repositoryApkLinks.iterator();
            while (repositoryApkLinkIterator.hasNext()) {
                InstrumentationDAO instrumentationDAO = InstrumentationDAO.getInstance();
                String apkLink = repositoryApkLinkIterator.next();
                try {
                    FileInputStream sourceFile = new FileInputStream("InstrumentationDependencies/files/catSources_Short.txt");
                    FileInputStream sinkFile = new FileInputStream("InstrumentationDependencies/files/catSinks_Short.txt");
                    FileInputStream easyTaintWrapperSource = new FileInputStream("InstrumentationDependencies/files/EasyTaintWrapperSource.txt");
                    URL url = new URL(apkLink);
                    URLConnection urlConnection = url.openConnection();
                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
                    byte[] apkBytes = IOUtils.toByteArray(urlConnection.getInputStream());
                    String sha512Hash = String.valueOf(Hex.encodeHex(messageDigest.digest(apkBytes)));

                    String currentDirectory = System.getProperty("user.dir");
                    String instrumentationJobsDirectory = currentDirectory + "/InstrumentationDependencies/instrumentation-server-jobs";
                    String alignedApkPath = instrumentationJobsDirectory + "/" + sha512Hash + "/alignedApk.apk";

                    ProcessBuilder processBuilder = createInstrumentationProcessBuilder(
                            sourceFile, sinkFile, easyTaintWrapperSource, apkBytes, sha512Hash);

                    if (!instrumentationDAO.checkIfApkAlreadyInstrumented(sha512Hash)) {
                        InstrumentationRunner instrumentationRunner = new InstrumentationRunner(alignedApkPath, processBuilder, apkBytes, sha512Hash);
                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                        executorService.execute(instrumentationRunner);
                        executorService.shutdown();
                        while (!executorService.isTerminated()) {
                            executorService.awaitTermination(Main.TIMEOUT_FOR_INSTRUMENTATION_IN_MINUTES, TimeUnit.MINUTES);
                        }
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //TODO do something better than returning null

    /**
     * Build the process builder for the instrumentation and set all necessary variables.
     */
    private ProcessBuilder createInstrumentationProcessBuilder(InputStream sourceFile,
                                                               InputStream sinkFile, InputStream easyTaintWrapperSource,
                                                               byte[] apkFile, String sha512Hash) {

        String instrumentationJobPath = Main.DATA_DIRECTORY + "/" + sha512Hash;
        File instrumentationJobDirectory = new File(instrumentationJobPath);
        try {
            if (instrumentationJobDirectory.exists()) {
                FileUtils.deleteDirectory(instrumentationJobDirectory);
            }
            instrumentationJobDirectory.mkdirs();
            FileUtils.forceDeleteOnExit(instrumentationJobDirectory);
            File sourceFileInstrumentationJob = createFileFromInputStream(sourceFile, instrumentationJobPath, "catSources_Short", ".txt");
            File sinkFileInstrumentationJob = createFileFromInputStream(sinkFile, instrumentationJobPath, "catSinks_Short", ".txt");
            File easyTaintWrapperSourceInstrumentationJob = createFileFromInputStream(easyTaintWrapperSource, instrumentationJobPath, "EasyTaintWrapperSource", ".txt");


            final File fileToInstrumentInstrumentationJob = new File(instrumentationJobPath + "/" + "fileToInstrument.apk");
            fileToInstrumentInstrumentationJob.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(fileToInstrumentInstrumentationJob, false);
            fileOutputStream.write(apkFile);

            Ini ini = new Ini(new File("config.ini"));
            String keystoreDirectory = ini.get("Keystore", "keyStorePath", String.class);
            String keystoreAlias = ini.get("Keystore", "alias", String.class);
            String keystorePass = ini.get("Keystore", "storePass", String.class);
            String androidJarDirectory = ini.get("Android Jar", "androidJarPath", String.class);

            String outputDirectoryAbsolutePath = instrumentationJobPath + "/sootOutput";
            String instrumentedApkPath = instrumentationJobPath + "/" + fileToInstrumentInstrumentationJob.getName();
            String signedApkPath = instrumentationJobPath + "/signedApk.apk";
            String alignedApkPath = instrumentationJobPath + "/alignedApk.apk";


            return new ProcessBuilder(Main.INSTRUMENTATION_SCRIPT_ABSOLUTE_PATH,
                    sourceFileInstrumentationJob.getAbsolutePath(), sinkFileInstrumentationJob.getAbsolutePath(),
                    fileToInstrumentInstrumentationJob.getAbsolutePath(), easyTaintWrapperSourceInstrumentationJob.getAbsolutePath(),
                    outputDirectoryAbsolutePath, androidJarDirectory, instrumentedApkPath, keystoreDirectory,
                    keystoreAlias, keystorePass, signedApkPath, alignedApkPath, String.valueOf(Main.TIMEOUT_FOR_INSTRUMENTATION_IN_MINUTES));

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
     * Create temporary files that the instrumentation request needs.
     */
    private File createFileFromInputStream(InputStream fileData, String instrumentationJobPath, String prefix, String suffix) {
        try {
            byte[] fileBytes = IOUtils.toByteArray(fileData);
            Path path = Paths.get(instrumentationJobPath + "/" + prefix + suffix);
            Files.write(path, fileBytes);
            return path.toFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}