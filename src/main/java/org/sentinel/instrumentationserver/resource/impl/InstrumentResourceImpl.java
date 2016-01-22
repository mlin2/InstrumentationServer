package org.sentinel.instrumentationserver.resource.impl;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.sentinel.instrumentationserver.InstrumentationDAO;
import org.sentinel.instrumentationserver.InstrumentationRunner;
import org.sentinel.instrumentationserver.generated.model.Apk;
import org.sentinel.instrumentationserver.generated.model.Apks;
import org.sentinel.instrumentationserver.generated.workaround.InstrumentResource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.List;

/**
 * The implementation of InstrumentResource. InstrumentResource handles instrumentation and getting information
 * about APKs or a binary blob for the APK. InstrumentResource was generated from a RAML file but had to be
 * edited manually in order to accept form-data MultiParts.
 */
public class InstrumentResourceImpl implements InstrumentResource {
    @Override
    public InstrumentResource.GetInstrumentResponse getInstrument() throws Exception {
        InstrumentationDAO instrumentationDAO = InstrumentationDAO.getInstance();
        List<String> allInstrumentedApkHashes = instrumentationDAO.getAllInstrumentedApkHashes();
        final Apks apks = new Apks().withSize(allInstrumentedApkHashes.size());
        Iterator<String> iterator = allInstrumentedApkHashes.iterator();
        while (iterator.hasNext()) {
            String hashOfCurrentApk = iterator.next();
            apks.getApks().add(new Apk().withHash(hashOfCurrentApk));
            iterator.remove();
        }

        return GetInstrumentResponse.withJsonOK(apks);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({
            "application/json"
    })
    public PostInstrumentResponse postInstrument(@FormDataParam("sourceFile") InputStream sourceFile, @FormDataParam("sinkFile") InputStream sinkFile,
                                                 @FormDataParam("easyTaintWrapperSource") InputStream easyTaintWrapperSource,
                                                 @FormDataParam("apkFile") InputStream apkFile, @FormDataParam("logo") InputStream logo,
                                                 @FormDataParam("apkName") String appName, @FormDataParam("packageName") String packageName) throws Exception {

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        byte[] apkFileBytes = IOUtils.toByteArray(apkFile);
        String sha512Hash = String.valueOf(Hex.encodeHex(messageDigest.digest(apkFileBytes)));

        // Save the logo as an array of bytes because the InputStream for the logo will
        // be closed after instrumentation.
        byte[] logoBytes = IOUtils.toByteArray(logo);

        InstrumentationDAO instrumentationDAO = InstrumentationDAO.getInstance();

        if (!instrumentationDAO.checkIfApkAlreadyInstrumented(sha512Hash)) {
            InstrumentationRunner instrumentationRunner = new InstrumentationRunner(sourceFile, sinkFile,
                    easyTaintWrapperSource, apkFileBytes, sha512Hash, logoBytes, appName, packageName);
            Thread thread = new Thread(instrumentationRunner);
            thread.start();
        }

        return PostInstrumentResponse.withJsonAccepted(new Apk().withHash(sha512Hash));
    }

    @Override
    @GET
    @Path("{apkHash}")
    @Produces({
            "application/json"
    })
    public GetInstrumentByApkHashResponse getInstrumentByApkHash(@PathParam("apkHash") String apkHash) throws Exception {

        InstrumentationDAO instrumentationDAO = InstrumentationDAO.getInstance();
        byte[] apkFile = instrumentationDAO.retrieveInstrumentedApkFromDatabase(apkHash);
        System.out.println(apkFile);
        return GetInstrumentByApkHashResponse.withOK(apkFile);
    }
}
