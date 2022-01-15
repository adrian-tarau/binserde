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

import com.github.binserde.dto.DtoUtils;
import com.github.binserde.io.Decoder;
import com.github.binserde.io.Encoder;
import com.github.binserde.io.InputStreamDecoder;
import com.github.binserde.io.OutputStreamEncoder;
import com.github.binserde.metadata.NullRegistry;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class AbstractSerdeTestCase {

    protected final SerializerFactory serializerFactory = SerializerFactory.getInstance();
    protected final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    protected Encoder encoder;
    protected Decoder decoder;

    @BeforeEach
    void setup() {
        serializerFactory.setRegistry(new NullRegistry());
        DtoUtils.init();
        outputStream.reset();
        encoder = new OutputStreamEncoder(outputStream);
    }

    protected void createDecoder() throws IOException {
        encoder.close();
        decoder = new InputStreamDecoder(new ByteArrayInputStream(outputStream.toByteArray()));
    }
}
