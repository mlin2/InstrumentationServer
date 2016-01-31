package org.sentinel.instrumentationserver;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.ini4j.Ini;
import org.sentinel.instrumentationserver.instrumentation.ApkFetcherRunner;
import org.sentinel.instrumentationserver.metadata.MetadataDAO;
import org.sentinel.instrumentationserver.metadata.MetadataFetcher;
import org.sentinel.instrumentationserver.resource.impl.InstrumentResourceImpl;
import org.sentinel.instrumentationserver.resource.impl.MetadataResourceImpl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This Main class handles the configuration and startup of the Jersey framework, Grizzly NIO framework and
 * instrumentation server components.
 */
public class Main {

    /**
     * Base URI the Grizzly HTTP server will listen on.
     */
    public static String BASE_URI;

    /**
     * In case a port is forwarded e.g. to make the server publicly available, use this URI.
     */
    public static String FORWARDED_URI;

    /**
     * After this time in minutes the instrumentation process will be cancelled.
     */
    public static long TIMEOUT_FOR_INSTRUMENTATION_IN_MINUTES;

    /**
     * The directory the instrumentation jobs will be stored in.
     */
    public static String INSTRUMENTATION_JOB_DIRECTORY;

    /**
     * Whether the instrumentation job directory should be deleted after instrumentation.
     */
    public static boolean DELETE_INSTRUMENTATION_JOB_DIRECTORY;

    /**
     * The Uniform Resource Identifier to the XML the metadata is retrieved from. The XML has to be in the format
     * of https://f-droid.org/repo/index.xml.
     */
    public static String METADATA_XML_URI;

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application, read the config file
     * and start all the configured services.
     */
    public static HttpServer startServer(Ini configIni) {
        // create a resource config that scans for JAX-RS resources and providers
        // in org.sentinel.instrumentationserver package
        final ResourceConfig rc = new ResourceConfig().packages("org.sentinel.instrumentationserver.resource");
        rc.register(InstrumentResourceImpl.class);
        rc.register(MetadataResourceImpl.class);
        rc.register(MultiPartFeature.class);
        rc.register(JacksonFeature.class);

        String serverUrl = configIni.get("URL", "ServerUrl", String.class);
        int serverPort = configIni.get("Port", "ServerPort", Integer.class);
        int forwardedPort = 0;
        if (configIni.get("Port", "ForwardedPort", Integer.class) != null) {
            forwardedPort = configIni.get("Port", "ForwardedPort", Integer.class);
        }
        boolean enableHttps = configIni.get("Security", "enableHTTPS", Boolean.class);
        String securityKeystoreFile = configIni.get("Security", "keyStorePathSecurity", String.class);
        String securityKeystorePassword = configIni.get("Security", "storePassSecurity", String.class);


        BASE_URI = serverUrl + ":" + serverPort + "/";

        if (forwardedPort != 0) {
            FORWARDED_URI = serverUrl + ":" + forwardedPort + "/";
        } else {
            FORWARDED_URI = BASE_URI;
        }
        if (enableHttps) {
            SSLContextConfigurator sslContextConfigurator = new SSLContextConfigurator();
            sslContextConfigurator.setKeyStoreFile(securityKeystoreFile);
            sslContextConfigurator.setKeyPass(securityKeystorePassword);
            SSLEngineConfigurator sslEngineConfigurator = new SSLEngineConfigurator(sslContextConfigurator).setClientMode(false);

            return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc, true, sslEngineConfigurator);
        }
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {

        Ini configIni = new Ini(new File("config.ini"));
        TIMEOUT_FOR_INSTRUMENTATION_IN_MINUTES = configIni.get("Fetch", "TimeoutForApkFetchingInMinutes", Integer.class);
        INSTRUMENTATION_JOB_DIRECTORY = configIni.get("Directories", "DataDirectory", String.class);
        DELETE_INSTRUMENTATION_JOB_DIRECTORY = configIni.get("Directories", "DeleteDataDirectory", Boolean.class);
        METADATA_XML_URI = configIni.get("Fetch", "metadataXmlURL", String.class);

        MetadataDAO metadataDAO = new MetadataDAO();
        metadataDAO.initializeDatabase();

        if (configIni.get("Fetch", "fetchMetadata", Boolean.class)) {
            System.out.println("Fetching metadata...");
            MetadataFetcher metadataFetcher = new MetadataFetcher();
            metadataFetcher.fetch();
        }

        if (configIni.get("Fetch", "fetchFdroidApks", Boolean.class)) {
            List<String> repositoryApkLinks = metadataDAO.getAllRepositoryApkLinks();
            ApkFetcherRunner apkFetcherRunner = new ApkFetcherRunner(repositoryApkLinks);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(apkFetcherRunner);
            executorService.shutdown();
        }

        final HttpServer server = startServer(configIni);
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));

        System.in.read();
        server.shutdown();
    }
}