# Copyright 2012-2013 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#     http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

module Messaging

import java.lang.Thread
import java.util.concurrent
import gololang.concurrent.messaging.MessagingEnvironment

local function receiver = |namespace, i, message| ->
  println(">>> (namespace: " + namespace + ") >>> receiver " + i + " : " + message)

function main = |args| {

  let env = MessagingEnvironment.builder(): withFixedThreadPool()
  let topic = env: topic("golo")

  println("namespace: " + topic: getNamespace())

  foreach i in range(0, 10) {
    println("registering: receiver " + i)
    let receiver_i = ^receiver: bindTo(topic: getNamespace()): bindTo(i)
    topic: register(receiver_i)
  }

  println("sending")

  foreach i in range(0, 10) {
    topic: send("[" + i + "]")
  }

  Thread.sleep(1000_L)

  let subtopic = env: topic("golo:messaging")

  println("namespace: " + subtopic: getNamespace())

  foreach i in range(0, 5) {
    println("registering: receiver " + i)
    let receiver_i = ^receiver: bindTo(subtopic: getNamespace()): bindTo(i)
    subtopic: register(receiver_i)
  }

  println("sending")

  foreach i in range(0, 10) {
    subtopic: send("[" + i + "]")
  }

  Thread.sleep(1000_L)

  println("spreading")

  foreach i in range(0, 10) {
    topic: spread("[" + i + "]")
  }

  Thread.sleep(1000_L)

  env: bury(subtopic)

  println("spreading")

  foreach i in range(0, 10) {
    topic: spread("[" + i + "]")
  }

  Thread.sleep(1000_L)

  println("spreading")

  foreach i in range(0, 10) {
    env: spread("golo", "[" + i + "]")
  }

  Thread.sleep(1000_L)

  println("spreading")

  foreach i in range(0, 10) {
    env: spread("[" + i + "]")
  }

  Thread.sleep(1000_L)

  let newtopic = env: topic()

  println("namespace: " + newtopic: getNamespace())

  foreach i in range(0, 5) {
    println("registering: receiver " + i)
    let receiver_i = ^receiver: bindTo(newtopic: getNamespace()): bindTo(i)
    newtopic: register(receiver_i)
  }

  println("spreading")

  foreach i in range(0, 10) {
    env: spread("[" + i + "]")
  }

  env: shutdown()
}

