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

package org.openehealth.ipf.commons.ihe.xds.rad75;

import org.junit.Test;
import org.openehealth.ipf.commons.audit.codes.EventActionCode;
import org.openehealth.ipf.commons.audit.codes.EventIdCode;
import org.openehealth.ipf.commons.audit.codes.EventOutcomeIndicator;
import org.openehealth.ipf.commons.audit.codes.ParticipantObjectTypeCode;
import org.openehealth.ipf.commons.audit.codes.ParticipantObjectTypeCodeRole;
import org.openehealth.ipf.commons.ihe.core.atna.AuditDataset.HumanUser;
import org.openehealth.ipf.commons.ihe.xds.atna.XdsAuditorTestBase;
import org.openehealth.ipf.commons.ihe.xds.core.audit.XdsIRetrieveAuditStrategy30;
import org.openehealth.ipf.commons.ihe.xds.core.audit.XdsNonconstructiveDocumentSetRequestAuditDataset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Christian Ohr
 */
public class Rad75AuditStrategyTest extends XdsAuditorTestBase {

    @Test
    public void testServerSide() {
        testRequest(true, new Rad75ServerAuditStrategy());
    }

    @Test
    public void testClientSide() {
        testRequest(false, new Rad75ClientAuditStrategy());
    }

    private void testRequest(boolean serverSide, XdsIRetrieveAuditStrategy30 strategy) {
        var auditDataset = getXdsAuditDataset(strategy);
        var auditMessage = makeAuditMessage(strategy, auditContext, auditDataset);

        assertNotNull(auditMessage);
        auditMessage.validate();

        // System.out.println(printAuditMessage(auditMessage));

        assertCommonXdsAuditAttributes(auditMessage,
                EventOutcomeIndicator.Success,
                serverSide ? EventIdCode.DICOMInstancesTransferred: EventIdCode.DICOMInstancesAccessed,
                serverSide ? EventActionCode.Read : EventActionCode.Create,
                !serverSide,
                true);

        assertEquals(3, auditMessage.findParticipantObjectIdentifications(
                poit -> poit.getParticipantObjectTypeCodeRole() == ParticipantObjectTypeCodeRole.Report).size());

        assertEquals(4, auditMessage.findParticipantObjectIdentifications(
                poit -> poit.getParticipantObjectTypeCode() == ParticipantObjectTypeCode.System)
                .get(0)
                .getParticipantObjectDetails().size());

    }

    private XdsNonconstructiveDocumentSetRequestAuditDataset getXdsAuditDataset(XdsIRetrieveAuditStrategy30 strategy) {
        var auditDataset = strategy.createAuditDataset();
        auditDataset.setEventOutcomeIndicator(EventOutcomeIndicator.Success);
        // auditDataset.setLocalAddress(SERVER_URI);
        auditDataset.setRemoteAddress(CLIENT_IP_ADDRESS);
        auditDataset.setSourceUserId(REPLY_TO_URI);
        auditDataset.setDestinationUserId(SERVER_URI);
        auditDataset.setRequestPayload(QUERY_PAYLOAD);
        auditDataset.setPurposesOfUse(PURPOSES_OF_USE);
        auditDataset.getPatientIds().add(PATIENT_IDS[0]);
        auditDataset.getHumanUsers().add(new HumanUser(USER_ID, USER_NAME, USER_ROLES));

        for (var i = 0; i < 3; i++) {
            auditDataset.getDocuments().add(
                    new XdsNonconstructiveDocumentSetRequestAuditDataset.Document(
                            DOCUMENT_OIDS[i],
                            REPOSITORY_OIDS[i],
                            HOME_COMMUNITY_IDS[i],
                            STUDY_INSTANCE_UUIDS[i],
                            SERIES_INSTANCE_UUIDS[i],
                            XdsNonconstructiveDocumentSetRequestAuditDataset.Status.SUCCESSFUL));
        }
        return auditDataset;
    }
}
