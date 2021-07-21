/*
 * Copyright 2017 the original author or authors.
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
package org.openehealth.ipf.platform.camel.ihe.xds.iti92

import org.apache.cxf.headers.Header
import org.apache.cxf.jaxb.JAXBDataBinding
import org.apache.cxf.transport.servlet.CXFServlet
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openehealth.ipf.commons.audit.codes.EventActionCode
import org.openehealth.ipf.commons.audit.codes.EventOutcomeIndicator
import org.openehealth.ipf.commons.audit.model.AuditMessage
import org.openehealth.ipf.commons.ihe.core.atna.event.IHEAuditMessageBuilder
import org.openehealth.ipf.commons.ihe.xds.core.SampleData
import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentAvailability
import org.openehealth.ipf.commons.ihe.xds.core.metadata.LocalizedString
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Version
import org.openehealth.ipf.commons.ihe.xds.core.responses.Response
import org.openehealth.ipf.platform.camel.ihe.ws.AbstractWsEndpoint
import org.openehealth.ipf.platform.camel.ihe.xds.XdsStandardTestContainer

import javax.xml.namespace.QName

import static org.openehealth.ipf.commons.ihe.xds.core.responses.Status.FAILURE
import static org.openehealth.ipf.commons.ihe.xds.core.responses.Status.SUCCESS

/**
 * Tests the RMU ITI-92 transaction with a webservice and client adapter defined via URIs.
 * @author Boris Stanojevic
 */
class TestIti92 extends XdsStandardTestContainer {
    
    def static CONTEXT_DESCRIPTOR = 'rmu-iti92.xml'
    
    def SERVICE1 = "rmu-iti92://localhost:${port}/rmu-iti92-service1"
    def SERVICE2 = "rmu-iti92://localhost:${port}/rmu-iti92-service2"
    def SERVICE3 = "rmu-iti92://localhost:${port}/rmu-iti92-service3"

    def SERVICE2_ADDR = "http://localhost:${port}/rmu-iti92-service2"
    
    def request
    def docEntry
    def folder

    static void main(args) {
        startServer(new CXFServlet(), CONTEXT_DESCRIPTOR, false, DEMO_APP_PORT)
    }
    
    @BeforeAll
    static void classSetUp() {
        startServer(new CXFServlet(), CONTEXT_DESCRIPTOR)
    }
    
    @BeforeEach
    void setUp() {
        request = SampleData.createRegisterDocumentSet()
        docEntry = request.documentEntries[0]
        docEntry.logicalUuid = 'urn:uuid:20744602-ba65-44e9-87ee-a52303a5183e'
        docEntry.version = new Version('123')
        docEntry.documentAvailability = DocumentAvailability.ONLINE
        request.folders.clear()
    }
    
    @Test
    void testIti92() {
        assert SUCCESS == sendIt(SERVICE1, 'service 1').status
        assert SUCCESS == sendIt(SERVICE2, 'service 2').status
        assert auditSender.messages.size() == 4

        checkAudit(EventOutcomeIndicator.Success)
    }

    @Test
    void testSoapHeaders() {
        Header header1 = new Header(new QName("http://acme.org", "MyHeader1"), "header 1 contents", new JAXBDataBinding(String.class))
        Header header2 = new Header(new QName("http://openehealth.org", "MyHeader2"), "header 2 contents", new JAXBDataBinding(String.class))

        Collection<Header> headerCollection = [header1, header2]
        Map<QName, Header> headerMap = [
                (header1.name) : header1,
                (header2.name) : header2,
        ]

        assert SUCCESS == sendIt(SERVICE3, 'service 3', headerCollection).status
        assert SUCCESS == sendIt(SERVICE3, 'service 3', headerMap).status

        assert FAILURE == sendIt(SERVICE3, 'service 3', header1).status
        assert FAILURE == sendIt(SERVICE3, 'service 3', 'garbage').status
        assert FAILURE == sendIt(SERVICE3, 'service 3', null).status
    }

    @Test
    void testIti92FailureAudit() {
        assert FAILURE == sendIt(SERVICE1, 'falsch').status
        assert auditSender.messages.size() == 2
    }

    void checkAudit(EventOutcomeIndicator outcome) {
        AuditMessage message = getAudit(EventActionCode.Update, SERVICE2_ADDR)[0]

        assert message.activeParticipants.size() == 2
        assert message.participantObjectIdentifications.size() == 2

        checkEvent(message.eventIdentification, '110107', 'ITI-92', EventActionCode.Update, outcome)
        checkSource(message.activeParticipants[0], true)
        checkDestination(message.activeParticipants[1], SERVICE2_ADDR, false)
        checkAuditSource(message.auditSourceIdentification, 'sourceId')
        checkPatient(message.participantObjectIdentifications[0])
        checkSubmissionSet(message.participantObjectIdentifications[1])
        checkParticipantObjectDetail(message.participantObjectIdentifications[1].participantObjectDetails[0],
                IHEAuditMessageBuilder.URN_IHE_ITI_XCA_2010_HOME_COMMUNITY_ID, 'urn:oid:1.2.3.4.5.6.2333.23')

        message = getAudit(EventActionCode.Update, SERVICE2_ADDR)[1]

        assert message.activeParticipants.size() == 2
        assert message.participantObjectIdentifications.size() == 2

        checkEvent(message.eventIdentification, '110106', 'ITI-92', EventActionCode.Update, outcome)
        checkSource(message.activeParticipants[0], true)
        checkDestination(message.activeParticipants[1], SERVICE2_ADDR, false)
        checkAuditSource(message.auditSourceIdentification, 'sourceId')
        checkPatient(message.participantObjectIdentifications[0])
        checkSubmissionSet(message.participantObjectIdentifications[1])
        checkParticipantObjectDetail(message.participantObjectIdentifications[1].participantObjectDetails[0],
                IHEAuditMessageBuilder.URN_IHE_ITI_XCA_2010_HOME_COMMUNITY_ID, 'urn:oid:1.2.3.4.5.6.2333.23')
    }

    def sendIt(endpoint, value, soapHeaders = null) {
        docEntry.comments = new LocalizedString(value)
        Map camelHeaders = soapHeaders ? [(AbstractWsEndpoint.OUTGOING_SOAP_HEADERS) : soapHeaders] : null
        return send(endpoint, request, Response.class, camelHeaders)
    }

}