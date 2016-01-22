package org.sentinel.instrumentationserver.resource.impl;

import org.sentinel.instrumentationserver.InstrumentationDAO;
import org.sentinel.instrumentationserver.generated.model.MetadataList;
import org.sentinel.instrumentationserver.generated.resource.MetadataResource;

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
}
