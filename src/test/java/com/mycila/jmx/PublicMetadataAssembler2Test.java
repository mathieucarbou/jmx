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

import org.junit.Test;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import static com.mycila.jmx.Throws.fire;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */

public final class PublicMetadataAssembler2Test extends JmxTest {

    @Override
    protected JmxMetadataAssembler getMetadataAssembler() {
        return new PublicMetadataAssembler(false);
    }

    /* OBJECT */

    @Test
    public void not_export_object_props() throws Exception {
        final ObjectName on = register(new Object());
        assertThat(new Code() {
            public void run() throws Throwable {
                server.getAttribute(on, "Class");
            }
        }, fire(AttributeNotFoundException.class, "Class"));
    }

    @Test
    public void export_prop_non_public() throws Exception {
        final ObjectName on = register(new Object() {
            private String name = "mat";

            public String getName() {
                return name;
            }

            private void setName(String name) {
                this.name = name;
            }
        });
        assertEquals("mat", server.getAttribute(on, "Name"));
        assertThat(new Code() {
            public void run() throws Throwable {
                server.setAttribute(on, new Attribute("Name", "new value"));
            }
        }, fire(ReflectionException.class, "Property not writable: Name"));
    }

    /* NO PROPS */

    @Test
    public void no_props() throws Exception {
        class A {
        }
        final ObjectName on = register(new A());
        assertEquals(0, server.getMBeanInfo(on).getAttributes().length);
    }

    /* INHERITANCE */

    @Test
    public void inheritance() throws Exception {
        class A {
            String getName() {
                return null;
            }
        }
        final ObjectName on = register(new A() {
            @Override
            public String getName() {
                return "mat";
            }
        });
        assertEquals("mat", server.getAttribute(on, "Name"));
    }


}