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

import com.mycila.jmx.util.JmxUtils;
import com.mycila.jmx.util.StringUtils;

import javax.management.Descriptor;
import javax.management.MBeanParameterInfo;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public abstract class MetadataAssemblerSkeleton implements JmxMetadataAssembler {
    @Override
    public JmxMetadata getMetadata(Class<?> managedClass) {
        return new MBeanMetadata(
                managedClass.getName(),
                getMBeanDescription(managedClass),
                getMBeanAttributes(managedClass),
                getMBeanOperations(managedClass));
    }

    protected String getMBeanDescription(Class<?> managedClass) {
        return managedClass.getName();
    }

    protected Collection<JmxAttribute<?>> getMBeanAttributes(Class<?> managedClass) {
        List<JmxAttribute<?>> jmxAttributes = new LinkedList<JmxAttribute<?>>();
        for (BeanProperty property : getProperties(managedClass))
            jmxAttributes.add(buildProperty(managedClass, property));
        for (Field field : getAttributes(managedClass))
            jmxAttributes.add(buildAttribute(managedClass, field));
        return jmxAttributes;
    }

    protected JmxAttribute<?> buildAttribute(Class<?> managedClass, Field field) {
        MBeanAttribute<?> jmxAttribute = new MBeanAttribute(
                field,
                getAttributeExportName(managedClass, field),
                getAttributeDescription(managedClass, field),
                getAttributeAccess(managedClass, field));
        Descriptor desc = jmxAttribute.getMetadata().getDescriptor();
        populateAttributeDescriptor(managedClass, field, desc);
        jmxAttribute.getMetadata().setDescriptor(desc);
        return jmxAttribute;
    }

    protected JmxAttribute<?> buildProperty(Class<?> managedClass, BeanProperty<?> property) {
        MBeanProperty<?> jmxAttribute = new MBeanProperty(
                property,
                getPropertyExportName(managedClass, property),
                getPropertyDescription(managedClass, property),
                getPropertyAccess(managedClass, property));
        Descriptor desc = jmxAttribute.getMetadata().getDescriptor();
        populatePropertyDescriptor(managedClass, property, desc);
        jmxAttribute.getMetadata().setDescriptor(desc);
        return jmxAttribute;
    }

    protected Collection<JmxOperation<?>> getMBeanOperations(Class<?> managedClass) {
        List<JmxOperation<?>> jmxOperations = new LinkedList<JmxOperation<?>>();
        for (Method method : getMethodOperations(managedClass))
            jmxOperations.add(buildOperation(managedClass, method));
        return jmxOperations;
    }

    protected JmxOperation<?> buildOperation(Class<?> managedClass, Method operation) {
        MBeanOperation<?> jmxOperation = new MBeanOperation(
                operation,
                getOperationExportName(managedClass, operation),
                getOperationDescription(managedClass, operation),
                getOperationParameters(managedClass, operation));
        Descriptor desc = jmxOperation.getMetadata().getDescriptor();
        populateOperationDescriptor(managedClass, operation, desc);
        jmxOperation.getMetadata().setDescriptor(desc);
        return jmxOperation;
    }

    // attributes

    protected abstract Collection<Field> getAttributes(Class<?> managedClass);

    protected String getAttributeExportName(Class<?> managedClass, Field attribute) {
        return attribute.getName();
    }

    protected String getAttributeDescription(Class<?> managedClass, Field attribute) {
        return "";
    }

    protected Access getAttributeAccess(Class<?> managedClass, Field attribute) {
        return Modifier.isFinal(attribute.getModifiers()) ? Access.RO : Access.RW;
    }

    protected void populateAttributeDescriptor(Class<?> managedClass, Field attribute, Descriptor desc) {
        JmxUtils.populateDeprecation(desc, attribute);
        JmxUtils.populateEnable(desc, true);
        JmxUtils.populateDisplayName(desc, attribute.getName());
        JmxUtils.populateVisibility(desc, 1);
    }

    // properties

    protected abstract Collection<BeanProperty<?>> getProperties(Class<?> managedClass);

    protected String getPropertyExportName(Class<?> managedClass, BeanProperty<?> property) {
        return StringUtils.capitalize(property.getName());
    }

    protected String getPropertyDescription(Class<?> managedClass, BeanProperty<?> property) {
        return "";
    }

    protected Access getPropertyAccess(Class<?> managedClass, BeanProperty<?> property) {
        if (property.isReadable() && property.isWritable())
            return Access.RW;
        if (property.isReadable() && !property.isWritable())
            return Access.RO;
        if (!property.isReadable() && property.isWritable())
            return Access.WO;
        return Access.NONE;
    }

    protected void populatePropertyDescriptor(Class<?> managedClass, BeanProperty<?> property, Descriptor desc) {
        JmxUtils.populateDeprecation(desc, property.getReadMethod());
        JmxUtils.populateDeprecation(desc, property.getWriteMethod());
        JmxUtils.populateEnable(desc, true);
        JmxUtils.populateDisplayName(desc, StringUtils.capitalize(property.getName()));
        JmxUtils.populateVisibility(desc, 1);
        JmxUtils.populateAccessors(desc, property);
    }

    // method operations

    protected abstract Collection<Method> getMethodOperations(Class<?> managedClass);

    protected String getOperationExportName(Class<?> managedClass, Method operation) {
        return operation.getName();
    }

    protected String getOperationDescription(Class<?> managedClass, Method operation) {
        return "";
    }

    protected MBeanParameterInfo[] getOperationParameters(Class<?> managedClass, Method operation) {
        Class<?>[] paramTypes = operation.getParameterTypes();
        MBeanParameterInfo[] params = new MBeanParameterInfo[paramTypes.length];
        for (int i = 0; i < params.length; i++)
            params[i] = new MBeanParameterInfo(
                    getParameterExportName(managedClass, operation, paramTypes[i], i),
                    paramTypes[i].getName(),
                    getParameterDescription(managedClass, operation, paramTypes[i], i));
        return params;
    }

    protected String getParameterExportName(Class<?> managedClass, Method operation, Class<?> paramType, int index) {
        return paramType.getSimpleName();
    }

    protected String getParameterDescription(Class<?> managedClass, Method operation, Class<?> paramType, int index) {
        return "";
    }

    protected void populateOperationDescriptor(Class<?> managedClass, Method operation, Descriptor desc) {
        JmxUtils.populateDeprecation(desc, operation);
        JmxUtils.populateEnable(desc, true);
        JmxUtils.populateDisplayName(desc, operation.getName());
        JmxUtils.populateVisibility(desc, 1);
        JmxUtils.populateRole(desc, Role.OPERATION);
        // verify if this is a property
        Collection<BeanProperty<?>> beanProperties = getProperties(managedClass);
        for (BeanProperty<?> beanProperty : beanProperties) {
            if (operation.equals(beanProperty.getReadMethod())) {
                JmxUtils.populateRole(desc, Role.GETTER);
                JmxUtils.populateVisibility(desc, 4);
                break;
            } else if (operation.equals(beanProperty.getWriteMethod())) {
                JmxUtils.populateRole(desc, Role.SETTER);
                JmxUtils.populateVisibility(desc, 4);
                break;
            }
        }
    }

}
