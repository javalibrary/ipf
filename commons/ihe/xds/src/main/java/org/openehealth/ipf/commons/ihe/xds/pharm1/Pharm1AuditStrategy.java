/*
 * Copyright 2020 the original author or authors.
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
package org.openehealth.ipf.commons.ihe.xds.pharm1;

import org.openehealth.ipf.commons.audit.AuditContext;
import org.openehealth.ipf.commons.audit.model.AuditMessage;
import org.openehealth.ipf.commons.ihe.xds.core.audit.XdsQueryAuditDataset;
import org.openehealth.ipf.commons.ihe.xds.core.audit.XdsQueryAuditStrategy30;
import org.openehealth.ipf.commons.ihe.xds.core.audit.codes.XdsEventTypeCode;
import org.openehealth.ipf.commons.ihe.xds.core.audit.codes.XdsParticipantObjectIdTypeCode;
import org.openehealth.ipf.commons.ihe.xds.core.audit.event.XdsQueryInformationBuilder;

/**
 * Audit strategy for PHARM-1.
 *
 * @author Quentin Ligier
 * @since 3.7
 */
public class Pharm1AuditStrategy extends XdsQueryAuditStrategy30 {

    public Pharm1AuditStrategy(boolean serverSide) {
        super(serverSide);
    }

    @Override
    public AuditMessage[] makeAuditMessage(AuditContext auditContext, XdsQueryAuditDataset auditDataset) {
        return new XdsQueryInformationBuilder(auditContext, auditDataset, XdsEventTypeCode.QueryPharmacyDocuments, auditDataset.getPurposesOfUse())
                .addPatients(auditDataset.getPatientId())
                .setQueryParameters(auditDataset, XdsParticipantObjectIdTypeCode.QueryPharmacyDocuments)
                .getMessages();
    }
}
