package org.sentinel.instrumentationserver.instrumentation;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.io.IOUtils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.ini4j.Ini;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sentinel.instrumentationserver.Main;
import org.sentinel.instrumentationserver.generated.model.Apk;
import org.sentinel.instrumentationserver.metadata.MetadataDAO;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class tests the parts of the server that handle the instrumentation.
 */
public class InstrumentationTest {

    private HttpServer server;
    private WebTarget target;

    /**
     * Set up the server for the test.
     */
    @Before
    public void setUp() throws Exception {
        Ini configIni = new Ini(new File("src/test/java/org/sentinel/instrumentationserver/test-config.ini"));
        Main.TIMEOUT_FOR_INSTRUMENTATION_IN_MINUTES = configIni.get("Fetch", "TimeoutForApkFetchingInMinutes", Integer.class);
        Main.INSTRUMENTATION_JOB_DIRECTORY = configIni.get("Directories", "DataDirectory", String.class);
        Main.DELETE_INSTRUMENTATION_JOB_DIRECTORY = configIni.get("Directories", "DeleteDataDirectory", Boolean.class);
        Main.METADATA_XML_URI = configIni.get("Fetch", "metadataXmlURL", String.class);

        MetadataDAO metadataDAO = new MetadataDAO();
        metadataDAO.initializeDatabase();

        server = Main.startServer(configIni);
        Client client = ClientBuilder.newBuilder()
                .register(MultiPartFeature.class)
                .build();

        if (Main.FORWARDED_URI.equals("")) {
            target = client.target(Main.FORWARDED_URI);
        } else {
            target = client.target(Main.BASE_URI);
        }

    }

    /**
     * Shut down the server after the test.
     */
    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    /**
     * This test is used for making sure the basic usecase of instrumenting an APK without metadata works.
     */
    @Test
    public void testInstrumentWithoutMetadata() {
        File sourcesFile = new File("InstrumentationDependencies/files/catSources_Short.txt");
        File sinksFile = new File("InstrumentationDependencies/files/catSinks_Short.txt");
        File easyTaintWrapperSource = new File("InstrumentationDependencies/files/EasyTaintWrapperSource.txt");

        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        FormDataBodyPart formDataBodyPartSourceFile;
        try {
            formDataBodyPartSourceFile = new FormDataBodyPart("sourceFile", new FileInputStream(sourcesFile), MediaType.APPLICATION_OCTET_STREAM_TYPE);
            FormDataBodyPart formDataBodyPartSinkFile = new FormDataBodyPart("sinkFile", new FileInputStream(sinksFile), MediaType.APPLICATION_OCTET_STREAM_TYPE);
            FormDataBodyPart formDataBodyPartEasyTaintWrapperSource = new FormDataBodyPart("easyTaintWrapperSource", new FileInputStream(easyTaintWrapperSource), MediaType.APPLICATION_OCTET_STREAM_TYPE);
            URL url = new URL("https://f-droid.org/repo/kr.softgear.multiping_11.apk");
            URLConnection urlConnection = url.openConnection();
            FormDataBodyPart formDataBodyPartApkFile = new FormDataBodyPart("apkFile", urlConnection.getInputStream(), MediaType.APPLICATION_OCTET_STREAM_TYPE);

            formDataMultiPart.bodyPart(formDataBodyPartSourceFile).bodyPart(formDataBodyPartSinkFile).bodyPart(formDataBodyPartEasyTaintWrapperSource).bodyPart(formDataBodyPartApkFile);

            final javax.ws.rs.core.Response hashResponse = target.path("instrument/withoutmetadata").request().post(Entity.entity(formDataMultiPart, formDataMultiPart.getMediaType()));
            String apkHash = hashResponse.readEntity(Apk.class).getHash();

            while (target.path("instrument/" + apkHash).request().get().getStatus() != 200) {
                TimeUnit.SECONDS.sleep(2);
            }
            final javax.ws.rs.core.Response apkResponse = target.path("instrument/" + apkHash).request().get();
            byte[] instrumentedApkResponse = apkResponse.readEntity(byte[].class);

            CRC32 crc32 = new CRC32();
            crc32.update(IOUtils.toByteArray(new FileInputStream("InstrumentationDependencies/resources/protect.png")));

            net.lingala.zip4j.core.ZipFile zipFile = new ZipFile(new File(Main.INSTRUMENTATION_JOB_DIRECTORY + "/" + apkHash + "/alignedApk.apk"));
            List<FileHeader> fileHeaders = zipFile.getFileHeaders();
            Iterator<FileHeader> fileHeadersIterator = fileHeaders.iterator();

            boolean shieldImageIncludedInApk = false;

            while (fileHeadersIterator.hasNext()) {
                FileHeader fileHeader = fileHeadersIterator.next();
                if (fileHeader.getCrc32() == crc32.getValue()) {
                    shieldImageIncludedInApk = true;
                }
            }

            boolean bytesInResponseEqualInstrumentedApk = Arrays.equals(IOUtils.toByteArray(new FileInputStream(
                    Main.INSTRUMENTATION_JOB_DIRECTORY + "/" + apkHash + "/alignedApk.apk")), instrumentedApkResponse);

            assertTrue(shieldImageIncludedInApk && bytesInResponseEqualInstrumentedApk);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (ZipException e) {
            e.printStackTrace();
        }

    }

    /**
     * Test whether the instrumented APK includes the file protect.png that can be found in
     * InstrumentationDependencies/resources. This file is included in the instrumented APK by DroidForce.
     */
    @Test
    public void testApkIncludesShieldImage() {

    }
}