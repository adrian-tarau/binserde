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

package com.github.binserde.io;

import com.github.binserde.SerializerFactory;
import com.github.binserde.metadata.ClassInfo;
import com.github.binserde.metadata.DataTypes;
import com.github.binserde.metadata.Registry;
import com.github.binserde.utils.ArgumentUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.github.binserde.io.IOUtils.BUFFER_SIZE;
import static com.github.binserde.io.IOUtils.RESERVED_HEADER;
import static com.github.binserde.metadata.DataTypes.*;

public abstract class AbstractEncoder implements Encoder {

    private final byte[] buffer = new byte[BUFFER_SIZE];
    private int position = RESERVED_HEADER;
    private Registry registry;

    public AbstractEncoder() {
        registry = SerializerFactory.getInstance().getRegistry();
    }

    @Override
    public final void writeBoolean(boolean value) throws IOException {
        writeRawByte((byte) (DataTypes.BOOLEAN | (value ? 1 : 0)));
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
            if (data.length <= LARGEST_SMALL_LENGTH) {
                writeRawByte((byte) (STRING | data.length));
                writeRawBytes(data);
            } else {
                writeRawByte((byte) (BASE | BASE_STRING16));
                writeRawShort((short) value.length());
                writeRawBytes(data);
            }
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
    public void writeNull() throws IOException {
        writeByte(NULL);
    }

    @Override
    public void writeTag(byte tag) throws IOException {
        writeRawByte(tag);
    }

    abstract void write(byte[] buffer, int offset, int length) throws IOException;

    protected final void flush() throws IOException {
        short length = (short) (position - RESERVED_HEADER);
        short totalLength = (short) position;
        int hash = IOUtils.hashCode(buffer, RESERVED_HEADER, length);
        position = 0;
        writeRawShort(length);
        writeRawInteger(hash);
        write(buffer, 0, totalLength);
    }

    private void require(int required) throws IOException {
        if (BUFFER_SIZE - position < required) {
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
}
