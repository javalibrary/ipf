/*
 * Copyright 2013 the original author or authors.
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
package config

import java.util.function.Function
import org.apache.camel.Expression
import org.apache.camel.model.ProcessorDefinition
import org.openehealth.ipf.commons.core.extend.config.DynamicExtension

/**
 * @author Christian Ohr
 */
class CustomModelExtensionModule implements DynamicExtension {

    static ProcessorDefinition setDestinationHeader(ProcessorDefinition delegate) {
        delegate.setHeader('destination').exchange({ exchange ->
            "transmogrified-${System.currentTimeMillis()}.html"
        } as Function)
    }

    @Override
    String getModuleName() {
        'CustomModelExtensionModule'
    }

    @Override
    String getModuleVersion() {
        '3.0'
    }

    @Override
    boolean isStatic() {
        false
    }
}