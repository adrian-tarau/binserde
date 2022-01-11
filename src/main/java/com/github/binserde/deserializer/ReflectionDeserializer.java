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
import com.github.binserde.metadata.ClassInfo;
import com.github.binserde.metadata.DataType;
import com.github.binserde.metadata.DataTypes;
import com.github.binserde.metadata.FieldInfo;
import com.github.binserde.serializer.SerializerException;
import com.github.binserde.utils.ArgumentUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.binserde.metadata.DataTypes.NULL;
import static com.github.binserde.metadata.DataTypes.tagToString;

public class ReflectionDeserializer<T> extends AbstractDeserializer<T> {

    private final Map<Class<?>, ClassInfo> classes = new HashMap<>();
    private final Map<String, ClassMapping> mappings = new HashMap<>();
    private Decoder decoder;

    private final ReflectionFieldDeserializer otherSerializer = new ReflectionOtherDeserializer(this);
    private final ReflectionFieldDeserializer numberSerializer = new ReflectionNumberDeserializer(this);
    private final ReflectionFieldDeserializer collectionSerializer = new ReflectionCollectionDeserializer(this);
    private final ReflectionFieldDeserializer timeSerializer = new ReflectionTimeDeserializer(this);

    public ReflectionDeserializer(Class<T> type) {
        super(type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(Decoder decoder) throws IOException {
        ArgumentUtils.requireNonNull(decoder);
        this.decoder = decoder;

        byte tag = decoder.peekTag();
        if (DataTypes.isClass(tag)) {
            ClassMapping classMapping = readClass();
            return (T) deserializeTree(classMapping);
        } else {
            throw new DeserializerException("A class signature is expected, but received tag " + tagToString(tag));
        }
    }

    private Object deserializeTree(ClassMapping classMapping) throws IOException {
        byte tag = decoder.peekTag();
        if (tag == NULL) return null;
        Object instance = classMapping.createInstance();
        int fieldIndex = 0;
        for (FieldInfo streamField : classMapping.streamFields) {
            if (streamField.getDataType() == DataType.OBJECT) {
                ClassMapping fieldClassMapping = readClass();
                return deserializeTree(fieldClassMapping);
            } else {
                tag = decoder.peekTag();
                if (tag != NULL) {
                    FieldInfo localField = classMapping.localFields[fieldIndex++];
                    Field _localField = localField != null ? localField.getField() : null;
                    Object value = null;
                    switch (streamField.getDataType().getCategory()) {
                        case OTHER:
                            value = otherSerializer.deserialize(streamField, _localField, decoder);
                            break;
                        case NUMBER:
                            value = numberSerializer.deserialize(streamField, _localField, decoder);
                            break;
                        case COLLECTION:
                            value = collectionSerializer.deserialize(streamField, _localField, decoder);
                            break;
                        case TIME:
                            value = timeSerializer.deserialize(streamField, _localField, decoder);
                            break;
                        default:
                            throw new SerializerException("Unhandled category " + streamField.getDataType().getCategory());
                    }
                    if (_localField != null) {
                        try {
                            _localField.set(instance, value);
                        } catch (IllegalAccessException e) {
                            throw new DeserializerException("Failed to set value for field '" + localField.getName(), e);
                        }
                    }
                }
            }
        }
        return instance;
    }

    private ClassMapping readClass() throws IOException {
        ClassInfo streamClassInfo = decoder.readClass();
        classes.putIfAbsent(streamClassInfo.getClass(), streamClassInfo);
        return mappings.computeIfAbsent(streamClassInfo.getSignature(),
                s -> new ClassMapping(ClassInfo.create(streamClassInfo.getClazz()), streamClassInfo));
    }

    private static class ClassMapping {
        private ClassInfo localClassInfo;
        private ClassInfo streamClassInfo;

        private FieldInfo[] streamFields;
        private FieldInfo[] localFields;

        ClassMapping(ClassInfo localClassInfo, ClassInfo streamClassInfo) {
            ArgumentUtils.requireNonNull(localClassInfo);
            ArgumentUtils.requireNonNull(streamClassInfo);

            this.localClassInfo = localClassInfo;
            this.streamClassInfo = streamClassInfo;
            initialize();
        }

        private Object createInstance() {
            try {
                Constructor<?> declaredConstructor = localClassInfo.getClazz().getDeclaredConstructor();
                return declaredConstructor.newInstance();
            } catch (NoSuchMethodException e) {
                throw new DeserializerException("A public constructor is not defined", e);
            } catch (Exception e) {
                throw new DeserializerException("Failed to create instance for " + localClassInfo.getClazz().getName(), e);
            }
        }

        private void initialize() {
            streamFields = streamClassInfo.getFields().toArray(new FieldInfo[0]);
            List<FieldInfo> localFields = new ArrayList<>();
            for (FieldInfo fieldInfo : streamClassInfo.getFields()) {
                FieldInfo localField;
                if (fieldInfo.getTag() == FieldInfo.NO_TAG) {
                    localField = localClassInfo.findField(fieldInfo.getName());
                } else {
                    localField = localClassInfo.findField(fieldInfo.getTag());
                }
                localFields.add(localField);
            }
            this.localFields = localFields.toArray(new FieldInfo[0]);
        }
    }
}
