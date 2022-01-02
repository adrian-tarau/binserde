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

import com.github.binserde.io.Decoder;
import com.github.binserde.utils.ArgumentUtils;

import java.io.IOException;
import java.lang.reflect.Field;

public class FieldInfo {

    public static final byte NO_VERSION = -1;
    public static final byte NO_TAG = -1;

    private final String name;
    private final byte dataType;

    private byte version = NO_VERSION;
    private short tag = NO_TAG;

    private Field field;
    private long fieldOffset;

    public static FieldInfo create(String name, byte dataType) {
        FieldInfo fieldInfo = new FieldInfo(name, dataType);
        return fieldInfo;
    }

    public static FieldInfo create(Decoder decoder) throws IOException {
        FieldInfo fieldInfo = new FieldInfo(decoder.readString(), decoder.readByte());
        return fieldInfo;
    }

    private FieldInfo(String name, byte dataType) {
        ArgumentUtils.requireNonNull(name);
        this.name = name;
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public byte getDataType() {
        return dataType;
    }
}
