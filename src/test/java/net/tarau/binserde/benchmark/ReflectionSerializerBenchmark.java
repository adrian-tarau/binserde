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

package net.tarau.binserde.benchmark;

import net.tarau.binserde.SerializerFactory;
import net.tarau.binserde.dto.Address;
import net.tarau.binserde.dto.DtoUtils;
import net.tarau.binserde.io.Decoder;
import net.tarau.binserde.io.Encoder;
import net.tarau.binserde.io.OutputStreamEncoder;
import net.tarau.binserde.metadata.MemoryRegistry;
import net.tarau.binserde.serializer.ReflectionSerializer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ReflectionSerializerBenchmark extends AbstractBenchmark {

    private SerializerFactory serializerFactory;
    private ByteArrayOutputStream outputStream;
    private Encoder encoder;
    private Decoder decoder;

    public void setupInvocation() throws Exception {
        super.setupInvocation();
        outputStream = new ByteArrayOutputStream(200);
        encoder = new OutputStreamEncoder(outputStream);
    }

    public void setupIteration() throws Exception {
        super.setupIteration();
        serializerFactory = SerializerFactory.getInstance();
        serializerFactory.setRegistry(new MemoryRegistry());
        DtoUtils.init();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void address(Blackhole blackhole) throws IOException {
        Address address = Address.create();
        ReflectionSerializer<Address> serializer = new ReflectionSerializer<>(Address.class);
        serializer.serialize(address, encoder);
        blackhole.consume(encoder);
        encoder.close();
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder().include(ReflectionSerializerBenchmark.class.getSimpleName()).build();
        new Runner(opt).run();
    }
}
