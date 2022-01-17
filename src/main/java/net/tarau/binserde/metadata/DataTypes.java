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

package net.tarau.binserde.metadata;

import net.tarau.binserde.utils.ArgumentUtils;
import net.tarau.binserde.utils.ClassUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.*;

public class DataTypes {

    public static final byte SMALL_INT_POSITIVE_MASK = (byte) 0x80;
    public static final byte SMALL_INT_POSITIVE_VALUE_MASK = (byte) 0x7f;
    public static final byte SMALL_INT_NEGATIVE_MASK = (byte) 0xF0;
    public static final byte SMALL_INT_NEGATIVE_VALUE_MASK = 0x0F;
    public static final long LARGEST_SMALL_POSITIVE = 127;
    public static final long LARGEST_SMALL_NEGATIVE_VALUE = -15;

    // 3 bit encoding for most common small data types
    // remaining 4 bytes are used to store the size, where applies
    public static final byte NULL = (byte) 0x80;
    public static final byte BOOLEAN = (byte) (0x80 + 0x10);
    public static final byte BASE = (byte) (0x80 + 0x60);
    public static final byte OBJECT = (byte) (0x80 + 0x70);

    // 4 byte codes for larger class metadata,  primitives & collections
    public static final byte BASE_INT16 = 0x00;
    public static final byte BASE_INT32 = 0x01;
    public static final byte BASE_INT64 = 0x02;
    public static final byte BASE_FLOAT32 = 0x03;
    public static final byte BASE_FLOAT64 = 0x04;
    public static final byte BASE_ENUM = 0x05;
    public static final byte BASE_STRING = 0x06;
    public static final byte BASE_MAP = 0x06;
    public static final byte BASE_ARRAY = 0x07;
    public static final byte BASE_BIN = 0x09;
    public static final byte BASE_OBJECT = 0x0D;
    public static final byte BASE_CLASS_INFO = 0x0E;
    public static final byte BASE_CLASS_SIGNATURE = 0x0F;

    public static boolean isSmallPositiveInteger(byte value) {
        return (value & SMALL_INT_POSITIVE_MASK) == 0;
    }

    public static boolean isSmallNegativeInteger(byte value) {
        return (value & SMALL_INT_NEGATIVE_MASK) == SMALL_INT_NEGATIVE_MASK;
    }

    public static boolean isShort(byte value) {
        return value == (BASE | BASE_INT16);
    }

    public static boolean isInteger(byte value) {
        return value == (BASE | BASE_INT32);
    }

    public static boolean isLong(byte value) {
        return value == (BASE | BASE_INT64);
    }

    public static boolean isFloat(byte value) {
        return value == (BASE | BASE_FLOAT32);
    }

    public static boolean isDouble(byte value) {
        return value == (BASE | BASE_FLOAT64);
    }

    public static boolean isClass(byte value) {
        return isClassInfo(value) || isClassSignature(value);
    }

    public static boolean isClassInfo(byte value) {
        return value == (BASE | BASE_CLASS_INFO);
    }

    public static boolean isClassSignature(byte value) {
        return value == (BASE | BASE_CLASS_SIGNATURE);
    }

    public static boolean isBoolean(byte value) {
        return (value & SMALL_INT_NEGATIVE_MASK) == BOOLEAN;
    }

    public static boolean getBoolean(byte value) {
        return (value & SMALL_INT_NEGATIVE_VALUE_MASK) != 0;
    }

    public static byte readSmallPositiveInteger(byte value) {
        return (byte) (value & SMALL_INT_POSITIVE_VALUE_MASK);
    }

    public static byte readSmallNegativeInteger(byte value) {
        return (byte) -(value & SMALL_INT_NEGATIVE_VALUE_MASK);
    }

    public static String tagToString(byte tag) {
        if (isSmallPositiveInteger(tag)) {
            return "small positive integer";
        } else if (isSmallNegativeInteger(tag)) {
            return "small negative integer";
        } else {
            return "more";
        }
    }

    public static DataType getDataType(Class<?> clazz) {
        ArgumentUtils.requireNonNull(clazz);
        DataType dataType = class2DataType.get(clazz);
        if (dataType != null) {
            return dataType;
        } else if (ClassUtils.isSubclass(clazz, Collection.class)) {
            if (ClassUtils.isSubclass(clazz, List.class)) {
                return DataType.LIST;
            } else if (ClassUtils.isSubclass(clazz, Set.class)) {
                if (ClassUtils.isSubclass(clazz, SortedSet.class)) {
                    return DataType.SORTED_SET;
                } else {
                    return DataType.SET;
                }
            } else if (ClassUtils.isSubclass(clazz, Queue.class)) {
                if (ClassUtils.isSubclass(clazz, Deque.class)) {
                    return DataType.DEQUE;
                } else {
                    return DataType.QUEUE;
                }
            } else {
                throw new MetadataException("Unknown collection class " + clazz.getName());
            }
        } else if (ClassUtils.isSubclass(clazz, Map.class)) {
            if (ClassUtils.isSubclass(clazz, SortedMap.class)) {
                return DataType.SORTED_MAP;
            } else {
                return DataType.MAP;
            }
        } else if (clazz.isEnum()) {
            return DataType.ENUM;
        } else {
            return DataType.OBJECT;
        }
    }

    private static final Map<Class<?>, DataType> class2DataType = new HashMap<>();

    static {
        class2DataType.put(Boolean.class, DataType.BOOLEAN);
        class2DataType.put(boolean.class, DataType.BOOLEAN);
        class2DataType.put(Byte.class, DataType.BYTE);
        class2DataType.put(byte.class, DataType.BYTE);
        class2DataType.put(Short.class, DataType.SHORT);
        class2DataType.put(short.class, DataType.SHORT);
        class2DataType.put(Integer.class, DataType.INTEGER);
        class2DataType.put(int.class, DataType.INTEGER);
        class2DataType.put(Long.class, DataType.LONG);
        class2DataType.put(long.class, DataType.LONG);
        class2DataType.put(Float.class, DataType.FLOAT);
        class2DataType.put(float.class, DataType.FLOAT);
        class2DataType.put(Double.class, DataType.DOUBLE);
        class2DataType.put(double.class, DataType.DOUBLE);
        class2DataType.put(Character.class, DataType.CHARACTER);
        class2DataType.put(char.class, DataType.CHARACTER);
        class2DataType.put(String.class, DataType.STRING);
        class2DataType.put(BigInteger.class, DataType.BIG_INTEGER);
        class2DataType.put(BigDecimal.class, DataType.BIG_DECIMAL);

        class2DataType.put(Duration.class, DataType.TIME_DURATION);
        class2DataType.put(Instant.class, DataType.TIME_INSTANT);
        class2DataType.put(LocalDate.class, DataType.TIME_LOCAL_DATE);
        class2DataType.put(LocalTime.class, DataType.TIME_LOCAL_TIME);
        class2DataType.put(LocalDateTime.class, DataType.TIME_LOCAL_DATETIME);
        class2DataType.put(OffsetDateTime.class, DataType.TIME_OFFSET_DATETIME);
        class2DataType.put(ZonedDateTime.class, DataType.TIME_ZONED_DATETIME);
        class2DataType.put(Period.class, DataType.TIME_PERIOD);
        class2DataType.put(ZoneId.class, DataType.TIME_ZONE_ID);
        class2DataType.put(ZoneOffset.class, DataType.TIME_ZONE_OFFSET);

    }
}
