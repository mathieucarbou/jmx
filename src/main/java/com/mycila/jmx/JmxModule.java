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

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.mycila.jmx.annotation.JmxBean;

import javax.inject.Provider;
import javax.inject.Singleton;
import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2013-06-08
 */
public class JmxModule extends AbstractModule {
    @Override
    protected void configure() {
        bindListener(ClassToTypeLiteralMatcherAdapter.adapt(Matchers.annotatedWith(JmxBean.class)), new TypeListener() {
            @Override
            public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
                final Provider<JmxExporter> exporter = encounter.getProvider(JmxExporter.class);
                encounter.register(new InjectionListener<I>() {
                    @Override
                    public void afterInjection(I injectee) {
                        exporter.get().register(injectee);
                    }
                });
            }
        });
    }

    @Provides
    @Singleton
    protected JmxExporter jmxExporter(MBeanServer server) {
        MycilaJmxExporter exporter = new MycilaJmxExporter(server);
        exporter.setExportBehavior(ExportBehavior.FAIL_ON_EXISTING);
        exporter.setEnsureUnique(false);
        return exporter;
    }

    @Provides
    @Singleton
    protected MBeanServer mBeanServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }
}
