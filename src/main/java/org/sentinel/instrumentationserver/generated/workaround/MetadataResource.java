
package org.sentinel.instrumentationserver.generated.workaround;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.sentinel.instrumentationserver.generated.model.Error;
import org.sentinel.instrumentationserver.generated.model.MetadataList;

/**
 * Get metadata for instrumented APKs, all stored metadata and logos of APKs from their hash. The kinds of metadata
 * saved are inspired from F-Droid (https://f-droid.org/). This file was generated but manually edited in order to
 * return a binary blob of the image as a png.
 */
@Path("metadata")
public interface MetadataResource {


    /**
     * Retrieve a list of all the metadata of all instrumented apps currently stored on the server.
     */
    @GET
    @Path("instrumented")
    @Produces({
            "application/json"
    })
    MetadataResource.GetMetadataInstrumentedResponse getMetadataInstrumented()
            throws Exception
    ;

    /**
     * Retrieve a list of all the metadata currently stored on the server.
     */
    @GET
    @Path("all")
    @Produces({
            "application/json"
    })
    MetadataResource.GetMetadataAllResponse getMetadataAll()
            throws Exception
    ;

    /**
     * Retrieve the logo of the APK corresponding to the hash specified by "{apkHash}" with the .png extension.
     */
    @GET
    @Path("logo/{apkHash}.png")
    @Produces({
            "image/png"
    })
    MetadataResource.GetMetadataLogoByApkHashResponse getMetadataLogoByApkHash(
            @PathParam("apkHash")
            String apkHash)
            throws Exception
    ;

    /**
     * The response for the /metadata/all resource. It returns a list of all the metadata on the server.
     */
    class GetMetadataAllResponse
            extends org.sentinel.instrumentationserver.generated.resource.support.ResponseWrapper {


        private GetMetadataAllResponse(Response delegate) {
            super(delegate);
        }

        public static MetadataResource.GetMetadataAllResponse withJsonOK(MetadataList entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new MetadataResource.GetMetadataAllResponse(responseBuilder.build());
        }

    }

    /**
     * The response for the /metatdata/instrumented resource. It returns a list of all the metadata corresponding
     * to instrumented APKs that are made public either because they were posted on /instrument/withMetadata and were
     * specified to be made public or because they were fetched from the F-Droid repository.
     */
    class GetMetadataInstrumentedResponse
            extends org.sentinel.instrumentationserver.generated.resource.support.ResponseWrapper {


        private GetMetadataInstrumentedResponse(Response delegate) {
            super(delegate);
        }

        public static MetadataResource.GetMetadataInstrumentedResponse withJsonOK(MetadataList entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new MetadataResource.GetMetadataInstrumentedResponse(responseBuilder.build());
        }

    }

    /**
     * The response for the /metadata/logo/apkhash resource that returns a binary dump with the .png extension.
     */
    class GetMetadataLogoByApkHashResponse
            extends org.sentinel.instrumentationserver.generated.resource.support.ResponseWrapper {


        private GetMetadataLogoByApkHashResponse(Response delegate) {
            super(delegate);
        }

        public static MetadataResource.GetMetadataLogoByApkHashResponse withFormdataOK(byte[] logoFile) {
            Response.ResponseBuilder responseBuilder = Response.status(200);
            responseBuilder.entity(logoFile);
            return new MetadataResource.GetMetadataLogoByApkHashResponse(responseBuilder.build());
        }

        public static MetadataResource.GetMetadataLogoByApkHashResponse withJsonNotFound(Error entity) {
            Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new MetadataResource.GetMetadataLogoByApkHashResponse(responseBuilder.build());
        }

    }

}
