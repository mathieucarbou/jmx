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

package com.mycila.jmx;

import com.mycila.jmx.export.MycilaJmxExporter;
import com.mycila.jmx.export.JmxExporter;
import mx4j.tools.adaptor.http.HttpAdaptor;
import mx4j.tools.adaptor.http.XSLTProcessor;

import java.util.concurrent.CountDownLatch;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class AnnotSample {

    public static void main(String[] args) throws Exception {
        JmxExporter jmxExporter = new MycilaJmxExporter();

        MyObject3 object3 = new MyObject3();
        jmxExporter.register(object3);

        HttpAdaptor httpAdaptor = new HttpAdaptor(80, "localhost");
        httpAdaptor.setProcessor(new XSLTProcessor());
        jmxExporter.register(httpAdaptor);
        httpAdaptor.start();
        
        new CountDownLatch(1).await();
    }

    public static class MyObject {
        public final String abc = "abc1";
        @Deprecated
        public final String ghi = "ghi";

        @Deprecated
        public void setName(String name) {
        }
    }

    public static class MyObject3 extends MyObject {
        public String abc = "abc2";
        String def = "def";
        public final String jkl = "jkl";

        public String getJkl() {
            return jkl;
        }

        public String getAbc() {
            return abc;
        }

        public void setAbc(String abc) {
            this.abc = abc;
        }

        public void start() {
        }

        public void isA() {
        }

        public void setA() {
        }

        public void getA() {
        }

        public int getVal() {
            return 0;
        }

        @Deprecated
        public void setVal(int val) {
        }

        public String getName() {
            return "hello";
        }
    }
}