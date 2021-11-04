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

package org.openehealth.ipf.commons.spring.core.config;

import org.springframework.aop.SpringProxy;
import org.springframework.aop.framework.Advised;
import org.springframework.util.Assert;

import java.lang.reflect.Proxy;

/**
 *
 */
final class ProxyUtils {

    static boolean isJDKDynamicProxy(Class<?> beanClass) {
        return Proxy.isProxyClass(beanClass);
    }

    static <T> Class<T> getFirstProxiedInterface(Class<?> proxyClass) {
        var nonUserInterfaceCount = 0;
        if (proxyClass.isAssignableFrom(SpringProxy.class)) {
            ++nonUserInterfaceCount;
        }
        if (proxyClass.isAssignableFrom(Advised.class)) {
            ++nonUserInterfaceCount;
        }
        var proxyInterfaces = proxyClass.getInterfaces();
        Class<?>[] userInterfaces = new Class[proxyInterfaces.length - nonUserInterfaceCount];
        System.arraycopy(proxyInterfaces, 0, userInterfaces, 0, userInterfaces.length);
        Assert.notEmpty(userInterfaces, "JDK proxy must implement one or more interfaces");
        return (Class<T>)userInterfaces[0];
    }

}