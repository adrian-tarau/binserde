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

package com.github.binserde;

import com.github.binserde.metadata.MetadataException;
import com.github.binserde.utils.ArgumentUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.binserde.utils.ArgumentUtils.requireNonNull;
import static com.github.binserde.utils.ClassUtils.MAX_CLASS_ID;
import static com.github.binserde.utils.ClassUtils.MIN_CLASS_ID;

/**
 * A factory to create serializers / deserializers.
 */
public class SerializerFactory {

    private final Map<Short, Class<?>> classesById = new ConcurrentHashMap<>();
    private final Map<Class<?>, Short> idsByClasses = new ConcurrentHashMap<>();

    public static SerializerFactory instance = new SerializerFactory();

    /**
     * Returns the singleton instance.
     *
     * @return a non-null instance
     */
    public static SerializerFactory getInstance() {
        return instance;
    }

    private SerializerFactory() {
    }

    /**
     * Registers a new class.
     *
     * @param clazz      the class
     * @param identifier the identifier
     */
    public void register(Class<?> clazz, int identifier) {
        requireNonNull(clazz);
        if (identifier < MIN_CLASS_ID || identifier > MAX_CLASS_ID) {
            throw new IllegalArgumentException("Class identifier must be between " + MIN_CLASS_ID + " and " + MAX_CLASS_ID);
        }
        short identifierShort = (short) identifier;
        synchronized (classesById) {
            Class<?> existingClass = classesById.get(identifierShort);
            if (existingClass != null && existingClass != clazz) {
                throw new IllegalArgumentException("Failed to register class " + clazz.getName() + " with identifier "
                        + identifier + ", another class (" + existingClass.getName() + ") is already registered");
            } else {
                classesById.put(identifierShort, clazz);
                idsByClasses.put(clazz, identifierShort);
            }
        }
    }

    /**
     * Returns whether the class has an identifier registered with the registry.
     *
     * @param clazz the class
     * @return {@code true} if the class is registered, {@code false} otherwise
     */
    public boolean isSupported(Class<?> clazz) {
        requireNonNull(clazz);
        return idsByClasses.containsKey(clazz);
    }

    /**
     * Returns the identifier associated with the class.
     *
     * @param clazz the class
     * @return the identifier
     * @throws MetadataException if such class is not registered with the factory
     */
    public short getIdentifier(Class<?> clazz) {
        ArgumentUtils.requireNonNull(clazz);
        Short identifier = idsByClasses.get(classesById);
        if (identifier == null) throw new MetadataException("Class '" + identifier + "' is not registered");
        return identifier;
    }

    /**
     * Returns the class associated with the identifier
     *
     * @param identifier the identifier
     * @return the class
     * @throws MetadataException if such identifier is not registered with the factory
     */
    public Class<?> getClass(int identifier) {
        Class<?> clazz = classesById.get((short) identifier);
        if (clazz == null) throw new MetadataException("Identifier '" + identifier + "' is not registered");
        return clazz;
    }
}
