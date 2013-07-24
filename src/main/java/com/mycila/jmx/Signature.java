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

import java.lang.reflect.Method;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
final class Signature {
    private final String signature;

    public Signature(Method method) {
        this(method.getName(), method.getParameterTypes());
    }

    public Signature(String name, Class<?>... params) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("(");
        if (params.length > 0)
            sb.append(params[0].getName());
        if (params.length > 1)
            for (int i = 1; i < params.length; i++)
                sb.append(", ").append(params[i].getName());
        sb.append(")");
        this.signature = sb.toString();
    }

    @Override
    public String toString() {
        return signature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Signature signature1 = (Signature) o;
        return signature.equals(signature1.signature);
    }

    @Override
    public int hashCode() {
        return signature.hashCode();
    }
}
