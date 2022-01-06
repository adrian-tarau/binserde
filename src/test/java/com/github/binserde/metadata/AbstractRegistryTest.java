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

import com.github.binserde.dto.DtoUtils;
import com.github.binserde.dto.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class AbstractRegistryTest {

    private TestRegistry registry;

    @BeforeEach
    public void setup() {
        DtoUtils.init();
        registry = new TestRegistry();
        registry.setRefreshInterval(Duration.ofMillis(100));
        assertEquals(100, registry.getRefreshInterval().toMillis());
    }

    @Test
    public void isAvailableWhenRegistryIsAvailable() {
        assertTrue(registry.isAvailable());
        registry.available = false;
        assertTrue(registry.isAvailable());
        assertEquals(0, registry.getUnavailableCount());
    }

    @Test
    public void isAvailableWhenRegistryIsNotAvailable() {
        registry.available = false;
        assertFalse(registry.isAvailable());
        assertEquals(1, registry.getUnavailableCount());
    }

    @Test
    public void isAvailableWithFailure() {
        registry.exceptionForAvailable = new IOException("Failure");
        assertFalse(registry.isAvailable());
        assertEquals(1, registry.getUnavailableCount());
    }

    @Test
    public void storeWithValidRegistry() {
        assertNotNull(registry.store(ClassInfo.create(Person.class)));
    }

    @Test
    public void storeWithUnavailableRegistry() {
        registry.available = false;
        assertNull(registry.store(ClassInfo.create(Person.class)));
        assertEquals(1, registry.getUnavailableCount());
    }

    @Test
    public void storeWithUnavailableRegistryDueToNetwork() {
        registry.exception = new IOException("Failure");
        assertNull(registry.store(ClassInfo.create(Person.class)));
        assertEquals(1, registry.getUnavailableCount());
    }

    @Test
    public void loadWithoutAnEntry() {
        assertThrows(MetadataNotAvailableException.class, () -> {
            registry.load("missing");
        });
    }

    @Test
    public void loadWithEntry() {
        ClassInfo classInfo = ClassInfo.create(Person.class);
        registry.cacheBySignature.put(classInfo.getSignature(), classInfo);
        assertEquals(classInfo, registry.load(classInfo.getSignature()));
    }

    @Test
    public void loadWithoutExistingEntry() {
        ClassInfo classInfo = ClassInfo.create(Person.class);
        assertThrows(MetadataNotAvailableException.class, () -> {
            registry.load(classInfo.getSignature());
        });
    }

    @Test
    public void loadWithException() {
        registry.exception = new IOException("Failure");
        assertThrows(MetadataNotAvailableException.class, () -> {
            registry.load("signature");
        });
    }

    static class TestRegistry extends AbstractRegistry {

        private final Map<String, ClassInfo> cacheBySignature = new ConcurrentHashMap<>();
        private final Map<ClassInfo, String> cacheByClass = new ConcurrentHashMap<>();

        private boolean available = true;
        private Exception exception;
        private Exception exceptionForAvailable;

        @Override
        protected String doStore(ClassInfo classInfo) throws Exception {
            failIfNeeded();
            cacheByClass.put(classInfo, classInfo.getSignature());
            cacheBySignature.put(classInfo.getSignature(), classInfo);
            return classInfo.getSignature();
        }

        @Override
        public String getName() {
            return "Test";
        }

        @Override
        protected ClassInfo doLoad(String signature) throws Exception {
            failIfNeeded();
            return cacheBySignature.get(signature);
        }

        @Override
        protected boolean doIsAvailable() throws Exception {
            if (exceptionForAvailable != null) throw exception;
            return available;
        }

        private void failIfNeeded() throws Exception {
            if (exception != null) throw exception;
        }
    }

}