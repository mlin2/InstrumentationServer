package org.sentinel.instrumentationserver.resource.impl;

import org.sentinel.instrumentationserver.InstrumentationServerManager;
import org.sentinel.instrumentationserver.generated.resource.InstrumentResource;
import org.sentinel.instrumentationserver.generated.model.Apk;
import org.sentinel.instrumentationserver.generated.model.Apks;

import javax.mail.internet.MimeMultipart;
import javax.ws.rs.PathParam;
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

    @Override
    public PostInstrumentResponse postInstrument(MimeMultipart entity) throws Exception {

        if (InstrumentationServerManager.getInstance().handleMultipartPost(entity)) {
            return PostInstrumentResponse.withJsonAccepted(new Apk());
        }


        return null;
    }

    @Override
    public GetInstrumentByApkHashResponse getInstrumentByApkHash(@PathParam("apkHash") String apkHash) throws Exception {
        return null;
    }
}
