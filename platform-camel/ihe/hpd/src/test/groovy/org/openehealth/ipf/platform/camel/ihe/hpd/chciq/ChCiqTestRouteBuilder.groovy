/*
 * Copyright 2018 the original author or authors.
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
package org.openehealth.ipf.platform.camel.ihe.hpd.chciq

import org.apache.camel.builder.RouteBuilder
import org.openehealth.ipf.commons.ihe.hpd.stub.chcidd.DownloadResponse
import org.openehealth.ipf.commons.ihe.hpd.stub.dsmlv2.BatchResponse

import static org.openehealth.ipf.platform.camel.ihe.hpd.HpdCamelValidators.chCiqRequestValidator
import static org.openehealth.ipf.platform.camel.ihe.hpd.HpdCamelValidators.chCiqResponseValidator

/**
 * @author Dmytro Rud
 */
class ChCiqTestRouteBuilder extends RouteBuilder {

    void configure() throws Exception {
        from('ch-ciq:ch-ciq-service1')
            .process(chCiqRequestValidator())
                .transform().constant(new BatchResponse(requestID : '456'))
            .process(chCiqResponseValidator())
    }
}
