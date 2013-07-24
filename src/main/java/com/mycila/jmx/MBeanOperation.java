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

import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ReflectionException;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import java.lang.reflect.Method;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class MBeanOperation<T> implements JmxOperation<T> {

    private final Signature signature;
    private final Method operation;
    private final ModelMBeanOperationInfo operationInfo;

    public MBeanOperation(Method operation, String exportName, String description, MBeanParameterInfo... parameters) {
        this.operation = operation;
        this.signature = new Signature(operation);
        this.operationInfo = new ModelMBeanOperationInfo(
            exportName,
            description,
            parameters,
            operation.getReturnType().getName(),
            MBeanOperationInfo.UNKNOWN);
    }

    @Override
    public Signature getSignature() {
        return signature;
    }

    @Override
    public ModelMBeanOperationInfo getMetadata() {
        return operationInfo;
    }

    @Override
    public T invoke(Object managedResource, Object... params) throws ReflectionException {
        try {
            return (T) operation.invoke(managedResource, params);
        } catch (Throwable e) {
            throw JmxUtils.rethrow(e);
        }
    }

    @Override
    public String toString() {
        return getMetadata().getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MBeanOperation that = (MBeanOperation) o;
        return signature.equals(that.signature);
    }

    @Override
    public int hashCode() {
        return signature.hashCode();
    }
}
