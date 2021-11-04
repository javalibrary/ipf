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
package org.openehealth.ipf.platform.camel.ihe.hl7v3.iti44;

import org.openehealth.ipf.commons.ihe.hl7v3.audit.Hl7v3AuditDataset;
import org.openehealth.ipf.commons.ihe.hl7v3.Hl7v3WsTransactionConfiguration;
import org.openehealth.ipf.commons.ihe.hl7v3.iti44.GenericIti44PortType;
import org.openehealth.ipf.commons.ihe.ws.JaxWsClientFactory;
import org.openehealth.ipf.platform.camel.ihe.ws.AbstractWsEndpoint;
import org.openehealth.ipf.platform.camel.ihe.ws.AbstractWsProducer;

import static org.openehealth.ipf.commons.xml.XmlUtils.rootElementName;

/**
 * Producer implementation for the ITI-44 component (PIX Feed v3).
 */
public class Iti44Producer extends AbstractWsProducer<Hl7v3AuditDataset, Hl7v3WsTransactionConfiguration, String, String> {
    /**
     * Constructs the producer.
     * @param endpoint
     *          the endpoint creating this producer.
     * @param clientFactory
     *          the factory for clients to produce messages for the service.              
     */
    public Iti44Producer(AbstractWsEndpoint<Hl7v3AuditDataset, Hl7v3WsTransactionConfiguration> endpoint, JaxWsClientFactory<Hl7v3AuditDataset> clientFactory) {
        super(endpoint, clientFactory, String.class, String.class);
    }

    @Override
    protected String callService(Object clientObject, String request) {
        var client = (GenericIti44PortType) clientObject;
        var rootElementName = rootElementName(request);
        if ("PRPA_IN201301UV02".equals(rootElementName)) {
            return client.recordAdded(request);
        }
        else if ("PRPA_IN201302UV02".equals(rootElementName)) {
            return client.recordRevised(request);
        }
        else if ("PRPA_IN201304UV02".equals(rootElementName)) {
            return client.duplicatesResolved(request);
        }
        throw new RuntimeException("Cannot dispatch message with root element " + rootElementName);
    }
}
