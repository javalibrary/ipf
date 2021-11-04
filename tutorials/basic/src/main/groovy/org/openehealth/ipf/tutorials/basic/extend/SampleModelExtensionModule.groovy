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
package org.openehealth.ipf.tutorials.basic.extend

import org.apache.camel.Exchange
import org.apache.camel.Expression
import org.apache.camel.model.ProcessorDefinition

/**
 * @author Martin Krasser
 */
class SampleModelExtensionModule {

    static ProcessorDefinition reverse(ProcessorDefinition self) {
        self.transmogrify { it.reverse() }
    }
    
    static ProcessorDefinition setFileHeaderFrom(ProcessorDefinition self, String sourceHeader) {
        self.setHeader(Exchange.FILE_NAME, {exchange, type ->
                def destination = exchange.in.headers."$sourceHeader"
                destination ? "${destination}.txt" : 'default.txt'
            } as Expression)
    }
     
}
