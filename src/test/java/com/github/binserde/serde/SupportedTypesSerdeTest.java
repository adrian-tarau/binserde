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

package com.github.binserde.serde;

import com.github.binserde.AbstractSerdeTestCase;
import com.github.binserde.deserializer.ReflectionDeserializer;
import com.github.binserde.dto.AllSupportedTypes;
import com.github.binserde.dto.CollectionTypes;
import com.github.binserde.dto.NumberTypes;
import com.github.binserde.dto.TimeTypes;
import com.github.binserde.serializer.ReflectionSerializer;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SupportedTypesSerdeTest extends AbstractSerdeTestCase {

    @Test
    void serializeNumberTypesEmpty() throws IOException {
        NumberTypes types = new NumberTypes();
        ReflectionSerializer<NumberTypes> serializer = new ReflectionSerializer<>(NumberTypes.class);
        serializer.serialize(types, encoder);
        createDecoder();
        assertEquals(169, outputStream.size());

        ReflectionDeserializer<NumberTypes> deserializer = new ReflectionDeserializer<>(NumberTypes.class);
        NumberTypes dtypes = deserializer.deserialize(decoder);
        assertNotNull(dtypes);
        assertThat(dtypes).usingRecursiveComparison().isEqualTo(types);
    }

    @Test
    void serializeNumberTypesWithValues() throws IOException {
        NumberTypes types = NumberTypes.create();
        ReflectionSerializer<NumberTypes> serializer = new ReflectionSerializer<>(NumberTypes.class);
        serializer.serialize(types, encoder);
        createDecoder();
        assertEquals(187, outputStream.size());

        ReflectionDeserializer<NumberTypes> deserializer = new ReflectionDeserializer<>(NumberTypes.class);
        NumberTypes dtypes = deserializer.deserialize(decoder);
        assertNotNull(dtypes);
        assertThat(dtypes).usingRecursiveComparison().isEqualTo(types);
    }

    @Test
    void serializeCollectionTypesEmpty() throws IOException {
        CollectionTypes types = new CollectionTypes();
        ReflectionSerializer<CollectionTypes> serializer = new ReflectionSerializer<>(CollectionTypes.class);
        serializer.serialize(types, encoder);
        createDecoder();
        assertEquals(122, outputStream.size());

        ReflectionDeserializer<CollectionTypes> deserializer = new ReflectionDeserializer<>(CollectionTypes.class);
        CollectionTypes dtypes = deserializer.deserialize(decoder);
        assertNotNull(dtypes);
        assertThat(dtypes).usingRecursiveComparison().isEqualTo(types);
    }

    @Test
    void serializeCollectionTypesWithValues() throws IOException {
        CollectionTypes types = CollectionTypes.create();
        ReflectionSerializer<CollectionTypes> serializer = new ReflectionSerializer<>(CollectionTypes.class);
        serializer.serialize(types, encoder);
        createDecoder();
        assertEquals(239, outputStream.size());

        ReflectionDeserializer<CollectionTypes> deserializer = new ReflectionDeserializer<>(CollectionTypes.class);
        CollectionTypes dtypes = deserializer.deserialize(decoder);
        assertNotNull(dtypes);
        assertThat(dtypes).usingRecursiveComparison().isEqualTo(types);
    }

    @Test
    void serializeTimeTypesEmpty() throws IOException {
        TimeTypes types = new TimeTypes();
        ReflectionSerializer<TimeTypes> serializer = new ReflectionSerializer<>(TimeTypes.class);
        serializer.serialize(types, encoder);
        createDecoder();
        assertEquals(127, outputStream.size());

        ReflectionDeserializer<TimeTypes> deserializer = new ReflectionDeserializer<>(TimeTypes.class);
        TimeTypes dtypes = deserializer.deserialize(decoder);
        assertNotNull(dtypes);
        assertThat(dtypes).usingRecursiveComparison().isEqualTo(types);
    }

    @Test
    void serializeTimeTypesWithValues() throws IOException {
        TimeTypes types = TimeTypes.create();
        ReflectionSerializer<TimeTypes> serializer = new ReflectionSerializer<>(TimeTypes.class);
        serializer.serialize(types, encoder);
        createDecoder();
        assertEquals(233, outputStream.size());

        ReflectionDeserializer<TimeTypes> deserializer = new ReflectionDeserializer<>(TimeTypes.class);
        TimeTypes dtypes = deserializer.deserialize(decoder);
        assertNotNull(dtypes);
        assertThat(dtypes).usingRecursiveComparison().isEqualTo(types);
    }

    @Test
    void serializeAllTypesEmptyObject() throws IOException {
        AllSupportedTypes types = new AllSupportedTypes();
        ReflectionSerializer<AllSupportedTypes> serializer = new ReflectionSerializer<>(AllSupportedTypes.class);
        serializer.serialize(types, encoder);
        createDecoder();
        assertEquals(117, outputStream.size());

        ReflectionDeserializer<AllSupportedTypes> deserializer = new ReflectionDeserializer<>(AllSupportedTypes.class);
        AllSupportedTypes dtypes = deserializer.deserialize(decoder);
        assertNotNull(dtypes);
        assertThat(dtypes).usingRecursiveComparison().isEqualTo(types);
    }

    @Test
    void serializeAllTypesWithValues() throws IOException {
        AllSupportedTypes types = AllSupportedTypes.create();
        ReflectionSerializer<AllSupportedTypes> serializer = new ReflectionSerializer<>(AllSupportedTypes.class);
        serializer.serialize(types, encoder);
        createDecoder();
        assertEquals(523, outputStream.size());

        ReflectionDeserializer<AllSupportedTypes> deserializer = new ReflectionDeserializer<>(AllSupportedTypes.class);
        AllSupportedTypes dtypes = deserializer.deserialize(decoder);
        assertNotNull(dtypes);
        assertThat(dtypes).usingRecursiveComparison().isEqualTo(types);
    }
}
