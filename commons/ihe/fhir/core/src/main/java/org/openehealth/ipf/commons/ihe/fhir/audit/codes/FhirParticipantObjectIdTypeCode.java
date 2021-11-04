/*
 * Copyright 2018 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.openehealth.ipf.commons.ihe.fhir.audit.codes;

import lombok.Getter;
import org.openehealth.ipf.commons.audit.types.EnumeratedCodedValue;
import org.openehealth.ipf.commons.audit.types.ParticipantObjectIdType;

/**
 * ParticipantObjectIdTypeCodes for the FHIR transactions in this module
 *
 * @author Christian Ohr
 */
public enum FhirParticipantObjectIdTypeCode implements ParticipantObjectIdType, EnumeratedCodedValue<ParticipantObjectIdType> {

    ProvideDocumentBundle("ITI-65", "Provide Document Bundle"),
    MobileDocumentManifestQuery("ITI-66", "Mobile Document Manifest Query"),
    MobileDocumentReferenceQuery("ITI-67", "Mobile Document Reference Query"),
    MobileDocumentRetrieval("ITI-68", "Mobile Document Retrieval"),
    MobilePatientDemographicsQuery("ITI-78", "Mobile Patient Demographics Query"),
    RetrieveATNAAuditEvent("ITI-81", "Retrieve ATNA AuditEvent"),
    MobilePatientIdentifierCrossReferenceQuery("ITI-83", "Mobile Patient Identifier Cross-reference Query"),
    MobileQueryExistingData("PCC-44", "Mobile Query Existing Data");

    @Getter
    private final ParticipantObjectIdType value;

    FhirParticipantObjectIdTypeCode(String code, String displayName) {
        this.value = ParticipantObjectIdType.of(code, "IHE Transactions", displayName);
    }

    public static ParticipantObjectIdType fromResourceType(String resourceType) {
        return ParticipantObjectIdType.of(
                resourceType,
                "http://hl7.org/fhir/resource-types",
                resourceType);
    }
}


