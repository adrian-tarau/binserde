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

package net.microfalx.binserde.deserializer;

import static net.microfalx.binserde.utils.ArgumentUtils.requireNonNull;

public abstract class AbstractDeserializer<T> implements Deserializer<T> {

    private final Class<T> type;

    public AbstractDeserializer(Class<T> type) {
        requireNonNull(type);
        this.type = type;
    }

    @Override
    public final Class<T> getType() {
        return type;
    }
}
