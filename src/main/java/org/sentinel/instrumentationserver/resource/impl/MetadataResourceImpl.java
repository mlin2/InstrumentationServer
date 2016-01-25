package org.sentinel.instrumentationserver.resource.impl;

import org.sentinel.instrumentationserver.InstrumentationDAO;
import org.sentinel.instrumentationserver.generated.model.MetadataList;
import org.sentinel.instrumentationserver.generated.workaround.MetadataResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * Created by sebastian on 1/22/16.
 */
public class MetadataResourceImpl implements MetadataResource {
    @Override
    public GetMetadataAllResponse getMetadataAll() throws Exception {
        InstrumentationDAO instrumentationDAO = InstrumentationDAO.getInstance();
        MetadataList metadataList = instrumentationDAO.getAllMetadata();

        return GetMetadataAllResponse.withJsonOK(metadataList);
    }

    @Override
    @GET
    @Path("logo/{apkHash}")
    @Produces({
            "image/png"
    })
    public GetMetadataLogoByApkHashResponse getMetadataLogoByApkHash(@PathParam("apkHash") String apkHash) throws Exception {
        InstrumentationDAO instrumentationDAO = InstrumentationDAO.getInstance();
        byte[] logoFile = instrumentationDAO.retrieveLogoFromDatabase(apkHash);
        return GetMetadataLogoByApkHashResponse.withFormdataOK(logoFile);
    }
}
