/*
 * Copyright 2019 the original author or authors.
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

package org.openehealth.ipf.platform.camel.ihe.hl7v2.intercept.consumer;

import ca.uhn.hl7v2.model.Message;
import org.apache.camel.Exchange;
import org.openehealth.ipf.commons.ihe.hl7v2.tracing.MessageTracer;
import org.openehealth.ipf.platform.camel.ihe.core.InterceptorFactory;
import org.openehealth.ipf.platform.camel.ihe.core.InterceptorSupport;
import org.openehealth.ipf.platform.camel.ihe.hl7v2.HL7v2Endpoint;

/**
 * @author Christian Ohr
 */
public class ConsumerTracingInterceptor extends InterceptorSupport<HL7v2Endpoint> {

    private final MessageTracer messageTracer;

    public ConsumerTracingInterceptor(MessageTracer messageTracer) {
        super();
        this.messageTracer = messageTracer;
        addAfter(ConsumerMarshalInterceptor.class.getName());
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        var msg = exchange.getIn().getMandatoryBody(Message.class);
        messageTracer.receiveMessage(msg, getEndpoint().getEndpointUri(), (message, span) ->
                getWrappedProcessor().process(exchange));
    }

    public static class Factory implements InterceptorFactory<HL7v2Endpoint, ConsumerTracingInterceptor> {
        private final MessageTracer messageTracer;

        public Factory(MessageTracer messageTracer) {
            this.messageTracer = messageTracer;
        }

        @Override
        public ConsumerTracingInterceptor getNewInstance() {
            return new ConsumerTracingInterceptor(messageTracer);
        }
    }
}
