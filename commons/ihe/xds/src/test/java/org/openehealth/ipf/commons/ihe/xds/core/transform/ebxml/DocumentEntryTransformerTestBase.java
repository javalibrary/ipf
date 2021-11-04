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
package org.openehealth.ipf.commons.ihe.xds.core.transform.ebxml;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openehealth.ipf.commons.ihe.xds.core.ebxml.EbXMLObjectLibrary;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.openehealth.ipf.commons.ihe.xds.core.metadata.Vocabulary.*;
import static org.openehealth.ipf.commons.ihe.xds.core.transform.ebxml.EbrsTestUtils.*;

/**
 * Tests for {@link DocumentEntryTransformer}.
 * @author Jens Riemschneider
 */
public abstract class DocumentEntryTransformerTestBase implements FactoryCreator {
    private DocumentEntryTransformer transformer;
    private DocumentEntry documentEntry;
    private EbXMLObjectLibrary objectLibrary;
    private boolean homeAware = true;
    
    /**
     * @param homeAware
     *          <code>true</code> to enable comparison of the homeCommunityId.
     */
    protected void setHomeAware(boolean homeAware) {
        this.homeAware = homeAware;
    }
    
    @BeforeEach
    public final void baseSetUp() {
        var factory = createFactory();
        transformer = new DocumentEntryTransformer(factory);
        objectLibrary = factory.createObjectLibrary();

        var author1 = new Author();
        author1.setAuthorPerson(createPerson(1));
        author1.getAuthorInstitution().add(new Organization("inst1"));
        author1.getAuthorInstitution().add(new Organization("inst2"));
        author1.getAuthorRole().add(new Identifiable("role1", new AssigningAuthority("2.3.1", "ISO")));
        author1.getAuthorRole().add(new Identifiable("role2"));
        author1.getAuthorSpecialty().add(new Identifiable("spec1", new AssigningAuthority("2.3.3", "ISO")));
        author1.getAuthorSpecialty().add(new Identifiable("spec2"));
        author1.getAuthorTelecom().add(new Telecom(null, null, 7771L, null));
        author1.getAuthorTelecom().add(new Telecom(null, null, 7772L, null));

        var author2 = new Author();
        author2.setAuthorPerson(createPerson(30));
        author2.getAuthorInstitution().add(new Organization("inst3"));
        author2.getAuthorInstitution().add(new Organization("inst4"));
        author2.getAuthorRole().add(new Identifiable("role3"));
        author2.getAuthorRole().add(new Identifiable("role4", new AssigningAuthority("2.3.6", "ISO")));
        author2.getAuthorSpecialty().add(new Identifiable("spec3"));
        author2.getAuthorSpecialty().add(new Identifiable("spec4", new AssigningAuthority("2.3.8", "ISO")));
        author2.getAuthorTelecom().add(new Telecom(null, null, 7773L, null));
        author2.getAuthorTelecom().add(new Telecom(null, null, 7774L, null));

        var address = new Address();
        address.setCity("city");
        address.setCountry("country");
        address.setCountyParishCode("countyParishCode");
        address.setOtherDesignation("otherDesignation");
        address.setStateOrProvince("stateOrProvince");
        address.setStreetAddress("streetAddress");
        address.setZipOrPostalCode("zipOrPostalCode");

        var sourcePatientInfo = new PatientInfo();
        sourcePatientInfo.getAddresses().add(address);
        sourcePatientInfo.setDateOfBirth("19800102");
        sourcePatientInfo.setGender("F");
        sourcePatientInfo.getNames().add(createName(3));
        sourcePatientInfo.getIds().add(createIdentifiable(5));
        sourcePatientInfo.getIds().add(createIdentifiable(6));

        documentEntry = new DocumentEntry();
        documentEntry.getAuthors().add(author1);
        documentEntry.getAuthors().add(author2);
        documentEntry.setAvailabilityStatus(AvailabilityStatus.APPROVED);
        documentEntry.setClassCode(createCode(1));
        documentEntry.setComments(createLocal(10));
        documentEntry.setCreationTime("20150206");
        documentEntry.setEntryUuid("uuid");
        documentEntry.setFormatCode(createCode(2));
        documentEntry.setHash("hash");
        documentEntry.setHealthcareFacilityTypeCode(createCode(3));
        documentEntry.setLanguageCode("languageCode");
        documentEntry.setLegalAuthenticator(createPerson(2));
        documentEntry.setMimeType("text/plain");
        documentEntry.setPatientId(createIdentifiable(3));
        documentEntry.setPracticeSettingCode(createCode(4));
        documentEntry.setServiceStartTime("20150207");
        documentEntry.setServiceStopTime("20150208");
        documentEntry.setSize(174L);
        documentEntry.setSourcePatientId(createIdentifiable(4));
        documentEntry.setSourcePatientInfo(sourcePatientInfo);
        documentEntry.setTitle(createLocal(11));
        documentEntry.setTypeCode(createCode(5));
        documentEntry.setUniqueId("uniqueId");
        documentEntry.setUri("uri");
        documentEntry.getConfidentialityCodes().add(createCode(6));
        documentEntry.getConfidentialityCodes().add(createCode(7));
        documentEntry.getEventCodeList().add(createCode(8));
        documentEntry.getEventCodeList().add(createCode(9));
        documentEntry.setRepositoryUniqueId("repo1");
        documentEntry.setDocumentAvailability(DocumentAvailability.ONLINE);
        documentEntry.setLimitedMetadata(true);

        documentEntry.getReferenceIdList().add(new ReferenceId(
                "ref-id-11", new CXiAssigningAuthority("ABCD", "1.1.2.3", "ISO"),
                ReferenceId.ID_TYPE_CODE_ORDER));
        documentEntry.getReferenceIdList().add(new ReferenceId(
                "ref-id-12", new CXiAssigningAuthority("DEFG", "2.1.2.3", "ISO"),
                ReferenceId.ID_TYPE_CODE_ACCESSION));

        if (homeAware) {
            documentEntry.setHomeCommunityId("123.456");
        }
    }

