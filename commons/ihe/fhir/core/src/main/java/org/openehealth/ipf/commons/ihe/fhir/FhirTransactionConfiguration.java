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
package org.openehealth.ipf.commons.ihe.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import org.openehealth.ipf.commons.ihe.core.TransactionConfiguration;
import org.openehealth.ipf.commons.ihe.core.atna.AuditStrategy;
import org.openehealth.ipf.commons.ihe.fhir.audit.FhirAuditDataset;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Static configuration for FHIR transaction components
 *
 * @author Christian Ohr
 * @since 3.2
 */
public class FhirTransactionConfiguration<T extends FhirAuditDataset> extends TransactionConfiguration<T> {

    // Avoid recreating the same FhirContext instances
    private static final ConcurrentHashMap<FhirVersionEnum, FhirContext> fhirContexts = new ConcurrentHashMap<>();

    private final FhirVersionEnum fhirVersion;
    private final Supplier<FhirContext> fhirContextProvider;
    private final List<? extends FhirProvider> staticResourceProviders;
    private final ClientRequestFactory<?> staticClientRequestFactory;
    private final FhirTransactionValidator fhirValidator;
    private boolean supportsLazyLoading;
    private Predicate<RequestDetails> staticConsumerSelector = o -> true;

    public FhirTransactionConfiguration(
            String name,
            String description,
            boolean isQuery,
            AuditStrategy<T> clientAuditStrategy,
            AuditStrategy<T> serverAuditStrategy,
            FhirContext defaultFhirContext,
            FhirProvider resourceProvider,
            ClientRequestFactory<?> clientRequestFactory,
            Function<FhirContext, FhirTransactionValidator> fhirValidator) {
        this(name, description, isQuery, clientAuditStrategy, serverAuditStrategy, defaultFhirContext,
                Collections.singletonList(resourceProvider), clientRequestFactory, fhirValidator);
    }

    public FhirTransactionConfiguration(
            String name,
            String description,
            boolean isQuery,
            AuditStrategy<T> clientAuditStrategy,
            AuditStrategy<T> serverAuditStrategy,
            FhirContext fhirContext,
            List<? extends FhirProvider> resourceProviders,
            ClientRequestFactory<?> clientRequestFactory,
            Function<FhirContext, FhirTransactionValidator> fhirValidator) {
        super(name, description, isQuery, clientAuditStrategy, serverAuditStrategy);
        this.fhirVersion = fhirContext.getVersion().getVersion();
        this.fhirContextProvider = () -> fhirContext;
        this.staticResourceProviders = resourceProviders;
        this.staticClientRequestFactory = clientRequestFactory;
        this.fhirValidator = fhirValidator != null ? fhirValidator.apply(fhirContext) : null;
    }

    public FhirTransactionConfiguration(
            String name,
            String description,
            boolean isQuery,
            AuditStrategy<T> clientAuditStrategy,
            AuditStrategy<T> serverAuditStrategy,
            FhirVersionEnum fhirVersion,
            FhirProvider resourceProvider,
            ClientRequestFactory<?> clientRequestFactory,
            Function<FhirContext, FhirTransactionValidator> fhirValidator) {
        this(name, description, isQuery, clientAuditStrategy, serverAuditStrategy, fhirVersion,
                Collections.singletonList(resourceProvider), clientRequestFactory, fhirValidator);
    }

    public FhirTransactionConfiguration(
            String name,
            String description,
            boolean isQuery,
            AuditStrategy<T> clientAuditStrategy,
            AuditStrategy<T> serverAuditStrategy,
            FhirVersionEnum fhirVersion,
            List<? extends FhirProvider> resourceProviders,
            ClientRequestFactory<?> clientRequestFactory,
            Function<FhirContext, FhirTransactionValidator> fhirValidator) {
        super(name, description, isQuery, clientAuditStrategy, serverAuditStrategy);
        this.fhirVersion = fhirVersion;
        this.fhirContextProvider = () -> initializeFhirContext(fhirVersion);
        this.staticResourceProviders = resourceProviders;
        this.staticClientRequestFactory = clientRequestFactory;
        this.fhirValidator = fhirValidator != null ? fhirValidator.apply(fhirContextProvider.get()) : null;
    }


    public List<? extends FhirProvider> getStaticResourceProvider() {
        return staticResourceProviders;
    }

    public ClientRequestFactory<?> getStaticClientRequestFactory() {
        return staticClientRequestFactory;
    }

    public void setStaticConsumerSelector(Predicate<RequestDetails> staticConsumerSelector) {
        this.staticConsumerSelector = staticConsumerSelector;
    }

    public Predicate<RequestDetails> getStaticConsumerSelector() {
        return staticConsumerSelector;
    }

    /**
     * Initializes the FHIR context by setting a SSL-aware REST client factory. Note that this method
     * is only called when the endpoint does not configure its custom (pre-initialized) FhirContext
     *
     * @return the initialized FhirContext
     */
    public FhirContext initializeFhirContext() {
        return fhirContextProvider.get();
    }

    private static FhirContext initializeFhirContext(FhirVersionEnum fhirVersion) {
        return fhirContexts.computeIfAbsent(fhirVersion, fhirVersionEnum -> {
            var fhirContext = new FhirContext(fhirVersionEnum);
            fhirContext.setRestfulClientFactory(new SslAwareApacheRestfulClientFactory(fhirContext));
            return fhirContext;
        });

    }

    public FhirVersionEnum getFhirVersion() {
        return fhirVersion;
    }

    public FhirTransactionValidator getFhirValidator() {
        return fhirValidator;
    }

    /**
     * Determines if the component and backend implementation does support lazy-loading of search result sets.
     * Even if true, the endpoint URI, however, must be explicitly configured to use lazy-loading.
     *
     * @param supportsLazyLoading true if this component support lazy-loading
     */
    public void setSupportsLazyLoading(boolean supportsLazyLoading) {
        this.supportsLazyLoading = supportsLazyLoading;
    }

    public boolean supportsLazyLoading() {
        return supportsLazyLoading;
    }



}
