/*
 * Copyright 2010 the original author or authors.
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
package org.openehealth.ipf.platform.camel.ihe.hl7v3.iti55

import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.GPathResult
import org.apache.camel.Message

import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.Duration

/**
 * Helper functions for XCPD tests.
 * @author Dmytro Rud
 */
class XcpdTestUtils {

    static void setTtl(Message message, int n) {
        Duration dura = DatatypeFactory.newInstance().newDuration("P${n}Y")
        TtlHeaderUtils.setTtl(dura, message)
    }

    static void testPositiveAckCode(String message) {
        GPathResult xml = new XmlSlurper().parseText(message)
        assert xml.acknowledgement.typeCode.@code == 'AA'
        assert xml.controlActProcess.queryAck.queryResponseCode.@code == 'OK'
    }

}
