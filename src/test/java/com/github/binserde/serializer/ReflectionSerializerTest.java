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

package com.github.binserde.serializer;

import com.github.binserde.SerializerFactory;
import com.github.binserde.dto.Address;
import com.github.binserde.dto.Customer;
import com.github.binserde.dto.DtoUtils;
import com.github.binserde.dto.Order;
import com.github.binserde.io.Decoder;
import com.github.binserde.io.Encoder;
import com.github.binserde.io.InputStreamDecoder;
import com.github.binserde.io.OutputStreamEncoder;
import com.github.binserde.metadata.MemoryRegistry;
import com.github.binserde.metadata.NullRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReflectionSerializerTest {

    private SerializerFactory serializerFactory = SerializerFactory.getInstance();
    private ByteArrayOutputStream outputStream;
    private Encoder encoder;
    private Decoder decoder;

    @BeforeEach
    void setup() {
        serializerFactory.setRegistry(new NullRegistry());
        DtoUtils.init();
        outputStream = new ByteArrayOutputStream();
        encoder = new OutputStreamEncoder(outputStream);
    }

    @Test
    void serializeAddress() throws IOException {
        ReflectionSerializer<Address> serializer = new ReflectionSerializer<>(Address.class);
        serializer.serialize(Address.create(), encoder);
        createDecoder();
        assertEquals(121, outputStream.size());
    }

    @Test
    void serializeCustomer() throws IOException {
        ReflectionSerializer<Customer> serializer = new ReflectionSerializer<>(Customer.class);
        serializer.serialize(Customer.create(), encoder);
        createDecoder();
        assertEquals(217, outputStream.size());
    }

    @Test
    void serializeCustomerWithRegistry() throws IOException {
        enableRegistry();
        ReflectionSerializer<Customer> serializer = new ReflectionSerializer<>(Customer.class);
        serializer.serialize(Customer.create(), encoder);
        createDecoder();
        assertEquals(121, outputStream.size());
    }

    @Test
    void serializeOrder() throws IOException {
        ReflectionSerializer<Order> serializer = new ReflectionSerializer<>(Order.class);
        serializer.serialize(Order.create(5), encoder);
        createDecoder();
        assertEquals(699, outputStream.size());
    }

    private void createDecoder() throws IOException {
        encoder.close();
        decoder = new InputStreamDecoder(new ByteArrayInputStream(outputStream.toByteArray()));
    }

    private void enableRegistry() {
        serializerFactory.setRegistry(new MemoryRegistry());
        outputStream = new ByteArrayOutputStream();
        encoder = new OutputStreamEncoder(outputStream);
    }
}