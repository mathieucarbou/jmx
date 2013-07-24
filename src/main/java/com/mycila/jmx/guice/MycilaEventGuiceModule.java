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
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.util.LinkedList;
import java.util.Queue;

import static com.google.inject.matcher.Matchers.*;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public class MycilaEventGuiceModule implements Module {

    /*@Inject
    Provider<AnnotationProcessor> annotationProcessor;*/

    protected final Processor processor = new Processor() {

        private final Queue<Object> references = new LinkedList<Object>();
        private volatile boolean injectedCreated;

        public <I> void process(I instance) {
            if (injectedCreated) ;
                //annotationProcessor.get().process(instance);
            else
                references.offer(instance);
        }

        public void start() {
            injectedCreated = true;
            while (!references.isEmpty()) {
                Object o = references.poll();
                if (o != null) ;
                //annotationProcessor.get().process(o);
            }
        }
    };

    public void afterCreation() {
        processor.start();
    }

    public void configure(Binder binder) {
        //bindDispatcher(binder.bind(Dispatcher.class)).in(Singleton.class);
        //bindAnnotationProcessor(binder.bind(AnnotationProcessor.class)).in(Singleton.class);
        binder.bind(MycilaEventGuiceModule.class).toInstance(this);
        binder.bindListener(any(), new TypeListener() {
            public <I> void hear(TypeLiteral<I> type, final TypeEncounter<I> encounter) {
                encounter.register(new InjectionListener<I>() {
                    public void afterInjection(I injectee) {
                        processor.process(injectee);
                    }
                });
            }
        });
    }

    /*protected ScopedBindingBuilder bindAnnotationProcessor(AnnotatedBindingBuilder<AnnotationProcessor> bindAnnotationProcessor) {
        return bindAnnotationProcessor.toProvider(new Provider<AnnotationProcessor>() {
            @Inject
            Provider<Dispatcher> dipatcher;
            public AnnotationProcessor get() {
                return AnnotationProcessors.create(dipatcher.get());
            }
        });
    }

    protected ScopedBindingBuilder bindDispatcher(AnnotatedBindingBuilder<Dispatcher> bindDispatcher) {
        return bindDispatcher.toProvider(new Provider<Dispatcher>() {
            public Dispatcher get() {
                return buildDispatcher(ErrorHandlers.rethrow());
            }
        });
    }

    protected Dispatcher buildDispatcher(ErrorHandler errorHandler) {
        return Dispatchers.synchronousUnsafe(errorHandler);
    }*/

    private static interface Processor {
        <I> void process(I instance);

        void start();
    }
}