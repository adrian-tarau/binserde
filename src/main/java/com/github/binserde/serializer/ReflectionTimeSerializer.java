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

import static com.github.binserde.metadata.DataTypes.BASE;
import static com.github.binserde.metadata.DataTypes.BASE_OBJECT;

public class ReflectionTimeSerializer extends ReflectionFieldSerializer {

    public ReflectionTimeSerializer(ReflectionSerializer<?> parent) {
        super(parent);
    }

    @Override
    void serialize(FieldInfo fieldInfo, Object value, Encoder encoder) throws IOException {
        encoder.writeTag((byte) (BASE | BASE_OBJECT));
        encoder.writeTag(fieldInfo.getDataType().getId());
        switch (fieldInfo.getDataType()) {
            case TIME_DURATION:
                break;
            case TIME_INSTANT:
                break;
            case TIME_LOCAL_DATE:

                break;
            case TIME_LOCAL_TIME:

                break;
            case TIME_LOCAL_DATETIME:

                break;
            case TIME_OFFSET_DATETIME:

                break;
            case TIME_ZONED_DATETIME:

                break;
            case TIME_PERIOD:

                break;
            case TIME_ZONE_ID:

                break;
            case TIME_ZONE_OFFSET:

                break;
            default:
                throw new SerializerException("Unhandled enum " + fieldInfo.getDataType());
        }
    }
}
