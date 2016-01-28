package org.sentinel.instrumentationserver.resource.impl;

import org.sentinel.instrumentationserver.generated.model.MetadataList;
import org.sentinel.instrumentationserver.generated.workaround.MetadataResource;
import org.sentinel.instrumentationserver.metadata.MetadataDAO;

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
        MetadataDAO metadataDAO = MetadataDAO.getInstance();
        MetadataList metadataList = metadataDAO.getAllMetadata();

        return GetMetadataAllResponse.withJsonOK(metadataList);
    }

    @Override
    public GetMetadataInstrumentedResponse getMetadataInstrumented() throws Exception {
        MetadataDAO metadataDAO = MetadataDAO.getInstance();
        MetadataList metadataList = metadataDAO.getInstrumentedMetadata();

        return GetMetadataInstrumentedResponse.withJsonOK(metadataList);
    }

    @Override
    @GET
    @Path("logo/{apkHash}.png")
    @Produces({
            "image/png"
    })
    public GetMetadataLogoByApkHashResponse getMetadataLogoByApkHash(@PathParam("apkHash") String apkHash) throws Exception {
        MetadataDAO metadataDAO = MetadataDAO.getInstance();
        byte[] logoFile = metadataDAO.retrieveLogoFromDatabase(apkHash);
        return GetMetadataLogoByApkHashResponse.withFormdataOK(logoFile);
    }
}
