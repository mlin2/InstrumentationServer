package org.sentinel.instrumentationserver;

import org.apache.commons.io.IOUtils;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

import java.io.*;

/**
 * Created by sebastian on 1/12/16.
 */
public class InstrumentationRunner implements Runnable {

    private String instrumentedApkPath;

    InputStream sourceFile;
    InputStream sinkFile;
    InputStream easyTaintWrapperSource;
    byte[] apkFile;
    String sha512Hash;

    public InstrumentationRunner(InputStream sourceFile, InputStream sinkFile, InputStream easyTaintWrapperSource,
                                 byte[] apkFile, String sha512Hash) {
        this.sourceFile = sourceFile;
        this.sinkFile = sinkFile;
        this.easyTaintWrapperSource = easyTaintWrapperSource;
        this.apkFile = apkFile;
        this.sha512Hash = sha512Hash;
    }

    private static void printLines(String name, InputStream ins) throws Exception {
        String line = null;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            System.out.println(name + " " + line);
        }
    }

    //TODO do something better than returning null
    private ProcessBuilder buildCommand(InputStream sourceFile, InputStream sinkFile, InputStream easyTaintWrapperSource, byte[] apkFile, String sha512Hash) {

        File sourceFileTemp = getTmpFile(sourceFile, "catSources_Short", ".txt");
        File sinkFileTemp = getTmpFile(sinkFile, "catSinks_Short", ".txt");
        File easyTaintWrapperSourceTemp = getTmpFile(easyTaintWrapperSource, "EasyTaintWrapperSource", ".txt");

        try {
        final File fileToInstrumentTemp = File.createTempFile("fileToInstrument", ".apk");
        fileToInstrumentTemp.deleteOnExit();
        FileOutputStream fileOutputStream = new FileOutputStream(fileToInstrumentTemp);
        fileOutputStream.write(apkFile);

            Ini ini = new Ini(new File("config.ini"));
            //TODO implement keystore signing
/*            String keystoreDirectory = ini.get("Keystore", "keyStorePath", String.class);
            String keystoreAlias = ini.get("Keystore", "mykeystore", String.class);
            String keystorePass = ini.get("Keystore", "laurent", String.class);*/
            String androidJarDirectory = ini.get("Android Jar", "androidJarPath", String.class);
            String currentDirectory = System.getProperty("user.dir");
            String instrumentationPepDirectory = currentDirectory + "/InstrumentationPEP/instrumentation-server-jobs";
            String outputDirectoryAbsolutePath = instrumentationPepDirectory + "/" + sha512Hash;

            instrumentedApkPath = instrumentationPepDirectory + "/" + sha512Hash + "/" + fileToInstrumentTemp.getName();

            ProcessBuilder processBuilder = new ProcessBuilder(currentDirectory + "/instrumentation.sh", sourceFileTemp.getAbsolutePath(), sinkFileTemp.getAbsolutePath(),
                    fileToInstrumentTemp.getAbsolutePath(), easyTaintWrapperSourceTemp.getAbsolutePath(), outputDirectoryAbsolutePath, androidJarDirectory);

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

    @Override
    public void run() {
        try {
            ProcessBuilder processBuilder = buildCommand(sourceFile, sinkFile, easyTaintWrapperSource, apkFile, sha512Hash);
            Process process = processBuilder.start();
            printLines(" STDOUT:", process.getInputStream());
            printLines(" STDERR:", process.getErrorStream());
            process.waitFor();
            System.out.println(" EXITVALUE " + process.exitValue());

/*            InstrumentationServerManager instrumentationServerManager = InstrumentationServerManager.getInstance();
            instrumentationServerManager.saveInstrumentedApkToDatabase(instrumentedApkPath, sha512Hash);*/

            InstrumentationServerManager.getInstance().saveApk(instrumentedApkPath, sha512Hash);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getInstrumentedApkPath() {
        return instrumentedApkPath;
    }
}
