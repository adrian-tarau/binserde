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

import net.tarau.binserde.utils.ArgumentUtils;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An abstract implementation for a registry which provides some common building blocks:
 * <ul>
 *     <li>caches the availability of the registry</li>
 *     <li>caches class information</li>
 *     <li>reports the number of times the registry is not available to store metadata</li>
 * </ul>
 */
public abstract class AbstractRegistry implements Registry {

    private final Map<String, ClassInfo> cacheBySignature = new ConcurrentHashMap<>();
    private final Map<ClassInfo, String> cacheByClass = new ConcurrentHashMap<>();
    private volatile Boolean available;
    private volatile long lastAvailabilityCheck = System.currentTimeMillis() - Duration.ofHours(1).toMillis();
    private final AtomicLong unavailableCount = new AtomicLong();

    private Duration refreshInterval = Duration.ofMillis(5_000);

    @Override
    public boolean isAvailable() {
        if (available != null && (System.currentTimeMillis() - lastAvailabilityCheck) < refreshInterval.toMillis()) {
            return available;
        }
        try {
            lastAvailabilityCheck = System.currentTimeMillis();
            available = doIsAvailable();
            if (!available) unavailableCount.incrementAndGet();
        } catch (Exception e) {
            unavailableCount.incrementAndGet();
            available = false;
        }
        return available;
    }

    @Override
    public long getUnavailableCount() {
        return unavailableCount.get();
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public String store(ClassInfo classInfo) {
        ArgumentUtils.requireNonNull(classInfo);

        String signature = cacheByClass.get(classInfo);
        if (signature != null) return signature;

        if (!isAvailable()) return null;

        try {
            signature = doStore(classInfo);
            updateCache(classInfo, signature);
            available = true;
            return signature;
        } catch (Exception e) {
            unavailableCount.incrementAndGet();
            available = false;
            return null;
        }
    }

    @Override
    public ClassInfo load(String signature) {
        ArgumentUtils.requireNonNull(signature);

        ClassInfo classInfo = cacheBySignature.get(signature);
        if (classInfo != null) return classInfo;

        if (!isAvailable()) throw new MetadataNotAvailableException("Registry '" + getName() + "' is not available");
        try {
            classInfo = doLoad(signature);
        } catch (Exception e) {
            throw new MetadataNotAvailableException("Registry '" + getName() + "' is not available", e);
        }
        if (classInfo != null) {
            updateCache(classInfo, signature);
            return classInfo;
        } else {
            throw new MetadataNotAvailableException("Class information not available in registry '" + getName() + "' for signature '" + signature + "'");
        }
    }

    /**
     * Returns the refresh interval for registry availability.
     *
     * @return a non-null instance
     */
    public Duration getRefreshInterval() {
        return refreshInterval;
    }

    /**
     * Changes the refresh interval for registry availability.
     *
     * @param interval the interval
     */
    public void setRefreshInterval(Duration interval) {
        ArgumentUtils.requireNonNull(interval);
        this.refreshInterval = interval;
    }

    /**
     * Subclasses will implement this method to store class information.
     * <p>
     * The registry implementation can use (as a key) and return {@link  ClassInfo#getSignature()} once the information was persisted
     *
     * @param classInfo the class information
     * @return the signature
     * @throws Exception if anything goes wrong
     */
    protected abstract String doStore(ClassInfo classInfo) throws Exception;

    /**
     * Subclasses will implement this method to load class information.
     * <p>
     * The registry implementation can use (as a key) and return the class information store under that key.
     *
     * @param signature the class signature
     * @return the class information
     * @throws Exception if anything goes wrong
     */
    protected abstract ClassInfo doLoad(String signature) throws Exception;

    /**
     * Subclasses will implement this method to validate whether the registry is available.
     *
     * @return {@code true} if the registry is available, {@code false} otherwise
     * @throws Exception if anything goes wrong
     */
    protected abstract boolean doIsAvailable() throws Exception;

    private void updateCache(ClassInfo classInfo, String signature) {
        cacheBySignature.put(signature, classInfo);
        cacheByClass.put(classInfo, signature);
    }
}
