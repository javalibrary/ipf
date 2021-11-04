/*
 * Copyright 2009 the original author or authors.
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
package org.openehealth.ipf.platform.camel.ihe.hl7v2;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ca.uhn.hl7v2.parser.Parser;
import org.apache.camel.Message;
import org.apache.camel.WrappedFile;
import org.apache.camel.converter.IOConverter;


/**
 * Various helper methods for data transformation.
 * @author Dmytro Rud
 */
public class Hl7v2MarshalUtils {

    private Hl7v2MarshalUtils() {
        throw new IllegalStateException("Cannot instantiate helper class");
    }
    
    
    /**
     * Converts a set of some standard data types to String.
     * @param message
     *      Camel message containing the data to be converted. 
     * @param charset
     *      character set. 
     * @param parser 
     *      HL7 parser. 
     * @return
     *      String representing the original exchange or <tt>null</tt>
     *      when the data type is unknown. 
     * @throws Exception
     *      on parsing and marshaling errors.
     */
    public static String marshalStandardTypes(Message message, String charset, Parser parser) throws Exception {
        var body = message.getBody();
        if( ! typeSupported(body)) {
            return null;
        }
        
        String s = null;
        if(body instanceof String) {
            s = (String) body;
        } else if(body instanceof ca.uhn.hl7v2.model.Message) {
            s = parser.encode((ca.uhn.hl7v2.model.Message) body);
        } else if(body instanceof File) {
            s = readFile(body, charset);
        } else if(body instanceof WrappedFile<?>) {
            var file = ((WrappedFile<?>) body).getFile();
            if(file instanceof File) {
                s = readFile(file, charset);
            }
        } else {
            // In standard Camel distribution this will concern  
            // byte[], InputStream and ByteBuffer.
            // See also: http://camel.apache.org/list-of-type-conversions.html
            var bytes = message.getBody(byte[].class);
            if(bytes != null) {
                s = new String(bytes, charset);
            }
        }
        return s;
    }
    

    private static String readFile(Object file, String charset) throws Exception {
        var bytes = IOConverter.toByteArray((File) file);
        return new String(bytes, charset).replace('\n', '\r');
    }
    
    
    /**
     * Determines whether the given object belongs to the predefined
     * set of supported data types. 
     * @param body
     *      The object to check. 
     * @return
     *      <code>true</code> when the type of the object is supported 
     *      by the HL7v2 adapter out-of-the-box, <code>false</code> otherwise.
     */
    public static boolean typeSupported(Object body) {
        final var knownTypes = new Class<?>[] {
            String.class,
            ca.uhn.hl7v2.model.Message.class,
            File.class,
            InputStream.class,
            java.nio.ByteBuffer.class,
            byte[].class,
            WrappedFile.class
        };
        
        for(var type : knownTypes) {
            try {
                type.cast(body);
                return true;
            } catch (ClassCastException cce) {
                // nop
            }
        }
        
        return false;
    }

    
    /**
     * Converts message contents to a {@link String} using the given character set
     * and replaces all <tt>'\n'</tt>'s with <tt>'\r'</tt>'s.  
     * If requested, segments will be defragmented as well.
     */
    public static String convertBodyToString(
            Message message, 
            String charset, 
            boolean defragmentSegments) throws IOException 
    {
        var stream = message.getBody(InputStream.class);
        try (var br = new BufferedReader(new InputStreamReader(stream, charset))){
            var s = IOConverter.toString(br);
            s = s.replace('\n', '\r');
            if (defragmentSegments) {
                s = s.replace("\rADD" + s.charAt(3), "");
            }
            return s;
        }
    }
    
    
    /**
     * Converts the contents of the given Camel message to a {@link Message}.
     * @param message
     *      Camel message to be converted.
     * @param charset
     *      character set.
     * @return
     *      a {@link Message} or <code>null</code> when it was impossible
     *      to get or create one.
     * @param parser 
     *      HL7 parser. 
     * @throws Exception
     */
    public static ca.uhn.hl7v2.model.Message extractHapiMessage(
            Message message,
            String charset,
            Parser parser) throws Exception 
    {
        var body = message.getBody();
        ca.uhn.hl7v2.model.Message msg = null;
        if (body instanceof ca.uhn.hl7v2.model.Message) {
            msg = (ca.uhn.hl7v2.model.Message) body;
        } else {
            // process all other types (String, File, InputStream, ByteBuffer, byte[])
            // by means of the standard routine.  An exception here will be o.k.
            var s = marshalStandardTypes(message, charset, parser);
            if(s != null) {
                s = s.replace('\n', '\r');
                msg = parser.parse(s);
            }
        } 
        return msg;
    }

}
