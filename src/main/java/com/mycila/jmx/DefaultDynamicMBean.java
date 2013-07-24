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

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class DefaultDynamicMBean implements DynamicMBean {

    private final Object managedResource;
    private final JmxMetadata jmxMetadata;

    public DefaultDynamicMBean(Object managedResource, JmxMetadata jmxMetadata) {
        this.managedResource = managedResource;
        this.jmxMetadata = jmxMetadata;
    }

    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        // validation from javax.management.modelmbean.RequiredModelMBean
        if (attribute == null)
            throw new RuntimeOperationsException(new IllegalArgumentException("attributeName must not be null"), "Exception occurred trying to get attribute of a " + getClass().getSimpleName());
        JmxAttribute attr = getJmxMetadata().getAttribute(attribute);
        return attr.get(getManagedResource());
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        // validation from javax.management.modelmbean.RequiredModelMBean
        if (attributes == null)
            throw new RuntimeOperationsException(new IllegalArgumentException("attributeNames must not be null"), "Exception occurred trying to get attributes of a " + getClass().getSimpleName());
        AttributeList list = new AttributeList();
        for (String attribute : attributes) {
            if (attribute == null)
                throw new RuntimeOperationsException(new IllegalArgumentException("attributeName must not be null"), "Exception occurred trying to get attribute of a " + getClass().getSimpleName());
            try {
                JmxAttribute attr = getJmxMetadata().getAttribute(attribute);
                list.add(new Attribute(attr.getMetadata().getName(), attr.get(getManagedResource())));
            } catch (AttributeNotFoundException ignored) {
            } catch (ReflectionException ignored) {
            }
        }
        return list;
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        // validation from javax.management.modelmbean.RequiredModelMBean
        if (attribute == null)
            throw new RuntimeOperationsException(new IllegalArgumentException("attribute must not be null"), "Exception occurred trying to set an attribute of a " + getClass().getSimpleName());
        JmxAttribute attr = getJmxMetadata().getAttribute(attribute.getName());
        attr.set(getManagedResource(), attribute.getValue());
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        // validation from javax.management.modelmbean.RequiredModelMBean
        if (attributes == null)
            throw new RuntimeOperationsException(new IllegalArgumentException("attributes must not be null"), "Exception occurred trying to set attributes of a " + getClass().getSimpleName());
        AttributeList list = new AttributeList();
        for (Attribute attribute : attributes.asList()) {
            try {
                JmxAttribute attr = getJmxMetadata().getAttribute(attribute.getName());
                attr.set(getManagedResource(), attribute.getValue());
                list.add(attribute);
            } catch (AttributeNotFoundException ignored) {
            } catch (ReflectionException ignored) {
            } catch (InvalidAttributeValueException ignored) {
            }
        }
        return list;
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException, RuntimeOperationsException {
        // validation from javax.management.modelmbean.RequiredModelMBean
        if (actionName == null)
            throw new RuntimeOperationsException(new IllegalArgumentException("Method name must not be null"), "An exception occurred while trying to invoke a method on a " + getClass().getSimpleName());
        Object o = getManagedResource();
        ClassLoader loader = o.getClass().getClassLoader();
        Class[] paramTypes = new Class[signature.length];
        try {
            for (int i = 0; i < signature.length; i++)
                paramTypes[i] = ClassUtils.forName(signature[i], loader);
        } catch (ClassNotFoundException e) {
            throw new ReflectionException(e, "An exception occurred while trying to invoke a method on a " + getClass().getSimpleName());
        }
        JmxOperation op;
        try {
            op = getJmxMetadata().getOperation(actionName, paramTypes);
        } catch (OperationNotFoundException e) {
            throw new RuntimeOperationsException(e, "An exception occurred while trying to find method " + actionName + " on " + getClass().getSimpleName());
        }
        return op.invoke(o, params);
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        return jmxMetadata.getMBeanInfo();
    }

    public JmxMetadata getJmxMetadata() {
        return jmxMetadata;
    }

    public Object getManagedResource() {
        return managedResource;
    }

}