    @Test
    public void testToEbXML() {
        var ebXML = transformer.toEbXML(documentEntry, objectLibrary);
        assertNotNull(ebXML);
        
        assertEquals(AvailabilityStatus.APPROVED, ebXML.getStatus());
        assertEquals("text/plain", ebXML.getMimeType());
        assertEquals("uuid", ebXML.getId());
        assertEquals(DocumentEntryType.STABLE.getUuid(), ebXML.getObjectType());
        if (homeAware) {
            assertEquals("123.456", ebXML.getHome());
        }
        
        assertEquals(createLocal(10), ebXML.getDescription());        
        assertEquals(createLocal(11), ebXML.getName());

        var slots = ebXML.getSlots();
        assertSlot(SLOT_NAME_CREATION_TIME, slots, "20150206");
        assertSlot(SLOT_NAME_HASH, slots, "hash");
        assertSlot(SLOT_NAME_LANGUAGE_CODE, slots, "languageCode");
        assertSlot(SLOT_NAME_SERVICE_START_TIME, slots, "20150207");
        assertSlot(SLOT_NAME_SERVICE_STOP_TIME, slots, "20150208");
        assertSlot(SLOT_NAME_SIZE, slots, "174");
        assertSlot(SLOT_NAME_SOURCE_PATIENT_ID, slots, "id 4^^^&uni 4&uniType 4");
        assertSlot(SLOT_NAME_URI, slots, "uri");
        assertSlot(SLOT_NAME_LEGAL_AUTHENTICATOR, slots, "id 2^familyName 2^givenName 2^prefix 2^second 2^suffix 2^degree 2^^&uni 2&uniType 2");
        assertSlot(SLOT_NAME_REPOSITORY_UNIQUE_ID, slots, "repo1");
        assertSlot(SLOT_NAME_SOURCE_PATIENT_INFO, slots,
                "PID-3|id 6^^^&uni 6&uniType 6~id 5^^^&uni 5&uniType 5",
                "PID-5|familyName 3^givenName 3^prefix 3^second 3^suffix 3^degree 3",
                "PID-7|19800102",
                "PID-8|F",
                "PID-11|streetAddress^otherDesignation^city^stateOrProvince^zipOrPostalCode^country^^^countyParishCode");

        assertSlot(SLOT_NAME_REFERENCE_ID_LIST, slots,
                "ref-id-11^^^ABCD&1.1.2.3&ISO^urn:ihe:iti:xds:2013:order",
                "ref-id-12^^^DEFG&2.1.2.3&ISO^urn:ihe:iti:xds:2013:accession");
        assertSlot(SLOT_NAME_DOCUMENT_AVAILABILITY, slots, "urn:ihe:iti:2010:DocumentAvailability:Online");

        var classification = assertClassification(DOC_ENTRY_AUTHOR_CLASS_SCHEME, ebXML, 0, "", -1);
        assertSlot(SLOT_NAME_AUTHOR_PERSON, classification.getSlots(), "id 1^familyName 1^givenName 1^prefix 1^second 1^suffix 1^degree 1^^&uni 1&uniType 1");
        assertSlot(SLOT_NAME_AUTHOR_INSTITUTION, classification.getSlots(), "inst1", "inst2");
        assertSlot(SLOT_NAME_AUTHOR_ROLE, classification.getSlots(), "role1^^^&2.3.1&ISO", "role2");
        assertSlot(SLOT_NAME_AUTHOR_SPECIALTY, classification.getSlots(), "spec1^^^&2.3.3&ISO", "spec2");
        assertSlot(SLOT_NAME_AUTHOR_TELECOM, classification.getSlots(), "^PRN^PH^^^^7771", "^PRN^PH^^^^7772");

        classification = assertClassification(DOC_ENTRY_AUTHOR_CLASS_SCHEME, ebXML, 1, "", -1);
        assertSlot(SLOT_NAME_AUTHOR_PERSON, classification.getSlots(), "id 30^familyName 30^givenName 30^prefix 30^second 30^suffix 30^degree 30^^&uni 30&uniType 30");
        assertSlot(SLOT_NAME_AUTHOR_INSTITUTION, classification.getSlots(), "inst3", "inst4");
        assertSlot(SLOT_NAME_AUTHOR_ROLE, classification.getSlots(), "role3", "role4^^^&2.3.6&ISO");
        assertSlot(SLOT_NAME_AUTHOR_SPECIALTY, classification.getSlots(), "spec3", "spec4^^^&2.3.8&ISO");
        assertSlot(SLOT_NAME_AUTHOR_TELECOM, classification.getSlots(), "^PRN^PH^^^^7773", "^PRN^PH^^^^7774");

        classification = assertClassification(DOC_ENTRY_CLASS_CODE_CLASS_SCHEME, ebXML, 0, "code 1", 1);
        assertSlot(SLOT_NAME_CODING_SCHEME, classification.getSlots(), "scheme 1");
        
        classification = assertClassification(DOC_ENTRY_CONFIDENTIALITY_CODE_CLASS_SCHEME, ebXML, 0, "code 6", 6);
        assertSlot(SLOT_NAME_CODING_SCHEME, classification.getSlots(), "scheme 6");

        classification = assertClassification(DOC_ENTRY_CONFIDENTIALITY_CODE_CLASS_SCHEME, ebXML, 1, "code 7", 7);
        assertSlot(SLOT_NAME_CODING_SCHEME, classification.getSlots(), "scheme 7");

        classification = assertClassification(DOC_ENTRY_EVENT_CODE_CLASS_SCHEME, ebXML, 0, "code 8", 8);
        assertSlot(SLOT_NAME_CODING_SCHEME, classification.getSlots(), "scheme 8");

        classification = assertClassification(DOC_ENTRY_EVENT_CODE_CLASS_SCHEME, ebXML, 1, "code 9", 9);
        assertSlot(SLOT_NAME_CODING_SCHEME, classification.getSlots(), "scheme 9");
        
        classification = assertClassification(DOC_ENTRY_FORMAT_CODE_CLASS_SCHEME, ebXML, 0, "code 2", 2);
        assertSlot(SLOT_NAME_CODING_SCHEME, classification.getSlots(), "scheme 2");

        classification = assertClassification(DOC_ENTRY_HEALTHCARE_FACILITY_TYPE_CODE_CLASS_SCHEME, ebXML, 0, "code 3", 3);
        assertSlot(SLOT_NAME_CODING_SCHEME, classification.getSlots(), "scheme 3");
        
        classification = assertClassification(DOC_ENTRY_PRACTICE_SETTING_CODE_CLASS_SCHEME, ebXML, 0, "code 4", 4);
        assertSlot(SLOT_NAME_CODING_SCHEME, classification.getSlots(), "scheme 4");

        classification = assertClassification(DOC_ENTRY_TYPE_CODE_CLASS_SCHEME, ebXML, 0, "code 5", 5);
        assertSlot(SLOT_NAME_CODING_SCHEME, classification.getSlots(), "scheme 5");
        
        assertExternalIdentifier(DOC_ENTRY_PATIENT_ID_EXTERNAL_ID, ebXML, 
                "id 3^^^&uni 3&uniType 3", DOC_ENTRY_LOCALIZED_STRING_PATIENT_ID);

        assertExternalIdentifier(DOC_ENTRY_UNIQUE_ID_EXTERNAL_ID, ebXML, 
                "uniqueId", DOC_ENTRY_LOCALIZED_STRING_UNIQUE_ID);

        assertClassification(DOC_ENTRY_LIMITED_METADATA_CLASS_NODE, ebXML, 0, null, 0);

        assertEquals(12, ebXML.getClassifications().size());
        assertEquals(13, ebXML.getSlots().size());
        assertEquals(2, ebXML.getExternalIdentifiers().size());
    }

