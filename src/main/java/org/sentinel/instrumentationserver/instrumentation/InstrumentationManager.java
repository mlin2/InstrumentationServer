package org.sentinel.instrumentationserver.instrumentation;


import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.sentinel.instrumentationserver.InstrumentationDAO;
import org.sentinel.instrumentationserver.InstrumentationWorker;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class InstrumentationManager {

    private final Long timeoutForInstrumentation;

    public InstrumentationManager(Long timeoutForInstrumentation) {
        this.timeoutForInstrumentation = timeoutForInstrumentation;
    }

    public void instrumentWithMetadata(InputStream sourceFile, InputStream sinkFile, InputStream easyTaintWrapperSource, byte[] apkFile, String sha512Hash,
                                       byte[] logo, String appName, String packageName, boolean makeAppPublic) {


        String currentDirectory = System.getProperty("user.dir");
        String instrumentationJobsDirectory = currentDirectory + "/InstrumentationPEP/instrumentation-server-jobs";
        String alignedApkPath = instrumentationJobsDirectory + "/" + sha512Hash + "/alignedApk.apk";
        ProcessBuilder processBuilder = createInstrumentationProcessBuilder(currentDirectory, instrumentationJobsDirectory, alignedApkPath, sourceFile,
                sinkFile, easyTaintWrapperSource, apkFile, sha512Hash);

            InstrumentationWorker instrumentationWorker = new InstrumentationWorker(alignedApkPath, processBuilder, apkFile, sha512Hash, logo, appName, packageName, makeAppPublic);
            instrumentationWorker.start();
            //instrumentationWorker.join(timeoutForInstrumentation);


    }

    public void instrumentFromLinks(List<String> repositoryApkLinks) {
        {
            Iterator<String> repositoryApkLinkIterator = repositoryApkLinks.iterator();
            while (repositoryApkLinkIterator.hasNext()) {
                InstrumentationDAO instrumentationDAO = InstrumentationDAO.getInstance();
                String apkLink = repositoryApkLinkIterator.next();
                URL url = null;
                try {
                    FileInputStream sourceFile = new FileInputStream("InstrumentationPEP/files/catSources_Short.txt");
                    FileInputStream sinkFile = new FileInputStream("InstrumentationPEP/files/catSinks_Short.txt");
                    FileInputStream easyTaintWrapperSource = new FileInputStream("InstrumentationPEP/files/EasyTaintWrapperSource.txt");
                    url = new URL(apkLink);
                    URLConnection urlConnection = url.openConnection();
                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
                    byte[] apkBytes = IOUtils.toByteArray(urlConnection.getInputStream());
                    String sha512Hash = String.valueOf(Hex.encodeHex(messageDigest.digest(apkBytes)));

                    String currentDirectory = System.getProperty("user.dir");
                    String instrumentationJobsDirectory = currentDirectory + "/InstrumentationPEP/instrumentation-server-jobs";
                    String alignedApkPath = instrumentationJobsDirectory + "/" + sha512Hash + "/alignedApk.apk";

                    ProcessBuilder processBuilder = createInstrumentationProcessBuilder(currentDirectory, instrumentationJobsDirectory, alignedApkPath,
                            sourceFile, sinkFile, easyTaintWrapperSource, apkBytes, sha512Hash);

                    if (!instrumentationDAO.checkIfApkAlreadyInstrumented(sha512Hash)) {
                        InstrumentationWorker instrumentationWorker = new InstrumentationWorker(alignedApkPath, processBuilder, apkBytes, sha512Hash, true);
                        instrumentationWorker.start();
                        instrumentationWorker.join(timeoutForInstrumentation);
                        if(instrumentationWorker.exit == null) {
                            instrumentationWorker.interrupt();
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
    private ProcessBuilder createInstrumentationProcessBuilder(String currentDirectory, String instrumentationJobsDirectory, String alignedApkPath,
                                                               InputStream sourceFile, InputStream sinkFile, InputStream easyTaintWrapperSource, byte[] apkFile,
                                                               String sha512Hash) {

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

            String outputDirectoryAbsolutePath = instrumentationJobsDirectory + "/" + sha512Hash;
            String instrumentedApkPath = instrumentationJobsDirectory + "/" + sha512Hash + "/" + fileToInstrumentTemp.getName();

            String signedApkPath = instrumentationJobsDirectory + "/" + sha512Hash + "/signedApk.apk";


            ProcessBuilder processBuilder = new ProcessBuilder(currentDirectory + "/instrumentation.sh", sourceFileTemp.getAbsolutePath(), sinkFileTemp.getAbsolutePath(),
                    fileToInstrumentTemp.getAbsolutePath(), easyTaintWrapperSourceTemp.getAbsolutePath(), outputDirectoryAbsolutePath, androidJarDirectory, instrumentedApkPath, keystoreDirectory,
                    keystoreAlias, keystorePass, signedApkPath, alignedApkPath);

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

    public void instrument(InputStream sourceFile, InputStream sinkFile, InputStream easyTaintWrapperSource, byte[] apkFile, String sha512Hash) {
        String currentDirectory = System.getProperty("user.dir");
        String instrumentationJobsDirectory = currentDirectory + "/InstrumentationPEP/instrumentation-server-jobs";
        String alignedApkPath = instrumentationJobsDirectory + "/" + sha512Hash + "/alignedApk.apk";
        ProcessBuilder processBuilder = createInstrumentationProcessBuilder(currentDirectory, instrumentationJobsDirectory, alignedApkPath, sourceFile,
                sinkFile, easyTaintWrapperSource, apkFile, sha512Hash);


            InstrumentationWorker instrumentationWorker = new InstrumentationWorker(alignedApkPath, processBuilder, apkFile, sha512Hash, false);
            instrumentationWorker.start();
            //instrumentationWorker.join(timeoutForInstrumentation);


    }
}