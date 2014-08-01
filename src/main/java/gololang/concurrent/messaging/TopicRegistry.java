/*
 * Copyright 2012-2013 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gololang.concurrent.messaging;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TopicRegistry is a abstraction to store the topics in a thread-safe way
 * using a ConcurrentHaspMap.
 */
public class TopicRegistry {

    private ConcurrentHashMap<String, Topic> registry = new ConcurrentHashMap();

    /**
     * TopicRegistry constructor
     */
    public TopicRegistry() {
        super();
    }

    /**
     * Puts a topic - identified by its namespace - to the registry
     *
     * @param namespace the topic namespace.
     * @param topic the topic object.
     */
    public void put(String namespace, Topic topic) {
        registry.putIfAbsent(namespace, topic);
    }

    /**
     * Gets a topic - identified by its namespace
     *
     * @param namespace the topic namespace.
     * @return the concerned topic or null if not included in the registry.
     */
    public Topic get(String namespace) {
        return registry.get(namespace);
    }

    /**
     * Removes a topic - identified by its namespace - from the registry
     *
     * @param namespace the topic namespace.
     * @return the topic removed or null if not included in the registry.
     */
    public Topic remove(String namespace) {
        return registry.remove(namespace);
    }

    /**
     * Returns true if the registry contains the topic
     *
     * @param namespace the topic namespace to check.
     * @return true if the registry contains the topic.
     */
    public boolean contains(String namespace) {
        return registry.containsKey(namespace);
    }

    /**
     * Returns a set of topic namespaces
     *
     * @return a set of topic namespaces.
     */
    public Set<String> namespaces() {
        return registry.keySet();
    }
}