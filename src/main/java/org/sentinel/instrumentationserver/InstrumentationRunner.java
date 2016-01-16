package org.sentinel.instrumentationserver;

import org.apache.commons.io.IOUtils;
import org.ini4j.Ini;

import java.io.*;

/**
 * Created by sebastian on 1/12/16.
 */
public class InstrumentationRunner {

    private String instrumentedApkPath;

    private static void printLines(String name, InputStream ins) throws Exception {
        String line = null;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            System.out.println(name + " " + line);
        }
    }

    public void run(InputStream sourceFile, InputStream sinkFile, InputStream easyTaintWrapperSource, InputStream apkFile, String sha512Hash) {

        try {
            ProcessBuilder processBuilder = buildCommand(sourceFile, sinkFile, easyTaintWrapperSource, apkFile);
            Process process = processBuilder.start();
            printLines(" STDOUT:", process.getInputStream());
            printLines(" STDERR:", process.getErrorStream());
            process.waitFor();
            System.out.println(" EXITVALUE " + process.exitValue());

            InstrumentationServerManager instrumentationServerManager = InstrumentationServerManager.getInstance();
            instrumentationServerManager.saveInstrumentedApkToDatabase(instrumentedApkPath, sha512Hash);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //TODO do something better than returning null
    private ProcessBuilder buildCommand(InputStream sourceFile, InputStream sinkFile, InputStream easyTaintWrapperSource, InputStream apkFile) {
        String instrumentJobDirectoryName = String.valueOf(Math.random());
        File directory = new File("sootOutput/" + instrumentJobDirectoryName);
        directory.mkdir();

        File sourceFileTemp = getTmpFile(sourceFile, "catSources_Short", ".txt");
        File sinkFileTemp = getTmpFile(sinkFile, "catSinks_Short", ".txt");
        File easyTaintWrapperSourceTemp = getTmpFile(easyTaintWrapperSource, "EasyTaintWrapperSource", ".txt");
        File fileToInstrumentTemp = getTmpFile(apkFile, "fileToInstrument", ".apk");

        try {
            Ini ini = new Ini(new File("config.ini"));

            String instrumentationPepDirectory = ini.get("InstrumentationPEP", "InstrumentationPepDirectory", String.class);
            //TODO implement keystore signing
/*            String keystoreDirectory = ini.get("Keystore", "keyStorePath", String.class);
            String keystoreAlias = ini.get("Keystore", "mykeystore", String.class);
            String keystorePass = ini.get("Keystore", "laurent", String.class);*/
            String outputDirectoryAbsolutePath = instrumentationPepDirectory + directory.getName();
            String androidJarDirectory = ini.get("Android Jar", "androidJarPath", String.class);
            String currentDirectory = System.getProperty("user.dir");

            ProcessBuilder processBuilder = new ProcessBuilder(currentDirectory + "/instrumentation.sh", sourceFileTemp.getAbsolutePath(), sinkFileTemp.getAbsolutePath(),
                    fileToInstrumentTemp.getAbsolutePath(), easyTaintWrapperSourceTemp.getAbsolutePath(), outputDirectoryAbsolutePath, androidJarDirectory);

            return processBuilder;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private File getTmpFile(InputStream fileData, String prefix, String suffix) {
        try {

            final File tempFile = File.createTempFile(prefix, suffix);
            tempFile.deleteOnExit();
            try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
                IOUtils.copy(fileData, fileOutputStream);
            }
            return tempFile;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
