/*
 * Copyright (c) 2022 Evgenii Plugatar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.plugatar.selenidehacks.impl;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.plugatar.selenidehacks.OuterCommand;
import com.plugatar.selenidehacks.SelenideElementInvocationException;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * Outer command implementation based on
 * {@link com.plugatar.selenidehacks.UnsafeSelenideElement}.
 *
 * @param <T> the command return type
 */
@ParametersAreNonnullByDefault
public class OuterCommandOf<T> implements OuterCommand<T> {
    private final String methodName;
    private final Object[] methodArgs;

    /**
     * Ctor.
     *
     * @param executeMethodFirstArg the execute method first argument
     * @param executeMethodRestArgs the execute method rest arguments
     * @throws NullPointerException if {@code executeMethodFirstArg} is {@code null}
     */
    public OuterCommandOf(final Command<? extends T> executeMethodFirstArg,
                          final Object... executeMethodRestArgs) {
        this(
            "execute",
            arrayWithFirstElement(
                Objects.requireNonNull(executeMethodFirstArg, "executeMethodFirstArg arg is null"),
                executeMethodRestArgs
            )
        );
    }

    /**
     * Ctor.
     *
     * @param methodName the method name
     * @param methodArgs the method arguments
     * @throws NullPointerException if {@code methodName} is {@code null}
     */
    public OuterCommandOf(final String methodName,
                          final Object... methodArgs) {
        this.methodName = Objects.requireNonNull(methodName, "methodName arg is null");
        this.methodArgs = methodArgs;
    }

    @Override
    public final T executeOn(final SelenideElement element) {
        if (element == null) { throw new SelenideElementInvocationException("element arg is null"); }
        if (!Proxy.isProxyClass(element.getClass())) {
            throw new SelenideElementInvocationException("element arg is not instantiated as a Proxy");
        }
        return new UnsafeSelenideElementOf(element).invoke(this.methodName, this.methodArgs);
    }

    private static Object[] arrayWithFirstElement(final Object firstElement,
                                                  final Object[] originArray) {
        if (originArray == null || originArray.length == 0) {
            return new Object[]{firstElement};
        } else {
            final Object[] array = new Object[originArray.length + 1];
            array[0] = firstElement;
            System.arraycopy(originArray, 0, array, 1, originArray.length - 1);
            return array;
        }
    }
}
