package org.sentinel.instrumentationserver.resource.impl;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.sentinel.instrumentationserver.InstrumentationRunner;
import org.sentinel.instrumentationserver.InstrumentationServerManager;
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
 * Created by sebastian on 1/9/16.
 */
public class InstrumentResourceImpl implements InstrumentResource {
    @Override
    public InstrumentResource.GetInstrumentResponse getInstrument() throws Exception {
        InstrumentationServerManager instrumentationServerManager = InstrumentationServerManager.getInstance();
        List<String> allInstrumentedApkHashes = instrumentationServerManager.getAllInstrumentedApkHashes();
        final Apks apks = new Apks().withSize(allInstrumentedApkHashes.size());
        Iterator<String> iterator = allInstrumentedApkHashes.iterator();
        while (iterator.hasNext()) {
            String hashOfCurrentApk = iterator.next();
            apks.getApks().add(new Apk().withHash(hashOfCurrentApk).withUrl(instrumentationServerManager.APK_URL +
                    hashOfCurrentApk));
            iterator.remove();
        }

        return GetInstrumentResponse.withJsonOK(apks);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({
            "application/json"
    })
    public PostInstrumentResponse postInstrument(@FormDataParam("sourceFile")InputStream sourceFile, @FormDataParam("sinkFile")InputStream sinkFile,
                                                 @FormDataParam("easyTaintWrapperSource")InputStream easyTaintWrapperSource, @FormDataParam("apkFile")InputStream apkFile) throws Exception {

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        byte[] apkFileBytes = IOUtils.toByteArray(apkFile);
        String sha512Hash = String.valueOf(Hex.encodeHex(messageDigest.digest(apkFileBytes)));

        InstrumentationServerManager instrumentationServerManager = InstrumentationServerManager.getInstance();

        if(!instrumentationServerManager.checkIfApkAlreadyInstrumented(sha512Hash)) {
            InstrumentationRunner instrumentationRunner = new InstrumentationRunner(sourceFile, sinkFile, easyTaintWrapperSource, apkFileBytes, sha512Hash);
            Thread thread = new Thread(instrumentationRunner);
            thread.start();
        }

        return PostInstrumentResponse.withJsonAccepted(new Apk());
    }

    @Override
    @GET
    @Path("{apkHash}")
    @Produces({
            "application/json"
    })
    public GetInstrumentByApkHashResponse getInstrumentByApkHash(@PathParam("apkHash") String apkHash) throws Exception {

        InstrumentationServerManager instrumentationServerManager = InstrumentationServerManager.getInstance();
        byte[] apkFile = instrumentationServerManager.retrieveInstrumentedApkFromDatabase(apkHash);
        System.out.println(apkFile);
        return GetInstrumentByApkHashResponse.withOK(apkFile);
    }
}
