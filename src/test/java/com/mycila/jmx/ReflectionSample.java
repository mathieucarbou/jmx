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

/*import com.mycila.jmx.export.CustomMetadataAssembler;
import com.mycila.jmx.export.MycilaJmxExporter;
import com.mycila.jmx.export.PublicMetadataAssembler;
import mx4j.tools.adaptor.http.HttpAdaptor;
import mx4j.tools.adaptor.http.XSLTProcessor;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.concurrent.CountDownLatch;*/

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class ReflectionSample {

    // old test with spring stuff

    /*public static void main(String[] args) throws Exception {

        CustomMetadataAssembler customMetadataAssembler = new CustomMetadataAssembler()
                .addAttribute(MyObject.class, "hello")
                .addAttribute(MyObject4.class, "hello2")
                .addOperation(MyObject4.class, "op")
                .addProperty(MyObject4.class, "mno");

        MycilaJmxExporter jmxExporter1 = new MycilaJmxExporter();
        jmxExporter1.setMetadataAssembler(customMetadataAssembler);
        jmxExporter1.register(new MyObject4());

        MycilaJmxExporter jmxExporter2 = new MycilaJmxExporter();
        jmxExporter2.setMetadataAssembler(new PublicMetadataAssembler());
        jmxExporter2.register(new MyObject3());

        HttpAdaptor httpAdaptor = new HttpAdaptor(8080, "localhost");
        httpAdaptor.setProcessor(new XSLTProcessor());
        ManagementFactory.getPlatformMBeanServer().registerMBean(httpAdaptor, ObjectName.getInstance("mx4j:type=HttpAdaptor"));
        httpAdaptor.start();

        ClassPathXmlApplicationContext c = new ClassPathXmlApplicationContext("/spring.xml");

        new CountDownLatch(1).await();
    }

    public static class MyObject {
        private String hello = "hello";
        private final String hello2 = "hello2";
        public final String abc = "abc1";
        @Deprecated
        public final String ghi = "ghi";

        @Deprecated
        public void setName(String name) {
        }

        public void setText(CharSequence c) {
        }

        public CharSequence getText() {
            return "hello UP";
        }

        private String getAbc() {
            return abc;
        }

        private void setMno(int a) {

        }
    }

    @ManagedResource(objectName = "bean:name=testBean4", description = "My Managed Bean")
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

        @ManagedAttribute(description = "Name")
        public String getName() {
            return "hello";
        }

        @Deprecated
        @ManagedAttribute(description = "Text")
        public String getText() {
            return "hello DOWN";
        }

        @ManagedAttribute(description = "Text")
        public void setText(String c) {
        }

        private void op(int a) {
        }

        int getMno() {
            return 0;
        }
    }

    public static class MyObject4 extends MyObject3 {

    }*/
}
