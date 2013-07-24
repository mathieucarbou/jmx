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

import com.mycila.jmx.annotation.JmxBean;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public class MBeanNamingStrategy implements JmxNamingStrategy {

    private final boolean ensureUniqueObjectNames;

    public MBeanNamingStrategy() {
        this(false);
    }

    public MBeanNamingStrategy(boolean ensureUniqueObjectNames) {
        this.ensureUniqueObjectNames = ensureUniqueObjectNames;
    }

    @Override
    public ObjectName getObjectName(Object managedBean) throws MalformedObjectNameException {
        ObjectName objectName = getObjectNameInternal(managedBean);
        return ensureUniqueObjectNames ?
            JmxUtils.appendIdentityToObjectName(objectName, managedBean) :
            objectName;
    }

    protected ObjectName getObjectNameInternal(Object managedBean) throws MalformedObjectNameException {
        Class<?> managedClass = AopUtils.getTargetClass(managedBean);
        // check JmxSelfNaming
        if (managedBean instanceof JmxSelfNaming)
            return ((JmxSelfNaming) managedBean).getObjectName();
        // check annotation
        JmxBean jmxBean = managedClass.getAnnotation(JmxBean.class);
        if (jmxBean != null) {
            if (StringUtils.hasLength(jmxBean.objectName()))
                return ObjectName.getInstance(jmxBean.objectName());
            if (StringUtils.hasLength(jmxBean.value()))
                return ObjectName.getInstance(jmxBean.value());
        }
        // default
        return ObjectName.getInstance(ClassUtils.getPackageName(managedClass) + ":type=" + ClassUtils.getShortName(managedClass));
    }
}
