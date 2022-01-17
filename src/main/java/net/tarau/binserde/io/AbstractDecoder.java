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

package net.tarau.binserde.io;

import net.tarau.binserde.SerializerFactory;
import net.tarau.binserde.deserializer.DeserializerException;
import net.tarau.binserde.metadata.ClassInfo;
import net.tarau.binserde.metadata.DataTypes;
import net.tarau.binserde.metadata.Registry;

import java.io.IOException;
import java.util.StringJoiner;

import static net.tarau.binserde.metadata.DataTypes.*;

public abstract class AbstractDecoder implements Decoder {

    private final SerializerFactory factory = SerializerFactory.getInstance();
    private final Registry registry = SerializerFactory.getInstance().getRegistry();

    private final byte[] buffer = new byte[IOUtils.CHUNK_SIZE];
    private final static byte[] EMPTY_BYTES = new byte[0];
    private int position = IOUtils.RESERVED_HEADER;
    private int length = 0;
    private byte version;

    @Override
    public byte getVersion() throws IOException {
        require(1);
        return version;
    }

    @Override
    public byte peekTag() throws IOException {
        require(1);
        return buffer[position];
    }

    @Override
    public final boolean readBoolean() throws IOException {
        byte tag = readRawByte();
        if (DataTypes.isBoolean(tag)) {
            return DataTypes.getBoolean(tag);
        } else {
            throw new DecoderException("Cannot decode boolean, tag " + DataTypes.tagToString(tag));
        }
    }

    @Override
    public byte readTag() throws IOException {
        return readRawByte();
    }

    @Override
    public final byte readByte() throws IOException {
        byte tag = readRawByte();
        if (DataTypes.isSmallPositiveInteger(tag)) {
            return DataTypes.readSmallPositiveInteger(tag);
        } else if (DataTypes.isSmallNegativeInteger(tag)) {
            return DataTypes.readSmallNegativeInteger(tag);
        } else if (DataTypes.isShort(tag)) {
            return (byte) readRawShort();
        } else {
            throw new DecoderException("Cannot decode byte, tag " + DataTypes.tagToString(tag));
        }
    }

    @Override
    public final short readShort() throws IOException {
        byte tag = readRawByte();
        if (DataTypes.isSmallPositiveInteger(tag)) {
            return DataTypes.readSmallPositiveInteger(tag);
        } else if (DataTypes.isSmallNegativeInteger(tag)) {
            return DataTypes.readSmallNegativeInteger(tag);
        } else if (DataTypes.isShort(tag)) {
            return readRawShort();
        } else {
            throw new DecoderException("Cannot decode short, tag " + DataTypes.tagToString(tag));
        }
    }

    @Override
    public final int readInteger() throws IOException {
        byte tag = readRawByte();
        if (DataTypes.isSmallPositiveInteger(tag)) {
            return DataTypes.readSmallPositiveInteger(tag);
        } else if (DataTypes.isSmallNegativeInteger(tag)) {
            return DataTypes.readSmallNegativeInteger(tag);
        } else if (DataTypes.isShort(tag)) {
            return readRawShort();
        } else if (DataTypes.isInteger(tag)) {
            return readRawInteger();
        } else {
            throw new DecoderException("Cannot decode integer, tag " + DataTypes.tagToString(tag));
        }
    }

    @Override
    public final long readLong() throws IOException {
        byte tag = readRawByte();
        if (DataTypes.isSmallPositiveInteger(tag)) {
            return DataTypes.readSmallPositiveInteger(tag);
        } else if (DataTypes.isSmallNegativeInteger(tag)) {
            return DataTypes.readSmallNegativeInteger(tag);
        } else if (DataTypes.isShort(tag)) {
            return readRawShort();
        } else if (DataTypes.isInteger(tag)) {
            return readRawInteger();
        } else if (DataTypes.isLong(tag)) {
            return readRawLong();
        } else {
            throw new DecoderException("Cannot decode long, tag " + DataTypes.tagToString(tag));
        }
    }

    @Override
    public final float readFloat() throws IOException {
        byte tag = readRawByte();
        if (DataTypes.isFloat(tag)) {
            return Float.intBitsToFloat(readRawInteger());
        } else if (DataTypes.isDouble(tag)) {
            return Double.doubleToRawLongBits(readRawLong());
        } else {
            throw new DecoderException("Cannot decode float, tag " + DataTypes.tagToString(tag));
        }
    }

    @Override
    public final double readDouble() throws IOException {
        byte tag = readRawByte();
        if (DataTypes.isDouble(tag)) {
            return Double.longBitsToDouble(readRawLong());
        } else if (DataTypes.isFloat(tag)) {
            return Float.intBitsToFloat(readRawInteger());
        } else {
            throw new DecoderException("Cannot decode double, tag " + DataTypes.tagToString(tag));
        }
    }

