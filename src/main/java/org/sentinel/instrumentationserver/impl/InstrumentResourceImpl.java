package org.sentinel.instrumentationserver.impl;

import org.sentinel.instrumentationserver.resource.InstrumentResource;

import javax.mail.internet.MimeMultipart;
import javax.ws.rs.PathParam;

/**
 * Created by sebastian on 1/9/16.
 */
public class InstrumentResourceImpl implements InstrumentResource {
    @Override
    public GetInstrumentResponse getInstrument() throws Exception {
        return null;
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
