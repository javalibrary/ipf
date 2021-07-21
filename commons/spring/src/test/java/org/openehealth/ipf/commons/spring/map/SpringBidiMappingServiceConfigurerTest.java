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
package org.openehealth.ipf.commons.spring.map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openehealth.ipf.commons.map.BidiMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Martin Krasser
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = { "/context-configurer.xml" })
public class SpringBidiMappingServiceConfigurerTest {

    @Autowired
    private BidiMappingService mappingService;
    
    @Test
    public void testMappings() {
        assertEquals("b1", mappingService.get("m1", "a1"));
        assertEquals("b2", mappingService.get("m2", "a2"));
        assertEquals("b3", mappingService.get("m3", "a3"));
    }
    
}
