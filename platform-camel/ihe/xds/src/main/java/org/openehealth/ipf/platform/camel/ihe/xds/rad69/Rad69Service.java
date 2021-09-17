/*
 * Copyright 2012 the original author or authors.
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
package org.openehealth.ipf.platform.camel.ihe.xds.rad69;

import org.openehealth.ipf.commons.ihe.xds.core.ebxml.ebxml30.RetrieveDocumentSetResponseType;
import org.openehealth.ipf.commons.ihe.xds.core.stub.xdsi.RetrieveImagingDocumentSetRequestType;
import org.openehealth.ipf.commons.ihe.xds.rad69.Rad69PortType;
import org.openehealth.ipf.platform.camel.ihe.xds.XdsRetrieveDocumentSetService;

/**
 * Service implementation for the IHE RAD-69 transaction (Retrieve Imaging Document Set).
 * <p/>
 * This implementation delegates to a Camel consumer by creating an exchange.
 *
 * @author Clay Sebourn
 */
public class Rad69Service extends XdsRetrieveDocumentSetService<RetrieveImagingDocumentSetRequestType> implements Rad69PortType {

    public Rad69Service() {
        super(null);
    }

    @Override
    public RetrieveDocumentSetResponseType documentRepositoryRetrieveImagingDocumentSet(RetrieveImagingDocumentSetRequestType body) {
        return processRequest(body);
    }
}
