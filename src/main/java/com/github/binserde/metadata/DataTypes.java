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

public class DataTypes {

    public static final byte SMALL_INT_POSITIVE_MASK = (byte) 0x80;
    public static final byte SMALL_INT_POSITIVE_VALUE_MASK = (byte) 0x7f;
    public static final byte SMALL_INT_NEGATIVE_MASK = (byte) 0xF0;
    public static final byte SMALL_INT_NEGATIVE_VALUE_MASK = 0x0F;

    // 3 bit encoding for most common small data types
    // remaining 4 bytes are used to store the size, where applies
    public static final byte NULL = (byte) 0x80;
    public static final byte BOOLEAN = (byte) (0x80 + 0x01);
    public static final byte MAP = (byte) (0x80 + 0x02);
    public static final byte ARRAY = (byte) (0x80 + 0x03);
    public static final byte STRING = (byte) (0x80 + 0x04);
    public static final byte BASE = (byte) (0x80 + 0x05);
    public static final byte OBJECT = (byte) (0x80 + 0x06);

    // 4 byte codes for larger class metadata,  primitives & collections
    public static final byte BASE_CLASS = 0x00;
    public static final byte BASE_INT16 = 0x01;
    public static final byte BASE_INT32 = 0x02;
    public static final byte BASE_INT64 = 0x03;
    public static final byte BASE_FLOAT32 = 0x04;
    public static final byte BASE_FLOAT64 = 0x05;
    public static final byte BASE_STRING16 = 0x06;
    public static final byte BASE_STRING32 = 0x07;
    public static final byte BASE_MAP16 = 0x08;
    public static final byte BASE_MAP32 = 0x09;
    public static final byte BASE_ARRAY16 = 0x0A;
    public static final byte BASE_ARRAY32 = 0x0B;
    public static final byte BASE_BIN16 = 0x0C;
    public static final byte BASE_BIN32 = 0x0D;
    public static final byte BASE_OBJECT = 0x0E;

    // 8 byte codes for base objects
    public static final byte BASE_BOOLEAN = 0x00;
    public static final byte BASE_CHAR = 0x01;
    public static final byte BASE_BYTE = 0x02;
    public static final byte BASE_SHORT = 0x03;
    public static final byte BASE_INTEGER = 0x04;
    public static final byte BASE_LONG = 0x05;
    public static final byte BASE_FLOAT = 0x06;
    public static final byte BASE_DOUBLE = 0x07;
    public static final byte BASE_STRING = 0x08;
    public static final byte BASE_BIG_INTEGER = 0x09;
    public static final byte BASE_BIG_DECIMAL = 0x0A;
    public static final byte BASE_LIST = 0x0B;
    public static final byte BASE_SET = 0x0C;
    public static final byte BASE_SORTED_SET = 0x0D;
    public static final byte BASE_QUEUE = 0x0E;
    public static final byte BASE_DEQUE = 0x0F;
    public static final byte BASE_MAP = 0x10;
    public static final byte BASE_SORTED_MAP = 0x11;

    public static final byte BASE_TIME_DURATION = 0x20;
    public static final byte BASE_TIME_INSTANCE = 0x21;
    public static final byte BASE_TIME_LOCAL_DATE = 0x22;
    public static final byte BASE_TIME_LOCAL_TIME = 0x23;
    public static final byte BASE_TIME_LOCAL_DATETIME = 0x24;
    public static final byte BASE_TIME_OFFSET_DATETIME = 0x25;
    public static final byte BASE_TIME_ZONED_DATETIME = 0x26;
    public static final byte BASE_TIME_PERIOD = 0x27;
    public static final byte BASE_TIME_ZONE_ID = 0x28;
    public static final byte BASE_TIME_ZONE_OFFSET = 0x29;

    public boolean isShortPositiveInteger(byte value) {
        return (value & SMALL_INT_POSITIVE_MASK) == 0;
    }

    public boolean isSmallNegativeInteger(byte value) {
        return (value & SMALL_INT_NEGATIVE_MASK) == SMALL_INT_NEGATIVE_MASK;
    }

    public boolean isBoolean(byte value) {
        return (value & SMALL_INT_NEGATIVE_MASK) == BOOLEAN;
    }

    public int readShortPositiveInteger(byte value) {
        return value & SMALL_INT_POSITIVE_VALUE_MASK;
    }

    public int readShortNegativeInteger(byte value) {
        return -(value & SMALL_INT_NEGATIVE_VALUE_MASK);
    }
}
