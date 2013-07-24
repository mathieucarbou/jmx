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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class CustomMetadataAssembler extends ReflectionMetadataAssemblerSkeleton {

    private final Collection<Field> fields = new HashSet<Field>();
    private final Collection<Method> methods = new HashSet<Method>();
    private final Collection<BeanProperty> properties = new HashSet<BeanProperty>();

    @Override
    public boolean canInclude(Class<?> managedClass, Field field) {
        return fields.contains(field);
    }

    @Override
    public boolean canInclude(Class<?> managedClass, Method method) {
        for (BeanProperty property : properties) {
            if (method.equals(property.getReadMethod())
                || method.equals(property.getWriteMethod()))
                return true;
        }
        return methods.contains(method);
    }

    @Override
    public boolean canInclude(Class<?> managedClass, BeanProperty<?> property) {
        return properties.contains(property);
    }

    public CustomMetadataAssembler addAttribute(Class<?> clazz, String name) {
        Field field = ReflectionUtils.findField(clazz, name);
        if (field == null)
            throw new IllegalArgumentException("Attribute '" + name + "' not found in class hierarchy " + clazz.getName());
        return addAttribute(field);
    }

    public CustomMetadataAssembler addAttribute(Class<?> clazz, String name, Class<?> type) {
        Field field = ReflectionUtils.findField(clazz, name, type);
        if (field == null)
            throw new IllegalArgumentException("Attribute '" + name + "' of type " + type + " not found in class hierarchy " + clazz.getName());
        return addAttribute(field);
    }

    public CustomMetadataAssembler addAttribute(Field field) {
        if (field == null) throw new NullPointerException("Field cannot be null");
        fields.add(field);
        return this;
    }

    public CustomMetadataAssembler addProperty(Class<?> clazz, String name) {
        BeanProperty property = BeanProperty.findProperty(clazz, name);
        if (property == null)
            throw new IllegalArgumentException("Property '" + name + "' not found in class hierarchy " + clazz.getName());
        return addProperty(property);
    }

    public CustomMetadataAssembler addProperty(Class<?> clazz, String name, Class<?> type) {
        BeanProperty<?> property = BeanProperty.findProperty(clazz, name, type);
        if (property == null)
            throw new IllegalArgumentException("Property '" + name + "' of type " + type + " not found in class hierarchy " + clazz.getName());
        return addProperty(property);
    }

    public CustomMetadataAssembler addProperty(BeanProperty<?> property) {
        if (property == null) throw new NullPointerException("Property cannot be null");
        properties.add(property);
        return this;
    }

    public CustomMetadataAssembler addOperation(Class<?> clazz, String name) {
        Method method = ReflectionUtils.findMethod(clazz, name, null);
        if (method == null)
            throw new IllegalArgumentException("Operation '" + name + "' not found in class hierarchy " + clazz.getName());
        return addOperation(method);
    }

    public CustomMetadataAssembler addOperation(Class<?> clazz, String name, Class<?> returnType, Class<?>... parameterTypes) {
        Method method = ReflectionUtils.findMethod(clazz, name, returnType, parameterTypes);
        if (method == null)
            throw new IllegalArgumentException("Operation '" + name + "' returning " + returnType + " not found in class hierarchy " + clazz.getName());
        return addOperation(method);
    }

    public CustomMetadataAssembler addOperation(Class<?> clazz, String name, Class<?> returnType) {
        Method method = ReflectionUtils.findMethod(clazz, name, returnType);
        if (method == null)
            throw new IllegalArgumentException("Operation '" + name + "' returning " + returnType + " not found in class hierarchy " + clazz.getName());
        return addOperation(method);
    }

    public CustomMetadataAssembler addOperation(Method method) {
        if (method == null) throw new NullPointerException("Method cannot be null");
        methods.add(method);
        return this;
    }
}
