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

package net.tarau.binserde.dto;

import java.time.ZonedDateTime;

public class Product {

    private String name;
    private String serialNumber;
    private float price;

    private ZonedDateTime created;
    private ZonedDateTime updated;

    public String getName() {
        return name;
    }

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public Product setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    public float getPrice() {
        return price;
    }

    public Product setPrice(float price) {
        this.price = price;
        return this;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public Product setCreated(ZonedDateTime created) {
        this.created = created;
        return this;
    }

    public ZonedDateTime getUpdated() {
        return updated;
    }

    public Product setUpdated(ZonedDateTime updated) {
        this.updated = updated;
        return this;
    }

    public static Product create() {
        return new Product().setCreated(ZonedDateTime.now()).setUpdated(ZonedDateTime.now())
                .setPrice((float) (20 + 10 * Math.random()))
                .setName(String.format("Product %05d", (int) (1 + 100 * Math.random())))
                .setSerialNumber(Long.toString(System.currentTimeMillis()));
    }
}
