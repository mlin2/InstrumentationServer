package org.sentinel.instrumentationserver;

import org.apache.commons.io.IOUtils;
import org.ini4j.Ini;

import java.io.*;

/**
 * Created by sebastian on 1/12/16.
 */
public class InstrumentationRunner {

    private static void printLines(String name, InputStream ins) throws Exception {
        String line = null;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            System.out.println(name + " " + line);
        }
    }

    private static void runProcess(String command) throws Exception {
        Process pro = Runtime.getRuntime().exec(command);
        printLines(command + " stdout:", pro.getInputStream());
        printLines(command + " stderr:", pro.getErrorStream());
        pro.waitFor();
        System.out.println(command + " exitValue() " + pro.exitValue());
    }

    public void run(InputStream sourceFile, InputStream sinkFile, InputStream easyTaintWrapperSource, InputStream apkFile) {
        try {


            //runProcess("javac");
            runProcess(buildCommand(sourceFile, sinkFile, easyTaintWrapperSource, apkFile));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //TODO do something better than returning null
    private String buildCommand(InputStream sourceFile, InputStream sinkFile, InputStream easyTaintWrapperSource, InputStream apkFile) {
        String instrumentJobDirectoryName = String.valueOf(Math.random());
        File directory = new File("sootOutput/" + instrumentJobDirectoryName);
        directory.mkdir();

        File sourceFileTemp = getTmpFile(sourceFile, "catSources_Short", ".txt");
        File sinkFileTemp = getTmpFile(sinkFile, "catSinks_Short", ".txt");
        File easyTaintWrapperSourceTemp = getTmpFile(easyTaintWrapperSource, "EasyTaintWrapperSource", ".txt");
        File fileToInstrumentTemp = getTmpFile(apkFile, "fileToInstrument", ".apk");

        try {
            Ini ini = new Ini(new File("config.ini"));

            String javaPath = ini.get("Global Java", "JavaPath", String.class);
            String encodingOption = ini.get("Global Java", "EncodingOption", String.class);

            String instrumentationPepDirectory = ini.get("InstrumentationPEP", "InstrumentationPepDirectory", String.class);
            String sootDirectory = ini.get("Soot", "SootDirectory", String.class);
            String jasminDirectory = ini.get("Jasmin", "JasminDirectory", String.class);
            String herosDirectory = ini.get("Heros", "HerosDirectory", String.class);
            String functionalJavaDirectory = ini.get("FunctionalJava", "PathToFunctional", String.class);
            String infoflowDirectory = ini.get("Infoflow", "PathSootInfoflow", String.class);
            String infoflowAndroidDirectory = ini.get("Infoflow Android", "PathSootInfoflowAndroid", String.class);
            String androidPlatformDirectory = ini.get("Android", "PlatformsPath", String.class);
            String androidJarDirectory = ini.get("Android Jar", "androidJarPath", String.class);
            //TODO implement keystore signing
/*            String keystoreDirectory = ini.get("Keystore", "keyStorePath", String.class);
            String keystoreAlias = ini.get("Keystore", "mykeystore", String.class);
            String keystorePass = ini.get("Keystore", "laurent", String.class);*/
            String outputDirectory = instrumentationPepDirectory + directory.getName();

            String mainMethod = "de.ecspride.Main";
            String javaExecutionDirectory = "-Duser.dir=" + instrumentationPepDirectory;
            String jarName = "-jar bit.jar";

            //TODO introduce constants for separators
            return "instrumentation.sh ./files/catSources_Short.txt ./files/catSinks_Short.txt /Users/laurentmeyer/Downloads/PolicyTester-release.apk ./files/EasyTaintWrapperSource.txt ./sootOutput";


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
