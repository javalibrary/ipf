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

package org.openehealth.ipf.commons.ihe.hl7v2.audit.it8;

import org.junit.jupiter.api.Test;
import org.openehealth.ipf.commons.audit.codes.EventActionCode;
import org.openehealth.ipf.commons.audit.codes.EventIdCode;
import org.openehealth.ipf.commons.audit.codes.EventOutcomeIndicator;
import org.openehealth.ipf.commons.ihe.hl7v2.audit.FeedAuditDataset;
import org.openehealth.ipf.commons.ihe.hl7v2.audit.Hl7v2AuditorTestBase;
import org.openehealth.ipf.commons.ihe.hl7v2.audit.iti8.Iti8AuditStrategy;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Christian Ohr
 */
public class Iti8AuditStrategyTest extends Hl7v2AuditorTestBase {

    @Test
    public void testCreateServerSide() {
        testRequest(true, EventActionCode.Create);
    }

    @Test
    public void testCreateClientSide() {
        testRequest(false, EventActionCode.Create);
    }

    @Test
    public void testUpdateServerSide() {
        testRequest(true, EventActionCode.Update);
    }

    @Test
    public void testUpdateClientSide() {
        testRequest(false, EventActionCode.Update);
    }


    private void testRequest(boolean serverSide, EventActionCode eventActionCode) {
        var strategy = new Iti8AuditStrategy(serverSide);
        var auditDataset = getHl7v2AuditDataset(strategy, eventActionCode);
        var auditMessage = makeAuditMessage(strategy, auditContext, auditDataset);

        assertNotNull(auditMessage);
        auditMessage.validate();
        assertCommonV2AuditAttributes(auditMessage,
                EventOutcomeIndicator.Success,
                EventIdCode.PatientRecord,
                eventActionCode,
                serverSide,
                true);
    }

    private FeedAuditDataset getHl7v2AuditDataset(Iti8AuditStrategy strategy, EventActionCode eventActionCode) {
        var auditDataset = strategy.createAuditDataset();
        auditDataset.setEventOutcomeIndicator(EventOutcomeIndicator.Success);
        // auditDataset.setLocalAddress(SERVER_URI);
        auditDataset.setRemoteAddress(CLIENT_IP_ADDRESS);
        auditDataset.setMessageControlId(MESSAGE_ID);
        auditDataset.setPatientId(PATIENT_IDS[0]);
        switch (eventActionCode) {
            case Create: auditDataset.setMessageType("A01"); break;
            case Update: auditDataset.setMessageType("A08"); break;
        }

        auditDataset.setSendingFacility(SENDING_FACILITY);
        auditDataset.setSendingApplication(SENDING_APPLICATION);
        auditDataset.setReceivingFacility(RECEIVING_FACILITY);
        auditDataset.setReceivingApplication(RECEIVING_APPLICATION);
        return auditDataset;
    }
}
