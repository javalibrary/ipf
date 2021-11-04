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
package org.openehealth.ipf.platform.camel.ihe.hl7v3.iti47

import ca.uhn.hl7v2.parser.DefaultModelClassFactory
import ca.uhn.hl7v2.parser.ModelClassFactory
import org.apache.camel.Exchange
import org.apache.cxf.transport.servlet.CXFServlet
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.openehealth.ipf.commons.core.config.ContextFacade
import org.openehealth.ipf.commons.core.config.Registry
import org.openehealth.ipf.commons.map.BidiMappingService
import org.openehealth.ipf.commons.map.MappingService
import org.openehealth.ipf.platform.camel.core.util.Exchanges
import org.openehealth.ipf.platform.camel.ihe.hl7v3.HL7v3StandardTestContainer

import static org.easymock.EasyMock.*
import static org.junit.jupiter.api.Assertions.assertTrue

class Testiti47CamelOnly extends HL7v3StandardTestContainer {

    private static String requestMessage, responseMessage


    @BeforeAll
    static void setUpClass() {
        BidiMappingService mappingService = new BidiMappingService()
        mappingService.setMappingScript(Testiti47CamelOnly.class.getResource("/example2.map"))
        ModelClassFactory mcf = new DefaultModelClassFactory()
        Registry registry = createMock(Registry)
        ContextFacade.setRegistry(registry)
        expect(registry.bean(MappingService)).andReturn(mappingService).anyTimes()
        expect(registry.bean(ModelClassFactory)).andReturn(mcf).anyTimes()
        replay(registry)

        requestMessage  = readFile("translation/pdq/v3/PDQ.xml")
        responseMessage = readFile("translation/pdq/v2/PDQ_Response.hl7")
        startServer(new CXFServlet(), "camel-only.xml")
    }


    @Test
    void testCamelOnly() {
        String endpointUri = "pdqv3-iti47://localhost:" + getPort() + "/iti47Service"
        Exchange responseExchange = (Exchange) send(endpointUri, getRequestMessage())
        String response = Exchanges.resultMessage(responseExchange).getBody(String.class)
        assertTrue(response.contains("<typeCode code=\"AA\"/>"))
    }


    static String getRequestMessage() {
        return requestMessage
    }


    static String getResponseMessage() {
        return responseMessage
    }
}
