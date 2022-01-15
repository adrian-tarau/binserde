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

import com.github.binserde.io.Encoder;
import com.github.binserde.metadata.DataType;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

class ReflectionCollectionSerializer extends ReflectionFieldSerializer {

    ReflectionCollectionSerializer(ReflectionSerializer<?> parent) {
        super(parent);
    }

    @Override
    void serialize(DataType dataType, Object value, Encoder encoder) throws IOException {
        switch (dataType) {
            case LIST:
            case SET:
            case QUEUE:
            case DEQUE:
            case SORTED_SET:
                serializeArray(value, encoder);
                break;
            case MAP:
            case SORTED_MAP:
                serializeMap(value, encoder);
                break;
            default:
                throw new SerializerException("Unhandled data type " + dataType);
        }
    }

    void serializeArray(Object value, Encoder encoder) throws IOException {
        Collection<Object> collection = (Collection<Object>) value;
        int size = collection.size();
        encoder.writeInteger(size);
        for (Object collectionValue : collection) {
            parent.serializeValue(collectionValue);
        }
    }

    void serializeMap(Object value, Encoder encoder) throws IOException {
        Map<Object, Object> map = (Map<Object, Object>) value;
        int size = map.size();
        encoder.writeInteger(size);
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            parent.serializeValue(entry.getKey());
            parent.serializeValue(entry.getValue());
        }
    }
}
