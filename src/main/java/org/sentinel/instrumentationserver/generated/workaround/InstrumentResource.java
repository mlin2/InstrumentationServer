
package org.sentinel.instrumentationserver.generated.workaround;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.sentinel.instrumentationserver.generated.model.Apk;
import org.sentinel.instrumentationserver.generated.model.Apks;
import org.sentinel.instrumentationserver.generated.model.Error;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;


/**
 * Instrument APKs based on the configuration files contained in the request and receive instrumented APKs as well as
 * their hashes. This file was generated but manually edited in order to allow form-data MultiPart data to be accepted
 * and in order to return a binary blob of the APK.
 */
@Path("instrument")
public interface InstrumentResource {


    /**
     * Instrument an apk file based on the configuration files attached
     * to the form-data MultiPart request and store it with the corresponding metadata. If the boolean "makeAppPublic"
     * is set to true, the APK will be made public in the sentinel app store.
     *
     * @param sourceFile             Source file containing the android's source methods e.g. UNIQUE_IDENTIFIER:
     *                               <android.telephony.TelephonyManager: java.lang.String getDeviceId()> (UNIQUE_IDENTIFIER)
     *                               <android.telephony.TelephonyManager: java.lang.String getSubscriberId()> (UNIQUE_IDENTIFIER)
     *                               <android.telephony.TelephonyManager: java.lang.String getSimSerialNumber()> (UNIQUE_IDENTIFIER)
     *                               <android.telephony.TelephonyManager: java.lang.String getLine1Number()> (UNIQUE_IDENTIFIER)
     * @param sinkFile               Sink file containing the android's sink methods e.g. LOG:
     *                               <android.util.Log: int d(java.lang.String,java.lang.String)> (LOG)
     *                               <android.util.Log: int d(java.lang.String,java.lang.String,java.lang.Throwable)> (LOG)
     *                               <android.util.Log: int e(java.lang.String,java.lang.String)> (LOG)
     *                               <android.util.Log: int e(java.lang.String,java.lang.String,java.lang.Throwable)> (LOG)
     *                               <android.util.Log: int i(java.lang.String,java.lang.String)> (LOG)
     *                               <android.util.Log: int i(java.lang.String,java.lang.String,java.lang.Throwable)> (LOG)
     *                               <android.util.Log: int v(java.lang.String,java.lang.String)> (LOG)
     *                               <android.util.Log: int v(java.lang.String,java.lang.String,java.lang.Throwable)> (LOG)
     *                               <android.util.Log: int w(java.lang.String,java.lang.Throwable)> (LOG)
     *                               <android.util.Log: int w(java.lang.String,java.lang.String)> (LOG)
     *                               <android.util.Log: int w(java.lang.String,java.lang.String,java.lang.Throwable)> (LOG)
     *                               <android.util.Log: int wtf(java.lang.String,java.lang.Throwable)> (LOG)
     *                               <android.util.Log: int wtf(java.lang.String,java.lang.String)> (LOG)
     *                               <android.util.Log: int wtf(java.lang.String,java.lang.String,java.lang.Throwable)> (LOG)
     *                               <android.app.UiModeManager: void setNightMode(int)> (LOG)
     * @param easyTaintWrapperSource Taint wrapper file containing the android's package names that
     *                               should be considered during the instrumentation phase
     *                               e.g. # Packages to include in the analysis
     *                               ^android.
     *                               ^java.
     *                               ^org.apache.http.
     * @param apkFile                APK file that should be instrumented
     * @param appName                The name of the APK
     * @param packageName            The name of the package of the APK
     * @param makeAppPublic          Whether the app should be made public in the sentinel app store.
     */
    @POST
    @Path("withmetadata")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({
            "application/json"
    })
    InstrumentResource.PostInstrumentWithmetadataResponse postInstrumentWithmetadata(@FormDataParam("file") InputStream sourceFile, @FormDataParam("file") InputStream sinkFile,
                                                                                     @FormDataParam("file") InputStream easyTaintWrapperSource, @FormDataParam("file") InputStream apkFile,
                                                                                     @FormDataParam("file") InputStream logo, @FormDataParam("text") String appName,
                                                                                     @FormDataParam("text") String packageName, @FormDataParam("makeAppPublic") boolean makeAppPublic)
            throws Exception
    ;

