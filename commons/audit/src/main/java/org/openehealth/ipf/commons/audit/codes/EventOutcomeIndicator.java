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

package org.openehealth.ipf.commons.audit.codes;


import lombok.Getter;
import org.openehealth.ipf.commons.audit.types.EnumeratedValueSet;

/**
 * Event Outcome Indicator codes as originally specified by https://tools.ietf.org/html/rfc3881#section-5.1
 * and now maintained in http://dicom.nema.org/medical/dicom/current/output/html/part15.html#sect_A.5.1.2
 * This value set is a literal part of the audit schema, ie.e. no other codes may be used.
 *
 * @author Christian Ohr
 * @since 3.5
 */
public enum EventOutcomeIndicator implements EnumeratedValueSet<Integer> {

    Success(0),
    MinorFailure(4),
    SeriousFailure(8),
    MajorFailure(12);

    @Getter
    private final Integer value;

    EventOutcomeIndicator(int value) {
        this.value = value;
    }

    public static EventOutcomeIndicator enumForCode(Integer code) {
        return EnumeratedValueSet.enumForCode(EventOutcomeIndicator.class, code);
    }
}
