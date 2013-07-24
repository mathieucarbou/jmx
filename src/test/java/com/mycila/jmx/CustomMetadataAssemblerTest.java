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
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.management.Attribute;
import javax.management.ObjectName;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
@RunWith(JUnit4.class)
public final class CustomMetadataAssemblerTest extends JmxTest {

    static String gone;

    @Override
    protected JmxMetadataAssembler getMetadataAssembler() {
        return new CustomMetadataAssembler()
                .addAttribute(MyClass.class, "rw")
                .addProperty(MyClass.class, "prop")
                .addOperation(MyClass.class, "go");
    }

    @Test
    public void test() throws Exception {
        ObjectName on = register(new MyClass());
        assertEquals("value", server.getAttribute(on, "Prop"));
        server.setAttribute(on, new Attribute("Prop", "new value"));
        assertEquals("new value", server.getAttribute(on, "Prop"));
        assertEquals("new value", server.getAttribute(on, "rw"));
        assertNull(gone);
        server.invoke(on, "go", new Object[]{"shopping"}, new String[]{String.class.getName()});
        assertEquals("shopping", gone);
    }

    public static class MyClass {
        public String rw = "value";

        public String getProp() {
            return rw;
        }

        public void setProp(String rw) {
            this.rw = rw;
        }

        public void go(String where) {
            gone = where;
        }
    }
}
