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

import javax.management.InvalidAttributeValueException;
import javax.management.ReflectionException;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import java.lang.reflect.Field;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class MBeanAttribute<T> implements JmxAttribute<T> {

    private final Field field;
    private final ModelMBeanAttributeInfo attributeInfo;

    public MBeanAttribute(Field field, String exportName, String description, Access access) {
        this.field = field;
        this.attributeInfo = new ModelMBeanAttributeInfo(
            exportName, field.getType().getName(), description,
            access == Access.RO || access == Access.RW,
            access == Access.WO || access == Access.RW,
            false);
    }

    @Override
    public String getName() {
        return getMetadata().getName();
    }

    @Override
    public ModelMBeanAttributeInfo getMetadata() {
        return attributeInfo;
    }

    @Override
    public T get(Object managedResource) throws ReflectionException {
        if (!getMetadata().isReadable())
            throw new ReflectionException(new IllegalAccessException("Attribute not readable: " + this), "Attribute not readable: " + this);
        if (!field.isAccessible())
            field.setAccessible(true);
        try {
            return (T) field.get(managedResource);
        } catch (Exception e) {
            throw JmxUtils.rethrow(e);
        }
    }

    @Override
    public void set(Object managedResource, T value) throws InvalidAttributeValueException, ReflectionException {
        if (!getMetadata().isWritable())
            throw new ReflectionException(new IllegalAccessException("Attribute not writable: " + this), "Attribute not writable: " + this);
        if (!field.isAccessible())
            field.setAccessible(true);
        if (!ClassUtils.isAssignableValue(field.getType(), value))
            throw new InvalidAttributeValueException("Invalid type specified for attribute " + this + ": " + value);
        try {
            field.set(managedResource, value);
        } catch (Exception e) {
            throw JmxUtils.rethrow(e);
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MBeanAttribute that = (MBeanAttribute) o;
        return field.equals(that.field);
    }

    @Override
    public int hashCode() {
        return field.hashCode();
    }
}
