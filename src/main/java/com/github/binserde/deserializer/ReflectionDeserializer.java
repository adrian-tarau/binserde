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
import com.github.binserde.utils.ArgumentUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.binserde.metadata.DataTypes.*;

public class ReflectionDeserializer<T> extends AbstractDeserializer<T> {

    private final Map<Class<?>, ClassInfo> classes = new HashMap<>();
    private final Map<String, ClassMapping> mappingsBySignature = new HashMap<>();
    private final Map<Short, ClassMapping> mappingByIdentifier = new HashMap<>();
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
            ClassMapping classMapping = readObjectHeader();
            return (T) deserializeTree(classMapping, true);
        } else {
            throw new DeserializerException("A class signature is expected, but received tag " + tagToString(tag));
        }
    }

    Object deserializeValue(Decoder decoder) throws IOException {
        byte tag = decoder.peekTag();
        if (tag == NULL) {
            return null;
        } else {
            tag = decoder.readTag();
            if (tag != OBJECT) throw new DeserializerException("Expected object tag, got " + DataTypes.tagToString(tag));
            DataType dataType = DataType.fromId(decoder.readTag());
            return deserializeBasic(dataType);
        }
    }

    private Object deserializeTree(ClassMapping classMapping, boolean root) throws IOException {
        Object instance = classMapping.createInstance();
        int fieldIndex = 0;
        for (FieldInfo streamField : classMapping.streamFields) {
            DataType streamDataType = streamField.getDataType();
            FieldInfo localField = classMapping.localFields[fieldIndex++];
            Field _localField = localField != null ? localField.getField() : null;
            Object value = null;
            byte tag = decoder.peekTag();
            if (tag != NULL) {
                if (streamField.getDataType() == DataType.OBJECT) {
                    ClassMapping fieldClassMapping = readObjectHeader();
                    value = deserializeTree(fieldClassMapping, false);
                } else {
                    value = deserializeBasic(streamDataType);
                }
            } else {
                tag = decoder.readTag();
            }
            if (_localField != null) {
                try {
                    _localField.set(instance, value);
                } catch (IllegalAccessException e) {
                    throw new DeserializerException("Failed to set value for field '" + localField.getName(), e);
                }
            }
        }
        return instance;
    }

    private Object deserializeBasic(DataType dataType) throws IOException {
        switch (dataType.getCategory()) {
            case OTHER:
                return otherSerializer.deserialize(dataType, decoder);
            case NUMBER:
                return numberSerializer.deserialize(dataType, decoder);
            case COLLECTION:
                return collectionSerializer.deserialize(dataType, decoder);
            case TIME:
                return timeSerializer.deserialize(dataType, decoder);
            default:
                throw new DeserializerException("Unhandled category " + dataType.getCategory());
        }
    }


    private void readClass() throws IOException {
        ClassInfo streamClassInfo = decoder.readClass();
        classes.putIfAbsent(streamClassInfo.getClazz(), streamClassInfo);
        ClassMapping classMapping = mappingsBySignature.computeIfAbsent(streamClassInfo.getSignature(),
                s -> new ClassMapping(ClassInfo.create(streamClassInfo.getClazz()), streamClassInfo));
        mappingByIdentifier.computeIfAbsent(streamClassInfo.getIdentifier(), integer -> classMapping);
    }

    private ClassMapping readObjectHeader() throws IOException {
        if (DataTypes.isClass(decoder.peekTag())) {
            readClass();
        }
        byte tag = decoder.readTag();
        if (tag != OBJECT) {
            throw new DeserializerException("Expecting an object tag, got " + DataTypes.tagToString(tag));
        }
        short identifier = decoder.readShort();
        ClassMapping classMapping = mappingByIdentifier.get(identifier);
        if (classMapping == null) {
            throw new DeserializerException("A class with identifier " + identifier + " is not registered");
        }
        return classMapping;
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
