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

import com.github.binserde.dto.Customer;
import com.github.binserde.dto.DtoUtils;
import com.github.binserde.dto.Order;
import com.github.binserde.metadata.ClassInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EncoderDecoderTest {

    private ByteArrayOutputStream outputStream;
    private Encoder encoder;
    private Decoder decoder;

    @BeforeEach
    void setup() {
        outputStream = new ByteArrayOutputStream();
        encoder = new OutputStreamEncoder(outputStream);
    }

    @Test
    void booleans() throws IOException {
        encoder.writeBoolean(false);
        encoder.writeBoolean(true);
        encoder.close();
        createDecoder();
        assertEquals(8, outputStream.size());
        assertEquals(false, decoder.readBoolean());
        assertEquals(true, decoder.readBoolean());
        decoder.close();
    }

    @Test
    void bytes() throws IOException {
        encoder.writeByte((byte) 0);
        encoder.writeByte(Byte.MIN_VALUE);
        encoder.writeByte(Byte.MAX_VALUE);
        encoder.close();
        createDecoder();
        assertEquals(11, outputStream.size());
        assertEquals(0, decoder.readByte());
        assertEquals(Byte.MIN_VALUE, decoder.readByte());
        assertEquals(Byte.MAX_VALUE, decoder.readByte());
    }

    @Test
    void allBytes() throws IOException {
        for (byte value = Byte.MIN_VALUE; value < Byte.MAX_VALUE; value++) {
            encoder.writeByte(value);
        }
        encoder.writeByte(Byte.MAX_VALUE);
        encoder.close();
        createDecoder();
        assertEquals(488, outputStream.size());
        for (byte value = Byte.MIN_VALUE; value < Byte.MAX_VALUE; value++) {
            assertEquals(value, decoder.readByte());
        }
        assertEquals(Byte.MAX_VALUE, decoder.readByte());
    }

    @Test
    void shorts() throws IOException {
        encoder.writeShort((short) 0);
        encoder.writeShort(Short.MIN_VALUE);
        encoder.writeShort(Short.MAX_VALUE);
        encoder.close();
        createDecoder();
        assertEquals(13, outputStream.size());
        assertEquals(0, decoder.readShort());
        assertEquals(Short.MIN_VALUE, decoder.readShort());
        assertEquals(Short.MAX_VALUE, decoder.readShort());
    }

    @Test
    void allShorts() throws IOException {
        int step = 1 + ThreadLocalRandom.current().nextInt(13);
        int iterations = 2 * (Short.MAX_VALUE / step);
        short value = Short.MIN_VALUE;
        for (; iterations-- > 0; value += step) {
            encoder.writeShort(value);
        }
        encoder.writeShort(Short.MAX_VALUE);
        encoder.close();
        createDecoder();
        iterations = 2 * (Short.MAX_VALUE / step);
        value = Short.MIN_VALUE;
        for (; iterations-- > 0; value += step) {
            assertEquals(value, decoder.readShort());
        }
        assertEquals(Short.MAX_VALUE, decoder.readShort());
    }

    @Test
    void integers() throws IOException {
        encoder.writeInteger(0);
        encoder.writeInteger(Integer.MIN_VALUE);
        encoder.writeInteger(Integer.MAX_VALUE);
        encoder.writeInteger(-1);
        encoder.close();
        createDecoder();
        assertEquals(18, outputStream.size());
        assertEquals(0, decoder.readInteger());
        assertEquals(Integer.MIN_VALUE, decoder.readInteger());
        assertEquals(Integer.MAX_VALUE, decoder.readInteger());
        assertEquals(-1, decoder.readInteger());
    }

    @Test
    void allIntegers() throws IOException {
        int step = 10_000 + ThreadLocalRandom.current().nextInt(9999);
        int iterations = 2 * (Integer.MAX_VALUE / step);
        int value = Integer.MIN_VALUE;
        for (; iterations-- > 0; value += step) {
            encoder.writeInteger(value);
        }
        encoder.writeInteger(Integer.MAX_VALUE);
        encoder.close();
        createDecoder();
        iterations = 2 * (Integer.MAX_VALUE / step);
        value = Integer.MIN_VALUE;
        for (; iterations-- > 0; value += step) {
            assertEquals(value, decoder.readInteger());
        }
        assertEquals(Integer.MAX_VALUE, decoder.readInteger());
    }

    @Test
    void longs() throws IOException {
        encoder.writeLong(0L);
        encoder.writeLong(Long.MIN_VALUE);
        encoder.writeLong(Long.MAX_VALUE);
        encoder.writeLong(-1);
        encoder.writeLong(Short.MAX_VALUE);
        encoder.writeLong(Integer.MAX_VALUE);
        encoder.close();
        createDecoder();
        assertEquals(34, outputStream.size());
        assertEquals(0, decoder.readLong());
        assertEquals(Long.MIN_VALUE, decoder.readLong());
        assertEquals(Long.MAX_VALUE, decoder.readLong());
        assertEquals(-1, decoder.readLong());
        assertEquals(Short.MAX_VALUE, decoder.readLong());
        assertEquals(Integer.MAX_VALUE, decoder.readLong());
    }

    @Test
    void allLongs() throws IOException {
        long step = 10_000_000_000_000L + ThreadLocalRandom.current().nextLong(9999999);
        long iterations = 2 * (Long.MAX_VALUE / step);
        long value = Long.MIN_VALUE;
        for (; iterations-- > 0; value += step) {
            encoder.writeLong(value);
        }
        encoder.writeLong(Long.MAX_VALUE);
        encoder.close();
        createDecoder();
        iterations = 2 * (Long.MAX_VALUE / step);
        value = Long.MIN_VALUE;
        for (; iterations-- > 0; value += step) {
            assertEquals(value, decoder.readLong());
        }
        assertEquals(Long.MAX_VALUE, decoder.readLong());
    }

    @Test
    void floats() throws IOException {
        encoder.writeFloat(0f);
        encoder.writeFloat(Float.MIN_VALUE);
        encoder.writeFloat(Float.MAX_VALUE);
        encoder.close();
        createDecoder();
        assertEquals(21, outputStream.size());
        assertEquals(0, decoder.readFloat());
        assertEquals(Float.MIN_VALUE, decoder.readFloat());
        assertEquals(Float.MAX_VALUE, decoder.readFloat());
    }

    @Test
    void doubles() throws IOException {
        encoder.writeDouble(0f);
        encoder.writeDouble(Double.MIN_VALUE);
        encoder.writeDouble(Double.MAX_VALUE);
        encoder.close();
        createDecoder();
        assertEquals(33, outputStream.size());
        assertEquals(0, decoder.readDouble());
        assertEquals(Double.MIN_VALUE, decoder.readDouble());
        assertEquals(Double.MAX_VALUE, decoder.readDouble());
    }

    @Test
    void chars() throws IOException {
        encoder.writeCharacter(' ');
        encoder.writeCharacter('0');
        encoder.writeCharacter('A');
        encoder.writeCharacter('a');
        encoder.close();
        createDecoder();
        assertEquals(10, outputStream.size());
        assertEquals(' ', decoder.readCharacter());
        assertEquals('0', decoder.readCharacter());
        assertEquals('A', decoder.readCharacter());
        assertEquals('a', decoder.readCharacter());
    }

    @Test
    void strings() throws IOException {
        encoder.writeString(null);
        encoder.writeString("");
        encoder.writeString(generateString(3));
        encoder.writeString(generateString(15));
        encoder.writeString(generateString(200));
        encoder.close();
        createDecoder();
        assertEquals(231, outputStream.size());
        assertEquals(null, decoder.readString());
        assertEquals("", decoder.readString());
        assertEquals(generateString(3), decoder.readString());
        assertEquals(generateString(15), decoder.readString());
        assertEquals(generateString(200), decoder.readString());
    }

    @Test
    void classes() throws IOException {
        DtoUtils.init();
        encoder.writeClass(ClassInfo.create(Customer.class));
        encoder.writeClass(ClassInfo.create(Order.class));
        encoder.close();
        createDecoder();
        assertEquals(116, outputStream.size());
        ClassInfo classInfo = decoder.readClass();
        assertEquals(100, classInfo.getIdentifier());
        assertEquals(5, classInfo.getFields().size());
        classInfo = decoder.readClass();
        assertEquals(200, classInfo.getIdentifier());
        assertEquals(3, classInfo.getFields().size());
    }

    private void createDecoder() {
        decoder = new InputStreamDecoder(new ByteArrayInputStream(outputStream.toByteArray()));
    }

    private String generateString(int length) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < length; index++) {
            builder.append((char) ((short) 'A' + (index % 26)));
        }
        return builder.toString();
    }

}