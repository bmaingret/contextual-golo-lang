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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A messaging environment is an abstraction over a set of spawned functions that can asynchronously process messages
 * sent through topics.
 * <p/>
 * Each topic is internally associated to a set of messaging functions and a messages queue. The messaging environment maintains
 * an executor that dispatches message processing jobs over its thread pool.
 */
public final class MessagingEnvironment {

    private final ExecutorService executor;
    private final TopicRegistry registry;

    /**
     * Creates a new messaging environment using an executor.
     *
     * @param executor the executor.
     */
    public MessagingEnvironment(ExecutorService executor) {
        this.executor = executor;
        this.registry = new TopicRegistry();
    }

    /**
     * @return a new messaging environment with a cached thread pool.
     * @see java.util.concurrent.Executors#newCachedThreadPool()
     */
    public static MessagingEnvironment newMessagingEnvironment() {
        return new MessagingEnvironment(Executors.newCachedThreadPool());
    }

    /**
     * @return a messaging environment builder object.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * messaging environment builder objects exist mostly to provide a good-looking API in Golo.
     */
    public static class Builder {

        /**
         * @return a messaging environment with a cached thread pool.
         * @see java.util.concurrent.Executors#newCachedThreadPool()
         */
        public MessagingEnvironment withCachedThreadPool() {
            return newMessagingEnvironment();
        }

        /**
         * @param size the thread pool size.
         * @return a messaging environment with a fixed-size thread pool.
         * @see java.util.concurrent.Executors#newFixedThreadPool(int)
         */
        public MessagingEnvironment withFixedThreadPool(int size) {
            return new MessagingEnvironment(Executors.newFixedThreadPool(size));
        }

        /**
         * @return a messaging environment with a fixed-size thread pool in the number of available processors.
         * @see java.util.concurrent.Executors#newFixedThreadPool(int)
         * @see Runtime#availableProcessors()
         */
        public MessagingEnvironment withFixedThreadPool() {
            return withFixedThreadPool(Runtime.getRuntime().availableProcessors());
        }

        /**
         * @return a messaging environment with a single executor thread.
         */
        public MessagingEnvironment withSingleThreadExecutor() {
            return new MessagingEnvironment(Executors.newSingleThreadExecutor());
        }
    }

    /**
     * Spawns a messaging topic.
     *
     * @return a topic to send messages.
     */
    public Topic topic() {
        Topic topic = new Topic(this, executor);
        registry.put(Integer.toString(topic.hashCode()), topic);
        return topic;
    }

    /**
     * Spawns a messaging topic.
     *
     * @param namespace the topic name space.
     * @return a topic to send messages.
     */
    public Topic topic(String namespace) {
        if (registry.contains(namespace)) {
            return registry.get(namespace);
        } else {
            Topic topic = new Topic(this, executor, namespace);
            registry.put(namespace, topic);
            return topic;
        }
    }

    /**
     * Spawns a messaging topic.
     *
     * @param topic the topic.
     * @return a topic to send messages.
     */
    public Topic topic(Topic topic) {
        return topic(topic.getNamespace());
    }

    /**
     * Buries a messaging topic
     *
     * @param topic the topic.
     * @return the topic.
     */
    public Topic bury(Topic topic) {
        return bury(topic.getNamespace());
    }

    /**
     * Buries a messaging topic
     *
     * @param namespace the topic namespace.
     * @return the topic.
     */
    public Topic bury(String namespace) {
        return registry.remove(namespace);
    }

    /**
     * Spreads a message to the set of listening messaging functions of all topics.
     * This method returns immediately as message processing is asynchronous.
     *
     * @param message the message of any type.
     * @return the first topic of the environment.
     */
    public Topic spread(Object message) {
        return spread("", message);
    }

    /**
     * Spreads a message to the set of listening messaging functions of a topic and all subtopics
     * identified by a spacename prefix. This method returns immediately as message processing is asynchronous.
     *
     * @param prefix  the spacename prefix.
     * @param message the message of any type.
     * @return the first topic matching the prefix.
     */
    public Topic spread(String prefix, Object message) {
        Topic first = null;
        for (String namespace : registry.namespaces()) {
            if (namespace.startsWith(prefix)) {
                Topic topic = registry.get(namespace);
                if (first == null) first = topic;
                topic.send(message);
            }
        }
        return first;
    }

    /**
     * Shutdown the messaging environment.
     *
     * @return the same messaging environment object.
     * @see java.util.concurrent.ExecutorService#shutdown()
     */
    public MessagingEnvironment shutdown() {
        executor.shutdown();
        return this;
    }

    /**
     * Waits until all remaining messages have been processed.
     *
     * @param millis the delay.
     * @see java.util.concurrent.ExecutorService#awaitTermination(long, java.util.concurrent.TimeUnit)
     */
    public boolean awaitTermination(int millis) throws InterruptedException {
        return awaitTermination((long) millis);
    }

    /**
     * Waits until all remaining messages have been processed.
     *
     * @param millis the delay.
     * @see java.util.concurrent.ExecutorService#awaitTermination(long, java.util.concurrent.TimeUnit)
     */
    public boolean awaitTermination(long millis) throws InterruptedException {
        return awaitTermination(millis, TimeUnit.MILLISECONDS);
    }

    /**
     * Waits until all remaining messages have been processed.
     *
     * @param timeout the delay.
     * @param unit    the delay time unit.
     * @see java.util.concurrent.ExecutorService#awaitTermination(long, java.util.concurrent.TimeUnit)
     */
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executor.awaitTermination(timeout, unit);
    }

    /**
     * @see java.util.concurrent.ExecutorService#isShutdown()
     */
    public boolean isShutdown() {
        return executor.isShutdown();
    }

    /**
     * @see java.util.concurrent.ExecutorService#isTerminated()
     */
    public boolean isTerminated() {
        return executor.isTerminated();
    }
}
