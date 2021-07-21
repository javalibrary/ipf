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

package org.openehealth.ipf.commons.ihe.hpd.iti59;

import org.junit.jupiter.api.Test;
import org.openehealth.ipf.commons.audit.codes.*;
import org.openehealth.ipf.commons.ihe.core.atna.AuditDataset.HumanUser;
import org.openehealth.ipf.commons.ihe.hpd.atna.HpdAuditorTestBase;
import org.openehealth.ipf.commons.ihe.hpd.audit.codes.HpdParticipantObjectIdTypeCode;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Christian Ohr
 */
public class Iti59AuditStrategyTest extends HpdAuditorTestBase {

    @Test
    public void testServerSide() {
        testRequest(true, new Iti59ServerAuditStrategy());
    }

    @Test
    public void testClientSide() {
        testRequest(false, new Iti59ClientAuditStrategy());
    }

    private void testRequest(boolean serverSide, Iti59AuditStrategy strategy) {
        var auditDataset = getHpdAuditDataset(strategy);
        var auditMessages = makeAuditMessages(strategy, auditContext, auditDataset);

        assertEquals(3, auditMessages.length);

        for (var auditMessage : auditMessages) {
            auditMessage.validate();
            // System.out.println(printAuditMessage(auditMessage));

            assertEquals(1, auditMessage.getParticipantObjectIdentifications().size());
            var participant = auditMessage.getParticipantObjectIdentifications().get(0);
            assertEquals(ParticipantObjectTypeCodeRole.Provider, participant.getParticipantObjectTypeCodeRole());
            assertEquals(HpdParticipantObjectIdTypeCode.RelativeDistinguishedName, participant.getParticipantObjectIDTypeCode());
        }

        assertCommonHpdAuditAttributes(auditMessages[0],
                EventOutcomeIndicator.Success,
                serverSide ? EventIdCode.Import : EventIdCode.Export,
                EventActionCode.Update,
                serverSide);

        var participant = auditMessages[2].getParticipantObjectIdentifications().get(0);
        assertEquals(1, participant.getParticipantObjectDetails().size());
        assertEquals("old uid", participant.getParticipantObjectDetails().get(0).getType());
        assertEquals("1.11.111.1111:klmno", new String(participant.getParticipantObjectDetails().get(0).getValue()));
        assertEquals("2.22.222.2222:klmno", participant.getParticipantObjectID());
    }

    private Iti59AuditDataset getHpdAuditDataset(Iti59AuditStrategy strategy) {
        var auditDataset = strategy.createAuditDataset();
        auditDataset.setEventOutcomeIndicator(EventOutcomeIndicator.Success);
        // auditDataset.setLocalAddress(SERVER_URI);
        auditDataset.setRemoteAddress(CLIENT_IP_ADDRESS);
        auditDataset.setSourceUserId(REPLY_TO_URI);
        auditDataset.setDestinationUserId(SERVER_URI);
        auditDataset.setRequestPayload(QUERY_PAYLOAD);
        auditDataset.setPurposesOfUse(PURPOSES_OF_USE);
        auditDataset.getHumanUsers().add(new HumanUser(USER_ID, USER_NAME, USER_ROLES));

        var requestItem1 = new Iti59AuditDataset.RequestItem(QUERY_ID, EventActionCode.Update);
        requestItem1.setUid("1.2.3.4.5.6:abcde");
        requestItem1.setParticipantObjectTypeCode(ParticipantObjectTypeCode.Organization);
        requestItem1.setOutcomeCode(EventOutcomeIndicator.Success);

        var requestItem2 = new Iti59AuditDataset.RequestItem(QUERY_ID, EventActionCode.Create);
        requestItem2.setUid("2.3.4.5.6.7:qrstu");
        requestItem2.setParticipantObjectTypeCode(ParticipantObjectTypeCode.Person);
        requestItem2.setOutcomeCode(EventOutcomeIndicator.MinorFailure);

        // shall be absent in the ATNA message because the UID is null
        var requestItem3 = new Iti59AuditDataset.RequestItem(QUERY_ID, EventActionCode.Execute);
        requestItem3.setUid(null);
        requestItem3.setNewUid("1.3.5.7:fghij");
        requestItem3.setParticipantObjectTypeCode(ParticipantObjectTypeCode.Person);
        requestItem3.setOutcomeCode(EventOutcomeIndicator.Success);

        var requestItem4 = new Iti59AuditDataset.RequestItem(QUERY_ID, EventActionCode.Execute);
        requestItem4.setUid("1.11.111.1111:klmno");
        requestItem4.setNewUid("2.22.222.2222:klmno");
        requestItem4.setParticipantObjectTypeCode(ParticipantObjectTypeCode.Organization);
        requestItem4.setParticipantObjectDataLifeCycle(ParticipantObjectDataLifeCycle.Translation);
        requestItem4.setOutcomeCode(EventOutcomeIndicator.Success);

        auditDataset.setRequestItems(new Iti59AuditDataset.RequestItem[]{requestItem1, requestItem2, requestItem3, requestItem4});
        return auditDataset;
    }
}
