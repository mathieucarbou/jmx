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

package com.mycila.jmx.export;

import com.mycila.jmx.export.annotation.JmxBean;
import org.junit.Test;

import javax.management.Attribute;
import javax.management.ObjectName;

import static org.junit.Assert.*;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class DelegatingMetadataAssemblerTest extends JmxTest {

    @Override
    protected JmxMetadataAssembler getMetadataAssembler() {
        return new DiscoveringMetadataAssembler();
    }

    @Test
    public void export_fields_public_rw_get() throws Exception {
        ObjectName on = register(new MyClass());
        assertEquals("value", server.getAttribute(on, "Prop"));
        server.setAttribute(on, new Attribute("Prop", "new value"));
        assertEquals("new value", server.getAttribute(on, "Prop"));
        assertEquals("new value", server.getAttribute(on, "rw"));
    }

    @JmxBean(assembler = PublicMetadataAssembler.class)
    public static class MyClass {
        public String rw = "value";

        public String getProp() {
            return rw;
        }

        public void setProp(String rw) {
            this.rw = rw;
        }
    }
}
