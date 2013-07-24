/**
 * Copyright (C) 2010 Mathieu Carbou <mathieu.carbou@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mycila.jmx.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class ExceptionUtils {

    private ExceptionUtils() {
    }

    public static Exception rethrowOrWrap(Throwable e) {
        if (e instanceof Error) throw (Error) e;
        if (e instanceof InvocationTargetException) e = ((InvocationTargetException) e).getTargetException();
        if (e instanceof RuntimeException) throw (RuntimeException) e;
        if (e instanceof Exception) return (Exception) e;
        Exception ee = new Exception(e.getMessage(), e);
        ee.setStackTrace(e.getStackTrace());
        return ee;
    }

    public static RuntimeException rethrow(Throwable e) {
        if (e instanceof InvocationTargetException)
            e = ((InvocationTargetException) e).getTargetException();
        if (e instanceof Error) throw (Error) e;
        if (e instanceof RuntimeException) throw (RuntimeException) e;
        RuntimeException ee = new RuntimeException(e.getMessage(), e);
        ee.setStackTrace(e.getStackTrace());
        throw ee;
    }

    public static String asString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.close();
        return sw.toString();
    }
}
