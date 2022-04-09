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

package net.microfalx.binserde.serde;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import net.microfalx.binserde.dto.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KryoSerializerTest {

    private Kryo kryo;
    private ByteArrayOutputStream outputStream;
    private Output output;

    @BeforeEach
    void setup() {
        kryo = new Kryo();
        kryo.register(Address.class);
        kryo.register(Address.Type.class);
        outputStream = new ByteArrayOutputStream();
        output = new Output(outputStream);
    }

    @Test
    void serializeAddress() throws IOException {
        Address address = Address.create();
        kryo.writeObject(output, address);
        output.close();
        assertEquals(36, outputStream.size());
    }
}
