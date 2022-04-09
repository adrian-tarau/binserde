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

package net.microfalx.binserde.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberTypes {

    private byte b1;
    private Byte b2;
    private short s1;
    private Short s2;
    private int i1;
    private Integer i2;
    private long l1;
    private Long l2;
    private float f1;
    private Float f2;
    private double d1;
    private Double d2;
    private BigInteger bi1;
    private BigDecimal bd1;

    public byte getB1() {
        return b1;
    }

    public NumberTypes setB1(byte b1) {
        this.b1 = b1;
        return this;
    }

    public Byte getB2() {
        return b2;
    }

    public NumberTypes setB2(Byte b2) {
        this.b2 = b2;
        return this;
    }

    public short getS1() {
        return s1;
    }

    public NumberTypes setS1(short s1) {
        this.s1 = s1;
        return this;
    }

    public Short getS2() {
        return s2;
    }

    public NumberTypes setS2(Short s2) {
        this.s2 = s2;
        return this;
    }

    public int getI1() {
        return i1;
    }

    public NumberTypes setI1(int i1) {
        this.i1 = i1;
        return this;
    }

    public Integer getI2() {
        return i2;
    }

    public NumberTypes setI2(Integer i2) {
        this.i2 = i2;
        return this;
    }

    public long getL1() {
        return l1;
    }

    public NumberTypes setL1(long l1) {
        this.l1 = l1;
        return this;
    }

    public Long getL2() {
        return l2;
    }

    public NumberTypes setL2(Long l2) {
        this.l2 = l2;
        return this;
    }

    public float getF1() {
        return f1;
    }

    public NumberTypes setF1(float f1) {
        this.f1 = f1;
        return this;
    }

    public Float getF2() {
        return f2;
    }

    public NumberTypes setF2(Float f2) {
        this.f2 = f2;
        return this;
    }

    public double getD1() {
        return d1;
    }

    public NumberTypes setD1(double d1) {
        this.d1 = d1;
        return this;
    }

    public Double getD2() {
        return d2;
    }

    public NumberTypes setD2(Double d2) {
        this.d2 = d2;
        return this;
    }

    public BigInteger getBi1() {
        return bi1;
    }

    public NumberTypes setBi1(BigInteger bi1) {
        this.bi1 = bi1;
        return this;
    }

    public BigDecimal getBd1() {
        return bd1;
    }

    public NumberTypes setBd1(BigDecimal bd1) {
        this.bd1 = bd1;
        return this;
    }

    public static NumberTypes create() {
        return new NumberTypes().setB1((byte) 10).setB2((byte) 20)
                .setS1((short) 20).setS2((short) 30)
                .setI1(30).setI2(40)
                .setL1(50L).setL2(60L)
                .setF1(0.1f).setF2(0.2f)
                .setD1(0.3).setD2(0.4)
                .setBi1(BigInteger.valueOf(100)).setBd1(BigDecimal.valueOf(0.5));
    }
}
