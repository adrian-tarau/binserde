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

package net.tarau.binserde.serde;

import net.tarau.binserde.SerializerFactory;
import net.tarau.binserde.deserializer.ReflectionDeserializer;
import net.tarau.binserde.dto.Address;
import net.tarau.binserde.dto.Customer;
import net.tarau.binserde.dto.DtoUtils;
import net.tarau.binserde.dto.Order;
import net.tarau.binserde.io.Decoder;
import net.tarau.binserde.io.Encoder;
import net.tarau.binserde.io.InputStreamDecoder;
import net.tarau.binserde.io.OutputStreamEncoder;
import net.tarau.binserde.metadata.MemoryRegistry;
import net.tarau.binserde.metadata.NullRegistry;
import net.tarau.binserde.serializer.ReflectionSerializer;
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
        Address address = Address.create();
        ReflectionSerializer<Address> serializer = new ReflectionSerializer<>(Address.class);
        serializer.serialize(address, encoder);
        createDecoder();
        assertEquals(136, outputStream.size());

        ReflectionDeserializer<Address> deserializer = new ReflectionDeserializer<>(Address.class);
        Address daddres = deserializer.deserialize(decoder);
        assertEquals(address.getCity(), daddres.getCity());
        assertEquals(address.getNumber(), daddres.getNumber());
        assertEquals(address.getState(), daddres.getState());
        assertEquals(address.getStreet(), daddres.getStreet());
        assertEquals(address.getType(), daddres.getType());
    }

    @Test
    void serializeCustomer() throws IOException {
        ReflectionSerializer<Customer> serializer = new ReflectionSerializer<>(Customer.class);
        serializer.serialize(Customer.create(), encoder);
        createDecoder();
        assertEquals(239, outputStream.size());
    }

    @Test
    void serializeCustomerWithRegistry() throws IOException {
        enableRegistry();
        ReflectionSerializer<Customer> serializer = new ReflectionSerializer<>(Customer.class);
        serializer.serialize(Customer.create(), encoder);
        createDecoder();
        assertEquals(132, outputStream.size());
    }

    @Test
    void serializeOrder() throws IOException {
        ReflectionSerializer<Order> serializer = new ReflectionSerializer<>(Order.class);
        serializer.serialize(Order.create(5), encoder);
        createDecoder();
        assertEquals(1066, outputStream.size());
    }

    @Test
    void serializeOrderWithRegistry() throws IOException {
        enableRegistry();
        ReflectionSerializer<Order> serializer = new ReflectionSerializer<>(Order.class);
        serializer.serialize(Order.create(5), encoder);
        createDecoder();
        assertEquals(845, outputStream.size());
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