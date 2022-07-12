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

import ca.uhn.hl7v2.model.Message
import ca.uhn.hl7v2.parser.PipeParser
import org.junit.jupiter.api.Test
import org.openehealth.ipf.platform.camel.ihe.mllp.core.AbstractMllpTest
import org.springframework.test.context.ContextConfiguration

import java.nio.ByteBuffer

import static org.junit.jupiter.api.Assertions.assertTrue

/**
 * Unit test for datatypes handling.
 * @author Dmytro Rud
 */
@ContextConfiguration('/iti8/iti-8-datatypes.xml')
class TestIti8Datatypes extends AbstractMllpTest {
    
    /**
     * Checks whether various request data types are being handled properly.
     */
    @Test
    void testRequestDataTypes() {
        def endpointUri = "pix-iti8://localhost:18188?audit=false&timeout=${TIMEOUT}"
        def originalBody = getMessageString('ADT^A01', '2.3.1')
        def body
        
        /*
         * Refer to {@link Hl7v2MarshalUtils#typeSupported()}
         * for the list of currently supported types. 
         */
        
        // String
        body = originalBody
        send(endpointUri, body)
        
        // HAPI message
        body = new PipeParser().parse(originalBody)
        send(endpointUri, body)
        
        // File
        URL fileUrl = TestIti8Datatypes.class.classLoader.getResource('iti8/hl7v2message.hl7')
        body = new File(fileUrl.toURI())
        send(endpointUri, body)
        
        // InputStream
        body = new ByteArrayInputStream(originalBody.bytes)
        send(endpointUri, body)
        
        // NIO ByteBuffer
        body = ByteBuffer.wrap(originalBody.bytes)
        send(endpointUri, body)
        
        // byte[]
        body = originalBody.bytes
        send(endpointUri, body)
        
        
        /* --------------- values of unsupported types and null should cause exceptions --------------- */
        def exceptionThrown = false
        try {
            body = null
            send(endpointUri, body)
        } catch (Exception e) {
            exceptionThrown = true
        }
        assertTrue(exceptionThrown)
        
        exceptionThrown = false
        try {
            body = new Integer(12345)
            send(endpointUri, body)
        } catch (Exception e) {
            exceptionThrown = true
        }
        assertTrue(exceptionThrown)
    }
    
    
    /**
     * Checks whether various response data types are being handled properly.
     */
    @Test
    void testResponseDataTypes() {
        final String endpointUri = "pix-iti8://localhost:18187?audit=false&timeout=${TIMEOUT}"
        final String body = getMessageString('ADT^A01', '2.3.1')
        DatatypesRouteBuilder.cleanCheckedContentTypes()
        
        // 0-8 should return ACks
        for(int i = 0; i <= 7; ++i) {
            def msg = send(endpointUri, body)
            assertACK(msg)
        }
        
        // 9-12 should throw exceptions
        for(int i = 8; i <= 11; ++i) {
            def msg = send(endpointUri, body)
            assertNAK(msg)
        }
        
        // 13-18 should return NAKs
        for(int i = 12; i <= 17; ++i) {
            Message msg = send(endpointUri, body)
            assertNAK(msg)
        }
        
        // prove that we have not missed any of the pre-configured data types
        assertTrue(DatatypesRouteBuilder.allContentTypesChecked())
    }
    
}