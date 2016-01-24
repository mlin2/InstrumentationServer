
package org.sentinel.instrumentationserver.generated.workaround;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.sentinel.instrumentationserver.generated.model.Error;
import org.sentinel.instrumentationserver.generated.model.MetadataList;

@Path("metadata")
public interface MetadataResource {


    /**
     * Retrieve a list of all the metadata of all instrumented apps
     * 
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
     * Retrieve the logo of the APK corresponding to the hash.
     * 
     * 
     * @param apkHash
     *     
     */
    @GET
    @Path("logo/{apkHash}")
    @Produces({
        "application/json"
    })
    MetadataResource.GetMetadataLogoByApkHashResponse getMetadataLogoByApkHash(
        @PathParam("apkHash")
        String apkHash)
        throws Exception
    ;

    public class GetMetadataAllResponse
        extends org.sentinel.instrumentationserver.generated.resource.support.ResponseWrapper
    {


        private GetMetadataAllResponse(Response delegate) {
            super(delegate);
        }

        /**
         * 
         * @param entity
         *     
         */
        public static MetadataResource.GetMetadataAllResponse withJsonOK(MetadataList entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new MetadataResource.GetMetadataAllResponse(responseBuilder.build());
        }

    }

    public class GetMetadataLogoByApkHashResponse
        extends org.sentinel.instrumentationserver.generated.resource.support.ResponseWrapper
    {


        private GetMetadataLogoByApkHashResponse(Response delegate) {
            super(delegate);
        }

        /**
         *
         * @param logoFile
         *     
         */
        public static MetadataResource.GetMetadataLogoByApkHashResponse withFormdataOK(byte[] logoFile) {
            Response.ResponseBuilder responseBuilder = Response.status(200);
            responseBuilder.entity(logoFile);
            return new MetadataResource.GetMetadataLogoByApkHashResponse(responseBuilder.build());
        }

        /**
         *  e.g. {
         *   "errorId": "1",
         *   "msg": "Bad format"
         * }
         * 
         * 
         * @param entity
         *     {
         *       "errorId": "1",
         *       "msg": "Bad format"
         *     }
         *     
         */
        public static MetadataResource.GetMetadataLogoByApkHashResponse withJsonNotFound(Error entity) {
            Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new MetadataResource.GetMetadataLogoByApkHashResponse(responseBuilder.build());
        }

    }

}
