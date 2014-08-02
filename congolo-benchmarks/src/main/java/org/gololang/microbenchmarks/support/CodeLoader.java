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

package org.gololang.microbenchmarks.support;

import fr.insalyon.citi.golo.compiler.GoloClassLoader;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class CodeLoader {

  private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

  public MethodHandle golo(String file, String func, int argCount) {
    GoloClassLoader classLoader = new GoloClassLoader();
    String filename = "snippets/golo/" + file + ".golo";
    Class<?> module = classLoader.load(filename, CodeLoader.class.getResourceAsStream("/" + filename));
    try {
      return LOOKUP.findStatic(module, func, MethodType.genericMethodType(argCount));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}