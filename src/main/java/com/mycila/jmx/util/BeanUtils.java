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

package com.mycila.jmx.util;

import com.mycila.jmx.export.BeanProperty;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class BeanUtils {

    private static final Map<Class<?>, Reference<Collection<BeanProperty>>> cache = new WeakHashMap<Class<?>, Reference<Collection<BeanProperty>>>();

    private BeanUtils() {
    }

    public static Collection<BeanProperty> getProperties(Class<?> clazz) {
        Reference<Collection<BeanProperty>> ref = cache.get(clazz);
        if (ref != null) {
            Collection<BeanProperty> list = ref.get();
            if (list != null)
                return list;
        }
        Set<BeanProperty> properties = new LinkedHashSet<BeanProperty>();
        for (Method method : ReflectionUtils.getDeclaredMethods(clazz)) {
            BeanProperty prop = BeanProperty.findProperty(clazz, method);
            if (prop != null)
                properties.add(prop);
        }
        cache.put(clazz, new WeakReference<Collection<BeanProperty>>(properties));
        return properties;
    }

}