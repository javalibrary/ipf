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
package org.openehealth.ipf.platform.camel.ihe.mllp.iti8

import org.apache.camel.builder.RouteBuilder
import static org.openehealth.ipf.platform.camel.core.util.Exchanges.resultMessage
import static org.openehealth.ipf.platform.camel.hl7.HL7v2.ack


/**
 * Camel route for generic unit tests.
 * @author Dmytro Rud
 */
class Iti8TestRouteBuilder extends RouteBuilder {
    
    void configure() throws Exception {

        // normal processing without auditing
        from('xds-iti8://0.0.0.0:18081?audit=false&interceptorFactories=#serverInLogger,#serverOutLogger')
                .transform(ack())
        
        // normal processing with auditing
        from('pix-iti8://0.0.0.0:18082?interceptorFactories=#serverInLogger,#serverOutLogger')
                .transform(ack())

        // normal processing with tracing
        from('pix-iti8://0.0.0.0:18083?interceptorFactories=#consumerTracingInterceptor,#serverInLogger,#serverOutLogger')
                .transform(ack())

        // fictive route to test producer-side acceptance checking
        from('pix-iti8://0.0.0.0:18084?interceptorFactories=#serverInLogger,#serverOutLogger')
                .process {
                    resultMessage(it).body.MSH[9][1] = 'DOES NOT MATTER'
                    resultMessage(it).body.MSH[9][2] = 'SHOULD FAIL IN INTERCEPTORS'
                }
        
        // route with normal exception
        from('xds-iti8://0.0.0.0:18085?interceptorFactories=#serverInLogger,#serverOutLogger')
                .onException(Exception.class)
                    .maximumRedeliveries(0)
                    .end()
                .process { throw new Exception('Why do you cry, Willy?') }
        
        // route with runtime exception
        from('pix-iti8://0.0.0.0:18086?interceptorFactories=#serverInLogger,#serverOutLogger')
                .onException(Exception.class)
                    .maximumRedeliveries(0)
                    .end()
                .process { throw new RuntimeException('Jump over the lazy dog, you fox.') }

        from('xds-iti8://0.0.0.0:18089?audit=false&'+
                'codec=#alternativeCodec&' +
                'exceptionHandler=#iti8MllpExceptionHandler' +
                '&interceptorFactories=#serverInLogger,#serverOutLogger')
                .transform(ack())

    }
}

