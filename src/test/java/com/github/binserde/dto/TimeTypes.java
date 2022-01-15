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

package com.github.binserde.dto;

import java.time.*;

public class TimeTypes {

    private Duration td;
    private Instant ti;
    private LocalDate tld;
    private LocalTime tlt;
    private LocalDateTime tldt;
    private OffsetDateTime todt;
    private ZonedDateTime tzdt;
    private Period tp;
    private ZoneId tzi;
    private ZoneOffset tzo;

    public Duration getTd() {
        return td;
    }

    public TimeTypes setTd(Duration td) {
        this.td = td;
        return this;
    }

    public Instant getTi() {
        return ti;
    }

    public TimeTypes setTi(Instant ti) {
        this.ti = ti;
        return this;
    }

    public LocalDate getTld() {
        return tld;
    }

    public TimeTypes setTld(LocalDate tld) {
        this.tld = tld;
        return this;
    }

    public LocalTime getTlt() {
        return tlt;
    }

    public TimeTypes setTlt(LocalTime tlt) {
        this.tlt = tlt;
        return this;
    }

    public LocalDateTime getTldt() {
        return tldt;
    }

    public TimeTypes setTldt(LocalDateTime tldt) {
        this.tldt = tldt;
        return this;
    }

    public OffsetDateTime getTodt() {
        return todt;
    }

    public TimeTypes setTodt(OffsetDateTime todt) {
        this.todt = todt;
        return this;
    }

    public ZonedDateTime getTzdt() {
        return tzdt;
    }

    public TimeTypes setTzdt(ZonedDateTime tzdt) {
        this.tzdt = tzdt;
        return this;
    }

    public Period getTp() {
        return tp;
    }

    public TimeTypes setTp(Period tp) {
        this.tp = tp;
        return this;
    }

    public ZoneId getTzi() {
        return tzi;
    }

    public TimeTypes setTzi(ZoneId tzi) {
        this.tzi = tzi;
        return this;
    }

    public ZoneOffset getTzo() {
        return tzo;
    }

    public TimeTypes setTzo(ZoneOffset tzo) {
        this.tzo = tzo;
        return this;
    }

    public static TimeTypes create() {
        return new TimeTypes().setTd(Duration.ofHours(1000))
                .setTi(Instant.now()).setTld(LocalDate.now()).setTlt(LocalTime.now())
                .setTldt(LocalDateTime.now()).setTzdt(ZonedDateTime.now())
                .setTodt(OffsetDateTime.now()).setTp(Period.ofDays(10))
                .setTzi(ZoneId.of("America/New_York"))
                .setTzo(ZoneOffset.ofHours(-5));
    }
}
