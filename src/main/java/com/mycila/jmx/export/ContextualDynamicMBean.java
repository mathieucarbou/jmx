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

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class ContextualDynamicMBean implements DynamicMBean {

    private final DynamicMBean delegate;
    private final ClassLoader classLoader;

    public ContextualDynamicMBean(DynamicMBean delegate, ClassLoader context) {
        this.delegate = delegate;
        this.classLoader = context;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public DynamicMBean getDelegate() {
        return delegate;
    }

    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClassLoader());
            return getDelegate().getAttribute(attribute);
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClassLoader());
            return getDelegate().getAttributes(attributes);
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClassLoader());
            getDelegate().setAttribute(attribute);
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClassLoader());
            return getDelegate().setAttributes(attributes);
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClassLoader());
            return getDelegate().invoke(actionName, params, signature);
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        return getDelegate().getMBeanInfo();
    }
}
