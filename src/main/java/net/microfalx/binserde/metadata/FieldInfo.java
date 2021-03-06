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

package net.microfalx.binserde.metadata;

import net.microfalx.binserde.SerializerFactory;
import net.microfalx.binserde.annotation.Tag;
import net.microfalx.binserde.io.Decoder;
import net.microfalx.binserde.io.Encoder;
import net.microfalx.binserde.utils.ArgumentUtils;

import java.io.IOException;
import java.lang.reflect.Field;

import static net.microfalx.binserde.metadata.DataType.OBJECT;

/**
 * Holds information about a field.
 * <p>
 * Each class is made out of fields and each field has a unique name, a data type and a class identifier (when the value
 * is not a "simple" type.
 */
public class FieldInfo {

    public static final byte NO_VERSION = -1;
    public static final byte NO_TAG = -1;

    private final String name;
    private final DataType dataType;
    private final boolean primitive;
    private final short classIdentifier;
    private final short tag;

    private Field field;

    public static FieldInfo create(Field field) {
        ArgumentUtils.requireNonNull(field);
        DataType dataType = DataTypes.getDataType(field.getType());
        short identifier = dataType == OBJECT ? SerializerFactory.getInstance().getIdentifier(field.getType()) : -1;
        short tag = field.getAnnotation(Tag.class) != null ? field.getAnnotation(Tag.class).value() : NO_TAG;
        FieldInfo fieldInfo = new FieldInfo(field.getName(), dataType, field.getType().isPrimitive(), identifier, tag);
        fieldInfo.field = field;
        return fieldInfo;
    }

    public static FieldInfo create(Decoder decoder) throws IOException {
        ArgumentUtils.requireNonNull(decoder);
        FieldInfo fieldInfo = new FieldInfo(decoder.readString(), DataType.fromId(decoder.readByte()),
                decoder.readBoolean(), decoder.readShort(), decoder.readShort());
        return fieldInfo;
    }

    private FieldInfo(String name, DataType dataType, boolean primitive, short classIdentifier, short tag) {
        ArgumentUtils.requireNonNull(name);
        ArgumentUtils.requireNonNull(dataType);
        this.name = name;
        this.dataType = dataType;
        this.primitive = primitive;
        this.classIdentifier = classIdentifier;
        this.tag = tag;
    }

    /**
     * Returns the field name.
     *
     * @return a non-empty String
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the data type.
     *
     * @return a non-null Enum
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Returns whether the data type represents a primitive.
     * <p>
     * It applies only to build in Java data types (byte/short/int/long, etc).
     *
     * @return {@code true} if a primitive, {@code false} otherwise
     */
    public boolean isPrimitive() {
        return primitive;
    }

    /**
     * Returns the class identifier.
     * <p>
     * It applies only to fields with data type {@link DataType#OBJECT}
     *
     * @return the class identifier, 0 if it does not apply
     */
    public short getClassIdentifier() {
        return classIdentifier;
    }

    /**
     * Returns the tag associated with the field.
     * <p>
     * Un the absence of tags, fields are resolved by field name which does not support renaming.
     *
     * @return the tag
     */
    public short getTag() {
        return tag;
    }

    /**
     * Return a cached reflection field.
     *
     * @return a non-null instance
     */
    public Field getField() {
        return field;
    }

    /**
     * Stores field information in the stream.
     * <p>
     * The method is called when class information is stored in the stream.
     *
     * @param encoder the encoder
     * @throws IOException if an I/O error occurs
     */
    void store(Encoder encoder) throws IOException {
        encoder.writeString(name);
        encoder.writeByte((byte) dataType.ordinal());
        encoder.writeBoolean(primitive);
        encoder.writeShort(classIdentifier);
        encoder.writeShort(tag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldInfo fieldInfo = (FieldInfo) o;

        if (primitive != fieldInfo.primitive) return false;
        if (classIdentifier != fieldInfo.classIdentifier) return false;
        if (tag != fieldInfo.tag) return false;
        if (!name.equals(fieldInfo.name)) return false;
        return dataType == fieldInfo.dataType;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + dataType.hashCode();
        result = 31 * result + (primitive ? 1 : 0);
        result = 31 * result + (int) classIdentifier;
        result = 31 * result + (int) tag;
        return result;
    }

    @Override
    public String toString() {
        return "FieldInfo{" + "name='" + name + '\'' + ", dataType=" + dataType + ", primitive=" + primitive + ", classIdentifier=" + classIdentifier + ", field=" + field + '}';
    }
}
