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

package org.openehealth.ipf.commons.ihe.fhir.pixpdq

import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException
import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException
import org.hl7.fhir.dstu3.model.OperationOutcome
import org.openehealth.ipf.commons.ihe.fhir.translation.UriMapper
import org.openehealth.ipf.modules.hl7.dsl.Repeatable

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 *
 * Some common utilities used by FHIR translators
 *
 * @author Christian Ohr
 * @since 3.1
 */
class Utils {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

    /**
     * Returns the next repetition of the given HL7 v2 field/segment/etc.
     */
    static def nextRepetition(Repeatable closure) {
        return closure(closure().size())
    }

    /**
     * Returns current timestamp in the format prescribed by HL7.
     */
    static String hl7Timestamp() {
        return ZonedDateTime.now().format(TIME_FORMAT)
    }

    static boolean populateIdentifier(def cx, UriMapper uriMapper, String uri, String identifier = null) {
        cx[1] = identifier ?: ''
        uriMapper.uriToNamespace(uri).ifPresent { cx[4][1] = it }
        uriMapper.uriToOid(uri).ifPresent {
            cx[4][2] = it
            cx[4][3] = 'ISO'
        }
        return cx[4][1]?.value || cx[4][2]?.value
    }

    static boolean populateIdentifier(def cx, String oid, String identifier = null) {
        cx[1] = identifier ?: ''
        cx[4][2] = oid
        cx[4][3] = 'ISO'
        true
    }

    // PIXm, Error Case 3
    static BaseServerResponseException unknownPatientId() {
        OperationOutcome oo = new OperationOutcome()
        oo.addIssue()
                .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                .setCode(OperationOutcome.IssueType.NOTFOUND)
                .setDiagnostics('sourceIdentifier Patient Identifier not found')
        return new ResourceNotFoundException('Unknown Patient ID', oo)
    }

    // PIXm, Error Case 4
    static BaseServerResponseException unknownSourceDomainCode(String domain = null) {
        OperationOutcome oo = new OperationOutcome()
        oo.addIssue()
                .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                .setCode(OperationOutcome.IssueType.CODEINVALID)
                .setDiagnostics("sourceIdentifier Assigning Authority not found (${domain ?: ''})")
        return new InvalidRequestException("Unknown Assigning Authority Domain ${domain ?: ''}", oo)
    }

    // PIXm, Error Case 5
    static BaseServerResponseException unknownTargetDomainCode(String domain = null) {
        OperationOutcome oo = new OperationOutcome()
        oo.addIssue()
                .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                .setCode(OperationOutcome.IssueType.CODEINVALID)
                .setDiagnostics("targetSystem not found (${domain ?: ''})")
        return new ForbiddenOperationException("Unknown Target Domain ${domain ?: ''}", oo)
    }

    // PDQm, Error Case ?
    static BaseServerResponseException unknownPatientDomain(String domain = null) {
        OperationOutcome oo = new OperationOutcome()
        oo.addIssue()
                .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                .setCode(OperationOutcome.IssueType.NOTFOUND)
                .setDiagnostics("sourceIdentifier Assigning Authority ${domain ?: ''} not found")
        return new InvalidRequestException("Unknown Patient Domain ${domain ?: ''}", oo)
    }

    // PDQm, Error Case 4
    static BaseServerResponseException unknownTargetDomainValue(String domain = null) {
        OperationOutcome oo = new OperationOutcome()
        oo.addIssue()
                .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                .setCode(OperationOutcome.IssueType.VALUE)
                .setDiagnostics("targetSystem not found (${domain ?: ''})")
        return new ResourceNotFoundException("Unknown Target Domain ${domain ?: ''}", oo)
    }

    static BaseServerResponseException unexpectedProblem() {
        OperationOutcome oo = new OperationOutcome()
        oo.addIssue()
                .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                .setCode(OperationOutcome.IssueType.EXCEPTION)
        return new InternalErrorException('Unexpected response from server', oo)
    }

}