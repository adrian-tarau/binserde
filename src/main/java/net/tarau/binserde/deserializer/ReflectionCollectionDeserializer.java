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

package net.tarau.binserde.deserializer;

import net.tarau.binserde.io.Decoder;
import net.tarau.binserde.metadata.DataType;

import java.io.IOException;
import java.util.*;

public class ReflectionCollectionDeserializer extends ReflectionFieldDeserializer {

    public ReflectionCollectionDeserializer(ReflectionDeserializer<?> parent) {
        super(parent);
    }

    @Override
    Object deserialize(DataType dataType, Decoder decoder) throws IOException {
        switch (dataType) {
            case COLLECTION:
            case LIST:
            case SET:
            case QUEUE:
            case DEQUE:
            case SORTED_SET:
                return deserializeArray(dataType, decoder);
            case MAP:
            case SORTED_MAP:
                return deserializeMap(dataType, decoder);
            default:
                throw new DeserializerException("Unhandled data type " + dataType);
        }
    }

    Object deserializeArray(DataType dataType, Decoder decoder) throws IOException {
        int size = decoder.readInteger();
        Collection<Object> collection = createCollectionType(dataType, size);
        while (size-- > 0) {
            collection.add(parent.deserializeValue(decoder));
        }
        return collection;
    }

    Object deserializeMap(DataType dataType, Decoder decoder) throws IOException {
        int size = decoder.readInteger();
        Map<Object, Object> map = createMapType(dataType, size);
        while (size-- > 0) {
            map.put(parent.deserializeValue(decoder), parent.deserializeValue(decoder));
        }
        return map;
    }

    private Collection<Object> createCollectionType(DataType dataType, int size) {
        switch (dataType) {
            case COLLECTION:
            case LIST:
                return new ArrayList<>(size);
            case SET:
                return new HashSet<>(size);
            case QUEUE:
            case DEQUE:
                return new ArrayDeque<>(size);
            case SORTED_SET:
                return new TreeSet<>();
            default:
                throw new DeserializerException("Unknown collection type: " + dataType);
        }
    }

    private Map<Object, Object> createMapType(DataType dataType, int size) {
        switch (dataType) {
            case MAP:
                return new HashMap<>(size);
            case SORTED_MAP:
                return new TreeMap<>();
            default:
                throw new DeserializerException("Unknown map type: " + dataType);
        }
    }
}