    /**
     * Instrument an apk file based on the configuration files attached
     * to the form-data MultiPart request without storing any metadata. On this resource, the APK will not be
     * made public in the sentinel app store.
     *
     * @param sourceFile             Source file containing the android's source methods e.g. UNIQUE_IDENTIFIER:
     *                               <android.telephony.TelephonyManager: java.lang.String getDeviceId()> (UNIQUE_IDENTIFIER)
     *                               <android.telephony.TelephonyManager: java.lang.String getSubscriberId()> (UNIQUE_IDENTIFIER)
     *                               <android.telephony.TelephonyManager: java.lang.String getSimSerialNumber()> (UNIQUE_IDENTIFIER)
     *                               <android.telephony.TelephonyManager: java.lang.String getLine1Number()> (UNIQUE_IDENTIFIER)
     * @param sinkFile               Sink file containing the android's sink methods e.g. LOG:
     *                               <android.util.Log: int d(java.lang.String,java.lang.String)> (LOG)
     *                               <android.util.Log: int d(java.lang.String,java.lang.String,java.lang.Throwable)> (LOG)
     *                               <android.util.Log: int e(java.lang.String,java.lang.String)> (LOG)
     *                               <android.util.Log: int e(java.lang.String,java.lang.String,java.lang.Throwable)> (LOG)
     *                               <android.util.Log: int i(java.lang.String,java.lang.String)> (LOG)
     *                               <android.util.Log: int i(java.lang.String,java.lang.String,java.lang.Throwable)> (LOG)
     *                               <android.util.Log: int v(java.lang.String,java.lang.String)> (LOG)
     *                               <android.util.Log: int v(java.lang.String,java.lang.String,java.lang.Throwable)> (LOG)
     *                               <android.util.Log: int w(java.lang.String,java.lang.Throwable)> (LOG)
     *                               <android.util.Log: int w(java.lang.String,java.lang.String)> (LOG)
     *                               <android.util.Log: int w(java.lang.String,java.lang.String,java.lang.Throwable)> (LOG)
     *                               <android.util.Log: int wtf(java.lang.String,java.lang.Throwable)> (LOG)
     *                               <android.util.Log: int wtf(java.lang.String,java.lang.String)> (LOG)
     *                               <android.util.Log: int wtf(java.lang.String,java.lang.String,java.lang.Throwable)> (LOG)
     *                               <android.app.UiModeManager: void setNightMode(int)> (LOG)
     * @param easyTaintWrapperSource Taint wrapper file containing the android's package names that
     *                               should be considered during the instrumentation phase
     *                               e.g. # Packages to include in the analysis
     *                               ^android.
     *                               ^java.
     *                               ^org.apache.http.
     * @param apkFile                APK file that should be instrumented
     */
    @POST
    @Path("withoutmetadata")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({
            "application/json"
    })
    InstrumentResource.PostInstrumentWithoutmetadataResponse postInstrumentWithoutmetadata(@FormDataParam("file") InputStream sourceFile, @FormDataParam("file") InputStream sinkFile,
                                                                                           @FormDataParam("file") InputStream easyTaintWrapperSource, @FormDataParam("file") InputStream apkFile)
            throws Exception
    ;

    /**
     * Retrieve the instrumented apk as a binary blob based on the hash sum value of the non-instrumented version of
     * the APK. The hash value is calculated from the non-instrumented apk with sha512.
     */
    @GET
    @Path("{apkHash}")
    @Produces({
            "application/json"
    })
    InstrumentResource.GetInstrumentByApkHashResponse getInstrumentByApkHash(
            @PathParam("apkHash")
            String apkHash)
            throws Exception
    ;

    /**
     * Retrieve a list of instrumented apk files
     */
    @GET
    @Path("all")
    @Produces({
            "application/json"
    })
    InstrumentResource.GetInstrumentAllResponse getInstrumentAll()
            throws Exception
    ;

