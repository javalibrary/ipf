/*
 * Copyright 2018 the original author or authors.
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

package org.openehealth.ipf.platform.camel.ihe.fhir.iti68;

import ca.uhn.fhir.rest.gclient.ICriterion;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openehealth.ipf.commons.audit.codes.*;
import org.openehealth.ipf.commons.audit.utils.AuditUtils;
import org.openehealth.ipf.commons.ihe.fhir.audit.codes.FhirEventTypeCode;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
public class TestIti68Success extends AbstractTestIti68 {

    private static final String CONTEXT_DESCRIPTOR = "iti-68.xml";

    @BeforeAll
    public static void setUpClass() {
        startServer(CONTEXT_DESCRIPTOR);
    }


    @Test
    public void testRetrieveDocument() {

        var response = sendViaProducer((ICriterion<?>) null);
        assertArrayEquals(Iti68TestRouteBuilder.DATA, response);

        // Check ATNA Audit

        var sender = getAuditSender();
        assertEquals(1, sender.getMessages().size());
        var event = sender.getMessages().get(0);

        // Event
        assertEquals(
                EventOutcomeIndicator.Success,
                event.getEventIdentification().getEventOutcomeIndicator());
        assertEquals(
                EventActionCode.Create,
                event.getEventIdentification().getEventActionCode());
        assertEquals(EventIdCode.Export, event.getEventIdentification().getEventID());
        assertEquals(FhirEventTypeCode.MobileDocumentRetrieval, event.getEventIdentification().getEventTypeCode().get(0));


        // ActiveParticipant Source
        var source = event.getActiveParticipants().get(0);
        assertTrue(source.isUserIsRequestor());
        assertEquals("127.0.0.1", source.getNetworkAccessPointID());
        assertEquals(NetworkAccessPointTypeCode.IPAddress, source.getNetworkAccessPointTypeCode());

        // ActiveParticipant Destination
        var destination = event.getActiveParticipants().get(1);
        assertFalse(destination.isUserIsRequestor());
        assertEquals(AuditUtils.getLocalIPAddress(), destination.getNetworkAccessPointID());
        assertEquals("http://localhost:" + DEMO_APP_PORT + "/", destination.getUserID());

        // Audit Source
        var sourceIdentificationType = event.getAuditSourceIdentification();
        assertEquals("IPF", sourceIdentificationType.getAuditSourceID());
        assertEquals("IPF", sourceIdentificationType.getAuditEnterpriseSiteID());

        var poit = event.findParticipantObjectIdentifications(p ->
                p.getParticipantObjectTypeCode() == ParticipantObjectTypeCode.System).get(0);
        assertEquals(Iti68TestRouteBuilder.DOCUMENT_UNIQUE_ID, poit.getParticipantObjectID());
        assertEquals(2, poit.getParticipantObjectDetails().size());

    }


}
