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
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.mycila.jmx.export.JmxExporter;

import java.lang.annotation.Annotation;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class MycilaJmx {
    private MycilaJmx() {
    }

    public static ExporterBuilder exportJmxBeans(Binder binder) {
        return new ExporterBuilder(binder);
    }

    public static final class ExporterBuilder {
        Key<? extends JmxExporter> key = Key.get(JmxExporter.class);

        private ExporterBuilder(Binder binder) {
            binder.bindListener(Matchers.any(), new TypeListener() {
                @Override
                public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
                    encounter.register(new InjectionListener<I>() {
                        @Override
                        public void afterInjection(I injectee) {
                            
                        }
                    });
                }
            });
        }

        public void using(Class<? extends JmxExporter> type) {
            this.key = Key.get(type);
        }

        public void using(Class<? extends JmxExporter> type, Class<? extends Annotation> annotationType) {
            this.key = Key.get(type, annotationType);
        }

        public void using(Class<? extends JmxExporter> type, Annotation annotation) {
            this.key = Key.get(type, annotation);
        }
    }
}
