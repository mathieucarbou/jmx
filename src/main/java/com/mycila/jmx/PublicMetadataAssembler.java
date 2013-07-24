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
import java.lang.reflect.Modifier;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public class PublicMetadataAssembler extends ReflectionMetadataAssemblerSkeleton {

    private boolean exposeObjectElements;

    public PublicMetadataAssembler() {
        this(true);
    }

    public PublicMetadataAssembler(boolean exposeObjectElements) {
        this.exposeObjectElements = exposeObjectElements;
    }

    @Override
    public boolean canInclude(Class<?> managedClass, BeanProperty property) {
        if (property.isReadable() &&
            (!exposeObjectElements && property.getReadMethod().getDeclaringClass().equals(Object.class)
                || Modifier.isStatic(property.getReadMethod().getModifiers())
                || !Modifier.isPublic(property.getReadMethod().getModifiers())))
            property.clearReadable();
        if (property.isWritable()
            && !Modifier.isPublic(property.getWriteMethod().getModifiers()))
            property.clearWritable();
        return property.isReadable() || property.isWritable();
    }

    @Override
    public boolean canInclude(Class<?> managedClass, Method method) {
        return Modifier.isPublic(method.getModifiers())
            && !Modifier.isStatic(method.getModifiers())
            && (exposeObjectElements || !Object.class.equals(method.getDeclaringClass()));
    }

    @Override
    public boolean canInclude(Class<?> managedClass, Field field) {
        return Modifier.isPublic(field.getModifiers())
            && !Modifier.isStatic(field.getModifiers());
    }
}