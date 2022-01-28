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
package org.openehealth.ipf.platform.camel.ihe.hl7v2.intercept.consumer;

import org.apache.camel.Exchange;
import org.apache.commons.lang3.Validate;
import org.openehealth.ipf.platform.camel.ihe.core.InterceptorSupport;

/**
 * Consumer-side HL7v2 interceptor which echoes one segment from
 * the request message in the corresponding response message.
 * When multiple segments with the same name exist in the request
 * and/or response message, the first occurrence will be processed.
 *
 * @author Dmytro Rud
 */
public class ConsumerSegmentEchoingInterceptor extends InterceptorSupport {
    private final String segmentName;


    /**
     * @param segmentName
     *      name of the segment to be echoed.
     */
    public ConsumerSegmentEchoingInterceptor(String segmentName) {
        this.segmentName = Validate.notEmpty(segmentName);
        addBefore(ConsumerMarshalInterceptor.class.getName());
    }


    @Override
    public void process(Exchange exchange) throws Exception {
        // determine segment boundaries in the request message
        var request = exchange.getIn().getBody(String.class);
        var requestQpdBoundaries = getQpdBoundaries(request);

        // run the route
        getWrappedProcessor().process(exchange);

        // replace the segment in the response message by the one from the request
        if (requestQpdBoundaries != null) {
            var response = exchange.getMessage().getBody(String.class);
            var responseQpdBoundaries = getQpdBoundaries(response);
            if (responseQpdBoundaries != null) {
                exchange.getMessage().setBody(response.substring(0, responseQpdBoundaries[0]) +
                        request.substring(requestQpdBoundaries[0], requestQpdBoundaries[1]) +
                        response.substring(responseQpdBoundaries[1]));
            }
        }
    }


    private int[] getQpdBoundaries(String s) {
        var pos1 = s.indexOf("\r" + segmentName + s.charAt(3));
        var pos2 = (pos1 > 0) ? s.indexOf("\r", pos1 + 4) : -1;
        return (pos2 > 0) ? new int[] {pos1, pos2} : null;
    }
}
