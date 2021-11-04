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
package org.openehealth.ipf.platform.camel.core.camel;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Martin Krasser
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"/context-camel-test.xml"})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
public abstract class TestSupport {

    @Autowired
    protected ProducerTemplate producerTemplate;

    protected List<String> sendBodies(String endpointUri, ExchangePattern pattern, String body, int repeats) {
        var result = new ArrayList<String>(repeats);
        for (var i = 0; i < repeats; i++) {
            result.add((String)producerTemplate.sendBody(endpointUri, pattern, body));
        }
        return result;
    }
    
    protected static List<String> bodies(List<Exchange> exchanges) {
        return exchanges.stream()
                .map(exchange -> (String) exchange.getIn().getBody())
                .collect(Collectors.toCollection(ArrayList::new));
    }
    
}
