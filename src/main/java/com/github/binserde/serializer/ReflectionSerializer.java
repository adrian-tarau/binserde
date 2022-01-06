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
import com.github.binserde.metadata.ClassInfo;
import com.github.binserde.metadata.DataType;
import com.github.binserde.metadata.FieldInfo;
import com.github.binserde.utils.ArgumentUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReflectionSerializer<T> extends AbstractSerializer<T> {

    private final Map<Class<?>, ClassInfo> classes = new HashMap<>();
    private Encoder encoder;

    private final ReflectionFieldSerializer otherSerializer = new ReflectionOtherSerializer(this);
    private final ReflectionFieldSerializer numberSerializer = new ReflectionNumberSerializer(this);
    private final ReflectionFieldSerializer collectionSerializer = new ReflectionCollectionSerializer(this);
    private final ReflectionFieldSerializer timeSerializer = new ReflectionTimeSerializer(this);

    public ReflectionSerializer(Class<T> type) {
        super(type);
    }

    @Override
    public void serialize(T data, Encoder encoder) throws IOException {
        ArgumentUtils.requireNonNull(data);
        ArgumentUtils.requireNonNull(encoder);
        this.encoder = encoder;
        serializeTree(data);
    }

    void serializeTree(Object data) throws IOException {
        ClassInfo classInfo = getClassInfo(data);
        for (FieldInfo fieldInfo : classInfo.getFields()) {
            Object value = get(data, fieldInfo);
            if (value == null) {
                encoder.writeNull();
            } else {
                serializeField(fieldInfo, value);
            }
        }
    }

    private void serializeField(FieldInfo fieldInfo, Object data) throws IOException {
        if (fieldInfo.getDataType() == DataType.OBJECT) {
            serializeTree(data);
        } else {
            switch (fieldInfo.getDataType().getCategory()) {
                case OTHER:
                    otherSerializer.serialize(fieldInfo, data, encoder);
                    break;
                case NUMBER:
                    numberSerializer.serialize(fieldInfo, data, encoder);
                    break;
                case COLLECTION:
                    collectionSerializer.serialize(fieldInfo, data, encoder);
                    break;
                case TIME:
                    timeSerializer.serialize(fieldInfo, data, encoder);
                    break;
                default:
                    throw new SerializerException("Unhandled enum " + fieldInfo.getDataType().getCategory());
            }
        }
    }

    private ClassInfo getClassInfo(Object data) throws IOException {
        Class<?> clazz = data.getClass();
        ClassInfo classInfo = classes.get(clazz);
        if (classInfo != null) return classInfo;
        classInfo = ClassInfo.create(clazz);
        encoder.writeClass(classInfo);
        classes.put(clazz, classInfo);
        return classInfo;
    }

    private Object get(Object data, FieldInfo field) {
        try {
            return field.getField().get(data);
        } catch (IllegalAccessException e) {
            throw new SerializerException("Failed to extract field value for " + field, e);
        }
    }

}
