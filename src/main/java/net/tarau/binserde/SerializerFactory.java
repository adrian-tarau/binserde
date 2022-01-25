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

package net.tarau.binserde;

import net.tarau.binserde.annotation.Tag;
import net.tarau.binserde.deserializer.Deserializer;
import net.tarau.binserde.deserializer.ReflectionDeserializer;
import net.tarau.binserde.io.InputStreamDecoder;
import net.tarau.binserde.io.OutputStreamEncoder;
import net.tarau.binserde.metadata.MetadataException;
import net.tarau.binserde.metadata.Registry;
import net.tarau.binserde.serializer.ReflectionSerializer;
import net.tarau.binserde.serializer.Serializer;
import net.tarau.binserde.utils.ClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.tarau.binserde.utils.ArgumentUtils.requireNonNull;

/**
 * A factory to create serializers / deserializers.
 */
public class SerializerFactory {

    private final Map<Short, Class<?>> classesById = new ConcurrentHashMap<>();
    private final Map<Class<?>, Short> idsByClasses = new ConcurrentHashMap<>();

    public static SerializerFactory instance = new SerializerFactory();

    private volatile Registry registry;

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
     * Serializes the object.
     *
     * @param object       the object to serialize
     * @param outputStream the output stream where to serialize the object
     * @param <T>          the object type
     * @throws IOException if an I/O error occurs
     */
    @SuppressWarnings("unchecked")
    public static <T> void serialize(T object, OutputStream outputStream) throws IOException {
        requireNonNull(object);
        requireNonNull(outputStream);

        OutputStreamEncoder encoder = new OutputStreamEncoder(outputStream);

        Serializer<T> serializer = (Serializer<T>) getInstance().createSerializer(object.getClass());
        serializer.serialize(object, encoder);
        encoder.close();
    }

    /**
     * Serializes the object.
     *
     * @param type        the type to deserialize
     * @param inputStream the input stream used to read the serialized object
     * @param <T>         the object type
     * @throws IOException if an I/O error occurs
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(Class<T> type, InputStream inputStream) throws IOException {
        requireNonNull(type);
        requireNonNull(inputStream);

        InputStreamDecoder decoder = new InputStreamDecoder(inputStream);
        Deserializer<T> deserializer = getInstance().createDeserializer(type);
        try {
            return deserializer.deserialize(decoder);
        } finally {
            decoder.close();
        }
    }

    /**
     * Returns the registry associated with the factory.
     *
     * @return the registry
     */
    public Registry getRegistry() {
        return registry;
    }

    /**
     * Changes the registry.
     *
     * @param registry the registry
     */
    public void setRegistry(Registry registry) {
        requireNonNull(registry);
        this.registry = registry;
    }

    /**
     * Registers a new class. The class needs to be tagged with {@link  Tag}
     *
     * @param clazz the class
     */
    public void register(Class<?> clazz) {
        requireNonNull(clazz);
        Tag tagAnnot = clazz.getAnnotation(Tag.class);
        if (tagAnnot == null) throw new IllegalArgumentException("Class must have a @Tag annotation");
        register(clazz, tagAnnot.value());
    }

    /**
     * Registers a new class.
     *
     * @param clazz      the class
     * @param identifier the identifier
     */
    public void register(Class<?> clazz, int identifier) {
        requireNonNull(clazz);
        if (identifier < ClassUtils.MIN_CLASS_ID || identifier > ClassUtils.MAX_CLASS_ID) {
            throw new IllegalArgumentException("Class identifier must be between " + ClassUtils.MIN_CLASS_ID + " and " + ClassUtils.MAX_CLASS_ID);
        }
        short identifierShort = (short) identifier;
        synchronized (classesById) {
            Class<?> existingClass = classesById.get(identifierShort);
            if (existingClass != null && existingClass != clazz) {
                throw new IllegalArgumentException("Failed to register class " + clazz.getName() + " with identifier " + identifier + ", another class (" + existingClass.getName() + ") is already registered");
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
        requireNonNull(clazz);
        Short identifier = idsByClasses.get(clazz);
        if (identifier == null) throw new MetadataException("Class '" + clazz.getName() + "' is not registered");
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

    /**
     * Creates a serializer for a given type.
     *
     * @param type the class to serialize
     * @param <T>  the data type
     * @return a non-null instance
     */
    public <T> Serializer<T> createSerializer(Class<T> type) {
        return new ReflectionSerializer<>(type);
    }

    /**
     * Creates a deserializer for a given type.
     *
     * @param type the class to serialize
     * @param <T>  the data type
     * @return a non-null instance
     */
    public <T> Deserializer<T> createDeserializer(Class<T> type) {
        return new ReflectionDeserializer<>(type);
    }

    /**
     * Resets the factory.
     */
    void reset() {
        idsByClasses.clear();
        classesById.clear();
    }
}
