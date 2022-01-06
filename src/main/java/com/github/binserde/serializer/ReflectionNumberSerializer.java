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

package com.github.binserde.serializer;

import com.github.binserde.io.Encoder;
import com.github.binserde.metadata.FieldInfo;

import java.io.IOException;

public class ReflectionNumberSerializer extends ReflectionFieldSerializer {

    public ReflectionNumberSerializer(ReflectionSerializer<?> parent) {
        super(parent);
    }

    @Override
    void serialize(FieldInfo fieldInfo, Object value, Encoder encoder) throws IOException {
        switch (fieldInfo.getDataType()) {
            case BYTE:
                encoder.writeByte((Byte) value);
                break;
            case SHORT:
                encoder.writeShort((Short) value);
                break;
            case INTEGER:
                encoder.writeInteger((Integer) value);
                break;
            case LONG:
                encoder.writeLong((Long) value);
                break;
            case FLOAT:
                encoder.writeFloat((Float) value);
                break;
            case DOUBLE:
                encoder.writeDouble((Double) value);
                break;
            case BIG_INTEGER:
                //encoder.writeString((String) value);
                break;
            case BIG_DECIMAL:
                //encoder.writeString((String) value);
                break;
            default:
                throw new SerializerException("Unhandled enum " + fieldInfo.getDataType());
        }
    }
}