    /**
     * The response for using GET on the /instrument/apkhash resource where apkhash corresponds to the sha512sum of
     * the APK sent to the server. The APK will be returned as a binary dump.
     * An OK gets returned if the instrumented, signed and aligned APK is stored in the database and otherwise
     * returns a Json not found error.
     */
    class GetInstrumentByApkHashResponse
            extends org.sentinel.instrumentationserver.generated.resource.support.ResponseWrapper {


        private GetInstrumentByApkHashResponse(Response delegate) {
            super(delegate);
        }

        /**
         * @param apkFile A binary blob of the APK that, before instrumentation and signing, corresponded
         *                to the SHA512 hash sum of an APK file in an instrumentation request.
         */
        public static InstrumentResource.GetInstrumentByApkHashResponse withOK(byte[] apkFile) {
            Response.ResponseBuilder responseBuilder = Response.status(200);
            responseBuilder.entity(apkFile);
            return new InstrumentResource.GetInstrumentByApkHashResponse(responseBuilder.build());
        }

        public static InstrumentResource.GetInstrumentByApkHashResponse withJsonNotFound(Error entity) {
            Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new InstrumentResource.GetInstrumentByApkHashResponse(responseBuilder.build());
        }

    }

    /**
     * The response for using GET on the /instrument/all resource. It lists all instrumented APKs.
     */
    class GetInstrumentAllResponse
            extends org.sentinel.instrumentationserver.generated.resource.support.ResponseWrapper {


        private GetInstrumentAllResponse(Response delegate) {
            super(delegate);
        }

        /**
         * @param entity a list of hashes of all the APKs that are instrumented, signed and available for retrieval.
         */
        public static InstrumentResource.GetInstrumentAllResponse withJsonOK(Apks entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new InstrumentResource.GetInstrumentAllResponse(responseBuilder.build());
        }

    }

    /**
     * The response for using POST on /instrument/withmetadata. The APK posted to the resource gets instrumented and
     * signed and stored in the database together with the APKs metadata. As soon as instrumentation is done, the APK can be retrieved by using
     * GET on /instrument/apkhash where apkhash corresponds to the sha512sum of the not yet instrumented APK.
     */
    class PostInstrumentWithmetadataResponse
            extends org.sentinel.instrumentationserver.generated.resource.support.ResponseWrapper {


        private PostInstrumentWithmetadataResponse(Response delegate) {
            super(delegate);
        }


        public static InstrumentResource.PostInstrumentWithmetadataResponse withJsonAccepted(Apk entity) {
            Response.ResponseBuilder responseBuilder = Response.status(202).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new InstrumentResource.PostInstrumentWithmetadataResponse(responseBuilder.build());
        }

        public static InstrumentResource.PostInstrumentWithmetadataResponse withJsonBadRequest(Error entity) {
            Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new InstrumentResource.PostInstrumentWithmetadataResponse(responseBuilder.build());
        }

    }

    /**
     * The response for using POST on /instrument/withoutmetadata. The APK posted to the resource gets instrumented and signed and
     * stored in the database. As soon as instrumentation is done, the APK can be retrieved by using
     * GET on /instrument/apkhash where apkhash corresponds to the sha512sum of the not yet instrumented APK.
     */
    class PostInstrumentWithoutmetadataResponse
            extends org.sentinel.instrumentationserver.generated.resource.support.ResponseWrapper {


        private PostInstrumentWithoutmetadataResponse(Response delegate) {
            super(delegate);
        }

        public static InstrumentResource.PostInstrumentWithoutmetadataResponse withJsonAccepted(Apk entity) {
            Response.ResponseBuilder responseBuilder = Response.status(202).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new InstrumentResource.PostInstrumentWithoutmetadataResponse(responseBuilder.build());
        }

        public static InstrumentResource.PostInstrumentWithoutmetadataResponse withJsonBadRequest(Error entity) {
            Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new InstrumentResource.PostInstrumentWithoutmetadataResponse(responseBuilder.build());
        }

    }


}
