/*
 * Copyright 2009 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.commons.ihe.xds.core.transform.requests.query;

import static org.openehealth.ipf.commons.ihe.xds.core.transform.requests.QueryParameter.*;

import org.openehealth.ipf.commons.ihe.xds.core.ebxml.EbXMLAdhocQueryRequest;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Hl7v2Based;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Identifiable;
import org.openehealth.ipf.commons.ihe.xds.core.requests.query.FindFoldersQuery;

/**
 * Transforms between a {@link FindFoldersQuery} and {@link EbXMLAdhocQueryRequest}.
 * @author Jens Riemschneider
 */
public class FindFoldersQueryTransformer extends AbstractStoredQueryTransformer<FindFoldersQuery>{

    /**
     * Transforms the query into its ebXML representation.
     * <p>
     * Does not perform any transformation if one of the parameters is <code>null</code>. 
     * @param query
     *          the query. Can be <code>null</code>.
     * @param ebXML
     *          the ebXML representation. Can be <code>null</code>.
     */
    @Override
    public void toEbXML(FindFoldersQuery query, EbXMLAdhocQueryRequest ebXML) {
        if (query == null || ebXML == null) {
            return;
        }

        super.toEbXML(query, ebXML);

        var slots = new QuerySlotHelper(ebXML);

        slots.fromString(FOLDER_PATIENT_ID, Hl7v2Based.render(query.getPatientId()));
        
        slots.fromTimestamp(FOLDER_LAST_UPDATE_TIME_FROM, query.getLastUpdateTime().getFrom());
        slots.fromTimestamp(FOLDER_LAST_UPDATE_TIME_TO, query.getLastUpdateTime().getTo());

        slots.fromCode(FOLDER_CODES, query.getCodes());
        
        slots.fromStatus(FOLDER_STATUS, query.getStatus());
        slots.fromInteger(METADATA_LEVEL, query.getMetadataLevel());
    }
    
    /**
     * Transforms the ebXML representation of a query into a query object.
     * <p>
     * Does not perform any transformation if one of the parameters is <code>null</code>. 
     * @param query
     *          the query. Can be <code>null</code>.
     * @param ebXML
     *          the ebXML representation. Can be <code>null</code>.
     */
    @Override
    public void fromEbXML(FindFoldersQuery query, EbXMLAdhocQueryRequest ebXML) {
        if (query == null || ebXML == null) {
            return;
        }

        super.fromEbXML(query, ebXML);

        var slots = new QuerySlotHelper(ebXML);
        var patientId = slots.toString(FOLDER_PATIENT_ID);
        query.setPatientId(Hl7v2Based.parse(patientId, Identifiable.class));
        
        query.setCodes(slots.toCodeQueryList(FOLDER_CODES, FOLDER_CODES_SCHEME));
        
        query.getLastUpdateTime().setFrom(slots.toTimestamp(FOLDER_LAST_UPDATE_TIME_FROM));
        query.getLastUpdateTime().setTo(slots.toTimestamp(FOLDER_LAST_UPDATE_TIME_TO));
        
        query.setStatus(slots.toStatus(FOLDER_STATUS));
        query.setMetadataLevel(slots.toInteger(METADATA_LEVEL));
    }
}
