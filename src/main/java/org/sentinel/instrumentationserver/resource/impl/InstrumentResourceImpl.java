package org.sentinel.instrumentationserver.resource.impl;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.sentinel.instrumentationserver.InstrumentationServerManager;
import org.sentinel.instrumentationserver.generated.model.Apk;
import org.sentinel.instrumentationserver.generated.model.Apks;
import org.sentinel.instrumentationserver.generated.workaround.InstrumentResource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
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

        if (InstrumentationServerManager.getInstance().handleMultipartPost(sourceFile, sinkFile, easyTaintWrapperSource, apkFile)) {
            return PostInstrumentResponse.withJsonAccepted(new Apk());
        }


        return PostInstrumentResponse.withJsonAccepted(new Apk());
    }

    @Override
    public GetInstrumentByApkHashResponse getInstrumentByApkHash(@PathParam("apkHash") String apkHash) throws Exception {
        return null;
    }
}
