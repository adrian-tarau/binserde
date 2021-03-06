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

package net.microfalx.binserde.io;

class IOUtils {

    /**
     * A hard coded chunk size
     */
    static final int CHUNK_SIZE = 16 * 1024;

    /**
     * The size of any block header is: 4 header + 2 blocks size + 4 checksum + 1 version
     */
    static final int RESERVED_HEADER = 11;

    /**
     * A header signature to make sure the block was serialized by the same library
     */
    static final byte[] HEADER = {(byte) 0xA8, 0x75, (byte) 0xe7, 0x23};

    /**
     * Returns a hash code based on the contents of the specified array.
     *
     * @param buffer the array whose hash value to compute
     * @param offset the offset of the first byte
     * @param length the number of bytes to calculate the hash
     * @return a content-based hash code for {@code a}
     */
    public static int hashCode(byte[] buffer, int offset, int length) {
        if (buffer == null) return 0;

        int result = 1;
        int endIndex = offset + length;
        for (int index = offset; index < endIndex; index++) {
            result = 31 * result + buffer[index];
        }
        return result;
    }
}
