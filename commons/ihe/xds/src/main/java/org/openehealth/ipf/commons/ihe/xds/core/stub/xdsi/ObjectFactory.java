/*
 * Copyright 2021 the original author or authors.
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
package org.openehealth.ipf.commons.ihe.xds.core.stub.xdsi;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import org.openehealth.ipf.commons.ihe.xds.core.stub.xdsi.RetrieveImagingDocumentSetRequestType.*;


/**
 * This object contains factory methods for each Java content interface and Java element interface
 * generated in the org.openehealth.ipf.commons.ihe.xds.core.stub.xdsi package.
 * <p>An ObjectFactory allows you to programmatically construct new instances of the Java representation
 * for XML content. The Java representation of XML content can consist of schema derived interfaces
 * and classes representing the binding of schema type definitions, element declarations and model
 * groups.  Factory methods for each of these are provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _RetrieveImagingDocumentSetRequest_QNAME = new QName("urn:ihe:rad:xdsi-b:2009", "RetrieveImagingDocumentSetRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package:
     * org.openehealth.ipf.commons.ihe.xds.core.stub.xdsi
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RetrieveImagingDocumentSetRequestType }
     * 
     */
    public RetrieveImagingDocumentSetRequestType createRetrieveImagingDocumentSetRequestType() {
        return new RetrieveImagingDocumentSetRequestType();
    }

    /**
     * Create an instance of {@link SeriesRequest }
     * 
     */
    public SeriesRequest createSeriesRequestType() {
        return new SeriesRequest();
    }

    /**
     * Create an instance of {@link StudyRequest }
     * 
     */
    public StudyRequest createStudyRequestType() {
        return new StudyRequest();
    }

    /**
     * Create an instance of {@link RetrieveImagingDocumentSetRequestType.TransferSyntaxUIDList }
     * 
     */
    public TransferSyntaxUIDList createRetrieveImagingDocumentSetRequestTypeTransferSyntaxUIDList() {
        return new TransferSyntaxUIDList();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RetrieveImagingDocumentSetRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ihe:rad:xdsi-b:2009", name = "RetrieveImagingDocumentSetRequest")
    public JAXBElement<RetrieveImagingDocumentSetRequestType> createRetrieveImagingDocumentSetRequest(RetrieveImagingDocumentSetRequestType value) {
        return new JAXBElement<>(_RetrieveImagingDocumentSetRequest_QNAME, RetrieveImagingDocumentSetRequestType.class, null, value);
    }

}
