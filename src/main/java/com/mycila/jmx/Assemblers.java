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

import com.mycila.jmx.annotation.JmxBean;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class Assemblers {

    private static final Map<Class<? extends JmxMetadataAssembler>, Reference<JmxMetadataAssembler>> cache = new WeakHashMap<>();

    private Assemblers() {
    }

    public static JmxMetadataAssembler get(Class<?> mbeanClass) {
        mbeanClass = AopUtils.getTargetClass(mbeanClass);
        JmxBean jmxBean = mbeanClass.getAnnotation(JmxBean.class);
        return jmxBean == null ? load(PublicMetadataAssembler.class) : load(jmxBean.assembler());
    }

    private static JmxMetadataAssembler load(Class<? extends JmxMetadataAssembler> exp) {
        if (exp == null) exp = AnnotationMetadataAssembler.class;
        Reference<JmxMetadataAssembler> ref = cache.get(exp);
        JmxMetadataAssembler exposure;
        if (ref != null) {
            exposure = ref.get();
            if (exposure != null)
                return exposure;
        }
        try {
            exposure = exp.getConstructor().newInstance();
        } catch (Throwable e) {
            throw ExceptionUtils.rethrow(e);
        }
        cache.put(exp, new WeakReference<JmxMetadataAssembler>(exposure));
        return exposure;
    }

}
