/*
 * Copyright 2019 the original author or authors.
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

package org.openehealth.ipf.commons.ihe.fhir.support;

import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.openehealth.ipf.commons.ihe.fhir.audit.IBaseOperationOutcomeOperations;

import java.util.Comparator;

/**
 * @author Christian Ohr
 * @since 3.6
 */
public class OperationOutcomeOperations implements IBaseOperationOutcomeOperations {

    public static final OperationOutcomeOperations INSTANCE = new OperationOutcomeOperations();

    @Override
    public boolean hasIssue(IBaseOperationOutcome operationOutcome) {
        return ((OperationOutcome)operationOutcome).hasIssue();
    }

    @Override
    public String getDiagnostics(IBaseOperationOutcome operationOutcome) {
        if (!hasIssue(operationOutcome)) {
            return null;
        }
        return ((OperationOutcome)operationOutcome).getIssue().get(0).getDiagnostics();
    }

    @Override
    public String getWorstIssueSeverity(IBaseOperationOutcome operationOutcome) {
        return ((OperationOutcome)operationOutcome).getIssue().stream()
                .map(OperationOutcome.OperationOutcomeIssueComponent::getSeverity)
                .min(Comparator.naturalOrder())
                .orElse(OperationOutcome.IssueSeverity.NULL)
                .toCode();
    }
}
