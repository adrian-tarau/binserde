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

package net.microfalx.binserde.deserializer;

import net.microfalx.binserde.io.Decoder;
import net.microfalx.binserde.metadata.DataType;

import java.io.IOException;
import java.time.*;

public class ReflectionTimeDeserializer extends ReflectionFieldDeserializer {

    public ReflectionTimeDeserializer(ReflectionDeserializer<?> parent) {
        super(parent);
    }

    @Override
    Object deserialize(DataType dataType, Decoder decoder) throws IOException {
        switch (dataType) {
            case TIME_DURATION:
                return readDuration(decoder);
            case TIME_INSTANT:
                return readInstant(decoder);
            case TIME_LOCAL_DATE:
                return readLocalDate(decoder);
            case TIME_LOCAL_TIME:
                return readLocalTime(decoder);
            case TIME_LOCAL_DATETIME:
                return readLocalDateTime(decoder);
            case TIME_OFFSET_DATETIME:
                return readOffsetDateTime(decoder);
            case TIME_ZONED_DATETIME:
                return readZonedDateTime(decoder);
            case TIME_PERIOD:
                return readPeriod(decoder);
            case TIME_ZONE_ID:
                return readZoneId(decoder);
            case TIME_ZONE_OFFSET:
                return readZoneOffset(decoder);
            default:
                throw new DeserializerException("Unhandled data type " + dataType);
        }
    }

    private Duration readDuration(Decoder decoder) throws IOException {
        return Duration.ofMillis(decoder.readLong());
    }

    private Instant readInstant(Decoder decoder) throws IOException {
        return Instant.ofEpochSecond(decoder.readLong(), decoder.readInteger());
    }

    private LocalDate readLocalDate(Decoder decoder) throws IOException {
        return LocalDate.of(decoder.readInteger(), decoder.readByte(), decoder.readByte());
    }

    private LocalTime readLocalTime(Decoder decoder) throws IOException {
        return LocalTime.of(decoder.readByte(), decoder.readByte(), decoder.readByte(), decoder.readInteger());
    }

    private LocalDateTime readLocalDateTime(Decoder decoder) throws IOException {
        return LocalDateTime.of(readLocalDate(decoder), readLocalTime(decoder));
    }

    private OffsetDateTime readOffsetDateTime(Decoder decoder) throws IOException {
        return OffsetDateTime.of(readLocalDateTime(decoder), readZoneOffset(decoder));
    }

    private ZonedDateTime readZonedDateTime(Decoder decoder) throws IOException {
        return ZonedDateTime.of(readLocalDateTime(decoder), readZoneId(decoder));
    }

    private ZoneOffset readZoneOffset(Decoder decoder) throws IOException {
        return ZoneOffset.ofTotalSeconds(decoder.readInteger());
    }

    private ZoneId readZoneId(Decoder decoder) throws IOException {
        return ZoneId.of(decoder.readString());
    }

    private Period readPeriod(Decoder decoder) throws IOException {
        return Period.of(decoder.readInteger(), decoder.readByte(), decoder.readByte());
    }
}
