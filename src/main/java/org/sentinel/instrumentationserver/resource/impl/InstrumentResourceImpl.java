package org.sentinel.instrumentationserver.resource.impl;

import org.sentinel.instrumentationserver.generated.resource.InstrumentResource;
import org.sentinel.instrumentationserver.generated.model.Apk;
import org.sentinel.instrumentationserver.generated.model.Apks;

import javax.mail.internet.MimeMultipart;
import javax.ws.rs.PathParam;

/**
 * Created by sebastian on 1/9/16.
 */
public class InstrumentResourceImpl implements InstrumentResource {
    @Override
    public InstrumentResource.GetInstrumentResponse getInstrument() throws Exception {
        final Apks apks = new Apks().withSize(1);
        apks.getApks().add(new Apk().withHash("16ac6ca7e19f2836f238a5f46609244c4e11864e60ad8d16e58e43524b42381417708152af9ce90bc0f934e4ae2f041cb90f9729e92b0223bee252ce0342fe16").withUrl("test.com/16ac6ca7e19f2836f238a5f46609244c4e11864e60ad8d16e58e43524b42381417708152af9ce90bc0f934e4ae2f041cb90f9729e92b0223bee252ce0342fe16"));
        return GetInstrumentResponse.withJsonOK(apks);
    }

    @Override
    public PostInstrumentResponse postInstrument(MimeMultipart entity) throws Exception {
        return null;
    }

    @Override
    public GetInstrumentByApkHashResponse getInstrumentByApkHash(@PathParam("apkHash") String apkHash) throws Exception {
        return null;
    }
}
