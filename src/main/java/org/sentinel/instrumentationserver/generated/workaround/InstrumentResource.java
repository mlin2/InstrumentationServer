
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
 * Instrument APKs based on the configuration files contained in the request and give information about the APKs.
 * This file was generated but manually edited in order to allow form-data MultiPart data to be accepted and
 * in order to return a binary blob of the APK.
 */
@Path("instrument")
public interface InstrumentResource {


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
     * Instrument an apk file based on the configuration files attached
     * to the request form-data MultiPart request.
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
     * Retrieve the instrumented apk as a binary blob based on its hash sum value.
     * The hash value is calculated from the non-instrumented apk with sha512.
     *
     * @param apkHash
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
     * The response for using GET on the /instrument/apkhash resource where apkhash corresponds to the sha512sum of
     * the not yet instrumented APK. An OK gets returned if the instrumented and signed APK
     * is already stored in the database and otherwise returns a Json not found error.
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

        /**
         * e.g. {
         * "errorId": "1",
         * "msg": "Bad format"
         * }
         *
         * @param entity {
         *               "errorId": "1",
         *               "msg": "Bad format"
         *               }
         */
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
            extends org.sentinel.instrumentationserver.generated.resource.support.ResponseWrapper
    {


        private GetInstrumentAllResponse(Response delegate) {
            super(delegate);
        }

        /**
         * @param entity a list of all the APKs that are instrumented, signed and available for retrieval.
         *               {
         *               "apks": [
         *               {
         *               "hash": "f4439018f1cd7e5d770c77743533e31bc76fcf31950dca09991c93bece5bb49e9896d6e3d5d597ed983fdb444505dbc9cd336b58c3917645cfbc97b6b05d8791",
         *               "url": "http://sentinel.de/instrumentedApps/f4439018f1cd7e5d770c77743533e31bc76fcf31950dca09991c93bece5bb49e9896d6e3d5d597ed983fdb444505dbc9cd336b58c3917645cfbc97b6b05d8791"
         *               }, {
         *               "hash": "1652d23fd36ffdb6eebe21b9b6ae5d09aeb6d40c929fdc45cb2621b4538084d5b0b34d64803608d2ad30d918d0d194a20ad8483a9ae564fefd41eddc43a6f05e",
         *               "url": "http://sentinel.de/instrumentedApps/1652d23fd36ffdb6eebe21b9b6ae5d09aeb6d40c929fdc45cb2621b4538084d5b0b34d64803608d2ad30d918d0d194a20ad8483a9ae564fefd41eddc43a6f05e"
         *               }
         *               ]
         *               }
         */
        public static InstrumentResource.GetInstrumentAllResponse withJsonOK(Apks entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new InstrumentResource.GetInstrumentAllResponse(responseBuilder.build());
        }

    }

    /**
     * The response for using POST on /instrument. The APK posted to the resource gets instrumented and signed and
     * stored in the database. As soon as instrumentation is done, the APK can be retrieved by using GET on
     * /instrument/apkhash where corresponds to the sha512sum of the not yet instrumented APK.
     */
    class PostInstrumentResponse
            extends org.sentinel.instrumentationserver.generated.resource.support.ResponseWrapper {


        private PostInstrumentResponse(Response delegate) {
            super(delegate);
        }

        /**
         * @param entity returns the sha512sum of the APK.
         */
        public static InstrumentResource.PostInstrumentResponse withJsonAccepted(Apk entity) {
            Response.ResponseBuilder responseBuilder = Response.status(202).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new InstrumentResource.PostInstrumentResponse(responseBuilder.build());
        }

        /**
         * e.g. {
         * "errorId": "1",
         * "msg": "Bad format"
         * }
         *
         * @param entity {
         *               "errorId": "1",
         *               "msg": "Bad format"
         *               }
         */
        public static InstrumentResource.PostInstrumentResponse withJsonBadRequest(Error entity) {
            Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new InstrumentResource.PostInstrumentResponse(responseBuilder.build());
        }

    }

    public class PostInstrumentWithmetadataResponse
            extends org.sentinel.instrumentationserver.generated.resource.support.ResponseWrapper
    {


        private PostInstrumentWithmetadataResponse(Response delegate) {
            super(delegate);
        }

        /**
         *  e.g. {
         *   "hash": "f4439018f1cd7e5d770c77743533e31bc76fcf31950dca09991c93bece5bb49e9896d6e3d5d597ed983fdb444505dbc9cd336b58c3917645cfbc97b6b05d8791"
         * }
         *
         *
         * @param entity
         *     {
         *       "hash": "f4439018f1cd7e5d770c77743533e31bc76fcf31950dca09991c93bece5bb49e9896d6e3d5d597ed983fdb444505dbc9cd336b58c3917645cfbc97b6b05d8791"
         *     }
         *
         */
        public static InstrumentResource.PostInstrumentWithmetadataResponse withJsonAccepted(Apk entity) {
            Response.ResponseBuilder responseBuilder = Response.status(202).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new InstrumentResource.PostInstrumentWithmetadataResponse(responseBuilder.build());
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
        public static InstrumentResource.PostInstrumentWithmetadataResponse withJsonBadRequest(Error entity) {
            Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new InstrumentResource.PostInstrumentWithmetadataResponse(responseBuilder.build());
        }

    }

    public class PostInstrumentWithoutmetadataResponse
            extends org.sentinel.instrumentationserver.generated.resource.support.ResponseWrapper
    {


        private PostInstrumentWithoutmetadataResponse(Response delegate) {
            super(delegate);
        }

        /**
         *  e.g. {
         *   "hash": "f4439018f1cd7e5d770c77743533e31bc76fcf31950dca09991c93bece5bb49e9896d6e3d5d597ed983fdb444505dbc9cd336b58c3917645cfbc97b6b05d8791"
         * }
         *
         *
         * @param entity
         *     {
         *       "hash": "f4439018f1cd7e5d770c77743533e31bc76fcf31950dca09991c93bece5bb49e9896d6e3d5d597ed983fdb444505dbc9cd336b58c3917645cfbc97b6b05d8791"
         *     }
         *
         */
        public static InstrumentResource.PostInstrumentWithoutmetadataResponse withJsonAccepted(Apk entity) {
            Response.ResponseBuilder responseBuilder = Response.status(202).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new InstrumentResource.PostInstrumentWithoutmetadataResponse(responseBuilder.build());
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
        public static InstrumentResource.PostInstrumentWithoutmetadataResponse withJsonBadRequest(Error entity) {
            Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new InstrumentResource.PostInstrumentWithoutmetadataResponse(responseBuilder.build());
        }

    }


}
