
package org.sentinel.instrumentationserver.generated.workaround;

import javax.mail.internet.MimeMultipart;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.sentinel.instrumentationserver.generated.model.Apk;
import org.sentinel.instrumentationserver.generated.model.Apks;
import org.sentinel.instrumentationserver.generated.model.Error;

import java.io.InputStream;


/**
 * Instrument apk based on the configuration files contained in the request.
 *
 *
 */
@Path("instrument")
public interface InstrumentResource {


    /**
     * Retrieve a list of instrumented apk files
     *
     */
    @GET
    @Produces({
            "application/json"
    })
    InstrumentResource.GetInstrumentResponse getInstrument()
            throws Exception
    ;

    /**
     * Instrument an apk file based on the configuration files attached
     * to the request
     *
     *
     * @param entity
     *     sourceFile: Source File - Source file containing the android's source methods e.g. UNIQUE_IDENTIFIER:
     *     <android.telephony.TelephonyManager: java.lang.String getDeviceId()> (UNIQUE_IDENTIFIER)
     *     <android.telephony.TelephonyManager: java.lang.String getSubscriberId()> (UNIQUE_IDENTIFIER)
     *     <android.telephony.TelephonyManager: java.lang.String getSimSerialNumber()> (UNIQUE_IDENTIFIER)
     *     <android.telephony.TelephonyManager: java.lang.String getLine1Number()> (UNIQUE_IDENTIFIER)
     *
     *     LOCATION_INFORMATION:
     *     <android.location.Location: double getLatitude()> (LOCATION_INFORMATION)
     *     <android.location.Location: double getLongitude()> (LOCATION_INFORMATION)
     *
     *     FILE_INFORMATION:
     *     <java.net.URLConnection: java.io.OutputStream getOutputStream()> (FILE_INFORMATION)
     *     <java.net.URLConnection: java.io.InputStream getInputStream()> (FILE_INFORMATION)
     *
     *     NETWORK_INFORMATION:
     *     <org.apache.http.HttpResponse: org.apache.http.HttpEntity getEntity()> (NETWORK_INFORMATION)
     *     <org.apache.http.util.EntityUtils: java.lang.String toString(org.apache.http.HttpEntity)> (NETWORK_INFORMATION)
     *     <org.apache.http.util.EntityUtils: java.lang.String toString(org.apache.http.HttpEntity,java.lang.String)> (NETWORK_INFORMATION)
     *     <org.apache.http.util.EntityUtils: byte[] toByteArray(org.apache.http.HttpEntity)> (NETWORK_INFORMATION)
     *     <org.apache.http.util.EntityUtils: java.lang.String getContentCharSet(org.apache.http.HttpEntity)> (NETWORK_INFORMATION)
     *     <android.net.wifi.WifiInfo: java.lang.String: getMacAddress()> (NETWORK_INFORMATION)
     *     <android.net.wifi.WifiInfo: java.lang.String getSSID()> (NETWORK_INFORMATION)
     *     <android.telephony.gsm.GsmCellLocation: int getCid()> (NETWORK_INFORMATION)
     *     <android.telephony.gsm.GsmCellLocation: int getLac()> (NETWORK_INFORMATION)
     *     <android.location.Location: double getLongitude()> (NETWORK_INFORMATION)
     *     <android.location.Location: double getLatitude()> (NETWORK_INFORMATION)
     *     <android.net.nsd.NsdServiceInfo: int getPort()> (NETWORK_INFORMATION)
     *     <android.net.ConnectivityManager: android.net.NetworkInfo[] getAllNetworkInfo()> (NETWORK_INFORMATION)
     *
     *     AUDIO:
     *     <android.media.AudioRecord: int read(short[],int,int)> (AUDIO)
     *     <android.media.AudioRecord: int read(byte[],int,int)> (AUDIO)
     *     <android.media.AudioRecord: int read(java.nio.ByteBuffer,int)> (AUDIO)
     *
     *     LOCATION_INFORMATION:
     *     <android.location.LocationManager: android.location.Location getLastKnownLocation(java.lang.String)> (LOCATION_INFORMATION)
     *     <java.util.Locale: java.lang.String getCountry()> (LOCATION_INFORMATION)
     *     <java.util.Calendar: java.util.TimeZone getTimeZone()> (LOCATION_INFORMATION)
     *
     *     PHONE_INFORMATION:
     *     <android.content.pm.PackageManager: java.util.List getInstalledApplications(int)> (PHONE_INFORMATION)
     *     <android.content.pm.PackageManager: java.util.List getInstalledPackages(int)> (PHONE_INFORMATION)
     *     <android.content.pm.PackageManager: java.util.List queryIntentActivities(android.content.Intent,int)> (PHONE_INFORMATION)
     *     <android.content.pm.PackageManager: java.util.List queryIntentServices(android.content.Intent,int)> (PHONE_INFORMATION)
     *     <android.content.pm.PackageManager: java.util.List queryBroadcastReceivers(android.content.Intent,int)> (PHONE_INFORMATION)
     *     <android.content.pm.PackageManager: java.util.List queryContentProviders(java.lang.String,int,int)> (PHONE_INFORMATION)
     *
     *     SMS_MMS:
     *     <android.os.Handler: android.os.Message obtainMessage()> (SMS_MMS)
     *     <android.os.Handler: android.os.Message obtainMessage(int,int,int)> (SMS_MMS)
     *     <android.os.Handler: android.os.Message obtainMessage(int,int,int,java.lang.Object)> (SMS_MMS)
     *     <android.os.Handler: android.os.Message obtainMessage(int)> (SMS_MMS)
     *     <android.os.Handler: android.os.Message obtainMessage(int,java.lang.Object)> (SMS_MMS)
     *
     *     BLUETOOTH_INFORMATION:
     *     <android.bluetooth.BluetoothAdapter: java.lang.String getAddress()> (BLUETOOTH_INFORMATION)
     *
     *     ACCOUNT_INFORMATION:
     *     <android.accounts.AccountManager: android.accounts.Account[] getAccounts()> (ACCOUNT_INFORMATION)
     *
     *     BROWSER_INFORMATION:
     *     <android.provider.Browser: android.database.Cursor getAllBookmarks()> (BROWSER_INFORMATION)
     *     <android.provider.Browser: android.database.Cursor getAllVisitedUrls()> (BROWSER_INFORMATION)
     *     <java.net.URL: java.net.URLConnection openConnection()> (BROWSER_INFORMATION)
     *
     *     DATABASE_INFORMATION:
     *     <android.content.ContentResolver: android.database.Cursor query(android.net.Uri,java.lang.String[],java.lang.String,java.lang.String[],java.lang.String)> (DATABASE_INFORMATION)
     *     <android.content.ContentResolver: android.database.Cursor query(android.net.Uri,java.lang.String[],java.lang.String,java.lang.String[],java.lang.String,android.os.CancellationSignal)> (DATABASE_INFORMATION)
     *
     *     INTER_APP_COMMUNICATION:
     *     <android.content.Intent: android.os.Bundle getExtras()> (INTER_APP_COMMUNICATION)<br/>
     *     sinkFile: Sink File - Sink file containing the android's sink methods e.g. LOG:
     *     <android.util.Log: int d(java.lang.String,java.lang.String)> (LOG)
     *     <android.util.Log: int d(java.lang.String,java.lang.String,java.lang.Throwable)> (LOG)
     *     <android.util.Log: int e(java.lang.String,java.lang.String)> (LOG)
     *     <android.util.Log: int e(java.lang.String,java.lang.String,java.lang.Throwable)> (LOG)
     *     <android.util.Log: int i(java.lang.String,java.lang.String)> (LOG)
     *     <android.util.Log: int i(java.lang.String,java.lang.String,java.lang.Throwable)> (LOG)
     *     <android.util.Log: int v(java.lang.String,java.lang.String)> (LOG)
     *     <android.util.Log: int v(java.lang.String,java.lang.String,java.lang.Throwable)> (LOG)
     *     <android.util.Log: int w(java.lang.String,java.lang.Throwable)> (LOG)
     *     <android.util.Log: int w(java.lang.String,java.lang.String)> (LOG)
     *     <android.util.Log: int w(java.lang.String,java.lang.String,java.lang.Throwable)> (LOG)
     *     <android.util.Log: int wtf(java.lang.String,java.lang.Throwable)> (LOG)
     *     <android.util.Log: int wtf(java.lang.String,java.lang.String)> (LOG)
     *     <android.util.Log: int wtf(java.lang.String,java.lang.String,java.lang.Throwable)> (LOG)
     *     <android.app.UiModeManager: void setNightMode(int)> (LOG)
     *
     *     FILE:
     *     <java.io.OutputStream: void write(byte[])> (FILE)
     *     <java.io.OutputStream: void write(byte[],int,int)> (FILE)
     *     <java.io.OutputStream: void write(int)> (FILE)
     *     <java.io.FileOutputStream: void write(byte[])> (FILE)
     *     <java.io.FileOutputStream: void write(byte[],int,int)> (FILE)
     *     <java.io.FileOutputStream: void write(int)> (FILE)
     *     <java.io.Writer: void write(char[])> (FILE)
     *     <java.io.Writer: void write(char[],int,int)> (FILE)
     *     <java.io.Writer: void write(int)> (FILE)
     *     <java.io.Writer: void write(java.lang.String)> (FILE)
     *     <java.io.Writer: void write(java.lang.String,int,int)> (FILE)
     *     <android.content.SharedPreferences$Editor: android.content.SharedPreferences$Editor putBoolean(java.lang.String,boolean)> (FILE)
     *     <android.content.SharedPreferences$Editor: android.content.SharedPreferences$Editor putFloat(java.lang.String,float)> (FILE)
     *     <android.content.SharedPreferences$Editor: android.content.SharedPreferences$Editor putInt(java.lang.String,int)> (FILE)
     *     <android.content.SharedPreferences$Editor: android.content.SharedPreferences$Editor putLong(java.lang.String,long)> (FILE)
     *     <android.content.SharedPreferences$Editor: android.content.SharedPreferences$Editor putString(java.lang.String,java.lang.String)> (FILE)
     *
     *     NETWORK:
     *     <java.net.URL: void set(java.lang.String,java.lang.String,int,java.lang.String,java.lang.String)> (NETWORK)
     *     <java.net.URL: void set(java.lang.String,java.lang.String,int,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String)> (NETWORK)
     *     <java.net.URLConnection: void setRequestProperty(java.lang.String,java.lang.String)> (NETWORK)
     *     <java.net.Socket: void connect(java.net.SocketAddress)> (NETWORK)
     *     <org.apache.http.impl.client.DefaultHttpClient: org.apache.http.HttpResponse execute(org.apache.http.client.methods.HttpUriRequest)> (NETWORK)
     *     <org.apache.http.client.HttpClient: org.apache.http.HttpResponse execute(org.apache.http.client.methods.HttpUriRequest)> (NETWORK)
     *
     *     VIDEO:
     *     <android.media.MediaRecorder: void setVideoSource(int)> (VIDEO)
     *     <android.media.MediaRecorder: void setPreviewDisplay(android.view.Surface)> (VIDEO)
     *     <android.media.MediaRecorder: void start()> (VIDEO)
     *
     *     SMS_MMS:
     *     <android.telephony.SmsManager: void sendTextMessage(java.lang.String,java.lang.String,java.lang.String,android.app.PendingIntent,android.app.PendingIntent)> android.permission.SEND_SMS (SMS_MMS)
     *     <android.telephony.SmsManager: void sendDataMessage(java.lang.String,java.lang.String,short,byte[],android.app.PendingIntent,android.app.PendingIntent)> android.permission.SEND_SMS (SMS_MMS)
     *     <android.telephony.SmsManager: void sendMultipartTextMessage(java.lang.String,java.lang.String,java.util.ArrayList,java.util.ArrayList,java.util.ArrayList)> android.permission.SEND_SMS (SMS_MMS)
     *
     *     INTER_APP_COMMUNICATION:
     *     <android.content.Context: void sendBroadcast(android.content.Intent)> (INTER_APP_COMMUNICATION)
     *     <android.content.Context: void sendBroadcast(android.content.Intent,java.lang.String)> (INTER_APP_COMMUNICATION)
     *     <android.os.Handler: boolean sendMessage(android.os.Message)> (INTER_APP_COMMUNICATION)
     *     <android.content.Context: android.content.Intent registerReceiver(android.content.BroadcastReceiver,android.content.IntentFilter)> (INTER_APP_COMMUNICATION)
     *     <android.content.Context: android.content.Intent registerReceiver(android.content.BroadcastReceiver,android.content.IntentFilter,java.lang.String,android.os.Handler)> (INTER_APP_COMMUNICATION)
     *     <android.content.Intent: android.content.Intent setAction(java.lang.String)> (INTER_APP_COMMUNICATION)
     *     <android.content.Intent: android.content.Intent setClassName(android.content.Context,java.lang.Class)> (INTER_APP_COMMUNICATION)
     *     <android.content.Intent: android.content.Intent setClassName(android.content.Context,java.lang.String)> (INTER_APP_COMMUNICATION)
     *     <android.content.Intent: android.content.Intent setComponent(android.content.ComponentName)> (INTER_APP_COMMUNICATION)
     *     <android.content.Context: void startActivity(android.content.Intent)> (INTER_APP_COMMUNICATION)<br/>
     *     easyTaintWrapperSource: Easy Taint Wrapper File - Taint wrapper file containing the android's package names that
     *     should be considered during the instrumentation phase
     *      e.g. # Packages to include in the analysis
     *     ^android.
     *     ^java.
     *     ^org.apache.http.
     *
     *     <java.util.Stack: java.lang.Object push(java.lang.Object)>
     *
     *     <java.util.Map: java.lang.Object put(java.lang.Object,java.lang.Object)>
     *     -<java.util.Map: void clear()>
     *
     *     <java.util.TreeMap: void <init>(java.util.Map)>
     *     <java.util.HashMap: void <init>(java.util.Map)>
     *     <java.util.WeakHashMap: void <init>(java.util.Map)>
     *     <java.util.ConcurrentHashMap: void <init>(java.util.Map)>
     *     <java.util.LinkedHashMap: void <init>(java.util.Map)>
     *
     *     ~<android.app.Activity: android.view.View findViewById(int)>
     *     ~<android.app.Activity: void setContentView(int)>
     *     ~<android.app.Activity: void setContentView(android.view.View)>
     *     <android.widget.EditText: android.text.Editable getText()>
     *     <android.text.Editable: java.lang.String toString()>
     *
     *     <javax.servlet.ServletResponse: java.io.PrintWriter getWriter()>
     *
     *     # Exclude the ServerSocket stuff
     *     # ~<java.net.ServerSocket: java.net.Socket accept()>
     *     # ~<java.net.Socket: java.io.InputStream getInputStream()>
     *     <br/>
     *     apkFile: APK File - APK file that should be instrumented<br/>
     *
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({
            "application/json"
    })
    InstrumentResource.PostInstrumentResponse postInstrument(@FormDataParam("file")InputStream sourceFile, @FormDataParam("file")InputStream sinkFile,
                                                             @FormDataParam("file")InputStream easyTaintWrapperSource, @FormDataParam("file")InputStream apkFile)
            throws Exception
    ;
    /**
     * Retrieve the location of the instrumented apk file based its hash sum value.
     * The hash value is calculated from the non-instrumented apk with sha512.
     *
     *
     * @param apkHash
     *
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

    public class GetInstrumentByApkHashResponse
            extends org.sentinel.instrumentationserver.generated.resource.support.ResponseWrapper
    {


        private GetInstrumentByApkHashResponse(Response delegate) {
            super(delegate);
        }

        /**
         *
         * @param apkFile
         */
        public static InstrumentResource.GetInstrumentByApkHashResponse withOK(byte[] apkFile) {
            Response.ResponseBuilder responseBuilder = Response.status(200);
            responseBuilder.entity(apkFile);
            return new InstrumentResource.GetInstrumentByApkHashResponse(responseBuilder.build());
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
        public static InstrumentResource.GetInstrumentByApkHashResponse withJsonNotFound(Error entity) {
            Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new InstrumentResource.GetInstrumentByApkHashResponse(responseBuilder.build());
        }

    }

