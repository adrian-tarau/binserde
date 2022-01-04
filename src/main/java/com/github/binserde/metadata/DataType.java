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

public enum DataType {

    BOOLEAN,
    CHARACTER,
    BYTE,
    SHORT,
    INTEGER,
    LONG,
    FLOAT,
    DOUBLE,
    STRING,
    BIG_INTEGER,
    BIG_DECIMAL,

    LIST,
    SET,
    SORTED_SET,
    QUEUE,
    DEQUE,
    MAP,
    SORTED_MAP,

    TIME_DURATION,
    TIME_INSTANT,
    TIME_LOCAL_DATE,
    TIME_LOCAL_TIME,
    TIME_LOCAL_DATETIME,
    TIME_OFFSET_DATETIME,
    TIME_ZONED_DATETIME,
    TIME_PERIOD,
    TIME_ZONE_ID,
    TIME_ZONE_OFFSET,

    OBJECT;

    public static DataType fromId(byte value) {
        return DataType.values()[value];
    }
}
