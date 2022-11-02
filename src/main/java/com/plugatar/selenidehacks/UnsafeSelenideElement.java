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
package com.plugatar.selenidehacks;

import com.codeborne.selenide.SelenideElement;
import com.plugatar.selenidehacks.impl.UnsafeSelenideElementOf;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Proxy;

/**
 * Represents an object that provides access to {@link SelenideElement}
 * by method name and method arguments.
 *
 * @see SelenideElement
 * @see Proxy
 */
@ParametersAreNonnullByDefault
public interface UnsafeSelenideElement {

    /**
     * Invokes {@link SelenideElement} method by given name and arguments.
     *
     * @param methodName the method name
     * @param methodArgs the method arguments that will be forwarded to the invoked method
     * @param <ANY>      the method return type
     * @return the value that returned by method invoking
     * @throws SelenideElementInvocationException if {@code methodName} is null
     *                                            of if method not found
     */
    <ANY> ANY invoke(String methodName,
                     Object... methodArgs);

    /**
     * Returns wrapped SelenideElement.
     *
     * @return wrapped SelenideElement
     */
    SelenideElement asSelenideElement();

    /**
     * Returns UnsafeSelenideElement of given SelenideElement.
     *
     * @param element the {@link SelenideElement} instantiated as a {@link Proxy}
     * @throws NullPointerException     if {@code element} is {@code null}
     * @throws IllegalArgumentException if {@code element} is not the {@link SelenideElement} instantiated
     *                                  as a {@link Proxy}
     */
    static UnsafeSelenideElement of(final SelenideElement element) {
        return new UnsafeSelenideElementOf(element);
    }
}
