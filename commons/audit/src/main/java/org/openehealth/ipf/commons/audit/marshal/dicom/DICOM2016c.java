/*
 * Copyright 2017 the original author or authors.
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
package org.openehealth.ipf.commons.audit.marshal.dicom;

import org.jdom2.Element;
import org.openehealth.ipf.commons.audit.types.AuditSource;

import static org.openehealth.ipf.commons.audit.XMLNames.*;

/**
 * CP 1362: Correct AuditSourceIdentification in DICOM audit message
 * http://dicom.nema.org/Dicom/News/January2016/docs/cpack86/cp1362.pdf
 *
 * Correct AuditSourceIdentification in DICOM audit message
 *
 * @author Christian Ohr
 * @since 3.5
 */
public class DICOM2016c extends DICOM2016a {

    @Override
    protected Element auditSourceType(AuditSource auditSourceType) {
        var element = new Element(AUDIT_SOURCE_TYPE_CODE);
        element.setAttribute(CODE, auditSourceType.getCode());
        conditionallyAddAttribute(element, CODE_SYSTEM_NAME, auditSourceType.getCodeSystemName());
        conditionallyAddAttribute(element, DISPLAY_NAME, auditSourceType.getDisplayName());
        conditionallyAddAttribute(element, ORIGINAL_TEXT, auditSourceType.getOriginalText());
        return element;
    }
}