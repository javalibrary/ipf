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

import org.openehealth.ipf.commons.map.BidiMappingService;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * BidiMappingService implementation that can be configured with Spring {@link Resource resources}
 * and using the {@link org.openehealth.ipf.commons.spring.map.config.CustomMappingsConfigurer}.
 *
 * @since 3.1
 */
public class SpringBidiMappingService extends BidiMappingService {

    private final Collection<Resource> resources = new ArrayList<>();

    public Collection<? extends Resource> getMappingResources() {
        return resources;
    }

    public synchronized void setMappingResource(Resource resource) {
        try {
            setMappingScript(resource.getURL());
            resources.add(resource);
        } catch (IOException e) {
            if (!isIgnoreResourceNotFound())
                throw new IllegalArgumentException(resource.getFilename() + " could not be read", e);
        }
    }

    public void setMappingResources(Collection<? extends Resource> resources) {
        resources.forEach(this::setMappingResource);
    }

}
