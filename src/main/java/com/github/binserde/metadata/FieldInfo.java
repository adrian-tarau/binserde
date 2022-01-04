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

package com.github.binserde.metadata;

import com.github.binserde.SerializerFactory;
import com.github.binserde.io.Decoder;
import com.github.binserde.io.Encoder;
import com.github.binserde.utils.ArgumentUtils;

import java.io.IOException;
import java.lang.reflect.Field;

import static com.github.binserde.metadata.DataType.OBJECT;

public class FieldInfo {

    public static final byte NO_VERSION = -1;
    public static final byte NO_TAG = -1;

    private final String name;
    private final DataType dataType;
    private final boolean primitive;
    private final short classIdentifier;

    private Field field;

    public static FieldInfo create(Field field) {
        ArgumentUtils.requireNonNull(field);
        DataType dataType = DataTypes.getDataType(field.getType());
        short identifier = dataType == OBJECT ? SerializerFactory.getInstance().getIdentifier(field.getType()) : -1;
        FieldInfo fieldInfo = new FieldInfo(field.getName(), dataType, field.getType().isPrimitive(), identifier);
        fieldInfo.field = field;
        return fieldInfo;
    }

    public static FieldInfo create(Decoder decoder) throws IOException {
        ArgumentUtils.requireNonNull(decoder);
        FieldInfo fieldInfo = new FieldInfo(decoder.readString(), DataType.fromId(decoder.readByte()), decoder.readBoolean(), decoder.readShort());
        return fieldInfo;
    }

    private FieldInfo(String name, DataType dataType, boolean primitive, short classIdentifier) {
        ArgumentUtils.requireNonNull(name);
        this.name = name;
        this.dataType = dataType;
        this.primitive = primitive;
        this.classIdentifier = classIdentifier;
    }

    public String getName() {
        return name;
    }

    public DataType getDataType() {
        return dataType;
    }

    public boolean isPrimitive() {
        return primitive;
    }

    public short getClassIdentifier() {
        return classIdentifier;
    }

    public Field getField() {
        return field;
    }

    void store(Encoder encoder) throws IOException {
        encoder.writeString(name);
        encoder.writeByte((byte) dataType.ordinal());
        encoder.writeBoolean(primitive);
        encoder.writeShort(classIdentifier);
    }

    @Override
    public String toString() {
        return "FieldInfo{" +
                "name='" + name + '\'' +
                ", dataType=" + dataType +
                ", primitive=" + primitive +
                ", classIdentifier=" + classIdentifier +
                ", field=" + field +
                '}';
    }
}
