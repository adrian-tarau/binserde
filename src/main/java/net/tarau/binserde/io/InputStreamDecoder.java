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

package net.tarau.binserde.io;

import net.tarau.binserde.utils.ArgumentUtils;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamDecoder extends AbstractDecoder {

    private final InputStream inputStream;

    public InputStreamDecoder(InputStream inputStream) {
        ArgumentUtils.requireNonNull(inputStream);
        this.inputStream = inputStream;
    }

    @Override
    int read(byte[] buffer, int offset, int length) throws IOException {
        return inputStream.read(buffer, offset, length);
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
