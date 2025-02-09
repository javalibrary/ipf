/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.commons.ihe.fhir.iti65;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DocumentManifest;
import org.openehealth.ipf.commons.audit.AuditContext;
import org.openehealth.ipf.commons.audit.codes.EventOutcomeIndicator;
import org.openehealth.ipf.commons.ihe.fhir.audit.FhirAuditStrategy;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Christian Ohr
 * @since 3.6
 */
public abstract class Iti65AuditStrategy extends FhirAuditStrategy<Iti65AuditDataset> {

    public Iti65AuditStrategy(boolean serverSide) {
        super(serverSide);
    }

    @Override
    public Iti65AuditDataset createAuditDataset() {
        return new Iti65AuditDataset(isServerSide());
    }

    @Override
    public Iti65AuditDataset enrichAuditDatasetFromRequest(Iti65AuditDataset auditDataset, Object request, Map<String, Object> parameters) {
        var dataset = super.enrichAuditDatasetFromRequest(auditDataset, request, parameters);
        var bundle = (Bundle) request;
        //
        var documentManifest = bundle.getEntry().stream()
                .map(Bundle.BundleEntryComponent::getResource)
                .filter(DocumentManifest.class::isInstance)
                .map(DocumentManifest.class::cast)
                .findFirst().orElseThrow(() -> new RuntimeException("ITI-65 bundle must contain DocumentManifest"));

        dataset.enrichDatasetFromDocumentManifest(documentManifest);
        return dataset;
    }

    @Override
    public boolean enrichAuditDatasetFromResponse(Iti65AuditDataset auditDataset, Object response, AuditContext auditContext) {
        var bundle = (Bundle) response;
        // Extract DocumentManifest (UU)IDs from the response bundle for auditing
        bundle.getEntry().stream()
                .map(Bundle.BundleEntryComponent::getResponse)
                .filter(Objects::nonNull)
                .filter(r -> r.getLocation() != null && r.getLocation().startsWith("DocumentManifest"))
                .findFirst()
                .ifPresent(r -> auditDataset.setDocumentManifestUuid(r.getLocation()));
        return super.enrichAuditDatasetFromResponse(auditDataset, response, auditContext);
    }

    /**
     * Look at the response codes in the bundle entries and derive the ATNA event outcome
     * @param resource FHIR resource
     * @return RFC3881EventOutcomeCode
     */
    @Override
    protected EventOutcomeIndicator getEventOutcomeCodeFromResource(Iti65AuditDataset auditDataset, IBaseResource resource) {
        var bundle = (Bundle) resource;
        var responseStatus = bundle.getEntry().stream()
                .map(Bundle.BundleEntryComponent::getResponse)
                .map(Bundle.BundleEntryResponseComponent::getStatus)
                .collect(Collectors.toSet());

        if (responseStatus.stream().anyMatch(s -> s.startsWith("4") || s.startsWith("5"))) {
            return EventOutcomeIndicator.MajorFailure;
        }
        return EventOutcomeIndicator.Success;
    }
}
