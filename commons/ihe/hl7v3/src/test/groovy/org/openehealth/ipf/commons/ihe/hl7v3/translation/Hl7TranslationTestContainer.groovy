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

import ca.uhn.hl7v2.HapiContext
import ca.uhn.hl7v2.model.Message
import org.apache.commons.io.IOUtils
import org.openehealth.ipf.commons.core.config.ContextFacade
import org.openehealth.ipf.commons.core.config.Registry
import org.openehealth.ipf.commons.ihe.hl7v3.Hl7v3InteractionId
import org.openehealth.ipf.commons.map.BidiMappingService
import org.openehealth.ipf.commons.map.MappingService
import org.openehealth.ipf.commons.xml.CombinedXmlValidator
import org.openehealth.ipf.modules.hl7.validation.Validator
import org.xmlunit.builder.DiffBuilder
import org.xmlunit.builder.Input
import org.xmlunit.diff.Diff

import java.nio.charset.StandardCharsets

import static org.easymock.EasyMock.*

/**
 * Test container for HL7 v3-v2 transformation routines.
 * @author Dmytro Rud
 */
class Hl7TranslationTestContainer {
    private static final boolean V3       = true
    private static final boolean V2       = false
    private static final boolean REQUEST  = true
    private static final boolean RESPONSE = false

    protected static final CombinedXmlValidator V3_VALIDATOR = new CombinedXmlValidator()
    
    static String transactionName
    static Hl7TranslatorV3toV2 v3tov2Translator
    static Hl7TranslatorV2toV3 v2tov3Translator
    static HapiContext context
    
    static void doSetUp(
            String transactionName, 
            Hl7TranslatorV3toV2 v3tov2Translator,
            Hl7TranslatorV2toV3 v2tov3Translator,
            HapiContext context) {
        Hl7TranslationTestContainer.transactionName = transactionName

        Hl7TranslationTestContainer.v3tov2Translator = v3tov2Translator
        Hl7TranslationTestContainer.v2tov3Translator = v2tov3Translator
        Hl7TranslationTestContainer.context = context

        BidiMappingService mappingService = new BidiMappingService()
        mappingService.setMappingScript(Hl7TranslationTestContainer.class.getResource('/META-INF/map/hl7-v2-v3-translation.map'))
        Registry registry = createMock(Registry)
        ContextFacade.setRegistry(registry)
        expect(registry.bean(MappingService)).andReturn(mappingService).anyTimes()
        expect(registry.bean(HapiContext)).andReturn(context).anyTimes()
        replay(registry)
    }      

    
    /**
     * Helper method to read in an HL7 message.
     * @param fn
     *      name root of the file
     * @param v3
     *      whether a v3 (<tt>true</tt>) or a v2 (<tt>false</tt>)
     *      message should be read
     * @param request
     *      whether a request (<tt>true</tt>) or a response (<tt>false</tt>)
     *      message should be read
     */
    String getFileContent(String fn, boolean v3, boolean request) {
        String resourceName = new StringBuilder()
            .append('translation/')
            .append(transactionName)
            .append(v3 ? '/v3/' : '/v2/')
            .append(fn)
            .append(request ? '' : '_Response')
            .append(v3 ? '.xml' : '.hl7')
            .toString()
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName)
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8)
    }


    void doTestV3toV2RequestTranslation(String fn, int v2index, Hl7v3InteractionId v3Id) {
        String v3request = getFileContent(fn, V3, REQUEST)
        V3_VALIDATOR.validate(v3request, v3Id.requestValidationProfile)
        
        String expectedV2request = getFileContent(fn, V2, REQUEST)
        Message translatedV2request = v3tov2Translator.translateV3toV2(v3request, null)
        Validator.validate(translatedV2request, null)
        assert translatedV2request.toString().trim() == expectedV2request.trim()
    }


    void doTestV2toV3ResponseTranslation(String fn, int v2index, Hl7v3InteractionId v3Id) {
        String v3request = getFileContent(fn, V3, REQUEST)
        String v2response = getFileContent(fn, V2, RESPONSE)
        Message msg = context.pipeParser.parse(v2response)
        Validator.validate(msg, null)

        String expectedV3response = getFileContent(fn, V3, RESPONSE)
        String translatedV3response = v2tov3Translator.translateV2toV3(msg, v3request, 'UTF-8')
        V3_VALIDATOR.validate(translatedV3response, v3Id.responseValidationProfile)

        Diff diff = DiffBuilder
                .compare(Input.fromString(expectedV3response))
                .withTest(translatedV3response)
                .normalizeWhitespace()
                .build()
        assert diff.differences.size() == 1
        assert diff.toString().contains('creationTime')
    }
    
    void doTestV2toV3RequestTranslation(String fn, int v2index, Hl7v3InteractionId v3Id) {
        String v2request = getFileContent(fn, V2, REQUEST)
        Message msg = context.pipeParser.parse(v2request)
        Validator.validate(msg, null)

        String expectedV3response = getFileContent(fn, V3, RESPONSE)
        String translatedV3response = v2tov3Translator.translateV2toV3(msg, null, 'UTF-8')
        V3_VALIDATOR.validate(translatedV3response, v3Id.requestValidationProfile)

        Diff diff = DiffBuilder
                .compare(Input.fromString(expectedV3response))
                .withTest(translatedV3response)
                .normalizeWhitespace()
                .build()
        assert diff.differences.size() == 1
        assert diff.toString().contains('creationTime')
    }

}
