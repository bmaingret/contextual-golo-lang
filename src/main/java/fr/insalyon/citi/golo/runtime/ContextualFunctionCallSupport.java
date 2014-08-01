/*
 * Copyright 2012-2014 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
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

package fr.insalyon.citi.golo.runtime;

import fr.insalyon.congolo.decisionmaker.DecisionMakerManager;
import fr.insalyon.congolo.event.EnvironmentProvider;
import fr.insalyon.congolo.event.Event;
import fr.insalyon.congolo.event.FunctionInvocationEvent;
import gololang.concurrent.messaging.MessagingEnvironment;
import gololang.concurrent.messaging.Topic;

import java.lang.invoke.*;

import static java.lang.invoke.MethodHandles.Lookup;
import static java.lang.invoke.MethodHandles.guardWithTest;
import static java.lang.invoke.MethodType.methodType;

public final class ContextualFunctionCallSupport {

  public static final String DM_TOPIC_NAME = "congolo:internal:decision";
  public static final String PROXY_TOPIC_NAME = "congolo:internal:proxy";
  public static final Object LOCK = new Object();
  private static final MethodHandle CALLBACK;
  private static final MethodHandle PROXY;
  private static final MethodHandle GUARD;
  private static final MessagingEnvironment env = EnvironmentProvider.getEnvironment();
  private static MethodHandle handleFromDM = null;
  private static final Topic DM_TOPIC = env.topic(DM_TOPIC_NAME);

  static {
    try {
      MethodHandles.Lookup lookup = MethodHandles.lookup();
      CALLBACK = lookup.findStatic(
          ContextualFunctionCallSupport.class,
          "callback",
          methodType(void.class, Event.class));
      PROXY = lookup.findStatic(
          ContextualFunctionCallSupport.class,
          "proxy",
          MethodType.methodType(Object.class, ContextualFunctionCallSite.class, Object[].class));
      GUARD = lookup.findStatic(
          ContextualFunctionCallSupport.class,
          "guard",
          methodType(boolean.class));
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new Error("Could not bootstrap the required method handles", e);
    }

    Topic topic = env.topic(PROXY_TOPIC_NAME);
    topic.register(CALLBACK);
  }

  public static CallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type) {
    ContextualFunctionCallSite callSite = new ContextualFunctionCallSite(caller, name, type);
    MethodHandle fallbackHandle = PROXY
        .bindTo(callSite)
        .asCollector(Object[].class, type.parameterCount())
        .asType(type);
    callSite.fallback = fallbackHandle;
    callSite.setTarget(fallbackHandle);
    return callSite;
  }

  public static Object proxy(ContextualFunctionCallSite callSite, Object[] args) throws Throwable {
    String functionName = callSite.name;
    MethodType type = callSite.type();
    Lookup caller = callSite.callerLookup;
    Class<?> callerClass = caller.lookupClass();

    if (null != callerClass) {
      Event event = new FunctionInvocationEvent(
          callerClass,
          functionName,
          args);
/*
      DM_TOPIC.send(event);

      synchronized (LOCK) {
        while (null == handleFromDM) {
          LOCK.wait();
        }
      }
      MethodHandle handle = handleFromDM;
      handleFromDM = null;
*/
      MethodHandle handle = ((FunctionInvocationEvent)DecisionMakerManager.getDecisionMaker().decide(event)).getHandle();
      MethodHandle guard = GUARD;
      MethodHandle root = guardWithTest(guard, handle, callSite.fallback);
      callSite.setTarget(root);
      return handle.invokeWithArguments(args);
    } else {
      return null;
    }
  }

  public static boolean guard() {
    return false;
  }

  public static void callback(Event event) {
    handleFromDM = ((FunctionInvocationEvent) event).getHandle();
    synchronized (LOCK) {
      LOCK.notifyAll();
    }
  }

  static class ContextualFunctionCallSite extends MutableCallSite {
    final Lookup callerLookup;
    final String name;
    MethodHandle fallback;

    ContextualFunctionCallSite(MethodHandles.Lookup callerLookup, String name, MethodType type) {
      super(type);
      this.callerLookup = callerLookup;
      this.name = name;
    }
  }
}
