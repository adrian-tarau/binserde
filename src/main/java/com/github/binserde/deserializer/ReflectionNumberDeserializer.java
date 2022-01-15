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

package com.github.binserde.deserializer;

import com.github.binserde.io.Decoder;
import com.github.binserde.metadata.DataType;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class ReflectionNumberDeserializer extends ReflectionFieldDeserializer {

    public ReflectionNumberDeserializer(ReflectionDeserializer<?> parent) {
        super(parent);
    }

    @Override
    Object deserialize(DataType dataType, Decoder decoder) throws IOException {
        switch (dataType) {
            case BYTE:
                return decoder.readByte();
            case SHORT:
                return decoder.readShort();
            case INTEGER:
                return decoder.readInteger();
            case LONG:
                return decoder.readLong();
            case FLOAT:
                return decoder.readFloat();
            case DOUBLE:
                return decoder.readDouble();
            case BIG_INTEGER:
                return new BigInteger(decoder.readBytes());
            case BIG_DECIMAL:
                int precision = decoder.readInteger();
                int scale = decoder.readInteger();
                BigInteger integer = new BigInteger(decoder.readBytes());
                MathContext context = new MathContext(precision);
                return new BigDecimal(integer, scale, context);
            default:
                throw new DeserializerException("Unhandled data type " + dataType);
        }
    }
}
