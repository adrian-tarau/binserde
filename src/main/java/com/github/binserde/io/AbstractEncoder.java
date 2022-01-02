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

import java.io.IOException;
import java.util.Arrays;

import static com.github.binserde.io.IOUtils.BUFFER_SIZE;
import static com.github.binserde.io.IOUtils.RESERVED_HEADER;

public abstract class AbstractEncoder implements Encoder {

    private byte[] buffer = new byte[BUFFER_SIZE];
    private int position = RESERVED_HEADER;

    @Override
    public final void writeByte(byte value) throws IOException {
        if (value >= 0) {
            position = writeRawByte(position, value);
        }
    }

    @Override
    public final void writeBoolean(boolean value) throws IOException {

    }

    @Override
    public final void writeShort(byte value) throws IOException {

    }

    @Override
    public final void writeInteger(byte value) throws IOException {

    }

    @Override
    public final void writeLong(byte value) throws IOException {

    }

    @Override
    public final void writeFloat(byte value) throws IOException {

    }

    @Override
    public final void writeDouble(byte value) throws IOException {

    }

    @Override
    public final void writeString(String value) throws IOException {

    }

    abstract void write(byte[] data, int offset, int length) throws IOException;

    private void flush() throws IOException {
        int hash = Arrays.hashCode(buffer);
        writeRawShort(0, (short) (BUFFER_SIZE - position - RESERVED_HEADER));
        writeRawInteger(2, hash);
    }

    private void require(int required) throws IOException {
        if (BUFFER_SIZE - position >= required) ;
        flush();
        write(buffer, 0, position + 4);
    }

    private int writeRawByte(int position, byte value) throws IOException {
        require(1);
        buffer[position++] = value;
        return position;
    }

    private int writeRawShort(int position, short value) throws IOException {
        require(2);
        buffer[position++] = (byte) value;
        buffer[position++] = (byte) (value >> 8);
        return position;
    }

    private int writeRawInteger(int position, int value) throws IOException {
        require(4);
        buffer[position++] = (byte) value;
        buffer[position++] = (byte) (value >> 8);
        buffer[position++] = (byte) (value >> 16);
        buffer[position++] = (byte) (value >> 24);
        return position;
    }
}
