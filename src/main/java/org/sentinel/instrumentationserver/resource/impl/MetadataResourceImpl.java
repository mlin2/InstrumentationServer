package org.sentinel.instrumentationserver.resource.impl;

import org.sentinel.instrumentationserver.generated.model.Error;
import org.sentinel.instrumentationserver.generated.model.MetadataList;
import org.sentinel.instrumentationserver.generated.workaround.MetadataResource;
import org.sentinel.instrumentationserver.metadata.MetadataDAO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * This endpoint interface implementation handles requests for metadata.
 */
public class MetadataResourceImpl implements MetadataResource {
    @Override
    public GetMetadataAllResponse getMetadataAll() throws Exception {
        MetadataDAO metadataDAO = new MetadataDAO();
        MetadataList metadataList = metadataDAO.getAllMetadata();

        return GetMetadataAllResponse.withJsonOK(metadataList);
    }

    @Override
    public GetMetadataInstrumentedResponse getMetadataInstrumented() throws Exception {
        MetadataDAO metadataDAO = new MetadataDAO();
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
        MetadataDAO metadataDAO = new MetadataDAO();
        byte[] logoFile = metadataDAO.retrieveLogoFromDatabase(apkHash);
        if (logoFile.length == 0) {
            return GetMetadataLogoByApkHashResponse.withJsonNotFound(new Error().withMsg("APK logo file not stored in the database"));
        }
        return GetMetadataLogoByApkHashResponse.withFormdataOK(logoFile);
    }
}
