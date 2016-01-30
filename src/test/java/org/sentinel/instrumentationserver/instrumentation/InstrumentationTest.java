package org.sentinel.instrumentationserver.instrumentation;

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
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertArrayEquals;

public class InstrumentationTest {

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() throws Exception {
        Ini configIni = new Ini(new File("src/test/java/org/sentinel/instrumentationserver/test-config.ini"));
        Main.TIMEOUT_FOR_INSTRUMENTATION_IN_MINUTES = configIni.get("Fetch", "TimeoutForApkFetchingInMinutes", Integer.class);
        Main.INSTRUMENTATION_JOB_DIRECTORY = configIni.get("Directories", "DataDirectory", String.class);
        Main.DELETE_INSTRUMENTATION_JOB_DIRECTORY = configIni.get("Directories", "DeleteDataDirectory", Boolean.class);
        Main.METADATA_XML_URI = configIni.get("Fetch", "metadataXmlURL", String.class);

        MetadataDAO metadataDAO = new MetadataDAO();
        metadataDAO.initializeDatabase();

/*        System.out.println("Fetching metadata...");
        MetadataFetcher metadataFetcher = new MetadataFetcher();
        metadataFetcher.fetch();

        List<String> repositoryApkLinks = metadataDAO.getAllRepositoryApkLinks();
        ApkFetcherRunner apkFetcherRunner = new ApkFetcherRunner(repositoryApkLinks);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(apkFetcherRunner);
        executorService.shutdown();*/

        // start the server
        server = Main.startServer(configIni);
        // create the client
        Client client = ClientBuilder.newBuilder()
                .register(MultiPartFeature.class)
                .build();

        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and Main.startServer())
        // --
        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

        if (Main.FORWARDED_URI.equals("")) {
            target = client.target(Main.FORWARDED_URI);
        } else {
            target = client.target(Main.BASE_URI);
        }

    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    /**
     * Test to see that the message "Got it!" is sent in the response.
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

            assertArrayEquals(IOUtils.toByteArray(new FileInputStream(
                    "src/test/java/org/sentinel/instrumentationserver/instrumentation/kr.softgear.multiping_11-instrumented.apk")), instrumentedApkResponse);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
