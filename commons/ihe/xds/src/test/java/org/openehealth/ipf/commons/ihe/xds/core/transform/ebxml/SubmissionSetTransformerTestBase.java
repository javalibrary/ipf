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
 * Tests for {@link SubmissionSetTransformer}.
 * @author Jens Riemschneider
 */
public abstract class SubmissionSetTransformerTestBase implements FactoryCreator {
    private SubmissionSetTransformer transformer;
    private SubmissionSet set;
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
        transformer = new SubmissionSetTransformer(factory);
        objectLibrary = factory.createObjectLibrary();

        var author1 = new Author();
        author1.setAuthorPerson(createPerson(1));
        author1.getAuthorInstitution().add(new Organization("inst1"));
        author1.getAuthorInstitution().add(new Organization("inst2"));
        author1.getAuthorRole().add(new Identifiable("role1", new AssigningAuthority("2.3.1", "ISO")));
        author1.getAuthorRole().add(new Identifiable("role2"));
        author1.getAuthorSpecialty().add(new Identifiable("spec1", new AssigningAuthority("2.3.3", "ISO")));
        author1.getAuthorSpecialty().add(new Identifiable("spec2"));
        author1.getAuthorTelecom().add(new Telecom(41L, 76L, 73901L, 0L));
        author1.getAuthorTelecom().add(new Telecom(41L, 76L, 73901L, 1L));

        var author2 = new Author();
        author2.setAuthorPerson(createPerson(30));
        author2.getAuthorInstitution().add(new Organization("inst3"));
        author2.getAuthorInstitution().add(new Organization("inst4"));
        author2.getAuthorRole().add(new Identifiable("role3", new AssigningAuthority("2.3.5", "ISO")));
        author2.getAuthorRole().add(new Identifiable("role4"));
        author2.getAuthorSpecialty().add(new Identifiable("spec3", new AssigningAuthority("2.3.7", "ISO")));
        author2.getAuthorSpecialty().add(new Identifiable("spec4"));
        author2.getAuthorTelecom().add(new Telecom(41L, 76L, 73901L, 2L));
        author2.getAuthorTelecom().add(new Telecom(41L, 76L, 73901L, 3L));

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
        sourcePatientInfo.setDateOfBirth("1980");
        sourcePatientInfo.setGender("F");
        sourcePatientInfo.getNames().add(createName(3));
        sourcePatientInfo.getIds().add(createIdentifiable(5));
        sourcePatientInfo.getIds().add(createIdentifiable(6));

        set = new SubmissionSet();
        set.getAuthors().add(author1);
        set.getAuthors().add(author2);
        set.setAvailabilityStatus(AvailabilityStatus.APPROVED);
        set.setComments(createLocal(10));
        set.setSubmissionTime("20150102030405");
        set.setEntryUuid("uuid");
        set.setPatientId(createIdentifiable(3));
        set.setTitle(createLocal(11));
        set.setUniqueId("uniqueId");
        set.setContentTypeCode(createCode(6));
        set.setSourceId("sourceId");
        set.setLimitedMetadata(true);
        set.getIntendedRecipients().add(new Recipient(createOrganization(20), createPerson(22), null));
        set.getIntendedRecipients().add(new Recipient(createOrganization(21), null, null));
        set.getIntendedRecipients().add(new Recipient(null, createPerson(23), null));

