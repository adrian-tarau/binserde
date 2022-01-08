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

package com.github.binserde.metadata;

import com.github.binserde.dto.Address;
import com.github.binserde.dto.Customer;
import com.github.binserde.dto.DtoUtils;
import com.github.binserde.dto.Order;
import com.github.binserde.io.Decoder;
import com.github.binserde.io.Encoder;
import com.github.binserde.io.OutputStreamEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ClassInfoTest {

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
    void fromCustomer() {
        ClassInfo classInfo = ClassInfo.create(Customer.class);
        assertEquals("Customer", classInfo.getName());
        assertEquals(Customer.class, classInfo.getClazz());
        assertEquals(5, classInfo.getFields().size());
        assertEquals("firstName", classInfo.getField("firstname").getName());
        assertEquals("firstName", classInfo.getField(0).getName());
        assertEquals(100, classInfo.getIdentifier());
        assertNotNull(classInfo.getSignature());
    }

    @Test
    void fromAddress() {
        ClassInfo classInfo = ClassInfo.create(Address.class);
        assertEquals("Address", classInfo.getName());
        assertEquals(Address.class, classInfo.getClazz());
        assertEquals(6, classInfo.getFields().size());
        assertEquals(DataType.ENUM, classInfo.getField("type").getDataType());
        assertEquals(101, classInfo.getIdentifier());
    }

    @Test
    void fromOrder() {
        ClassInfo classInfo = ClassInfo.create(Order.class);
        assertEquals("Order", classInfo.getName());
        assertEquals(Order.class, classInfo.getClazz());
        assertEquals(3, classInfo.getFields().size());
        assertEquals(200, classInfo.getIdentifier());
    }

    @Test
    void validaToString() throws Exception {
        ClassInfo classInfo = ClassInfo.create(Customer.class);
        assertNotNull(classInfo.toString());
    }

}