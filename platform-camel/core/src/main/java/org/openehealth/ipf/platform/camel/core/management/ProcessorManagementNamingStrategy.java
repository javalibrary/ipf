/*
 * Copyright 2011 the original author or authors.
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
package org.openehealth.ipf.platform.camel.core.management;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.camel.CamelContext;
import org.apache.camel.NamedNode;
import org.apache.camel.Processor;
import org.apache.camel.management.DefaultManagementObjectNameStrategy;
import org.apache.camel.model.ProcessorDefinitionHelper;

/**
 * @author Reinhard Luft
 */
public class ProcessorManagementNamingStrategy extends DefaultManagementObjectNameStrategy {

    public static final String KEY_ROUTE = "route";

    @Override
    public ObjectName getObjectNameForProcessor(CamelContext context,
                                                Processor processor,
                                                NamedNode namedNode)
            throws MalformedObjectNameException {

        var buffer = new StringBuilder();
        buffer.append(domainName).append(":");
        buffer.append(KEY_CONTEXT + "=").append(getContextId(context)).append(",");
        buffer.append(KEY_TYPE + "=").append(TYPE_PROCESSOR).append(",");

        var route = ProcessorDefinitionHelper.getRoute(namedNode);

        if (route != null) {
            buffer.append(KEY_ROUTE + "=").append(ObjectName.quote(route.getId())).append(",");
        }

        buffer.append(KEY_NAME + "=").append(ObjectName.quote(namedNode.getId()));
        return createObjectName(buffer);
    }

}
