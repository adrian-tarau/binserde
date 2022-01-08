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

package com.github.binserde.dto;

import com.github.binserde.annotation.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Tag(200)
public class Order {

    private Customer customer;
    private Address shipping;

    private List<Entry> entries = new ArrayList<>();

    public Customer getCustomer() {
        return customer;
    }

    public Order setCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public Address getShipping() {
        return shipping;
    }

    public Order setShipping(Address shipping) {
        this.shipping = shipping;
        return this;
    }

    public Collection<Entry> getEntries() {
        return entries;
    }

    public Order setEntries(List<Entry> entries) {
        this.entries = entries;
        return this;
    }

    @Tag(201)
    public static class Entry {

        private Product product;
        private double amount;
        private float count;

        public Product getProduct() {
            return product;
        }

        public Entry setProduct(Product product) {
            this.product = product;
            return this;
        }

        public double getAmount() {
            return amount;
        }

        public Entry setAmount(double amount) {
            this.amount = amount;
            return this;
        }

        public float getCount() {
            return count;
        }

        public Entry setCount(float count) {
            this.count = count;
            return this;
        }

        public static Entry create() {
            return new Entry().setProduct(Product.create()).setAmount(10 + 20 * Math.random())
                    .setCount((float) (1 + 5 * Math.random()));
        }
    }

    public static Order create(int entries) {
        Order order = new Order().setCustomer(Customer.create()).setShipping(Address.create());
        List<Entry> list = new ArrayList<>();
        for (int index = 0; index < entries; index++) {
            list.add(Entry.create());
        }
        order.setEntries(list);
        return order;
    }
}
