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

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
final class ReflectionUtils {

    private static final Map<Class<?>, Reference<Collection<Method>>> declaredMethods = new WeakHashMap<Class<?>, Reference<Collection<Method>>>();

    private ReflectionUtils() {
    }

    public static boolean isSetter(Method method) {
        return method != null
            && method.getName().startsWith("set")
            && method.getParameterTypes().length == 1
            && method.getReturnType() == Void.TYPE;
    }

    public static boolean isGetter(Method method) {
        return isGetMethod(method) || isIsMethod(method);
    }

    public static boolean isGetMethod(Method method) {
        return method != null
            && method.getParameterTypes().length == 0
            && method.getReturnType() != Void.TYPE
            && method.getName().startsWith("get");
    }

    public static boolean isIsMethod(Method method) {
        return method != null
            && method.getParameterTypes().length == 0
            && (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class)
            && method.getName().startsWith("is");
    }

    public static Collection<Method> getDeclaredMethods(Class<?> clazz) {
        Reference<Collection<Method>> ref = declaredMethods.get(clazz);
        Collection<Method> list;
        if (ref != null) {
            list = ref.get();
            if (list != null)
                return list;
        }
        Map<Signature, Method> signatureMethod = new LinkedHashMap<Signature, Method>();
        while (clazz != null) {
            Method[] methods = clazz.isInterface() ? clazz.getMethods() : clazz.getDeclaredMethods();
            for (Method method : methods) {
                Signature signature = new Signature(method);
                if (method.isSynthetic() || method.isBridge() || signatureMethod.containsKey(signature))
                    continue;
                signatureMethod.put(signature, method);
            }
            clazz = clazz.getSuperclass();
        }
        declaredMethods.put(clazz, new WeakReference<Collection<Method>>(list = signatureMethod.values()));
        return list;
    }

    /**
     * Attempt to find a {@link java.lang.reflect.Field field} on the supplied {@link Class} with
     * the supplied <code>name</code>. Searches all superclasses up to
     * {@link Object}.
     *
     * @param clazz the class to introspect
     * @param name  the name of the field
     * @return the corresponding Field object, or <code>null</code> if not found
     */
    public static Field findField(Class<?> clazz, String name) {
        return findField(clazz, name, null);
    }

    /**
     * Attempt to find a {@link java.lang.reflect.Field field} on the supplied {@link Class} with
     * the supplied <code>name</code> and/or {@link Class type}. Searches all
     * superclasses up to {@link Object}.
     *
     * @param clazz the class to introspect
     * @param name  the name of the field (may be <code>null</code> if type is specified)
     * @param type  the type of the field (may be <code>null</code> if name is specified)
     * @return the corresponding Field object, or <code>null</code> if not found
     */
    public static Field findField(Class<?> clazz, String name, Class<?> type) {
        Class<?> searchType = clazz;
        while (!Object.class.equals(searchType) && searchType != null) {
            Field[] fields = searchType.getDeclaredFields();
            for (Field field : fields)
                if ((name == null || name.equals(field.getName())) && (type == null || type.equals(field.getType())))
                    return field;
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    /**
     * Attempt to find a {@link java.lang.reflect.Method} on the supplied class with the supplied name
     * and no parameters. Searches all superclasses up to <code>Object</code>.
     * <p>Returns <code>null</code> if no {@link java.lang.reflect.Method} can be found.
     *
     * @param clazz the class to introspect
     * @param name  the name of the method
     * @return the Method object, or <code>null</code> if none found
     */
    public static Method findMethod(Class<?> clazz, String name) {
        return findMethod(clazz, name, null, new Class[0]);
    }

    public static Method findMethod(Class<?> clazz, String name, Class<?> returnType) {
        return findMethod(clazz, name, returnType, (Class<?>[]) null);
    }

    public static Method findMethod(Class<?> clazz, String name, Class<?> returnType, Class<?>... paramTypes) {
        if (paramTypes != null && paramTypes.length == 1 && paramTypes[0] == null) paramTypes = null;
        while (clazz != null) {
            Method[] methods = (clazz.isInterface() ? clazz.getMethods() : clazz.getDeclaredMethods());
            for (Method method : methods) {
                if (name.equals(method.getName())
                    && (returnType == null || method.getReturnType().equals(returnType))
                    && (paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes()))) {
                    return method;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

}