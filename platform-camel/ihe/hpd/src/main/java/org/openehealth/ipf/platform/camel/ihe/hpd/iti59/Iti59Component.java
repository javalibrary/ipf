/*
 * Copyright 2017 the original author or authors.
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
package org.openehealth.ipf.platform.camel.ihe.hpd.iti59;

import org.apache.camel.Endpoint;
import org.openehealth.ipf.commons.ihe.hpd.HPD;
import org.openehealth.ipf.commons.ihe.hpd.iti59.Iti59AuditDataset;
import org.openehealth.ipf.commons.ihe.hpd.stub.dsmlv2.BatchRequest;
import org.openehealth.ipf.commons.ihe.hpd.stub.dsmlv2.BatchResponse;
import org.openehealth.ipf.commons.ihe.ws.JaxWsClientFactory;
import org.openehealth.ipf.commons.ihe.ws.WsInteractionId;
import org.openehealth.ipf.commons.ihe.ws.WsTransactionConfiguration;
import org.openehealth.ipf.platform.camel.ihe.hpd.HpdEndpoint;
import org.openehealth.ipf.platform.camel.ihe.ws.AbstractWsComponent;
import org.openehealth.ipf.platform.camel.ihe.ws.AbstractWsEndpoint;
import org.openehealth.ipf.platform.camel.ihe.ws.AbstractWsProducer;
import org.openehealth.ipf.platform.camel.ihe.ws.SimpleWsProducer;

import java.util.Map;

/**
 * @author Dmytro Rud
 */
public class Iti59Component extends AbstractWsComponent<Iti59AuditDataset, WsTransactionConfiguration<Iti59AuditDataset>, WsInteractionId<WsTransactionConfiguration<Iti59AuditDataset>>> {

    public Iti59Component() {
        super(HPD.FeedInteractions.ITI_59);
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) {
        return new HpdEndpoint<>(uri, remaining, this, parameters, Iti59Service.class) {
            @Override
            public AbstractWsProducer<Iti59AuditDataset, WsTransactionConfiguration<Iti59AuditDataset>, ?, ?> getProducer(AbstractWsEndpoint<Iti59AuditDataset, WsTransactionConfiguration<Iti59AuditDataset>> endpoint, JaxWsClientFactory<Iti59AuditDataset> clientFactory) {
                return new SimpleWsProducer<>(endpoint, clientFactory, BatchRequest.class, BatchResponse.class);
            }
        };
    }

}
