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

package org.openehealth.ipf.platform.camel.ihe.fhir.iti81;

import org.openehealth.ipf.commons.ihe.fhir.iti81.FhirAuditEventQueryAuditDataset;
import org.openehealth.ipf.platform.camel.ihe.fhir.core.FhirEndpoint;
import org.openehealth.ipf.platform.camel.ihe.fhir.core.FhirEndpointConfiguration;

/**
 * @author Christian Ohr
 * @since 3.6
 */
public class Iti81Endpoint extends FhirEndpoint<FhirAuditEventQueryAuditDataset, Iti81Component> {

    public Iti81Endpoint(String uri, Iti81Component fhirComponent, FhirEndpointConfiguration<FhirAuditEventQueryAuditDataset> config) {
        super(uri, fhirComponent, config);
    }

    @Override
    protected String createEndpointUri() {
        return "atna-iti81:" + "not-implemented yet";
    }
}
