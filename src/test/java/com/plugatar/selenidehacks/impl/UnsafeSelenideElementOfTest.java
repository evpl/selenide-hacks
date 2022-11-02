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
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link UnsafeSelenideElementOf}.
 */
final class UnsafeSelenideElementOfTest {

    @Test
    void ctorThrowsExceptionForNullParam() {
        assertThatCode(() -> new UnsafeSelenideElementOf((SelenideElement) null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void ctorThrowsExceptionForNotProxyParam() {
        assertThatCode(() -> new UnsafeSelenideElementOf(mock(SelenideElement.class)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void invokeMethodThrowsExceptionForNullMethodNameParam() {
        final SelenideElement proxySelenideElement = (SelenideElement) Proxy.newProxyInstance(
            this.getClass().getClassLoader(),
            new Class[]{SelenideElement.class},
            (proxy, method, proxyArgs) -> null
        );
        final UnsafeSelenideElement unsafeSelenideElement = new UnsafeSelenideElementOf(proxySelenideElement);

        assertThatCode(() -> unsafeSelenideElement.invoke((String) null))
            .isInstanceOf(SelenideElementInvocationException.class);
    }

    private static Stream<Arguments> methodsNamesWithArgs() {
        return Stream.of(
            Arguments.of(new Object[]{}, null),
            Arguments.of(new Object[]{"abc"}, "res"),
            Arguments.of(new Object[]{123, "abc", 4L}, 12),
            Arguments.of(new Object[]{}, 4L),
            Arguments.of(new Object[]{123, "abc", 4L}, new Object())
        );
    }

    @ParameterizedTest
    @MethodSource("methodsNamesWithArgs")
    void invokeMethodUseCorrectObjects(final Object[] args,
                                       final Object result) {
        final String methodName = "toString";
        final AtomicReference<Object> proxyReference = new AtomicReference<>();
        final AtomicReference<String> methodNameReference = new AtomicReference<>();
        final AtomicReference<Object[]> argsReference = new AtomicReference<>();
        final SelenideElement proxySelenideElement = (SelenideElement) Proxy.newProxyInstance(
            this.getClass().getClassLoader(),
            new Class[]{SelenideElement.class},
            (proxy, method, proxyArgs) -> {
                if (methodName.equals(method.getName())) {
                    proxyReference.set(proxy);
                    methodNameReference.set(method.getName());
                    argsReference.set(proxyArgs);
                    return result;
                }
                return null;
            }
        );

        final Object invocationResult = new UnsafeSelenideElementOf(proxySelenideElement)
            .invoke(methodName, args);
        final SoftAssertions referencesAssertions = new SoftAssertions();
        referencesAssertions
            .assertThat(proxyReference.get())
            .as("proxy param is origin")
            .isSameAs(proxySelenideElement);
        referencesAssertions
            .assertThat(methodNameReference.get())
            .as("method param is origin")
            .isSameAs(methodName);
        referencesAssertions
            .assertThat(argsReference.get())
            .as("args param is origin")
            .isSameAs(args);
        referencesAssertions
            .assertThat(invocationResult)
            .as("result is origin")
            .isSameAs(result);
        referencesAssertions.assertAll();
    }

    @Test
    void invokeMethodThrowsExceptionForMethodException() {
        final Throwable methodException = new Throwable();
        final SelenideElement proxySelenideElement = (SelenideElement) Proxy.newProxyInstance(
            this.getClass().getClassLoader(),
            new Class[]{SelenideElement.class},
            (proxy, method, proxyArgs) -> {
                throw methodException;
            }
        );

        final UnsafeSelenideElement unsafeSelenideElement = new UnsafeSelenideElementOf(proxySelenideElement);
        assertThatCode(() -> unsafeSelenideElement.invoke("toString"))
            .isSameAs(methodException);
    }

    @Test
    void asSelenideElementMethod() {
        final SelenideElement proxySelenideElement = (SelenideElement) Proxy.newProxyInstance(
            this.getClass().getClassLoader(),
            new Class[]{SelenideElement.class},
            (proxy, method, proxyArgs) -> new Object()
        );

        final UnsafeSelenideElement unsafeSelenideElement = new UnsafeSelenideElementOf(proxySelenideElement);
        assertThat(unsafeSelenideElement.asSelenideElement()).isSameAs(proxySelenideElement);
    }
}
