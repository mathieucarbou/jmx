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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public abstract class ReflectionMetadataAssemblerSkeleton extends MetadataAssemblerSkeleton {

    @Override
    protected Collection<Field> getAttributes(Class<?> managedClass) {
        List<Field> fields = new LinkedList<Field>();
        while (managedClass != null
            && !managedClass.equals(Object.class)) {
            for (Field field : managedClass.getDeclaredFields())
                if (!field.isSynthetic() && canInclude(managedClass, field))
                    fields.add(field);
            managedClass = managedClass.getSuperclass();
        }
        return fields;
    }

    protected abstract boolean canInclude(Class<?> managedClass, Field attribute);

    @Override
    protected Collection<BeanProperty> getProperties(Class<?> managedClass) {
        Map<String, BeanProperty> properties = new HashMap<String, BeanProperty>();
        for (BeanProperty prop : BeanUtils.getProperties(managedClass))
            if (!properties.containsKey(prop.getName())
                && canInclude(managedClass, prop))
                properties.put(prop.getName(), prop);
        return properties.values();
    }

    protected abstract boolean canInclude(Class<?> managedClass, BeanProperty property);

    @Override
    protected Collection<Method> getMethodOperations(Class<?> managedClass) {
        List<Method> methods = new LinkedList<Method>();
        for (Method method : ReflectionUtils.getDeclaredMethods(managedClass))
            if (canInclude(managedClass, method))
                methods.add(method);
        return methods;
    }

    protected abstract boolean canInclude(Class<?> managedClass, Method operation);

}