package org.sentinel.instrumentationserver;

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

    public void run(Object sourceFile, Object sinkFile, Object easyTaintWrapperSource, Object apkFile) {
        try {


            //runProcess("javac");
            runProcess(buildCommand(sourceFile, sinkFile, easyTaintWrapperSource, apkFile));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //TODO do something better than returning null
    private String buildCommand(Object sourceFile, Object sinkFile, Object easyTaintWrapperSource, Object apkFile) {
        String instrumentJobDirectoryName = String.valueOf(Math.random());
        File directory = new File(instrumentJobDirectoryName);
        directory.mkdir();

        File sourceFileInJobDirectory = new File("instrumentJobDirectoryName/catSources_Short.txt");
        File sinkFileInJobDirectory = new File("instrumentJobDirectoryName/catSinks_Short.txt");
        File easyTaintWrapperSourceInJobDirectory = new File("instrumentJobDirectoryName/EasyTaintWrapperSource.txt");
        File apkFileInJobDirectory = new File("instrumentJobDirectoryName/fileToInstrument.apk");

        writeFileToDisk(sourceFile, sourceFileInJobDirectory);
        writeFileToDisk(sinkFile, sinkFileInJobDirectory);
        writeFileToDisk(easyTaintWrapperSource, easyTaintWrapperSourceInJobDirectory);
        writeFileToDisk(apkFile, apkFileInJobDirectory);

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
            String keystoreDirectory = ini.get("Keystore", "keyStorePath", String.class);
            String keystoreAlias = ini.get("Keystore", "mykeystore", String.class);
            String keystorePass = ini.get("Keystore", "laurent", String.class);
            String outputDirectory = instrumentationPepDirectory + directory.getAbsolutePath();

            String mainMethod = "de.ecspride.Main";

            //TODO introduce constants for separators
            return javaPath + encodingOption + "-classpath" + instrumentationPepDirectory + "/bin:" + instrumentationPepDirectory + "/libs/*" +
                    ":" + sootDirectory + "/testclasses:" + sootDirectory + "/classes:" + sootDirectory + "/libs/*" +
                    ":" + jasminDirectory + "classes:" + jasminDirectory + "/libs/*" +
                    ":" + herosDirectory + "target/classes:" + herosDirectory + "target/testclasses:" + herosDirectory + "/*" +
                    ":" + functionalJavaDirectory +
                    ":" + infoflowDirectory + "/bin:" + infoflowDirectory + "/lib/*" +
                    ":" + infoflowAndroidDirectory + "/bin:" + "/lib/*" +
                    " " + mainMethod +
                    " " + "-sourceFile " + sourceFileInJobDirectory +
                    " " + "-sinkFile" + sinkFileInJobDirectory +
                    " " + "-apkFile" + apkFileInJobDirectory +
                    " " + "-taintWrapper" + easyTaintWrapperSourceInJobDirectory +
                    " " + "-androidPlatforms" + androidPlatformDirectory +
                    " " + "-androidJar" + androidJarDirectory +
                    " " + "-j -o" + outputDirectory;


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private void writeFileToDisk(Object fileData, File file) {
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(fileData);
            fos.write(byteArrayOutputStream.toByteArray());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
