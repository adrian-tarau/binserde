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

import java.util.*;

public class CollectionTypes {

    private List<Integer> list = new ArrayList<>();
    private Set<String> set = new HashSet<>();
    private SortedSet<String> sortedSet = new TreeSet<>();
    private Queue<Long> queue = new ArrayDeque<>();
    private Deque<Double> dequeue = new ArrayDeque<>();
    private Map<Integer, String> map = new HashMap<>();
    private SortedMap<Integer, String> sortedMap = new TreeMap<>();

    public List<Integer> getList() {
        return list;
    }

    public CollectionTypes setList(List<Integer> list) {
        this.list = list;
        return this;
    }

    public Set<String> getSet() {
        return set;
    }

    public CollectionTypes setSet(Set<String> set) {
        this.set = set;
        return this;
    }

    public SortedSet<String> getSortedSet() {
        return sortedSet;
    }

    public CollectionTypes setSortedSet(SortedSet<String> sortedSet) {
        this.sortedSet = sortedSet;
        return this;
    }

    public Queue<Long> getQueue() {
        return queue;
    }

    public CollectionTypes setQueue(Queue<Long> queue) {
        this.queue = queue;
        return this;
    }

    public Deque<Double> getDequeue() {
        return dequeue;
    }

    public CollectionTypes setDequeue(Deque<Double> dequeue) {
        this.dequeue = dequeue;
        return this;
    }

    public Map<Integer, String> getMap() {
        return map;
    }

    public CollectionTypes setMap(Map<Integer, String> map) {
        this.map = map;
        return this;
    }

    public SortedMap<Integer, String> getSortedMap() {
        return sortedMap;
    }

    public CollectionTypes setSortedMap(Map<Integer, String> sortedMap) {
        this.sortedMap = new TreeMap<>();
        this.sortedMap.putAll(sortedMap);
        return this;
    }

    public static CollectionTypes create() {
        return new CollectionTypes().setList(Arrays.asList(1, 2, 3))
                .setSet(new HashSet<>(Arrays.asList("a", "b", "c")))
                .setSortedSet(new TreeSet<>(Arrays.asList("a", "b", "c")))
                .setQueue(new ArrayDeque<>(Arrays.asList(10L, 20L, 30L)))
                .setDequeue(new ArrayDeque<>(Arrays.asList(10d, 20d, 30d)))
                .setMap(Map.of(1, "v1", 2, "v2"))
                .setSortedMap(Map.of(1, "v1", 2, "v2"));
    }
}