    public class GetInstrumentResponse
            extends org.sentinel.instrumentationserver.generated.resource.support.ResponseWrapper
    {


        private GetInstrumentResponse(Response delegate) {
            super(delegate);
        }

        /**
         *  e.g. {
         *   "apks": [
         *     {
         *       "hash": "f4439018f1cd7e5d770c77743533e31bc76fcf31950dca09991c93bece5bb49e9896d6e3d5d597ed983fdb444505dbc9cd336b58c3917645cfbc97b6b05d8791",
         *       "url": "http://sentinel.de/instrumentedApps/f4439018f1cd7e5d770c77743533e31bc76fcf31950dca09991c93bece5bb49e9896d6e3d5d597ed983fdb444505dbc9cd336b58c3917645cfbc97b6b05d8791"
         *     }, {
         *       "hash": "1652d23fd36ffdb6eebe21b9b6ae5d09aeb6d40c929fdc45cb2621b4538084d5b0b34d64803608d2ad30d918d0d194a20ad8483a9ae564fefd41eddc43a6f05e",
         *       "url": "http://sentinel.de/instrumentedApps/1652d23fd36ffdb6eebe21b9b6ae5d09aeb6d40c929fdc45cb2621b4538084d5b0b34d64803608d2ad30d918d0d194a20ad8483a9ae564fefd41eddc43a6f05e"
         *     }
         *   ]
         * }
         *
         *
         * @param entity
         *     {
         *       "apks": [
         *         {
         *           "hash": "f4439018f1cd7e5d770c77743533e31bc76fcf31950dca09991c93bece5bb49e9896d6e3d5d597ed983fdb444505dbc9cd336b58c3917645cfbc97b6b05d8791",
         *           "url": "http://sentinel.de/instrumentedApps/f4439018f1cd7e5d770c77743533e31bc76fcf31950dca09991c93bece5bb49e9896d6e3d5d597ed983fdb444505dbc9cd336b58c3917645cfbc97b6b05d8791"
         *         }, {
         *           "hash": "1652d23fd36ffdb6eebe21b9b6ae5d09aeb6d40c929fdc45cb2621b4538084d5b0b34d64803608d2ad30d918d0d194a20ad8483a9ae564fefd41eddc43a6f05e",
         *           "url": "http://sentinel.de/instrumentedApps/1652d23fd36ffdb6eebe21b9b6ae5d09aeb6d40c929fdc45cb2621b4538084d5b0b34d64803608d2ad30d918d0d194a20ad8483a9ae564fefd41eddc43a6f05e"
         *         }
         *       ]
         *     }
         *
         */
        public static InstrumentResource.GetInstrumentResponse withJsonOK(Apks entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new InstrumentResource.GetInstrumentResponse(responseBuilder.build());
        }

    }

    public class PostInstrumentResponse
            extends org.sentinel.instrumentationserver.generated.resource.support.ResponseWrapper
    {


        private PostInstrumentResponse(Response delegate) {
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
        public static InstrumentResource.PostInstrumentResponse withJsonAccepted(Apk entity) {
            Response.ResponseBuilder responseBuilder = Response.status(202).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new InstrumentResource.PostInstrumentResponse(responseBuilder.build());
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
        public static InstrumentResource.PostInstrumentResponse withJsonBadRequest(Error entity) {
            Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new InstrumentResource.PostInstrumentResponse(responseBuilder.build());
        }

    }

}
