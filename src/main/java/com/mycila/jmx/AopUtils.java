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

import java.lang.reflect.Proxy;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
final class AopUtils {

    private AopUtils() {
    }

    /**
     * Determine the target class of the given bean instance,
     * which might be an AOP proxy.
     * <p>Returns the target class for an AOP proxy and the plain class else.
     *
     * @param candidate the instance to check (might be an AOP proxy)
     * @return the target class (or the plain class of the given object as fallback)
     */
    public static Class<?> getTargetClass(Object candidate) {
        for (SupportedProxy supportedProxy : SupportedProxy.values()) {
            if (supportedProxy.isProxy(candidate)) {
                Class<?> c = supportedProxy.getTargetType(candidate);
                if (c != null) return c;
            }
        }
        return candidate.getClass();
    }

    private static enum SupportedProxy {
        SPRING {
            @Override
            Class<?> getTargetType(Object proxy) {
                try {
                    for (Class<?> itf : proxy.getClass().getInterfaces())
                        if ("org.springframework.aop.TargetClassAware".equals(itf.getName()))
                            return (Class<?>) proxy.getClass().getMethod("getTargetClass").invoke(proxy);
                } catch (Exception ignored) {
                }
                return null;
            }

            @Override
            boolean isProxyClass(Class<?> c) {
                for (Class<?> itf : c.getInterfaces())
                    if ("org.springframework.aop.SpringProxy".equals(itf.getName())
                        || "org.springframework.aop.TargetClassAware".equals(itf.getName()))
                        return true;
                return false;
            }
        },
        CGLIB {
            @Override
            Class<?> getTargetType(Object proxy) {
                return proxy.getClass().getSuperclass();
            }

            @Override
            boolean isProxyClass(Class<?> c) {
                if (c.getName().contains("$$"))
                    return true;
                for (Class<?> itf : c.getInterfaces())
                    if ("net.sf.cglib.proxy.Factory".equals(itf.getName()))
                        return true;
                return false;
            }
        },
        JDK {
            @Override
            Class<?> getTargetType(Object proxy) {
                return null;
            }

            @Override
            boolean isProxyClass(Class<?> c) {
                return Proxy.isProxyClass(c);
            }
        };

        boolean isProxy(Object o) {
            return isProxyClass(o.getClass());
        }

        abstract boolean isProxyClass(Class<?> c);

        abstract Class<?> getTargetType(Object proxy);
    }
}
