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

import org.junit.Test;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import static com.mycila.jmx.Throws.fire;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class MycilaJmxExporterTest {

    @Test
    public void test_beavior() throws Exception {
        final MycilaJmxExporter exporter = new MycilaJmxExporter();
        exporter.setMetadataAssembler(new PublicMetadataAssembler());
        exporter.setExportBehavior(ExportBehavior.FAIL_ON_EXISTING);

        final ObjectName on = ObjectName.getInstance("a:type=b");

        exporter.register(new Object() {
            public String val = "0";
        }, on);
        assertTrue(exporter.getMBeanServer().isRegistered(on));
        assertEquals("0", exporter.getMBeanServer().getAttribute(on, "val"));

        assertThat(new Code() {
            public void run() throws Throwable {
                exporter.register(new Object(), on);
            }
        }, fire(JmxExportException.class, "Unable to register MBean [com.mycila.jmx.ContextualDynamicMBean] with object name [a:type=b]"));

        exporter.setExportBehavior(ExportBehavior.SKIP_EXISTING);
        exporter.register(new Object() {
            public String val = "1";
        }, on);
        assertTrue(exporter.getMBeanServer().isRegistered(on));
        assertEquals("0", exporter.getMBeanServer().getAttribute(on, "val"));

        exporter.setExportBehavior(ExportBehavior.REPLACE_EXISTING);
        exporter.register(new Object() {
            public String val = "1";
        }, on);
        assertTrue(exporter.getMBeanServer().isRegistered(on));
        assertEquals("1", exporter.getMBeanServer().getAttribute(on, "val"));
    }

    @Test
    public void test_registration() throws Exception {
        final MycilaJmxExporter exporter = new MycilaJmxExporter();
        exporter.setMetadataAssembler(new PublicMetadataAssembler());
        ObjectName a = exporter.register(new Object());
        assertFalse(exporter.getMBeanServer().isRegistered(ObjectName.getInstance("inexisting:type=a")));
        exporter.unregister(ObjectName.getInstance("inexisting:type=a"));
        exporter.unregister(a);
        assertFalse(exporter.getMBeanServer().isRegistered(a));
    }

    @Test
    public void test_unique() throws Exception {
        final MycilaJmxExporter exporter = new MycilaJmxExporter();
        exporter.setMetadataAssembler(new PublicMetadataAssembler());
        exporter.setEnsureUnique(true);

        Object o1 = new Object();
        Object o2 = new Object();
        ObjectName on1 = exporter.register(o1);
        ObjectName on2 = exporter.register(o2);
        assertEquals(on1.getKeyProperty("identity"), "" + Integer.toHexString(o1.hashCode()));
        assertEquals(on2.getKeyProperty("identity"), "" + Integer.toHexString(o2.hashCode()));
    }

    @Test
    public void test_malformed() throws Exception {
        final MycilaJmxExporter exporter = new MycilaJmxExporter();
        exporter.setMetadataAssembler(new PublicMetadataAssembler());
        exporter.setEnsureUnique(true);

        assertThat(new Code() {
            public void run() throws Throwable {
                exporter.register(new JmxSelfNaming() {
                    @Override
                    public ObjectName getObjectName() throws MalformedObjectNameException {
                        return ObjectName.getInstance("----");
                    }
                });
            }
        }, fire(JmxExportException.class, "Unable to generate ObjectName for MBean [com.mycila.jmx.MycilaJmxExporterTest$5$1]"));
    }

}
