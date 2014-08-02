/*
 * Copyright (c) 2005, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package benchmark.fr.insalyon.congolo;

import org.openjdk.jmh.annotations.*;
import org.gololang.microbenchmarks.support.CodeLoader;

import java.lang.invoke.MethodHandle;
import java.util.concurrent.TimeUnit;

import fr.insalyon.congolo.context.*;
import fr.insalyon.congolo.decisionmaker.*;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1)
@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 20, time = 1, timeUnit = TimeUnit.SECONDS)
public class CongoloMicroBenchmark {
  
  @State(Scope.Thread)
  static public class ContextualState {
    String arg1 = "arg1";
    int arg2 = 2;
  }

  @State(Scope.Thread)
  static public class GoloState {
    MethodHandle contextTest;
    MethodHandle noContextTest;
    MethodHandle contextTestMultiple;
    MethodHandle noContextTestMultiple;
    
    ContextManager cm;
    DecisionMaker dm;
    Context contextLTE;
    Context contextWiFi;

    @Setup(Level.Trial)
    public void prepare() {
      contextTest = new CodeLoader().golo("congolo", "contextTestInvoke", 2);
      noContextTest = new CodeLoader().golo("congolo", "noContextTestInvoke", 2);
      contextTestMultiple = new CodeLoader().golo("congolo", "contextTest_1Invoke", 0);
      noContextTestMultiple = new CodeLoader().golo("congolo", "noContextTest_1Invoke", 0);

      cm = ContextManager.INSTANCE;
      contextLTE = new LTEConnectivityContext((Object)new Boolean(true));
      contextWiFi = new WiFiConnectivityContext((Object)new Boolean(false));
      cm.register("Benchmark", contextLTE);
      dm = new DefaultDecisionMaker();
      DecisionMakerManager.setDecisionMaker(dm);
    }

    @TearDown(Level.Iteration)
    public void switchContext(){
      ((ConcreteValueContext)contextLTE).setValue(!(Boolean)((ConcreteValueContext)contextLTE).getValue());
      ((ConcreteValueContext)contextWiFi).setValue(!(Boolean)((ConcreteValueContext)contextLTE).getValue());
    }

    @TearDown(Level.Trial)
    public void cleanup(){
      fr.insalyon.congolo.event.EnvironmentProvider.getEnvironment().shutdown();
    }
  }

  @Benchmark
  public Object golo_contextual(ContextualState contextualState, GoloState goloState) throws Throwable {
    return goloState.contextTest.invokeExact((Object) contextualState.arg1, (Object) contextualState.arg2);
  }

  @Benchmark
  public Object golo_contextual_baseline(ContextualState contextualState, GoloState goloState) throws Throwable {
    return goloState.noContextTest.invokeExact((Object) contextualState.arg1, (Object) contextualState.arg2);
  }

  @Benchmark
  public Object golo_contextual_multiple(ContextualState contextualState, GoloState goloState) throws Throwable {
    return goloState.contextTestMultiple.invoke();
  }

  @Benchmark
  public Object golo_contextual_multiple_baseline(ContextualState contextualState, GoloState goloState) throws Throwable {
    return goloState.noContextTestMultiple.invoke();
  }
}
