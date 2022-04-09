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

public class AllSupportedTypes {

    private boolean b1;
    private Boolean b2;
    private char c1;
    private Character c2;
    private String s2;
    private Type type1;
    private NumberTypes numbers;
    private TimeTypes time;

    public boolean isB1() {
        return b1;
    }

    public AllSupportedTypes setB1(boolean b1) {
        this.b1 = b1;
        return this;
    }

    public Boolean getB2() {
        return b2;
    }

    public AllSupportedTypes setB2(Boolean b2) {
        this.b2 = b2;
        return this;
    }

    public char getC1() {
        return c1;
    }

    public AllSupportedTypes setC1(char c1) {
        this.c1 = c1;
        return this;
    }

    public Character getC2() {
        return c2;
    }

    public AllSupportedTypes setC2(Character c2) {
        this.c2 = c2;
        return this;
    }

    public String getS2() {
        return s2;
    }

    public AllSupportedTypes setS2(String s2) {
        this.s2 = s2;
        return this;
    }

    public Type getType1() {
        return type1;
    }

    public AllSupportedTypes setType1(Type type1) {
        this.type1 = type1;
        return this;
    }

    public NumberTypes getNumbers() {
        return numbers;
    }

    public AllSupportedTypes setNumbers(NumberTypes numbers) {
        this.numbers = numbers;
        return this;
    }

    public TimeTypes getTime() {
        return time;
    }

    public AllSupportedTypes setTime(TimeTypes time) {
        this.time = time;
        return this;
    }

    public static AllSupportedTypes create() {
        return new AllSupportedTypes().setNumbers(NumberTypes.create()).setTime(TimeTypes.create())
                .setB1(true).setB2(false).setC1('a').setC2('b').setS2("string").setType1(Type.TYPE2);
    }

    public enum Type {
        TYPE1,
        TYPE2
    }
}
