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

import net.tarau.binserde.SerializerFactory;
import net.tarau.binserde.annotation.Tag;
import net.tarau.binserde.io.Decoder;
import net.tarau.binserde.io.Encoder;
import net.tarau.binserde.utils.ArgumentUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds information about a class.
 * <p>
 * Each class has an identifier associated with the class. Only the identifier is stored in the stream
 * during serialization.
 * <p>
 * Class information can be stored outside in a {@link Registry} to avoid storing class information in the stream.
 */
public class ClassInfo {

    private final Class<?> clazz;
    private final String name;
    private final short identifier;
    private final Map<String, FieldInfo> fieldsByName = new HashMap<>();
    private final List<FieldInfo> fieldsByIndex = new ArrayList<>();

    private volatile String signature;
    private final static Map<Short, ClassInfo> cache = new ConcurrentHashMap<>();

    /**
     * Creates class information out of a Java class.
     *
     * @param clazz the class
     * @return a non-null instance
     */
    public static ClassInfo create(Class<?> clazz) {
        short identifier = SerializerFactory.getInstance().getIdentifier(clazz);
        ClassInfo classInfo = cache.get(identifier);
        if (classInfo != null) return classInfo;
        classInfo = new ClassInfo(clazz, identifier, clazz.getSimpleName());
        classInfo.load();
        cache.put(identifier, classInfo);
        return classInfo;
    }

    /**
     * Creates class information from a serialized stream.
     *
     * @param decoder the decoder
     * @return a non-null instance
     * @throws IOException if an I/O error occurs
     */
    public static ClassInfo create(Decoder decoder) throws IOException {
        short identifier = decoder.readShort();
        String name = decoder.readString();
        Class<?> clazz = SerializerFactory.getInstance().getClass(identifier);
        ClassInfo classInfo = new ClassInfo(clazz, identifier, name);
        classInfo.load(decoder);
        return classInfo;
    }

    private ClassInfo(Class<?> clazz, short identifier, String name) {
        ArgumentUtils.requireNonNull(clazz);
        this.identifier = identifier;
        this.name = name;
        this.clazz = clazz;
    }

    /**
     * Returns the Java class represented by this class information.
     *
     * @return a non-null instance
     */
    public Class<?> getClazz() {
        return clazz;
    }

    /**
     * Returns the class simple name (documentation purposes)
     *
     * @return a non-empty String
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the class identifier.
     * <p>
     * The identifier is assigned at registration or extracted from a {@link Tag}
     * annotation on the class
     *
     * @return the identifier
     */
    public short getIdentifier() {
        return identifier;
    }

    /**
     * Returns the class signature.
     * <p>
     * A signature is generated based on class identifier and a hash which uniquely identifies the version of the class.
     *
     * @return a non-empty String
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Returns a field by its name.
     *
     * @param name the name, case-insensitive
     * @return the field if exists, {@code NULL} otherwise
     */
    public FieldInfo findField(String name) {
        ArgumentUtils.requireNonNull(name);
        return fieldsByName.get(name.toLowerCase());
    }

    /**
     * Returns a field by its name.
     *
     * @param name the name, case-insensitive
     * @return the field
     * @throws MetadataException if the field does not exist
     */
    public FieldInfo getField(String name) {
        ArgumentUtils.requireNonNull(name);
        FieldInfo fieldInfo = fieldsByName.get(name.toLowerCase());
        if (fieldInfo == null) throw new MetadataException("A field with name '" + name + "' does not exist");
        return fieldInfo;
    }

    /**
     * Returns a field by its tag.
     *
     * @param tag the tag, case-insensitive
     * @return the field if exists, {@code NULL} otherise
     */
    public FieldInfo findField(int tag) {
        FieldInfo fieldInfo = fieldsByIndex.get(tag);
        if (fieldInfo == null) throw new MetadataException("A field with tag '" + tag + "' does not exist");
        return fieldInfo;
    }


    /**
     * Returns a field by its tag.
     *
     * @param tag the tag, case-insensitive
     * @return the field
     * @throws MetadataException if the field does not exist
     */
    public FieldInfo getField(int tag) {
        FieldInfo fieldInfo = fieldsByIndex.get(tag);
        if (fieldInfo == null) throw new MetadataException("A field with tag '" + tag + "' does not exist");
        return fieldInfo;
    }

    /**
     * Returns a collection of fields.
     *
     * @return a non-null Collection
     */
    public List<FieldInfo> getFields() {
        return Collections.unmodifiableList(fieldsByIndex);
    }

    /**
     * Stores class information with the stream.
     *
     * @param encoder the encoder
     * @throws IOException if an I/O error occurs
     */
    public void store(Encoder encoder) throws IOException {
        encoder.writeShort(identifier);
        encoder.writeString(name);
        encoder.writeShort((short) fieldsByIndex.size());
        for (FieldInfo fieldInfo : fieldsByIndex) {
            fieldInfo.store(encoder);
        }
    }

    private void load() {
        Class<?> rootClass = clazz;
        while (rootClass != Object.class) {
            Field[] fields = rootClass.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                FieldInfo fieldInfo = FieldInfo.create(field);
                register(fieldInfo);
                if (!field.isAccessible()) field.setAccessible(true);
            }
            rootClass = rootClass.getSuperclass();
        }
        signature = calculateSignature();
    }

    private void load(Decoder decoder) throws IOException {
        short fieldCount = decoder.readShort();
        for (int index = 0; index < fieldCount; index++) {
            FieldInfo fieldInfo = FieldInfo.create(decoder);
            register(fieldInfo);
        }
        signature = calculateSignature();
    }

    private void register(FieldInfo fieldInfo) {
        String name = fieldInfo.getName();
        if (fieldsByName.containsKey(name.toLowerCase())) {
            throw new MetadataException("A field with name '" + name + "' is already registered with " + clazz.getName());
        }
        fieldsByName.put(name.toLowerCase(), fieldInfo);
        fieldsByIndex.add(fieldInfo);
    }

    private String calculateSignature() {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            List<FieldInfo> sortedFields = new ArrayList<>(fieldsByIndex);
            sortedFields.sort(Comparator.comparing(FieldInfo::getName));
            for (FieldInfo sortedField : sortedFields) {
                digest.update(sortedField.getName().getBytes());
                digest.update(sortedField.getDataType().getId());
                digest.update((byte) (sortedField.getClassIdentifier() & 0xFF));
                digest.update((byte) ((sortedField.getClassIdentifier() >> 8) & 0xFF));
            }
            StringBuilder builder = new StringBuilder();
            builder.append(Integer.toString(((identifier >> 8) & 0xFF), 16).toLowerCase());
            builder.append(Integer.toString((identifier & 0xFF), 16).toLowerCase());
            builder.append("-");
            byte[] bytes = digest.digest();
            long hash = ((long) bytes[0] << 56) + ((long) bytes[1] << 48) + ((long) bytes[2] << 40) + ((long) bytes[3] << 32)
                    + (bytes[4] << 24) + (bytes[5] << 16) + (bytes[6] << 8) + bytes[7];
            builder.append(Long.toString(hash, 26));
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new MetadataException("Cannot calculate class identifier", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassInfo classInfo = (ClassInfo) o;

        return signature.equals(classInfo.signature);
    }

    @Override
    public int hashCode() {
        return signature.hashCode();
    }

    @Override
    public String toString() {
        return "ClassInfo{" + "clazz=" + clazz.getName() + ", identifier=" + identifier + ", fields=" + fieldsByIndex + '}';
    }
}
