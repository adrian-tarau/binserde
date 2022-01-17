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

/**
 * An enum used to identify data types supported by the library.
 */
public enum DataType {

    OBJECT(0, (Category.OTHER)),

    BOOLEAN(1, Category.OTHER),
    CHARACTER(2, Category.OTHER),
    ENUM(3, Category.OTHER),
    BYTE(4, Category.NUMBER),
    SHORT(5, Category.NUMBER),
    INTEGER(6, Category.NUMBER),
    LONG(7, Category.NUMBER),
    FLOAT(8, Category.NUMBER),
    DOUBLE(9, Category.NUMBER),
    STRING(10, Category.OTHER),
    BIG_INTEGER(11, Category.NUMBER),
    BIG_DECIMAL(12, Category.NUMBER),

    LIST(20, Category.COLLECTION),
    SET(21, Category.COLLECTION),
    SORTED_SET(22, Category.COLLECTION),
    QUEUE(23, Category.COLLECTION),
    DEQUE(24, Category.COLLECTION),
    MAP(25, Category.COLLECTION),
    SORTED_MAP(26, Category.COLLECTION),

    TIME_DURATION(30, Category.TIME),
    TIME_INSTANT(31, Category.TIME),
    TIME_LOCAL_DATE(32, Category.TIME),
    TIME_LOCAL_TIME(33, Category.TIME),
    TIME_LOCAL_DATETIME(34, Category.TIME),
    TIME_OFFSET_DATETIME(35, Category.TIME),
    TIME_ZONED_DATETIME(36, Category.TIME),
    TIME_PERIOD(37, Category.TIME),
    TIME_ZONE_ID(38, Category.TIME),
    TIME_ZONE_OFFSET(39, Category.TIME);

    public enum Category {
        NUMBER,
        COLLECTION,
        TIME,
        OTHER
    }

    private Category category;
    private byte id;

    DataType(int id, Category category) {
        this.id = (byte) id;
        this.category = category;
    }

    public byte getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public static DataType fromId(byte value) {
        return DataType.values()[value];
    }
}
