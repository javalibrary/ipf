/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openehealth.ipf.platform.camel.ihe.fhir.iti65;

import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.ExpressionAdapter;
import org.hl7.fhir.r4.model.*;
import org.openehealth.ipf.platform.camel.core.adapter.ValidatorAdapter;
import org.openehealth.ipf.platform.camel.ihe.fhir.test.FhirTestContainer;

import java.util.Date;
import java.util.UUID;

import static org.openehealth.ipf.platform.camel.ihe.fhir.core.FhirCamelValidators.*;

/**
 *
 */
public class Iti65TestRouteBuilder extends RouteBuilder {

    private final boolean returnError;
    private final boolean startSecondRoute;

    public Iti65TestRouteBuilder(boolean returnError, boolean startSecondRoute) {
        this.returnError = returnError;
        this.startSecondRoute = startSecondRoute;
    }

    @Override
    public void configure() {

        from("direct:input")
                .toF("mhd-iti65:localhost:%d", FhirTestContainer.DEMO_APP_PORT);

        from("mhd-iti65:stub?audit=true&fhirContext=#fhirContext")
                .errorHandler(noErrorHandler())
                .setHeader(ValidatorAdapter.NEED_VALIDATION_HEADER_NAME, constant(true))
                .setHeader(VALIDATION_MODE, constant(SCHEMA | SCHEMATRON )) // | MODEL))
                .process(itiRequestValidator())
                .transform(new Responder());

        // Start up a second transaction route, sharing the same resource provider
        // and the same FHIR context
        if (startSecondRoute) {
            from("batch:batch?audit=false&fhirContext=#fhirContext")
                    .errorHandler(noErrorHandler())
                    .throwException(new UnprocessableEntityException("This sucks"));
        }
    }


    private class Responder extends ExpressionAdapter {

        @Override
        public Object evaluate(Exchange exchange) {

            if (returnError) throw new InternalErrorException("Something went wrong");

            var requestBundle = exchange.getIn().getBody(Bundle.class);

            var responseBundle = new Bundle()
                    .setType(Bundle.BundleType.TRANSACTIONRESPONSE)
                    .setTotal(requestBundle.getTotal());

            for (var requestEntry : requestBundle.getEntry()) {
                var response = new Bundle.BundleEntryResponseComponent()
                        .setStatus("201 Created")
                        .setLastModified(new Date())
                        .setLocation(requestEntry.getResource().getClass().getSimpleName() + "/" + 4711);
                responseBundle.addEntry()
                        .setResponse(response)
                        .setResource(responseResource(requestEntry.getResource()));
            }
            return responseBundle;
        }

    }

    private Resource responseResource(Resource request) {
        if (request instanceof DocumentManifest) {
            return new DocumentManifest().setId(UUID.randomUUID().toString());
        } else if (request instanceof DocumentReference) {
            return new DocumentReference().setId(UUID.randomUUID().toString());
        } else if (request instanceof ListResource) {
            return new ListResource().setId(UUID.randomUUID().toString());
        } else if (request instanceof Binary) {
            return new Binary().setId(UUID.randomUUID().toString());
        } else {
            throw new IllegalArgumentException(request + " is not allowed here");
        }
    }


}
