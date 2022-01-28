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
package org.openehealth.ipf.platform.camel.ihe.mllp.iti64

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
 * Unit tests for the Notify XAD-PID Link Change transaction a.k.a. ITI-64.
 * @author Boris Stanojevic
 */
@ContextConfiguration('/iti64/iti-64.xml')
class TestIti64 extends AbstractMllpTest {
    
    static String getMessageString(msh9, msh12) {
        def s = 'MSH|^~\\&|REPOSITORY|ENT|RSP1P8|GOOD HEALTH HOSPITAL|200701051530|' +
                "SEC|${msh9}|0000009|P|${msh12}\n" +
                'EVN|A43|200701051530\n' +
                'PID|1||new^^^&1.2.3.4&ISO~previous^^^&1.2.3.4&ISO||EVERYWOMAN^EVE||\n' +
                'MRG|old^^^&1.2.3.4&ISO~subsumed^^^&1.2.3.4&ISO|||\n'
        s
    }
    
    /**
     * Happy case, audit either enabled or disabled.
     * Expected result: ACK response, two or zero audit items.
     */
    @Test
    void testHappyCase() {
        doTestHappyCaseAndAudit("xpid-iti64://localhost:18491?timeout=${TIMEOUT}", 2)
    }
    
    def doTestHappyCaseAndAudit(String endpointUri, int expectedAuditItemsCount) {
        final String body = getMessageString('ADT^A43^ADT_A43', '2.5')
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
        doTestInacceptanceOnConsumer('ADT^A43', '2.3.1')
    }
    @Test
    public void testInacceptanceOnConsumer3() {
        doTestInacceptanceOnConsumer('ADT^A43^ADT_A44', '2.5')
    }

    def doTestInacceptanceOnConsumer(msh9, msh12) {
        def endpointUri = 'xpid-iti64://localhost:18090'
        def endpoint = camelContext.getEndpoint(endpointUri)
        def consumer = endpoint.createConsumer(
                [process : { Exchange e -> /* nop */ }] as Processor
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
        doTestInacceptanceOnProducer('ADT^A01', '2.5')
    }
    @Test
    void testInacceptanceOnProducer2() {
        doTestInacceptanceOnProducer('ADT^A43^ADT_A44', '2.5')
    }
    @Test
    void testInacceptanceOnProducer3() {
        doTestInacceptanceOnProducer('ADT^A43', '2.3.1')
    }

    def doTestInacceptanceOnProducer(String msh9, String msh12) {
        def endpointUri = "xpid-iti64://localhost:18490?timeout=${TIMEOUT}"
        def body = getMessageString(msh9, msh12)
        def failed = true

        try {
            send(endpointUri, body)
        } catch (Exception e) {
            def cause = e.getCause()
            if((e instanceof HL7Exception) || (cause instanceof HL7Exception)) {
                failed = false
            }
        }
        assertFalse(failed)
        assertAuditEvents { it.messages.empty }
    }
}
