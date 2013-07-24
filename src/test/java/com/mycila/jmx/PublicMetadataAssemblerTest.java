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
import javax.management.RuntimeMBeanException;
import javax.management.RuntimeOperationsException;
import java.util.List;

import static com.mycila.jmx.Throws.fire;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */

public final class PublicMetadataAssemblerTest extends JmxTest {

    @Override
    protected JmxMetadataAssembler getMetadataAssembler() {
        return new PublicMetadataAssembler();
    }

    /* ATTRIBUTES */

    @Test
    public void export_fields_public_rw_get() throws Exception {
        ObjectName on = register(new Object() {
            public String rw = "value";
        });
        assertEquals("value", server.getAttribute(on, "rw"));
    }

    @Test
    public void export_fields_public_rw_set() throws Exception {
        ObjectName on = register(new Object() {
            public String rw = "value";
        });
        server.setAttribute(on, new Attribute("rw", "new value"));
        assertEquals("new value", server.getAttribute(on, "rw"));
    }

    @Test
    public void export_fields_public_ro_get() throws Exception {
        ObjectName on = register(new Object() {
            public final String ro = "value";
        });
        assertEquals("value", server.getAttribute(on, "ro"));
    }

    @Test
    public void export_fields_public_ro_set() throws Exception {
        final ObjectName on = register(new Object() {
            public final String ro = "value";
        });
        assertThat(new Code() {
            public void run() throws Throwable {
                server.setAttribute(on, new Attribute("ro", "new value"));
            }
        }, fire(ReflectionException.class, "Attribute not writable: ro"));
    }

    @Test
    public void export_fields_non_public() throws Exception {
        final ObjectName on = register(new Object() {
            String hidden1 = "";
            private String hidden2 = "";
            protected String hidden3 = "";
        });
        assertThat(new Code() {
            public void run() throws Throwable {
                server.getAttribute(on, "hidden1");
            }
        }, fire(AttributeNotFoundException.class, "hidden1"));
        assertThat(new Code() {
            public void run() throws Throwable {
                server.getAttribute(on, "hidden2");
            }
        }, fire(AttributeNotFoundException.class, "hidden2"));
        assertThat(new Code() {
            public void run() throws Throwable {
                server.getAttribute(on, "hidden3");
            }
        }, fire(AttributeNotFoundException.class, "hidden3"));
    }

    /* OPERATIONS */

    @Test
    public void export_op_public() throws Exception {
        final ObjectName on = register(new Object() {
            public String toString() {
                return "hello";
            }
        });
        assertEquals("hello", server.invoke(on, "toString", new Object[0], new String[0]));
    }

    @Test
    public void export_op_public_err() throws Exception {
        final ObjectName on = register(new Object() {
            public String toString() {
                throw new IllegalArgumentException("bouh!");
            }
        });
        assertThat(new Code() {
            public void run() throws Throwable {
                server.invoke(on, "toString", new Object[0], new String[0]);
            }
        }, fire(RuntimeMBeanException.class, "java.lang.IllegalArgumentException: bouh!"));
    }

    @Test
    public void export_op_non_public() throws Exception {
        final ObjectName on = register(new Object() {
            void hidden1() {
            }

            protected void hidden2() {
            }

            private void hidden3() {
            }
        });
        assertThat(new Code() {
            public void run() throws Throwable {
                server.invoke(on, "hidden1", new Object[0], new String[0]);
            }
        }, fire(RuntimeOperationsException.class, "An exception occurred while trying to find method hidden1 on DefaultDynamicMBean"));
        assertThat(new Code() {
            public void run() throws Throwable {
                server.invoke(on, "hidden2", new Object[0], new String[0]);
            }
        }, fire(RuntimeOperationsException.class, "An exception occurred while trying to find method hidden2 on DefaultDynamicMBean"));
        assertThat(new Code() {
            public void run() throws Throwable {
                server.invoke(on, "hidden3", new Object[0], new String[0]);
            }
        }, fire(RuntimeOperationsException.class, "An exception occurred while trying to find method hidden3 on DefaultDynamicMBean"));
    }

    /* PROPERTIES */

    @Test
    public void export_prop_public_rw() throws Exception {
        final ObjectName on = register(new Object() {
            private String name = "mat";

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        });
        assertEquals("mat", server.getAttribute(on, "Name"));
        server.setAttribute(on, new Attribute("Name", "cass"));
        assertEquals("cass", server.getAttribute(on, "Name"));
    }

    @Test
    public void export_props_public_rw() throws Exception {
        final ObjectName on = register(new Object() {
            private String name = "mat";

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        });
        List<Attribute> list = server.getAttributes(on, new String[]{"Name", "Class"}).asList();
        assertEquals("mat", list.get(0).getValue());
        assertTrue(list.get(1).getValue().toString().contains(PublicMetadataAssemblerTest.class.getName()));
    }

    @Test
    public void export_prop_public_ro() throws Exception {
        final ObjectName on = register(new Object() {
            private String name = "mat";

            public String getName() {
                return name;
            }
        });
        assertEquals("mat", server.getAttribute(on, "Name"));
        assertThat(new Code() {
            public void run() throws Throwable {
                server.setAttribute(on, new Attribute("Name", "new value"));
            }
        }, fire(ReflectionException.class, "Property not writable: Name"));
    }

    @Test
    public void export_prop_public_wo() throws Exception {
        final ObjectName on = register(new Object() {
            private String name = "mat";

            public void setName(String name) {
                this.name = name;
            }
        });
        server.setAttribute(on, new Attribute("Name", "cass"));
        assertThat(new Code() {
            public void run() throws Throwable {
                server.getAttribute(on, "Name");
            }
        }, fire(ReflectionException.class, "Property not readable: Name"));
    }

    @Test
    public void export_prop_non_public() throws Exception {
        final ObjectName on = register(new Object() {
            private String name = "mat";

            private String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        });
        assertThat(new Code() {
            public void run() throws Throwable {
                server.getAttribute(on, "Name");
            }
        }, fire(ReflectionException.class, "Property not readable: Name"));
    }

    @Test
    public void export_object_props() throws Exception {
        final ObjectName on = register(new Object() {
        });
        server.getAttribute(on, "Class");
    }

}
