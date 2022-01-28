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
package org.openehealth.ipf.platform.camel.ihe.mllp.iti10

import ca.uhn.hl7v2.HL7Exception
import ca.uhn.hl7v2.parser.PipeParser
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.support.DefaultExchange
import org.junit.jupiter.api.Test
import org.openehealth.ipf.platform.camel.core.util.Exchanges
import org.openehealth.ipf.platform.camel.ihe.mllp.core.AbstractMllpTest
import org.springframework.test.context.ContextConfiguration

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertFalse

/**
 * Unit tests for the PIX Update Notification transaction a.k.a. ITI-10.
 * @author Dmytro Rud
 */
@ContextConfiguration('/iti10/iti-10.xml')
class TestIti10 extends AbstractMllpTest {

    /**
     * Happy case, audit either enabled or disabled.
     * Expected result: ACK response, two or zero audit items.
     */
    @Test
    void testHappyCaseAndAudit1() {
        doTestHappyCaseAndAudit("pix-iti10://localhost:18108?timeout=${TIMEOUT}", 2)
    }

    @Test
    void testHappyCaseAndAudit2() {
        doTestHappyCaseAndAudit("pix-iti10://localhost:18107?audit=false&timeout=${TIMEOUT}", 0)
    }

    def doTestHappyCaseAndAudit(String endpointUri, int expectedAuditItemsCount) {
        final String body = getMessageString10('ADT^A31^ADT_A05', '2.5')
        def msg = send(endpointUri, body)
        assertACK(msg)
        assertAuditEvents { it.messages.size() == expectedAuditItemsCount }
    }

    /**
     * Inacceptable messages (wrong message type, wrong trigger event, wrong version), 
     * on consumer side, audit enabled.
     * Expected results: NAK responses, no audit.
     * <p>
     * We do not use MLLP producers, because they perform their own acceptance
     * tests and do not pass inacceptable messages to the consumers
     * (it is really a feature, not a bug! ;-)) 
     */
    @Test
    public void testInacceptanceOnConsumer1() {
        doTestInacceptanceOnConsumer('MDM^T01', '2.5')
    }

    @Test
    public void testInacceptanceOnConsumer2() {
        doTestInacceptanceOnConsumer('ADT^A02', '2.5')
    }

    @Test
    public void testInacceptanceOnConsumer3() {
        doTestInacceptanceOnConsumer('ADT^A31', '2.3.1')
    }

    @Test
    public void testInacceptanceOnConsumer4() {
        doTestInacceptanceOnConsumer('ADT^A31', '3.1415926')
    }

    @Test
    public void testInacceptanceOnConsumer5() {
        doTestInacceptanceOnConsumer('ADT^A31^ADT_A02', '2.5')
    }

    def doTestInacceptanceOnConsumer(String msh9, String msh12) {
        def endpointUri = 'pix-iti10://localhost:18108'
        def endpoint = camelContext.getEndpoint(endpointUri)
        def consumer = endpoint.createConsumer(
                [process: { Exchange e -> /* nop */ }] as Processor
        )
        def processor = consumer.processor

        def body = getMessageString(msh9, msh12)
        def exchange = new DefaultExchange(camelContext)
        exchange.in.body = body

        processor.process(exchange)
        def response = Exchanges.resultMessage(exchange).body
        def msg = new PipeParser().parse(response)
        assertNAK(msg)
        assertAuditEvents { it.messages.empty }
    }

    /**
     * Inacceptable messages (wrong message type, wrong trigger event, wrong version), 
     * on producer side, audit enabled.
     * Expected results: raise of corresponding HL7-related exceptions, no audit.
     */
    @Test
    void testInacceptanceOnProducer1() {
        doTestInacceptanceOnProducer('MDM^T01', '2.5')
    }

    @Test
    void testInacceptanceOnProducer2() {
        doTestInacceptanceOnProducer('ADT^A02', '2.5')
    }

    @Test
    void testInacceptanceOnProducer3() {
        doTestInacceptanceOnProducer('ADT^A31', '2.3.1')
    }

    @Test
    void testInacceptanceOnProducer4() {
        doTestInacceptanceOnProducer('ADT^A31', '3.1415926')
    }

    @Test
    void testInacceptanceOnProducer5() {
        doTestInacceptanceOnProducer('ADT^A31^ADT_A02', '2.5')
    }

    def doTestInacceptanceOnProducer(String msh9, String msh12) {
        def endpointUri = 'pix-iti10://localhost:18108'
        def body = getMessageString(msh9, msh12)
        def failed = true
        def ex

        try {
            send(endpointUri, body)
        } catch (Exception e) {
            def cause = e.getCause()
            if ((e instanceof HL7Exception) || (cause instanceof HL7Exception)) {
                failed = false
            }
            ex = e
        }
        assertFalse(failed, "Expected HL7Exception but got ${ex} with cause ${ex.cause}")
        assertAuditEvents { it.messages.empty }
    }

}
