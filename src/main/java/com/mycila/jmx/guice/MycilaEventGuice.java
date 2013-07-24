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

package com.mycila.jmx.guice;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.binder.ScopedBindingBuilder;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class MycilaEventGuice {
    private MycilaEventGuice() {
    }

    public static <T> Provider<T> publisher(final Class<T> clazz) {
        return new Provider<T>() {
            //@Inject
            //Provider<AnnotationProcessor> annotationProcessor;
            @Inject
            Provider<Injector> injector;

            public T get() {
                T proxy = /*annotationProcessor.get().proxy(clazz);*/ null;
                injector.get().injectMembers(proxy);
                return proxy;
            }
        };
    }

    public static <T> ScopedBindingBuilder bindPublisher(Binder binder, Class<T> clazz) {
        return binder.bind(clazz).toProvider(publisher(clazz));
    }
}