    @Test
    public void testToEbXMLNull() {
        assertNull(transformer.toEbXML(null, objectLibrary));
    }
   
    @Test
    public void testToEbXMLEmpty() {
        var ebXML = transformer.toEbXML(new DocumentEntry(), objectLibrary);
        assertNotNull(ebXML);
        
        assertNull(ebXML.getStatus());
        assertEquals("application/octet-stream", ebXML.getMimeType());
        assertNull(ebXML.getId());
        
        assertNull(ebXML.getDescription());        
        assertNull(ebXML.getName());
        
        assertEquals(0, ebXML.getSlots().size());
        assertEquals(0, ebXML.getClassifications().size());
        assertEquals(0, ebXML.getExternalIdentifiers().size());
    }

    @Test
    public void testFromEbXML() {
        var ebXML = transformer.toEbXML(documentEntry, objectLibrary);
        var result = transformer.fromEbXML(ebXML);

        assertNotNull(result);
        assertEquals(documentEntry, result);
    }
    
    @Test
    public void testFromEbXMLNull() {
        assertNull(transformer.fromEbXML(null));
    }
    
    @Test
    public void testFromEbXMLEmpty() {
        var ebXML = transformer.toEbXML(new DocumentEntry(), objectLibrary);
        var result = transformer.fromEbXML(ebXML);

        var expected = new DocumentEntry();
        expected.setMimeType("application/octet-stream");
        assertEquals(expected, result);
    }
}
