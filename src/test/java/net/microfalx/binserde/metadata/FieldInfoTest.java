/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.microfalx.binserde.metadata;

import net.microfalx.binserde.dto.Customer;
import net.microfalx.binserde.dto.DtoUtils;
import net.microfalx.binserde.dto.Order;
import net.microfalx.binserde.io.Decoder;
import net.microfalx.binserde.io.Encoder;
import net.microfalx.binserde.io.InputStreamDecoder;
import net.microfalx.binserde.io.OutputStreamEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class FieldInfoTest {

    private ByteArrayOutputStream outputStream;
    private Encoder encoder;
    private Decoder decoder;

    @BeforeEach
    void setup() {
        outputStream = new ByteArrayOutputStream();
        encoder = new OutputStreamEncoder(outputStream);
        DtoUtils.init();
    }

    @Test
    void fromFieldWithPrimitive() throws Exception {
        Field field = Customer.class.getDeclaredField("age");
        FieldInfo fieldInfo = FieldInfo.create(field);
        assertEquals("age", fieldInfo.getName());
        assertEquals(DataType.INTEGER, fieldInfo.getDataType());
        assertTrue(fieldInfo.isPrimitive());
        assertEquals("age", fieldInfo.getField().getName());
    }

    @Test
    void fromFieldWithString() throws Exception {
        Field field = Customer.class.getDeclaredField("firstName");
        FieldInfo fieldInfo = FieldInfo.create(field);
        assertEquals("firstName", fieldInfo.getName());
        assertEquals(DataType.STRING, fieldInfo.getDataType());
        assertFalse(fieldInfo.isPrimitive());
        assertEquals("firstName", fieldInfo.getField().getName());
    }

    @Test
    void fromFieldWithObject() throws Exception {
        Field field = Order.class.getDeclaredField("customer");
        FieldInfo fieldInfo = FieldInfo.create(field);
        assertEquals("customer", fieldInfo.getName());
        assertEquals(DataType.OBJECT, fieldInfo.getDataType());
        assertFalse(fieldInfo.isPrimitive());
        assertEquals("customer", fieldInfo.getField().getName());
    }

    @Test
    void fromStream() throws Exception {
        Field field = Customer.class.getDeclaredField("firstName");
        FieldInfo fieldInfo = FieldInfo.create(field);
        fieldInfo.store(encoder);
        encoder.close();
        createDecoder();
        fieldInfo = FieldInfo.create(decoder);
        assertEquals("firstName", fieldInfo.getName());
        assertEquals(DataType.STRING, fieldInfo.getDataType());
        assertFalse(fieldInfo.isPrimitive());
    }

    @Test
    void validaToString() throws Exception {
        Field field = Customer.class.getDeclaredField("firstName");
        FieldInfo fieldInfo = FieldInfo.create(field);
        assertTrue(fieldInfo.toString().contains("FieldInfo"));
    }

    private void createDecoder() {
        decoder = new InputStreamDecoder(new ByteArrayInputStream(outputStream.toByteArray()));
    }


}