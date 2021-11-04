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
package org.openehealth.ipf.commons.ihe.hl7v3.translation


import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.openehealth.ipf.commons.ihe.hl7v2.definitions.HapiContextFactory
import org.openehealth.ipf.commons.ihe.hl7v3.PIXV3
import org.openehealth.ipf.gazelle.validation.profile.pixpdq.PixPdqTransactions

/**
 * Test for PIX Feed translator.
 * @author Marek Václavík, Dmytro Rud
 */
class PixFeedTranslatorTest extends Hl7TranslationTestContainer {
 
    @BeforeAll
    static void setUpClass() {
        doSetUp('pixfeed',
                new PixFeedRequest3to2Translator(),
                new PixFeedAck2to3Translator(),
                HapiContextFactory.createHapiContext(PixPdqTransactions.ITI8))
    }        
  
    @Test
    void testMaximalMergeRequest() {
        doTestV3toV2RequestTranslation('PIX_FEED_MERGE_Maximal_Request', 8, PIXV3.Interactions.ITI_44_PIX)
    }
  
    @Test
    void testMaximalRegistrationRequest() {
        doTestV3toV2RequestTranslation('PIX_FEED_REG_Maximal_Request', 8, PIXV3.Interactions.ITI_44_PIX)
    }
  
    @Test
    void testMaximalRevRequest() {
        doTestV3toV2RequestTranslation('PIX_FEED_REV_Maximal_Request', 8, PIXV3.Interactions.ITI_44_PIX)
    }
  
}
