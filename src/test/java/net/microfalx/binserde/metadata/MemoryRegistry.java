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

package net.microfalx.binserde.metadata;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A registry used for testing.
 */
public class MemoryRegistry extends AbstractRegistry {

    private final Map<String, ClassInfo> cacheBySignature = new ConcurrentHashMap<>();
    private final Map<ClassInfo, String> cacheByClass = new ConcurrentHashMap<>();

    @Override
    protected String doStore(ClassInfo classInfo) throws Exception {
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
        return cacheBySignature.get(signature);
    }

    @Override
    protected boolean doIsAvailable() throws Exception {
        return true;
    }
}
