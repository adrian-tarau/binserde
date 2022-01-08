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
import com.github.binserde.metadata.FieldInfo;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static com.github.binserde.metadata.DataTypes.*;

public class ReflectionCollectionSerializer extends ReflectionFieldSerializer {

    public ReflectionCollectionSerializer(ReflectionSerializer<?> parent) {
        super(parent);
    }

    @Override
    void serialize(FieldInfo fieldInfo, Object value, Encoder encoder) throws IOException {
        encoder.writeTag((byte) (BASE | BASE_OBJECT));
        encoder.writeTag(fieldInfo.getDataType().getId());
        switch (fieldInfo.getDataType()) {
            case LIST:
            case SET:
            case QUEUE:
            case DEQUE:
            case SORTED_SET:
                serializeArray(fieldInfo, value, encoder);
                break;
            case MAP:
            case SORTED_MAP:
                serializeMap(fieldInfo, value, encoder);
                break;
            default:
                throw new SerializerException("Unhandled data type " + fieldInfo.getDataType());
        }
    }

    void serializeArray(FieldInfo fieldInfo, Object value, Encoder encoder) throws IOException {
        Collection<Object> collection = (Collection<Object>) value;
        int size = collection.size();
        encoder.writeTag((byte) (BASE | BASE_ARRAY));
        encoder.writeTag(fieldInfo.getDataType().getId());
        encoder.writeInteger(size);
        for (Object collectionValue : collection) {
            parent.serializeTree(collectionValue);
        }
    }

    void serializeMap(FieldInfo fieldInfo, Object value, Encoder encoder) throws IOException {
        Map<Object, Object> map = (Map<Object, Object>) value;
        int size = map.size();
        encoder.writeTag((byte) (BASE | BASE_ARRAY));
        encoder.writeTag(fieldInfo.getDataType().getId());
        encoder.writeInteger(size);
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            parent.serializeTree(entry.getKey());
            parent.serializeTree(entry.getValue());
        }
    }
}
