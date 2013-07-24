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

package com.mycila.jmx.annotation;

import com.mycila.jmx.Access;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a property to be exposed. A JavaBean property is a pair of getter / setter
 * which can be access to set / get a value
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface JmxProperty {
    /**
     * Equivalent to {@link #name()}}
     */
    public abstract String value() default "";

    /**
     * Property name to expose. If not given, will use field name.
     */
    public abstract String name() default "";

    public abstract String description() default "";

    /**
     * Property access type: read-only, read-write or write only
     */
    public abstract Access access() default Access.RO;
}