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

import com.codeborne.selenide.SelenideElement;
import com.plugatar.selenidehacks.SelenideElementInvocationException;
import com.plugatar.selenidehacks.UnsafeSelenideElement;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link UnsafeSelenideElement} based on {@link SelenideElement}
 * {@link Proxy} implementation.
 *
 * <p>See {@code com.codeborne.selenide.impl.SelenideElementProxy} (class has package-private access modifier).</p>
 */
@ParametersAreNonnullByDefault
public class UnsafeSelenideElementOf implements UnsafeSelenideElement {
    private static final Supplier<Map<String, Method>> CACHED_METHODS_MAPPING = new Supplier<Map<String, Method>>() {
        private volatile Map<String, Method> mapping = null;

        @Override
        public Map<String, Method> get() {
            Map<String, Method> result;
            if ((result = this.mapping) == null) {
                synchronized (this) {
                    if ((result = this.mapping) == null) {
                        result = new HashMap<>(
                            Arrays.stream(SelenideElement.class.getMethods())
                                .collect(Collectors.toMap(
                                    Method::getName,
                                    Function.identity(),
                                    (method1, method2) -> method1
                                ))
                        );
                        this.mapping = result;
                    }
                    return result;
                }
            }
            return result;
        }
    };
    private final InvocationHandler invocationHandler;
    private final SelenideElement proxy;

    /**
     * Ctor.
     *
     * @param element the {@link SelenideElement} instantiated as a {@link Proxy}
     * @throws NullPointerException     if {@code element} is {@code null}
     * @throws IllegalArgumentException if {@code element} is not instantiated as a {@link Proxy}
     */
    public UnsafeSelenideElementOf(final SelenideElement element) {
        this.proxy = Objects.requireNonNull(element, "element arg is null");
        this.invocationHandler = Proxy.getInvocationHandler(element);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <ANY> ANY invoke(final String methodName,
                                  final Object... methodArgs) {
        if (methodName == null) { throw new SelenideElementInvocationException("methodName arg is null"); }
        final Method method = CACHED_METHODS_MAPPING.get().get(methodName);
        if (method == null) {
            throw new SelenideElementInvocationException("Not found SelenideElement method by name: " + methodName);
        }
        try {
            return (ANY) this.invocationHandler.invoke(this.proxy, method, methodArgs);
        } catch (final UndeclaredThrowableException ex) {
            final Throwable causeEx = ex.getUndeclaredThrowable();
            if (causeEx != null) {
                throw sneakyThrow(causeEx);
            } else {
                throw sneakyThrow(ex);
            }
        } catch (final Throwable ex) {
            throw sneakyThrow(ex);
        }
    }

    @Override
    public final SelenideElement asSelenideElement() {
        return this.proxy;
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> RuntimeException sneakyThrow(final Throwable throwable) throws E {
        throw (E) throwable;
    }
}
