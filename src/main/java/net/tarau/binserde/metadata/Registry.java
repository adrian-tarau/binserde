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

/**
 * A registry for class metadata.
 * <p>
 * When a registry is configured with {@link SerializerFactory}, only the class signature is
 * stored in the stream. Each version of the class has a unique signature which gives the class {@link  ClassInfo#getSignature()}
 * <p>
 * Clients should use {@link AbstractRegistry} to implement registries.
 * <p>
 * If the registry is not available when the class needs to be stored, the class information is stored with the stream
 * automatically. This makes sure class metadata is always available and the serialization does not fail.
 * <p>
 * If the class is successfully stored in the registry, the {@link #store(ClassInfo)} method returns the class signature
 * to be written in the stream (which ideally it is {@link ClassInfo#getSignature()} but it is up to the registry implementation).
 * <p>
 * A registry can be implemented with a database, distributed configuration store (Apache ZooKeeper, HashCorp Consul, etc),
 * an event streaming platform (Apache Kafka, RabbitMQ, etc).
 */
public interface Registry {

    /**
     * Returns a friendly name for a registry.
     *
     * @return a non-null instance
     */
    String getName();

    /**
     * Returns whether the registry is available.
     *
     * @return {@code true} if the registry is available, {@code false} otherwise
     */
    boolean isAvailable();

    /**
     * Returns the number of time the registry was unavailable.
     *
     * @return a positive integer
     */
    long getUnavailableCount();

    /**
     * Stores the class information in the registry.
     * <p>
     * The method is called every time class info needs to be stored in a stream.
     *
     * @param classInfo the class information
     * @return the class signature (stored with the stream), {@code NULL} if class information should be stored
     * with the stream
     */
    String store(ClassInfo classInfo);

    /**
     * Retrieves class information from the registry.
     *
     * @param signature the class signature
     * @return the class information
     * @throws MetadataException if the class does not exist in registry
     */
    ClassInfo load(String signature);
}
