/*
 * Copyright 2015 the original author or authors.
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
package org.openehealth.ipf.platform.camel.ihe.mllp.iti30

import org.apache.camel.builder.RouteBuilder

import static org.openehealth.ipf.platform.camel.hl7.HL7v2.ack

/**
 * Camel route for generic unit tests.
 * @author Christian Ohr
 */
class Iti30TestRouteBuilder extends RouteBuilder {

    void configure() throws Exception {

        // normal processing with auditing
        from('pam-iti30://0.0.0.0:18100')
                .transform(ack())

        // fictive route to test producer-side acceptance checking
        from('pam-iti30://0.0.0.0:18101')
                .process {
            it.message.body.MSH[9][1] = 'DOES NOT MATTER'
            it.message.body.MSH[9][2] = 'SHOULD FAIL IN INTERCEPTORS'
        }

        // route with runtime exception
        from('pam-iti30://0.0.0.0:18102')
            .onException(Exception.class)
                .maximumRedeliveries(0)
                .end()
            .process { throw new RuntimeException('Jump over the lazy dog, you fox.') }

        // route with explicit options
        from('pam-iti30://0.0.0.0:18103?iheOptions=MERGE,LINK_UNLINK')
                .routeId("withOptions")
                .transform(ack())
    }
}

