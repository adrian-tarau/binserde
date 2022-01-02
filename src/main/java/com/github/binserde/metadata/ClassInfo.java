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

import com.github.binserde.SerializerFactory;
import com.github.binserde.io.Decoder;
import com.github.binserde.utils.ArgumentUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class ClassInfo {

    private Class<?> clazz;
    private short identifier;
    private Collection<FieldInfo> fields = new ArrayList<>();

    public static ClassInfo create(Class<?> clazz) {
        short identifier = SerializerFactory.getInstance().getIdentifier(clazz);
        ClassInfo classInfo = new ClassInfo(clazz, identifier);
        classInfo.load();
        return classInfo;
    }

    public static ClassInfo create(Decoder decoder) throws IOException {
        short identifier = decoder.readShort();
        Class<?> clazz = SerializerFactory.getInstance().getClass(identifier);
        ClassInfo classInfo = new ClassInfo(clazz, identifier);
        classInfo.load(decoder);
        return classInfo;
    }

    private ClassInfo(Class<?> clazz, short identifier) {
        ArgumentUtils.requireNonNull(clazz);
        this.identifier = identifier;
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public short getIdentifier() {
        return identifier;
    }

    private void load() {

    }

    private void load(Decoder decoder) throws IOException {

    }


}
