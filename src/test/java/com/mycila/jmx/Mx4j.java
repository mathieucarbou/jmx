/**
 * Copyright (C) 2010 Mycila (mathieu.carbou@gmail.com)
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
package com.mycila.jmx;

import mx4j.tools.adaptor.http.HttpAdaptor;
import mx4j.tools.adaptor.http.XSLTProcessor;

import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class Mx4j {

    private static HttpAdaptor httpAdaptor;

    static {
        try {
            httpAdaptor = new HttpAdaptor(8080, "localhost");
            httpAdaptor.setProcessor(new XSLTProcessor());
            ManagementFactory.getPlatformMBeanServer().registerMBean(httpAdaptor, ObjectName.getInstance("mx4j:type=HttpAdaptor"));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static void start() throws Exception {
        if (!httpAdaptor.isActive())
            httpAdaptor.start();
    }

    public static void stop() {
        if (httpAdaptor.isActive())
            httpAdaptor.stop();
    }
}
