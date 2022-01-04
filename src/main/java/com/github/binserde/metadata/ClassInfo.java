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
import com.github.binserde.io.Encoder;
import com.github.binserde.utils.ArgumentUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class ClassInfo {

    private Class<?> clazz;
    private String name;
    private short identifier;
    private Map<String, FieldInfo> fieldsByName = new HashMap<>();
    private List<FieldInfo> fieldsByIndex = new ArrayList<>();

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
        this.name = clazz.getSimpleName();
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getName() {
        return name;
    }

    public short getIdentifier() {
        return identifier;
    }

    public FieldInfo getField(String name) {
        ArgumentUtils.requireNonNull(name);
        FieldInfo fieldInfo = fieldsByName.get(name.toLowerCase());
        if (fieldInfo == null) throw new MetadataException("A field with name '" + name + "' does not exist");
        return fieldInfo;
    }

    public FieldInfo getField(int index) {
        return fieldsByIndex.get(index);
    }

    public List<FieldInfo> getFields() {
        return Collections.unmodifiableList(fieldsByIndex);
    }

    private void load() {
        Class<?> rootClass = clazz;
        while (rootClass != Object.class) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                FieldInfo fieldInfo = FieldInfo.create(field);
                String name = fieldInfo.getName();
                if (fieldsByName.containsKey(name.toLowerCase()))
                    throw new MetadataException("A field with name '" + name + "' is already registered with "
                            + rootClass.getName());
                fieldsByName.put(name.toLowerCase(), fieldInfo);
                fieldsByIndex.add(fieldInfo);
            }
            rootClass = rootClass.getSuperclass();
        }
    }

    void load(Decoder decoder) throws IOException {

    }

    void store(Encoder encoder) {

    }

    @Override
    public String toString() {
        return "ClassInfo{" +
                "clazz=" + clazz.getName() +
                ", identifier=" + identifier +
                ", fields=" + fieldsByIndex +
                '}';
    }
}
