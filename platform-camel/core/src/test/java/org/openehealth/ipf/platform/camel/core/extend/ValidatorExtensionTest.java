/*
 * Copyright 2008 the original author or authors.
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
package org.openehealth.ipf.platform.camel.core.extend;

import org.apache.camel.RuntimeCamelException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Martin Krasser
 */
@ContextConfiguration(locations = { "/context-core-extend-validator.xml" })
public class ValidatorExtensionTest extends AbstractExtensionTest {

    @Test
    public void testBooleanClosureOneParamSuccess() throws InterruptedException {
        mockOutput.expectedBodiesReceived("blah");
        var result = producerTemplate.request("direct:input1", exchange -> exchange.getIn().setBody("blah"));
        assertNull(result.getException());
        mockOutput.assertIsSatisfied();
    }
    
    @Test
    public void testBooleanClosureOneParamInOutFailure() throws InterruptedException {
        mockOutput.expectedMessageCount(0);
        var result = producerTemplate.request("direct:input1", exchange -> exchange.getIn().setBody("blub"));
        assertEquals("validation closure returned false", result.getException().getMessage());
        mockOutput.assertIsSatisfied();
    }
    
    @Test
    public void testExceptionClosureOneParamInOutFailure() throws InterruptedException {
        mockOutput.expectedMessageCount(0);
        var result = producerTemplate.request("direct:input2", exchange -> exchange.getIn().setBody("blub"));
        assertEquals("juhu", result.getException().getMessage());
        mockOutput.assertIsSatisfied();
    }
    
    @Test
    public void testBooleanClosureOneParamInOnlySuccess() throws InterruptedException {
        mockOutput.expectedBodiesReceived("blah");
        producerTemplate.sendBody("direct:input1", "blah");
        mockOutput.assertIsSatisfied();
    }
    
    @Test
    public void testBooleanClosureOneParamInOnlyFailure() {
        Assertions.assertThrows(RuntimeCamelException.class, () -> producerTemplate.sendBody("direct:input1", "blub"));
    }
    
    @Test
    public void testClosureTwoParamsDefaultProfile() throws InterruptedException {
        mockOutput.expectedBodiesReceived("blah");
        producerTemplate.sendBody("direct:input3", "blah");
        mockOutput.assertIsSatisfied();
    }
    
    @Test
    public void testClosureTwoParamsCustomProfile() throws InterruptedException {
        mockOutput.expectedBodiesReceived("blah");
        producerTemplate.sendBody("direct:input4", "blah");
        mockOutput.assertIsSatisfied();
    }
    
    @Test
    public void testClosureTwoParamsCustomProfileAndInput() throws InterruptedException {
        mockOutput.expectedBodiesReceived("abcd");
        producerTemplate.sendBody("direct:input5", "abcd");
        mockOutput.assertIsSatisfied();
    }
    
    @Test
    public void testValidatorBean() throws InterruptedException {
        mockOutput.expectedBodiesReceived("bean");
        producerTemplate.sendBody("direct:input6", "bean");
        mockOutput.assertIsSatisfied();
    }
    
    @Test
    public void testValidatorObject() throws InterruptedException {
        mockOutput.expectedBodiesReceived("object");
        producerTemplate.sendBody("direct:input7", "object");
        mockOutput.assertIsSatisfied();
    }
    
    @Test
    public void testValidatorProfileExpression() throws InterruptedException {
        mockOutput.expectedBodiesReceived("blah");
        producerTemplate.sendBodyAndHeader("direct:input8", "blah", "profile", "derived");
        mockOutput.assertIsSatisfied();
    }
    
}
