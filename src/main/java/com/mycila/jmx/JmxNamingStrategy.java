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

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public interface JmxNamingStrategy {
    /**
     * Obtain an <code>ObjectName</code> for the supplied bean.
     *
     * @param managedBean the bean that will be exposed under the
     *                    returned <code>ObjectName</code>
     * @return the <code>ObjectName</code> instance
     * @throws javax.management.MalformedObjectNameException
     *          if the resulting <code>ObjectName</code> is invalid
     */
    ObjectName getObjectName(Object managedBean) throws MalformedObjectNameException;
}
