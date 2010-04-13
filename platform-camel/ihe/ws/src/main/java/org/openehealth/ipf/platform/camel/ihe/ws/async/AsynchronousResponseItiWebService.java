/*
 * Copyright 2010 the original author or authors.
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
package org.openehealth.ipf.platform.camel.ihe.ws.async;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.handler.MessageContext;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxws.context.WebServiceContextImpl;
import org.openehealth.ipf.commons.ihe.ws.cxf.async.InRelatesToHackInterceptor;
import org.openehealth.ipf.platform.camel.ihe.ws.DefaultItiWebService;

/**
 * Base class for receivers of asynchronous responses for Web Service-based IHE transactions.
 * @author Dmytro Rud
 */
public class AsynchronousResponseItiWebService extends DefaultItiWebService {
    private static final transient Log LOG = LogFactory.getLog(AsynchronousResponseItiWebService.class);
    
    /**
     * Before calling the base method, determines correlation key  
     * and stores it into message headers. 
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Exchange process(
            Object body, 
            Map<String, Object> headers,
            ExchangePattern exchangePattern) 
    {
        MessageContext messageContext = new WebServiceContextImpl().getMessageContext();
        List<Header> messageHeaders = (List<Header>) messageContext.get(Header.HEADER_LIST);        
        String messageId = InRelatesToHackInterceptor.retrieveMessageId(messageHeaders);

        if (messageId != null) {
            AsynchronousItiEndpoint endpoint = (AsynchronousItiEndpoint) getConsumer().getEndpoint();
            
            // expose user-defined correlation key as message header
            String correlationKey = endpoint.getCorrelator().getCorrelationKey(messageId);
            if (correlationKey != null) {
                if (headers == null) {
                    // NB: it shouldn't be a non-modifiable singleton map...
                    headers = new HashMap<String, Object>();
                }
                headers.put(AsynchronousItiEndpoint.CORRELATION_KEY_HEADER_NAME, correlationKey);
            }
            
            // correlation data in not necessary any more
            endpoint.getCorrelator().delete(messageId);
        } else {
            LOG.error("Cannot retrieve WSA RelatesTo header, message correlation not possible");
        }

        return super.process(body, headers, exchangePattern);
    }


}

