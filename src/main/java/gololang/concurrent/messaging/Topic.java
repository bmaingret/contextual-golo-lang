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

import java.lang.invoke.MethodHandle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.invoke.MethodHandleProxies.asInterfaceInstance;

/**
 * A topic is the communication endpoint to a set of registered listening messaging functions.
 * <p/>
 * A topic is obtained from a messaging environment when spawned. It can then be used to send
 * messages that will be eventually processed by the set of target registered functions. Messages
 * are delivered using a thread poll not considering message order.
 */
public final class Topic {

    private final MessagingEnvironment environment;
    private final ExecutorService executor;
    private String namespace;

    private final ConcurrentHashMap<MessagingFunction, MessagingFunction> functions = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<Object> queue = new ConcurrentLinkedQueue<>();

    private final AtomicBoolean running = new AtomicBoolean(false);

    /**
     * Checks if the namespace is correct. Throws an IllegalArgumentException if not.
     *
     * The namespace has to obey to the following grammar:
     * <pre>
     * {@code [a-zA-Z0-9]+[:a-zA-Z0-9]*}
     * </pre>
     *
     * Examples:
     * golo:messaging1:topic2subtopic3 -> correct
     * golo>test!:notgood -> uncorrect
     *
     * @param namespace the namespace to check.
     * @return the namespace.
     */
    public static String checkNamespace(String namespace) {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9]+[:a-zA-Z0-9]*");
        Matcher matcher = pattern.matcher(namespace);
        if (!matcher.matches()) throw new IllegalArgumentException("namespace not correct");
        return namespace;
    }

    /**
     * Topic constructor.
     *
     * @param environment the messaging environment the topic is assigned to.
     * @param executor    the executor to dispatch the asynchronous message handling jobs to.
     */
    public Topic(MessagingEnvironment environment, ExecutorService executor) {
        this.environment = environment;
        this.executor = executor;
        this.namespace = Integer.toString(hashCode());
    }

    /**
     * Topic constructor.
     *
     * @param environment the messaging environment the topic is assigned to.
     * @param executor    the executor to dispatch the asynchronous message handling jobs to.
     * @param namespace   the name space of the topic
     */
    public Topic(MessagingEnvironment environment, ExecutorService executor, String namespace) {
        this.environment = environment;
        this.executor = executor;
        this.namespace = checkNamespace(namespace);
    }

    /**
     * Returns the topic namespace
     *
     * @return the topic namespace.
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Reinitialize the topic namespace
     *
     * @param the new topic namespace.
     */
    public void setNamespace(String namespace) {
        checkNamespace(namespace);
        if (!this.namespace.equals(namespace)) {
            environment.bury(this);
            this.namespace = namespace;
            environment.topic(this);
        }
    }

    /**
     * Registers an new listening function.
     *
     * @param handle the messaging function target.
     */
    public void register(MethodHandle handle) {
        registerListener(asInterfaceInstance(MessagingFunction.class, handle));
    }

    /**
     * Registers an new listening function.
     *
     * @param function the messaging function target.
     */
    public void registerListener(MessagingFunction function) {
        functions.putIfAbsent(function, function);
    }

    /**
     * Unregisters a listening function.
     *
     * @param handle the messaging function target.
     */
    public void unregister(MethodHandle handle) {
        unregisterListener(asInterfaceInstance(MessagingFunction.class, handle));
    }

    /**
     * Unregisters a listening function.
     *
     * @param function the messaging function target.
     */
    public void unregisterListener(MessagingFunction function) {
        functions.remove(function);
    }

    private class Runner implements Runnable {
        MessagingFunction function;
        Object message;

        public Runner(MessagingFunction function, Object message) {
            this.function = function;
            this.message = message;
        }

        public void run() {
            try {
                function.apply(message);
            } finally {
                scheduleNext();
            }
        }
    }

    private void scheduleNext() {
        if (!queue.isEmpty() && running.compareAndSet(false, true)) {
            try {
                Object message = queue.poll();
                for (MessagingFunction function : functions.keySet()) {
                    executor.execute(new Runner(function, message));
                }
                running.set(false);
            } catch (Throwable t) {
                running.set(false);
                throw t;
            }
        }
    }

    /**
     * Sends a message to the set of listening messaging functions of the current local topic.
     * This method returns immediately as message processing is asynchronous.
     *
     * @param message the message of any type.
     * @return the same topic object.
     */
    public Topic send(Object message) {
        queue.offer(message);
        scheduleNext();
        return this;
    }

    /**
     * Spreads a message to the set of listening messaging functions of the current local topic
     * and all its subtopics (identified by a subspacename). This method returns immediately as
     * message processing is asynchronous.
     *
     * @param message the message of any type.
     * @return the same topic object.
     */
    public Topic spread(Object message) {
        environment.spread(getNamespace(), message);
        return this;
    }
}
