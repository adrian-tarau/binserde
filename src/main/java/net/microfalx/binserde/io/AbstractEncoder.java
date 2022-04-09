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

package net.microfalx.binserde.io;

import net.microfalx.binserde.SerializerFactory;
import net.microfalx.binserde.metadata.ClassInfo;
import net.microfalx.binserde.metadata.DataTypes;
import net.microfalx.binserde.metadata.Registry;
import net.microfalx.binserde.utils.ArgumentUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

import static net.microfalx.binserde.metadata.DataTypes.*;

public abstract class AbstractEncoder implements Encoder {

    private static final byte VERSION = 1;

    private final byte[] buffer = new byte[IOUtils.CHUNK_SIZE];
    private int position = IOUtils.RESERVED_HEADER;
    private final SerializerFactory factory = SerializerFactory.getInstance();
    private final Registry registry = SerializerFactory.getInstance().getRegistry();

    @Override
    public final void writeBoolean(boolean value) throws IOException {
        writeRawByte((byte) (DataTypes.BOOLEAN | (value ? 1 : 0)));
    }

    @Override
    public void writeEnum(Enum<?> value) throws IOException {
        if (value == null) {
            writeNull();
        } else {
            writeTag((byte) (BASE | BASE_ENUM));
            writeRawShort(factory.getIdentifier(value.getClass()));
            writeInteger(value.ordinal());
        }
    }

    @Override
    public final void writeByte(byte value) throws IOException {
        if (value >= 0) {
            writeRawByte(value);
        } else if (value >= LARGEST_SMALL_NEGATIVE_VALUE) {
            value = (byte) -value;
            writeRawByte((byte) (DataTypes.SMALL_INT_NEGATIVE_MASK | value));
        } else {
            writeRawByte((byte) (BASE | BASE_INT16));
            writeRawShort(value);
        }
    }

    @Override
    public final void writeShort(short value) throws IOException {
        if (value >= 0 && value <= LARGEST_SMALL_POSITIVE) {
            writeRawByte((byte) value);
        } else if (value < 0 && value >= LARGEST_SMALL_NEGATIVE_VALUE) {
            value = (byte) -value;
            writeRawByte((byte) (DataTypes.SMALL_INT_NEGATIVE_MASK | value));
        } else {
            writeRawByte((byte) (BASE | BASE_INT16));
            writeRawShort(value);
        }
    }

    @Override
    public final void writeInteger(int value) throws IOException {
        if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            writeShort((short) value);
        } else {
            writeRawByte((byte) (BASE | BASE_INT32));
            writeRawInteger(value);
        }
    }

    @Override
    public final void writeLong(long value) throws IOException {
        if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
            writeInteger((int) value);
        } else {
            writeRawByte((byte) (BASE | BASE_INT64));
            writeRawLong(value);
        }
    }

    @Override
    public final void writeFloat(float value) throws IOException {
        writeRawByte((byte) (BASE | BASE_FLOAT32));
        writeRawInteger(Float.floatToIntBits(value));
    }

    @Override
    public final void writeDouble(double value) throws IOException {
        writeRawByte((byte) (BASE | BASE_FLOAT64));
        writeRawLong(Double.doubleToLongBits(value));
    }

    @Override
    public void writeCharacter(char value) throws IOException {
        writeShort((short) value);
    }

    @Override
    public final void writeString(String value) throws IOException {
        if (value == null) {
            writeRawByte(NULL);
        } else {
            byte[] data = value.getBytes(StandardCharsets.UTF_8);
            writeRawByte((byte) (BASE | BASE_STRING));
            writeInteger(value.length());
            writeRawBytes(data);
        }
    }

    @Override
    public void writeClass(ClassInfo clazz) throws IOException {
        ArgumentUtils.requireNonNull(clazz);
        boolean storeInfo = true;
        if (registry != null) {
            String signature = registry.store(clazz);
            if (signature != null) {
                writeRawByte((byte) (BASE | BASE_CLASS_SIGNATURE));
                writeString(signature);
            }
            storeInfo = signature == null;
        }
        if (storeInfo) {
            writeRawByte((byte) (BASE | BASE_CLASS_INFO));
            clazz.store(this);
        }
    }

    @Override
    public void writeBytes(byte[] value) throws IOException {
        if (value == null) {
            writeNull();
        } else {
            writeRawByte((byte) (BASE | BASE_BIN));
            writeInteger(value.length);
            writeRawBytes(value);
        }
    }

    @Override
    public void writeNull() throws IOException {
        writeRawByte(NULL);
    }

    @Override
    public void writeTag(byte tag) throws IOException {
        writeRawByte(tag);
    }

    abstract void write(byte[] buffer, int offset, int length) throws IOException;

    protected final void flush() throws IOException {
        short length = (short) (position - IOUtils.RESERVED_HEADER);
        short totalLength = (short) position;
        int hash = IOUtils.hashCode(buffer, IOUtils.RESERVED_HEADER, length);
        position = 0;
        for (int index = 0; index < IOUtils.HEADER.length; index++) {
            buffer[position++] = IOUtils.HEADER[index];
        }
        writeRawShort(length);
        writeRawInteger(hash);
        writeRawByte(VERSION);
        write(buffer, 0, totalLength);
    }

    private void require(int required) throws IOException {
        if (IOUtils.CHUNK_SIZE - position < required) {
            flush();
        }
    }

    private void writeRawByte(byte value) throws IOException {
        require(1);
        buffer[position++] = value;
    }

    private void writeRawShort(short value) throws IOException {
        require(2);
        buffer[position++] = (byte) (value >> 8);
        buffer[position++] = (byte) (value >> 0);
    }

    private void writeRawInteger(int value) throws IOException {
        require(4);
        buffer[position++] = (byte) (value >>> 24);
        buffer[position++] = (byte) (value >> 16);
        buffer[position++] = (byte) (value >>> 8);
        buffer[position++] = (byte) (value >>> 0);
    }

    private void writeRawLong(long value) throws IOException {
        require(8);
        buffer[position++] = (byte) (value >>> 56);
        buffer[position++] = (byte) (value >>> 48);
        buffer[position++] = (byte) (value >>> 40);
        buffer[position++] = (byte) (value >>> 32);
        buffer[position++] = (byte) (value >>> 24);
        buffer[position++] = (byte) (value >> 16);
        buffer[position++] = (byte) (value >>> 8);
        buffer[position++] = (byte) (value >>> 0);
    }

    private void writeRawBytes(byte[] data) throws IOException {
        require(data.length);
        for (byte _byte : data) {
            buffer[position++] = _byte;
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AbstractEncoder.class.getSimpleName() + "[", "]").add("position=" + position).toString();
    }
}
