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
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link OuterCommandOf}.
 */
final class OuterCommandOfTest {

    @Test
    void commandCtorThrowsExceptionForNullCommandParam() {
        assertThatCode(() -> new OuterCommandOf<>((Command<?>) null, new Object[0]))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void commandCtorDoesntThrowExceptionForNullArgsParam() {
        assertThatCode(() -> new OuterCommandOf<>((Command<?>) mock(Command.class), (Object[]) null))
            .doesNotThrowAnyException();
    }

    @Test
    void methodCtorThrowsExceptionForNullMethodNameParam() {
        assertThatCode(() -> new OuterCommandOf<>((String) null, new Object[0]))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void methodCtorDoesntThrowExceptionForNullArgsParam() {
        assertThatCode(() -> new OuterCommandOf<>("", (Object[]) null))
            .doesNotThrowAnyException();
    }

    @Test
    void executeOnMethodThrowsExceptionForNullElementParam() {
        final OuterCommand<Object> outerCommand = new OuterCommandOf<>("toString");
        assertThatCode(() -> outerCommand.executeOn((SelenideElement) null))
            .isInstanceOf(SelenideElementInvocationException.class);
    }

    @Test
    void executeOnMethodThrowsExceptionForNotProxyElementParam() {
        final OuterCommand<Object> outerCommand = new OuterCommandOf<>("toString");
        assertThatCode(() -> outerCommand.executeOn(mock(SelenideElement.class)))
            .isInstanceOf(SelenideElementInvocationException.class);
    }

    private static Stream<Arguments> methodsArgs() {
        return Stream.of(
            Arguments.of(new Object[]{}, null),
            Arguments.of(new Object[]{"abc"}, "res"),
            Arguments.of(new Object[]{123, "abc", 4L}, 12),
            Arguments.of(new Object[]{}, 4L),
            Arguments.of(new Object[]{123, "abc", 4L}, new Object())
        );
    }

    @ParameterizedTest
    @MethodSource("methodsArgs")
    void executeOnMethodUseCorrectObjectsForMethodNameCtor(final Object[] args,
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

        final Object invocationResult = new OuterCommandOf<>(methodName, args).executeOn(proxySelenideElement);
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

    @ParameterizedTest
    @MethodSource("methodsArgs")
    void executeOnMethodUseCorrectObjectsForCommandCtor(final Object[] args,
                                                        final Object result) {
        final String methodName = "execute";
        final Command<Object> command = (proxy, locator, commandArgs) -> new Object();
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

        final Object invocationResult = new OuterCommandOf<>(command, args).executeOn(proxySelenideElement);
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
            .hasSizeGreaterThan(0);
        referencesAssertions
            .assertThat(argsReference.get()[0])
            .as("args first param is command")
            .isSameAs(command);
        referencesAssertions
            .assertThat(invocationResult)
            .as("result is origin")
            .isSameAs(result);
        referencesAssertions.assertAll();
    }
}