        if (homeAware) {
            set.setHomeCommunityId("123.456");
        }
    }

    @Test
    public void testToEbXML() {
        var ebXML = transformer.toEbXML(set, objectLibrary);
        assertNotNull(ebXML);
        
        assertEquals(AvailabilityStatus.APPROVED, ebXML.getStatus());
        assertEquals("uuid", ebXML.getId());
        assertNull(ebXML.getObjectType());
        if (homeAware) {
            assertEquals("123.456", ebXML.getHome());
        }

        assertEquals(createLocal(10), ebXML.getDescription());        
        assertEquals(createLocal(11), ebXML.getName());
        
        assertSlot(SLOT_NAME_SUBMISSION_TIME, ebXML.getSlots(), "20150102030405");
        
        assertSlot(SLOT_NAME_INTENDED_RECIPIENT, ebXML.getSlots(),
                "orgName 20^^^^^&uni 20&uniType 20^^^^id 20|id 22^familyName 22^givenName 22^prefix 22^second 22^suffix 22^degree 22^^&uni 22&uniType 22",
                "orgName 21^^^^^&uni 21&uniType 21^^^^id 21",
                "|id 23^familyName 23^givenName 23^prefix 23^second 23^suffix 23^degree 23^^&uni 23&uniType 23");


        var classification = assertClassification(SUBMISSION_SET_AUTHOR_CLASS_SCHEME, ebXML, 0, "", -1);
        assertSlot(SLOT_NAME_AUTHOR_PERSON, classification.getSlots(), "id 1^familyName 1^givenName 1^prefix 1^second 1^suffix 1^degree 1^^&uni 1&uniType 1");
        assertSlot(SLOT_NAME_AUTHOR_INSTITUTION, classification.getSlots(), "inst1", "inst2");
        assertSlot(SLOT_NAME_AUTHOR_ROLE, classification.getSlots(), "role1^^^&2.3.1&ISO", "role2");
        assertSlot(SLOT_NAME_AUTHOR_SPECIALTY, classification.getSlots(), "spec1^^^&2.3.3&ISO", "spec2");
        assertSlot(SLOT_NAME_AUTHOR_TELECOM, classification.getSlots(), "^PRN^PH^^41^76^73901^0", "^PRN^PH^^41^76^73901^1");

        classification = assertClassification(SUBMISSION_SET_AUTHOR_CLASS_SCHEME, ebXML, 1, "", -1);
        assertSlot(SLOT_NAME_AUTHOR_PERSON, classification.getSlots(), "id 30^familyName 30^givenName 30^prefix 30^second 30^suffix 30^degree 30^^&uni 30&uniType 30");
        assertSlot(SLOT_NAME_AUTHOR_INSTITUTION, classification.getSlots(), "inst3", "inst4");
        assertSlot(SLOT_NAME_AUTHOR_ROLE, classification.getSlots(), "role3^^^&2.3.5&ISO", "role4");
        assertSlot(SLOT_NAME_AUTHOR_SPECIALTY, classification.getSlots(), "spec3^^^&2.3.7&ISO", "spec4");
        assertSlot(SLOT_NAME_AUTHOR_TELECOM, classification.getSlots(), "^PRN^PH^^41^76^73901^2", "^PRN^PH^^41^76^73901^3");

        classification = assertClassification(SUBMISSION_SET_CONTENT_TYPE_CODE_CLASS_SCHEME, ebXML, 0, "code 6", 6);
        assertSlot(SLOT_NAME_CODING_SCHEME, classification.getSlots(), "scheme 6");
        
        assertExternalIdentifier(SUBMISSION_SET_PATIENT_ID_EXTERNAL_ID, ebXML,
                "id 3^^^&uni 3&uniType 3", SUBMISSION_SET_LOCALIZED_STRING_PATIENT_ID);

        assertExternalIdentifier(SUBMISSION_SET_UNIQUE_ID_EXTERNAL_ID, ebXML,
                "uniqueId", SUBMISSION_SET_LOCALIZED_STRING_UNIQUE_ID);

        assertExternalIdentifier(SUBMISSION_SET_SOURCE_ID_EXTERNAL_ID, ebXML,
                "sourceId", SUBMISSION_SET_LOCALIZED_STRING_SOURCE_ID);

        assertClassification(SUBMISSION_SET_LIMITED_METADATA_CLASS_NODE, ebXML, 0, null, 0);

        assertEquals(4, ebXML.getClassifications().size());
        assertEquals(2, ebXML.getSlots().size());
        assertEquals(3, ebXML.getExternalIdentifiers().size());
    }

    @Test
    public void testToEbXMLNull() {
        assertNull(transformer.toEbXML(null, objectLibrary));
    }
   
    @Test
    public void testToEbXMLEmpty() {
        var ebXML = transformer.toEbXML(new SubmissionSet(), objectLibrary);
        assertNotNull(ebXML);
        
        assertNull(ebXML.getStatus());
        assertNull(ebXML.getId());
        
        assertNull(ebXML.getDescription());        
        assertNull(ebXML.getName());
        
        assertEquals(0, ebXML.getSlots().size());
        assertEquals(0, ebXML.getClassifications().size());
        assertEquals(0, ebXML.getExternalIdentifiers().size());
    }
    
    
    
    @Test
    public void testFromEbXML() {
        var ebXML = transformer.toEbXML(set, objectLibrary);
        var result = transformer.fromEbXML(ebXML);
        
        assertNotNull(result);
        assertEquals(set, result);
    }
    
    @Test
    public void testFromEbXMLNull() {
        assertNull(transformer.fromEbXML(null));
    }
    
    @Test
    public void testFromEbXMLEmpty() {
        var ebXML = transformer.toEbXML(new SubmissionSet(), objectLibrary);
        var result = transformer.fromEbXML(ebXML);
        assertEquals(new SubmissionSet(), result);
    }
}
