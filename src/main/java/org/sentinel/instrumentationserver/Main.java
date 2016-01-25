package org.sentinel.instrumentationserver;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.ini4j.Ini;
import org.sentinel.instrumentationserver.resource.impl.InstrumentResourceImpl;
import org.sentinel.instrumentationserver.resource.impl.MetadataResourceImpl;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * This Main class handles the configuration and startup of the Jersey framework, Grizzly NIO framework and
 * instrumentation database components.
 */
public class Main {

    // Base URI the Grizzly HTTP server will listen on
    public static String BASE_URI;

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     *
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in org.sentinel.instrumentationserver package
        final ResourceConfig rc = new ResourceConfig().packages("org.sentinel.instrumentationserver.resource");
        rc.register(InstrumentResourceImpl.class);
        rc.register(MetadataResourceImpl.class);
        rc.register(MultiPartFeature.class);
        rc.register(JacksonFeature.class);

        // Determine the URL and port the Grizzly HTTP server will listen on
        Ini ini = null;
        try {
            ini = new Ini(new File("config.ini"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String serverUrl = ini.get("URL", "ServerUrl", String.class);
        int serverPort = ini.get("Port", "ServerPort", Integer.class);
        int forwardedPort = 0;
        if (ini.get("Port", "ForwardedPort", Integer.class) != null) {
            forwardedPort = ini.get("Port", "ForwardedPort", Integer.class);
        }


        if(forwardedPort != 0) {
            BASE_URI = serverUrl + ":" + forwardedPort + "/";
        } else {
            BASE_URI = serverUrl + ":" + serverPort + "/";
        }
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }
    
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));

        InstrumentationDAO instrumentationDAO = InstrumentationDAO.getInstance();
        instrumentationDAO.initializeDatabase();


        System.in.read();
        server.stop();
    }
}