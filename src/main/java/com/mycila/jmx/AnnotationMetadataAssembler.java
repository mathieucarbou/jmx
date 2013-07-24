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
import com.mycila.jmx.annotation.JmxField;
import com.mycila.jmx.annotation.JmxMethod;
import com.mycila.jmx.annotation.JmxMetric;
import com.mycila.jmx.annotation.JmxParam;
import com.mycila.jmx.annotation.JmxProperty;

import javax.management.Descriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public class AnnotationMetadataAssembler extends ReflectionMetadataAssemblerSkeleton {

    // MBEAN

    @Override
    protected String getMBeanDescription(Class<?> managedClass) {
        return isAnnotated(managedClass) ?
            managedClass.getAnnotation(JmxBean.class).description() :
            super.getMBeanDescription(managedClass);
    }

    protected boolean isAnnotated(Class<?> managedClass) {
        return managedClass.isAnnotationPresent(JmxBean.class);
    }

    // ATTRIBUTES

    @Override
    public boolean canInclude(Class<?> managedClass, Field field) {
        return isAnnotated(managedClass) && field.isAnnotationPresent(JmxField.class);
    }

    @Override
    protected String getAttributeExportName(Class<?> managedClass, Field attribute) {
        String name = attribute.getAnnotation(JmxField.class).name();
        if (name.length() == 0)
            name = attribute.getAnnotation(JmxField.class).value();
        return name.length() != 0 ? name : super.getAttributeExportName(managedClass, attribute);
    }

    @Override
    protected String getAttributeDescription(Class<?> managedClass, Field attribute) {
        return attribute.getAnnotation(JmxField.class).description();
    }

    @Override
    protected Access getAttributeAccess(Class<?> managedClass, Field attribute) {
        return attribute.getAnnotation(JmxField.class).access();
    }

    @Override
    protected void populateAttributeDescriptor(Class<?> managedClass, Field attribute, Descriptor desc) {
        super.populateAttributeDescriptor(managedClass, attribute, desc);
        JmxMetric metric = attribute.getAnnotation(JmxMetric.class);
        if (metric != null) fillMetric(metric, desc);
    }

    // OPERATIONS

    @Override
    public boolean canInclude(Class<?> managedClass, Method method) {
        return isAnnotated(managedClass) && method.isAnnotationPresent(JmxMethod.class);
    }

    @Override
    protected String getOperationExportName(Class<?> managedClass, Method operation) {
        String name = operation.getAnnotation(JmxMethod.class).name();
        if (name.length() == 0)
            name = operation.getAnnotation(JmxMethod.class).value();
        return name.length() != 0 ? name : super.getOperationExportName(managedClass, operation);
    }

    @Override
    protected String getOperationDescription(Class<?> managedClass, Method operation) {
        return operation.getAnnotation(JmxMethod.class).description();
    }

    @Override
    protected String getParameterExportName(Class<?> managedClass, Method operation, Class<?> paramType, int index) {
        JmxParam[] params = operation.getAnnotation(JmxMethod.class).parameters();
        if (params.length == 0)
            return super.getParameterExportName(managedClass, operation, paramType, index);
        if (params.length <= index)
            throw new IllegalStateException("Missing @JmxParam on operation " + operation);
        String name = params[index].name();
        if (name.length() == 0)
            name = params[index].value();
        return name.length() != 0 ? name : super.getParameterExportName(managedClass, operation, paramType, index);
    }

    @Override
    protected String getParameterDescription(Class<?> managedClass, Method operation, Class<?> paramType, int index) {
        JmxParam[] params = operation.getAnnotation(JmxMethod.class).parameters();
        if (params.length == 0)
            return super.getParameterExportName(managedClass, operation, paramType, index);
        if (params.length <= index)
            throw new IllegalStateException("Missing @JmxParam on operation " + operation);
        return params[index].description();
    }

    @Override
    protected void populateOperationDescriptor(Class<?> managedClass, Method operation, Descriptor desc) {
        super.populateOperationDescriptor(managedClass, operation, desc);
        JmxMetric metric = operation.getAnnotation(JmxMetric.class);
        if (metric != null) fillMetric(metric, desc);
    }

    // PROPERTIES

    @Override
    public boolean canInclude(Class<?> managedClass, BeanProperty property) {
        return isAnnotated(managedClass) && property.isAnnotationPresent(JmxProperty.class);
    }

    @Override
    protected String getPropertyExportName(Class<?> managedClass, BeanProperty property) {
        String name = property.getAnnotation(JmxProperty.class).name();
        if (name.length() == 0)
            name = property.getAnnotation(JmxProperty.class).value();
        return name.length() != 0 ? name : super.getPropertyExportName(managedClass, property);
    }

    @Override
    protected String getPropertyDescription(Class<?> managedClass, BeanProperty property) {
        return property.getAnnotation(JmxProperty.class).description();
    }

    @Override
    protected Access getPropertyAccess(Class<?> managedClass, BeanProperty property) {
        return property.getAnnotation(JmxProperty.class).access();
    }

    @Override
    protected void populatePropertyDescriptor(Class<?> managedClass, BeanProperty property, Descriptor desc) {
        super.populatePropertyDescriptor(managedClass, property, desc);
        JmxMetric metric = property.getAnnotation(JmxMetric.class);
        if (metric != null) fillMetric(metric, desc);
    }

    // METRICS

    private void fillMetric(JmxMetric metric, Descriptor desc) {
        if (metric.unit().length() > 0)
            desc.setField("units", metric.unit());
        if (metric.category().length() > 0)
            desc.setField("metricCategory", metric.category());
        desc.setField("metricType", metric.type().toString());
    }
}
