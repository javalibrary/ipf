/*
 * Copyright 2011 the original author or authors.
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
package org.openehealth.ipf.commons.ihe.hl7v3;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.openehealth.ipf.commons.audit.AuditContext;
import org.openehealth.ipf.commons.ihe.core.atna.AuditStrategy;
import org.openehealth.ipf.commons.ihe.hl7v3.audit.Hl7v3AuditDataset;
import org.openehealth.ipf.commons.ihe.ws.JaxWsClientFactory;
import org.openehealth.ipf.commons.ihe.ws.WsSecurityInformation;
import org.openehealth.ipf.commons.ihe.ws.cxf.audit.AuditResponseInterceptor;
import org.openehealth.ipf.commons.ihe.ws.cxf.databinding.plainxml.PlainXmlDataBinding;

import java.util.List;
import java.util.Map;


/**
 * Special factory for HL7 v3 Deferred Response senders.
 * @author Dmytro Rud
 */
public class Hl7v3DeferredResponderFactory extends JaxWsClientFactory<Hl7v3AuditDataset> {

    public Hl7v3DeferredResponderFactory(
            Hl7v3WsTransactionConfiguration wsTransactionConfiguration,
            String serviceUrl,
            AuditStrategy<Hl7v3AuditDataset> auditStrategy,
            AuditContext auditContext,
            InterceptorProvider customInterceptors,
            List<AbstractFeature> features,
            Map<String, Object> properties,
            WsSecurityInformation securityInformation)
    {
        super(wsTransactionConfiguration, serviceUrl, auditStrategy, auditContext,
                customInterceptors, features, properties, null, securityInformation);
    }


    @Override
    protected void configureInterceptors(Client client) {
        super.configureInterceptors(client);
        client.getEndpoint().getService().setDataBinding(new PlainXmlDataBinding());

        if (auditStrategy != null) {
            var auditInterceptor =
                    new AuditResponseInterceptor<>(auditStrategy, auditContext, true, null, false);
            client.getOutInterceptors().add(auditInterceptor);
            client.getOutFaultInterceptors().add(auditInterceptor);
        }
    }
}