    @Override
    public char readCharacter() throws IOException {
        return (char) readShort();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Enum<E>> E readEnum() throws IOException {
        byte tag = readRawByte();
        if (tag == NULL) {
            return null;
        } else if (tag == (BASE | BASE_ENUM)) {
            Class<Enum<?>> enumClass = (Class<Enum<?>>) factory.getClass(readRawShort());
            int ordinal = readInteger();
            return (E) enumClass.getEnumConstants()[ordinal];
        } else {
            throw new DecoderException("Cannot decode enum, tag " + DataTypes.tagToString(tag));
        }
    }

    @Override
    public final String readString() throws IOException {
        byte tag = readRawByte();
        if (tag == NULL) {
            return null;
        } else if (tag == (BASE | BASE_STRING)) {
            int length = readInteger();
            return readRawString(length);
        } else {
            throw new DecoderException("Cannot decode string, tag " + DataTypes.tagToString(tag));
        }
    }

    @Override
    public ClassInfo readClass() throws IOException {
        byte tag = readRawByte();
        if (DataTypes.isClass(tag)) {
            if (DataTypes.isClassInfo(tag)) {
                return ClassInfo.create(this);
            } else {
                String signature = readString();
                return registry.load(signature);
            }
        } else {
            throw new DecoderException("Cannot decode class, tag " + DataTypes.tagToString(tag));
        }
    }

    @Override
    public byte[] readBytes() throws IOException {
        byte tag = readRawByte();
        if (tag == NULL) {
            return null;
        } else {
            int length = readInteger();
            require(length);
            byte[] bytes = new byte[length];
            for (int index = 0; index < length; index++) {
                bytes[index] = buffer[position++];
            }
            return bytes;
        }
    }

    abstract int read(byte[] buffer, int offset, int length) throws IOException;

    private void require(int bytes) throws IOException {
        boolean shouldRead = length == 0 || position + bytes > length + IOUtils.RESERVED_HEADER;
        if (shouldRead) {
            int read = read(buffer, 0, IOUtils.RESERVED_HEADER);
            if (read != IOUtils.RESERVED_HEADER) throw new DeserializerException("Corrupted data, invalid block header");
            length = IOUtils.RESERVED_HEADER;
            position = 0;
            for (int index = 0; index < IOUtils.HEADER.length; index++) {
                if (readRawByte() != IOUtils.HEADER[index]) throw new DeserializerException("Invalid block header signature");
            }
            int storedLength = readRawShort();
            int storedHash = readRawInteger();
            version = readRawByte();
            length = storedLength;
            int dataLength = read(buffer, IOUtils.RESERVED_HEADER, length);
            if (storedLength != dataLength) throw new DeserializerException("Corrupted data, invalid block length");
            int dataHash = IOUtils.hashCode(buffer, IOUtils.RESERVED_HEADER, length);
            if (storedHash != dataHash) throw new DeserializerException("Corrupted data, invalid block hash");
        }
    }

    private byte readRawByte() throws IOException {
        require(1);
        return buffer[position++];
    }

    private short readRawShort() throws IOException {
        require(2);
        int b0 = buffer[position++] & 0xFF;
        int b1 = buffer[position++] & 0xFF;
        return (short) ((b0 << 8) + b1);
    }

    private int readRawInteger() throws IOException {
        require(4);
        int b0 = buffer[position++] & 0xFF;
        int b1 = buffer[position++] & 0xFF;
        int b2 = buffer[position++] & 0xFF;
        int b3 = buffer[position++] & 0xFF;
        return (b0 << 24) + (b1 << 16) + (b2 << 8) + b3;
    }

    private long readRawLong() throws IOException {
        require(4);
        long b0 = buffer[position++] & 0xFF;
        long b1 = buffer[position++] & 0xFF;
        long b2 = buffer[position++] & 0xFF;
        long b3 = buffer[position++] & 0xFF;
        long b4 = buffer[position++] & 0xFF;
        long b5 = buffer[position++] & 0xFF;
        long b6 = buffer[position++] & 0xFF;
        long b7 = buffer[position++] & 0xFF;
        return (b0 << 56) + (b1 << 48) + (b2 << 40) + (b3 << 32) + (b4 << 24) + (b5 << 16) + (b6 << 8) + b7;
    }

    private byte[] readRawBytes(int length) throws IOException {
        if (length == 0) return EMPTY_BYTES;
        require(length);
        byte[] bytes = new byte[length];
        for (int index = 0; index < bytes.length; index++) {
            bytes[index] = buffer[position++];
        }
        return bytes;
    }

    private String readRawString(int length) throws IOException {
        byte[] bytes = readRawBytes(length);
        return new String(bytes);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AbstractDecoder.class.getSimpleName() + "[", "]")
                .add("position=" + position)
                .add("length=" + length)
                .toString();
    }
}
