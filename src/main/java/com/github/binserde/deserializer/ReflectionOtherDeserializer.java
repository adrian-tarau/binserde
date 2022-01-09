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

package com.github.binserde.deserializer;

import com.github.binserde.io.Decoder;
import com.github.binserde.metadata.FieldInfo;
import com.github.binserde.serializer.SerializerException;

import java.io.IOException;
import java.lang.reflect.Field;

public class ReflectionOtherDeserializer extends ReflectionFieldDeserializer {

    public ReflectionOtherDeserializer(ReflectionDeserializer<?> parent) {
        super(parent);
    }

    @SuppressWarnings("unchecked")
    @Override
    Object deserialize(FieldInfo fieldInfo, Field field, Decoder decoder) throws IOException {
        switch (fieldInfo.getDataType()) {
            case BOOLEAN:
                return decoder.readBoolean();
            case CHARACTER:
                return decoder.readCharacter();
            case STRING:
                return decoder.readString();
            case ENUM:
                Class<Enum> enumClass = field != null ? (Class<Enum>) field.getType() : Enum.class;
                return decoder.readEnum(enumClass);
            default:
                throw new SerializerException("Unhandled data type " + fieldInfo.getDataType());
        }
    }
}
