/*
 * Copyright 2011 the original author or authors.
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

import ca.uhn.hl7v2.model.Message
import org.junit.BeforeClass
import org.junit.Test
import org.openehealth.ipf.commons.ihe.hl7v2.PIX
import org.openehealth.ipf.commons.ihe.hl7v2.definitions.CustomModelClassUtils
import org.openehealth.ipf.commons.ihe.hl7v2.definitions.HapiContextFactory
import org.openehealth.ipf.commons.ihe.hl7v3.PIXV3
import org.openehealth.ipf.gazelle.validation.profile.pixpdq.PixPdqTransactions

import static org.junit.Assert.assertTrue

/**
 * @author Boris Stanojevic
 */
class PixFeedSourceTranslatorTest extends Hl7TranslationTestContainer {

    @BeforeClass
    static void setUpClass() {
		PixFeedRequest2to3Translator translator = new PixFeedRequest2to3Translator()
		translator.providerOrganizationIdRoot = '2.16.840.1.113883.3.37.4.1.1.2.1.1'
        doSetUp('pixsource',
                null,
				translator,
                HapiContextFactory.createHapiContext(
                        CustomModelClassUtils.createFactory("pix", "2.3.1"),
                        PixPdqTransactions.ITI8))
    }

	@Test
	void testCreateMessage() {
		doTestV2toV3RequestTranslation('A01', 8, PIXV3.Interactions.ITI_44_PIX)
        doTestV2toV3RequestTranslation('A04', 8, PIXV3.Interactions.ITI_44_PIX)
        doTestV2toV3RequestTranslation('A01_with_BR', 8, PIXV3.Interactions.ITI_44_PIX)
	}

	@Test
	void testUpdateMessage() {
		doTestV2toV3RequestTranslation('A08', 8, PIXV3.Interactions.ITI_44_PIX)
	}

	@Test
	void testMergeMessage() {
		doTestV2toV3RequestTranslation('A40', 8, PIXV3.Interactions.ITI_44_PIX)
	}

    @Test
	void testNotSupportedMessage() throws Exception {
		String v2request = getFileContent('A10', false, true)
        Message msg = context.pipeParser.parse(v2request)
        try{
            v2tov3Translator.translateV2toV3(msg, null, 'UTF-8')
        } catch (Exception e){
            assertTrue(e.message.contains('Not supported HL7 message event'))
        }
	}
}
