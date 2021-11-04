/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.platform.camel.ihe.mllp.iti30;

import org.apache.camel.CamelContext;
import org.openehealth.ipf.commons.ihe.core.TransactionOptionsUtils;
import org.openehealth.ipf.commons.ihe.hl7v2.Hl7v2TransactionOptions;
import org.openehealth.ipf.commons.ihe.hl7v2.Hl7v2TransactionOptionsProvider;
import org.openehealth.ipf.commons.ihe.hl7v2.audit.FeedAuditDataset;
import org.openehealth.ipf.commons.ihe.hl7v2.options.Iti30OptionsProvider;
import org.openehealth.ipf.commons.ihe.hl7v2.options.Iti30Options;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.MllpTransactionComponent;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.MllpTransactionEndpointConfiguration;

import java.util.List;
import java.util.Map;

import static org.openehealth.ipf.commons.ihe.hl7v2.PAM.Interactions.ITI_30;

/**
 * Camel component for ITI-30 (Patient Identity Management).
 * The endpoints take an additional parameter "options", that refer to the
 * transaction options as specified in {@link Iti30Options} that need to be supported.
 * You can also provide different options specified by an additional parameter "optionsProvider".
 *
 * @author Christian Ohr
 */
public class Iti30Component extends MllpTransactionComponent<FeedAuditDataset> {


    public Iti30Component() {
        super(ITI_30);
    }

    public Iti30Component(CamelContext camelContext) {
        super(camelContext, ITI_30);
    }

    @Override
    protected MllpTransactionEndpointConfiguration createConfig(String uri, Map<String, Object> parameters) throws Exception {
        var config = super.createConfig(uri, parameters);
        Hl7v2TransactionOptionsProvider<FeedAuditDataset, ? extends Hl7v2TransactionOptions> optionsProvider =
                getAndRemoveOrResolveReferenceParameter(parameters, "optionsProvider", Hl7v2TransactionOptionsProvider.class, new Iti30OptionsProvider());
        var options = getAndRemoveParameter(parameters, "options", String.class, optionsProvider.getDefaultOption().name());
        List<? extends Hl7v2TransactionOptions> iti30Options = TransactionOptionsUtils.split(options, Iti30Options.class);
        if (iti30Options.isEmpty()) {
            throw new IllegalArgumentException("Options parameter for pam-iti30 is invalid");
        }
        getInteractionId().init(optionsProvider, iti30Options);
        return config;
    }

}
