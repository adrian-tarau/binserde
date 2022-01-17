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

package net.tarau.binserde.serializer;

import net.tarau.binserde.io.Encoder;
import net.tarau.binserde.metadata.DataType;

import java.io.IOException;
import java.time.*;

class ReflectionTimeSerializer extends ReflectionFieldSerializer {

    ReflectionTimeSerializer(ReflectionSerializer<?> parent) {
        super(parent);
    }

    @Override
    void serialize(DataType dataType, Object value, Encoder encoder) throws IOException {
        switch (dataType) {
            case TIME_DURATION:
                writeDuration((Duration) value, encoder);
                break;
            case TIME_INSTANT:
                writeInstant((Instant) value, encoder);
                break;
            case TIME_LOCAL_DATE:
                writeLocalDate((LocalDate) value, encoder);
                break;
            case TIME_LOCAL_TIME:
                writeLocalTime((LocalTime) value, encoder);
                break;
            case TIME_LOCAL_DATETIME:
                writeLocalDateTime((LocalDateTime) value, encoder);
                break;
            case TIME_OFFSET_DATETIME:
                writeOffsetDateTime((OffsetDateTime) value, encoder);
                break;
            case TIME_ZONED_DATETIME:
                writeZonedDateTime((ZonedDateTime) value, encoder);
                break;
            case TIME_PERIOD:
                writePeriod((Period) value, encoder);
                break;
            case TIME_ZONE_ID:
                writeZoneId((ZoneId) value, encoder);
                break;
            case TIME_ZONE_OFFSET:
                writeZoneOffset((ZoneOffset) value, encoder);
                break;
            default:
                throw new SerializerException("Unhandled data type " + dataType);
        }
    }

    private void writeDuration(Duration duration, Encoder encoder) throws IOException {
        encoder.writeLong(duration.toMillis());
    }

    private void writeInstant(Instant instant, Encoder encoder) throws IOException {
        encoder.writeLong(instant.getEpochSecond());
        encoder.writeInteger(instant.getNano());
    }

    private void writeLocalDate(LocalDate localDate, Encoder encoder) throws IOException {
        encoder.writeInteger(localDate.getYear());
        encoder.writeByte((byte) localDate.getMonthValue());
        encoder.writeByte((byte) localDate.getDayOfMonth());
    }

    private void writeLocalTime(LocalTime localTime, Encoder encoder) throws IOException {
        encoder.writeByte((byte) localTime.getHour());
        encoder.writeByte((byte) localTime.getMinute());
        encoder.writeByte((byte) localTime.getSecond());
        encoder.writeInteger(localTime.getNano());
    }

    private void writeLocalDateTime(LocalDateTime localDataTime, Encoder encoder) throws IOException {
        writeLocalDate(localDataTime.toLocalDate(), encoder);
        writeLocalTime(localDataTime.toLocalTime(), encoder);
    }

    private void writeOffsetDateTime(OffsetDateTime offsetDataTime, Encoder encoder) throws IOException {
        writeLocalDateTime(offsetDataTime.toLocalDateTime(), encoder);
        writeZoneOffset(offsetDataTime.getOffset(), encoder);
    }

    private void writeZonedDateTime(ZonedDateTime zonedDataTime, Encoder encoder) throws IOException {
        writeLocalDateTime(zonedDataTime.toLocalDateTime(), encoder);
        writeZoneId(zonedDataTime.getZone(), encoder);
    }

    private void writeZoneOffset(ZoneOffset zoneOffset, Encoder encoder) throws IOException {
        encoder.writeInteger(zoneOffset.getTotalSeconds());
    }

    private void writeZoneId(ZoneId zoneId, Encoder encoder) throws IOException {
        encoder.writeString(zoneId.getId());
    }

    private void writePeriod(Period period, Encoder encoder) throws IOException {
        encoder.writeInteger(period.getYears());
        encoder.writeByte((byte) period.getMonths());
        encoder.writeByte((byte) period.getDays());
    }


}
