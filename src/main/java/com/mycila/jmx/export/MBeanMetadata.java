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

import javax.management.AttributeNotFoundException;
import javax.management.ImmutableDescriptor;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class MBeanMetadata implements JmxMetadata {

    private final MBeanInfo mBeanInfo;
    private final Map<String, JmxAttribute<?>> attributes = new HashMap<String, JmxAttribute<?>>();
    private final Map<Signature, JmxOperation<?>> operations = new HashMap<Signature, JmxOperation<?>>();

    public MBeanMetadata(String className, String description, Collection<JmxAttribute<?>> attributes, Collection<JmxOperation<?>> operations) {
        List<MBeanAttributeInfo> attrs = new ArrayList<MBeanAttributeInfo>(attributes.size());
        for (JmxAttribute attribute : attributes) {
            if (this.attributes.put(attribute.getName(), attribute) != null)
                throw new IllegalArgumentException("Duplicate attribute found: " + attribute.getName());
            attrs.add(attribute.getMetadata());
        }
        List<MBeanOperationInfo> ops = new ArrayList<MBeanOperationInfo>(attributes.size());
        for (JmxOperation operation : operations) {
            if (this.operations.put(operation.getSignature(), operation) != null)
                throw new IllegalArgumentException("Duplicate operation found: " + operation.getSignature());
            ops.add(operation.getMetadata());
        }
        this.mBeanInfo = new MBeanInfo(
                className,
                description,
                attrs.toArray(new MBeanAttributeInfo[attrs.size()]),
                null,
                ops.toArray(new MBeanOperationInfo[ops.size()]),
                null,
                new ImmutableDescriptor("immutableInfo=true"));
    }

    @Override
    public JmxAttribute<?> getAttribute(String attribute) throws AttributeNotFoundException {
        JmxAttribute att = attributes.get(attribute);
        if (att == null) throw new AttributeNotFoundException(attribute);
        return att;
    }

    @Override
    public JmxOperation<?> getOperation(String operation, Class<?>... paramTypes) throws OperationNotFoundException {
        Signature signature = new Signature(operation, paramTypes);
        JmxOperation op = operations.get(signature);
        if (op == null) throw new OperationNotFoundException(signature.toString());
        return op;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        return mBeanInfo;
    }

}
