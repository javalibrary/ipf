/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.platform.camel.ihe.hl7v3.iti55.deferredresponse;

import org.apache.camel.Exchange;
import org.apache.cxf.jaxws.context.WrappedMessageContext;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.RelatesToType;
import org.openehealth.ipf.commons.ihe.hl7v3.audit.Hl7v3AuditDataset;
import org.openehealth.ipf.commons.ihe.hl7v3.Hl7v3WsTransactionConfiguration;
import org.openehealth.ipf.commons.ihe.hl7v3.iti55.asyncresponse.Iti55DeferredResponsePortType;
import org.openehealth.ipf.commons.ihe.ws.JaxWsClientFactory;
import org.openehealth.ipf.commons.ihe.ws.cxf.audit.AbstractAuditInterceptor;
import org.openehealth.ipf.commons.ihe.ws.cxf.audit.WsAuditDataset;
import org.openehealth.ipf.platform.camel.ihe.ws.AbstractWsEndpoint;
import org.openehealth.ipf.platform.camel.ihe.ws.AbstractWsProducer;

import static org.apache.cxf.ws.addressing.JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES;

/**
 * @author Dmytro Rud
 */
public class Iti55DeferredResponseProducer extends AbstractWsProducer<Hl7v3AuditDataset, Hl7v3WsTransactionConfiguration, String, String> {

    public Iti55DeferredResponseProducer(AbstractWsEndpoint<Hl7v3AuditDataset, Hl7v3WsTransactionConfiguration> endpoint,
                                         JaxWsClientFactory<Hl7v3AuditDataset> clientFactory) {
        super(endpoint, clientFactory, String.class, String.class);
    }


    @Override
    protected void enrichRequestContext(Exchange exchange, WrappedMessageContext requestContext) {
        // NB: Camel message headers used here are set in Iti55Service's intern Callable.

        // propagate WS-Addressing request message ID
        var requestMessageId = exchange.getIn().getHeader("iti55.deferred.requestMessageId", String.class);
        if (requestMessageId != null) {
            var relatesToHolder = new RelatesToType();
            relatesToHolder.setValue(requestMessageId);
            var apropos = new AddressingProperties();
            apropos.setRelatesTo(relatesToHolder);
            requestContext.put(CLIENT_ADDRESSING_PROPERTIES, apropos);
        }

        // inject audit dataset
        var auditDataset = exchange.getIn().getHeader("iti55.deferred.auditDataset", WsAuditDataset.class);
        if (auditDataset != null) {
            requestContext.put(AbstractAuditInterceptor.DATASET_CONTEXT_KEY, auditDataset);
        }
    }


    @Override
    protected String callService(Object client, String responseString) {
        return ((Iti55DeferredResponsePortType) client).receiveDeferredResponse(responseString);
    }
}
