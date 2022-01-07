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

package com.github.binserde;

import com.github.binserde.dto.Address;
import com.github.binserde.dto.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SerializerFactoryTest {

    private SerializerFactory factory;

    @BeforeEach
    public void setup() {
        factory = SerializerFactory.getInstance();
        factory.reset();
    }

    @Test
    void getInstance() {
        assertNotNull(factory);
    }

    @Test
    void register() {
        assertFalse(factory.isSupported(Customer.class));
        factory.register(Customer.class, Customer.ID);
        assertTrue(factory.isSupported(Customer.class));
    }

    @Test
    void registerWithWrongIdentifier() {
        assertThrows(IllegalArgumentException.class, () -> factory.register(Customer.class, -49));
        assertThrows(IllegalArgumentException.class, () -> factory.register(Customer.class, 40000));
    }

    @Test
    void registerWithAlreadyRegisteredIdentifier() {
        factory.register(Customer.class, Customer.ID);
        assertThrows(IllegalArgumentException.class, () -> factory.register(Address.class, Customer.ID));
    }

    @Test
    void isSupported() {
    }
}