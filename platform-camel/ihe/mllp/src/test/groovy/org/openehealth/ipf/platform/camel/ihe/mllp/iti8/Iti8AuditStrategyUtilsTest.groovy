/*
 * Copyright 2013 the original author or authors.
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

import ca.uhn.hl7v2.HL7Exception
import ca.uhn.hl7v2.HapiContext
import ca.uhn.hl7v2.model.Message
import org.junit.jupiter.api.Test
import org.openehealth.ipf.commons.ihe.hl7v2.audit.FeedAuditDataset
import org.openehealth.ipf.commons.ihe.hl7v2.audit.iti8.Iti8AuditStrategyUtils
import org.openehealth.ipf.commons.ihe.hl7v2.definitions.CustomModelClassUtils
import org.openehealth.ipf.commons.ihe.hl7v2.definitions.HapiContextFactory
import org.openehealth.ipf.gazelle.validation.profile.pixpdq.PixPdqTransactions

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNull

/**
 *
 */
class Iti8AuditStrategyUtilsTest {

    private static final HapiContext CONTEXT = HapiContextFactory.createHapiContext(
            CustomModelClassUtils.createFactory("pix", "2.3.1"), PixPdqTransactions.ITI8)

    @Test
    void testExtractPatientId(){
        Message message = load(CONTEXT, 'iti8/iti8-a40.hl7')
        FeedAuditDataset dataset = new FeedAuditDataset(true)
        Iti8AuditStrategyUtils.enrichAuditDatasetFromRequest(dataset, message)
        assertEquals('305014^^^MPI-NS-P&2.16.840.1.113883.3.37.4.1.1.2.1.1&ISO'
                + '~7200117317^^^BBB&2.16.840.1.113883.3.37.4.1.1.2.611.1&ISO'
                + '~7200117355^^^CCC&2.16.840.1.113883.3.37.4.1.1.2.711.1&ISO', dataset.patientId)
        assertEquals('305010~7200117359^^^BBB&2.16.840.1.113883.3.37.4.1.1.2.611.1&ISO', dataset.oldPatientId)
    }

    @Test
    void testExtractPatientIdWithoutPID(){
        Message message = load(CONTEXT, 'iti8/iti8-a01-incomplete.hl7')
        FeedAuditDataset dataset = new FeedAuditDataset(true)
        Iti8AuditStrategyUtils.enrichAuditDatasetFromRequest(dataset, message)
        assertNull(dataset.patientId)
    }

    private static <T extends Message> T load(HapiContext context, String fileName) throws HL7Exception {
        return (T)context.getPipeParser().parse(
                new Scanner(Iti8AuditStrategyUtilsTest.class.getResourceAsStream("/" + fileName)).useDelimiter("\\A").next())
    }
}
