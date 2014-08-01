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

import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MessagingTest {

    @Test
    public void make_a_sum() throws InterruptedException {

    final AtomicInteger counter = new AtomicInteger(0);
    final AtomicBoolean stopCondition = new AtomicBoolean(false);
    final int MAX = 1000;

    MessagingEnvironment environment = MessagingEnvironment.newMessagingEnvironment();
    Topic topic = environment.topic();
    topic.registerListener(new MessagingFunction() {
        @Override
        public void apply(Object message) {
            if (counter.addAndGet((Integer) message) >= MAX) {
                stopCondition.set(true);
            }
        }
    });

    for (int i = 0; i < 1000; i++) {
        topic.send(1);
    }
    while (!stopCondition.get()) {
        // Just wait
    }
    assertThat(counter.get() >= MAX, is(true));
    environment.shutdown();
    }
}
