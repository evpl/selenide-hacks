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
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link CustomArgsCommandOf}.
 */
final class CustomArgsCommandOfTest {

    @Test
    void ctorThrowsExceptionForNullOriginCommandParam() {
        assertThatCode(() -> new CustomArgsCommandOf<>((Command<Object>) null, new Object[0]))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void executeMethodReplaceOnlyArgs() throws IOException {
        final SelenideElement trueProxy = mock(SelenideElement.class);
        final WebElementSource trueLocator = mock(WebElementSource.class);
        final Object[] trueArgs = new Object[]{};

        final AtomicReference<SelenideElement> proxyReference = new AtomicReference<>();
        final AtomicReference<WebElementSource> locatorReference = new AtomicReference<>();
        final AtomicReference<Object[]> argsReference = new AtomicReference<>();

        final Command<Object> setReferencesCommand = (selenideElement, webElementSource, args) -> {
            proxyReference.set(selenideElement);
            locatorReference.set(webElementSource);
            argsReference.set(args);
            return "setReferencesCommand";
        };

        new CustomArgsCommandOf<>(setReferencesCommand, trueArgs).execute(
            trueProxy,
            trueLocator,
            new Object[]{}
        );

        final SoftAssertions referencesAssertions = new SoftAssertions();
        referencesAssertions
            .assertThat(proxyReference.get())
            .as("proxy param is origin")
            .isSameAs(trueProxy);
        referencesAssertions
            .assertThat(locatorReference.get())
            .as("locator param is origin")
            .isSameAs(trueLocator);
        referencesAssertions
            .assertThat(argsReference.get())
            .as("args param is changed")
            .isSameAs(trueArgs);
        referencesAssertions.assertAll();
    }
}
