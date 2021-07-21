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
package org.openehealth.ipf.commons.ihe.xds.core.transform.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openehealth.ipf.commons.ihe.xds.core.SampleData;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.AssociationType;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntryType;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Vocabulary;
import org.openehealth.ipf.commons.ihe.xds.core.requests.RegisterDocumentSet;
import org.openehealth.ipf.commons.ihe.xds.core.transform.ebxml.FactoryCreator;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link RegisterDocumentSetTransformer}.
 * @author Jens Riemschneider
 */
public abstract class RegisterDocumentSetTransformerTestBase implements FactoryCreator {
    private RegisterDocumentSetTransformer transformer;
    private RegisterDocumentSet request;

    @BeforeEach
    public void setUp() {
        var factory = createFactory();
        transformer = new RegisterDocumentSetTransformer(factory);        

        request = SampleData.createRegisterDocumentSet();
    }

    @Test
    public void testToEbXML() {
        var ebXML = transformer.toEbXML(request);
        assertEquals(1, ebXML.getExtrinsicObjects().size());
        assertEquals(2, ebXML.getRegistryPackages().size());
        assertEquals(0, ebXML.getSlots().size());

        var associations = ebXML.getAssociations();
        assertEquals(3, associations.size());
        assertEquals(AssociationType.HAS_MEMBER, associations.get(0).getAssociationType());
        assertEquals("submissionSet01", associations.get(0).getSource());
        assertEquals("document01", associations.get(0).getTarget());
        
        assertEquals(AssociationType.HAS_MEMBER, associations.get(1).getAssociationType());
        assertEquals("submissionSet01", associations.get(1).getSource());
        assertEquals("folder01", associations.get(1).getTarget());
        
        assertEquals(AssociationType.HAS_MEMBER, associations.get(2).getAssociationType());
        assertEquals("folder01", associations.get(2).getSource());
        assertEquals("document01", associations.get(2).getTarget());

        var docEntries = ebXML.getExtrinsicObjects(DocumentEntryType.STABLE_OR_ON_DEMAND);
        assertEquals(1, docEntries.size());
        assertEquals("document01", docEntries.get(0).getId());
        assertEquals("Document 01", docEntries.get(0).getName().getValue());

        var folders = ebXML.getRegistryPackages(Vocabulary.FOLDER_CLASS_NODE);
        assertEquals(1, folders.size());
        assertEquals("Folder 01", folders.get(0).getName().getValue());

        var submissionSets = ebXML.getRegistryPackages(Vocabulary.SUBMISSION_SET_CLASS_NODE);
        assertEquals(1, submissionSets.size());
        assertEquals("Submission Set 01", submissionSets.get(0).getName().getValue());
        assertEquals("urn:oid:1.2.3.4.5.6.2333.23", submissionSets.get(0).getHome());
    }
    
    @Test
    public void testToEbXMLEmpty() {
        var ebXML = transformer.toEbXML(new RegisterDocumentSet());
        
        assertEquals(0, ebXML.getAssociations().size());
        assertEquals(0, ebXML.getExtrinsicObjects().size());
        assertEquals(0, ebXML.getRegistryPackages().size());
        assertEquals(0, ebXML.getSlots().size());
    }

    @Test
    public void testFromEbXML() {
        var ebXML = transformer.toEbXML(request);
        var result = transformer.fromEbXML(ebXML);
        assertEquals(request, result);
    }
    
    @Test
    public void testFromEbXMLEmpty() {
        var ebXML = transformer.toEbXML(new RegisterDocumentSet());
        var result = transformer.fromEbXML(ebXML);
        assertEquals(new RegisterDocumentSet(), result);      
    }
}
