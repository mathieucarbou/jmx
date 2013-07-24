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

package com.mycila.jmx.test;

import com.mycila.jmx.util.ExceptionUtils;
import org.hamcrest.Description;
import org.junit.internal.matchers.TypeSafeMatcher;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class Throws extends TypeSafeMatcher<Code> {

    private final Class<? extends Throwable> exceptionClass;
    private final String message;
    private final boolean checkMessage;

    public Throws(Class<? extends Throwable> exceptionClass, String message, boolean checkMessage) {
        super(Code.class);
        this.exceptionClass = exceptionClass;
        this.message = message;
        this.checkMessage = checkMessage;
    }

    @Override
    public boolean matchesSafely(Code item) {
        try {
            item.run();
            throw new AssertionError("Code must have thrown an exception");
        } catch (Throwable throwable) {
            if (!exceptionClass.isInstance(throwable)) {
                throwable.printStackTrace();
                throw new AssertionError("Code thrown bad exception: " + throwable.getClass().getName());
            }
            if (checkMessage
                    && (throwable.getMessage() == null && message != null
                    || throwable.getMessage() != null && !throwable.getMessage().equals(message))) {
                System.out.println(ExceptionUtils.asString(throwable));
                throw new AssertionError("Code thrown bad message: " + throwable.getMessage());
            }
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("code throws exception [")
                .appendText(exceptionClass.getName())
                .appendText("]");
        if (checkMessage)
            description.appendText(" with message [")
                    .appendText(message)
                    .appendText("]");
    }

    @Override
    public String toString() {
        return "sfsf";
    }

    public static Throws fire(Class<? extends Throwable> exceptionClass) {
        return new Throws(exceptionClass, null, false);
    }

    public static Throws fire(Class<? extends Throwable> exceptionClass, String message) {
        return new Throws(exceptionClass, message, true);
    }
}
