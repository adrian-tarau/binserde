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

package net.microfalx.binserde.serializer;

import net.microfalx.binserde.io.Encoder;
import net.microfalx.binserde.metadata.DataType;

import java.io.IOException;

class ReflectionOtherSerializer extends ReflectionFieldSerializer {

    ReflectionOtherSerializer(ReflectionSerializer<?> parent) {
        super(parent);
    }

    @Override
    void serialize(DataType dataType, Object value, Encoder encoder) throws IOException {
        switch (dataType) {
            case BOOLEAN:
                encoder.writeBoolean((Boolean) value);
                break;
            case CHARACTER:
                encoder.writeCharacter((Character) value);
                break;
            case STRING:
                encoder.writeString((String) value);
                break;
            case ENUM:
                encoder.writeEnum((Enum<?>) value);
                break;
            default:
                throw new SerializerException("Unhandled data type " + dataType);
        }
    }
}
