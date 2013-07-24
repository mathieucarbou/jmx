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

import com.mycila.jmx.export.annotation.JmxBean;
import com.mycila.jmx.export.annotation.JmxField;
import com.mycila.jmx.export.annotation.JmxMethod;
import com.mycila.jmx.export.annotation.JmxParam;
import com.mycila.jmx.export.annotation.JmxProperty;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
@JmxBean("app:name=MyService")
public final class MyService {

    private String name;

    @JmxField
    private int internalField = 10;

    @JmxProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JmxMethod(parameters = {@JmxParam(value = "number", description = "put a big number please !")})
    void increment(int n) {
        internalField += n;
    }
}
