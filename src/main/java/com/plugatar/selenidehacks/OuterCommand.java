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

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.plugatar.selenidehacks.impl.OuterCommandOf;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Proxy;

/**
 * Represents command that can be executed on an {@link SelenideElement}
 *
 * @param <T> the command return type
 */
@ParametersAreNonnullByDefault
public interface OuterCommand<T> {

    /**
     * Executes this command on given SelenideElement.
     *
     * @param element the SelenideElement for execution
     * @return the command returned value
     * @throws SelenideElementInvocationException if {@code element} is {@code null}
     *                                            or if {@code element} is not instantiated as a {@link Proxy}
     */
    T executeOn(SelenideElement element);

    /**
     * Returns OuterCommand of given method name and arguments.
     *
     * @param methodName the method name
     * @param methodArgs the method arguments
     * @param <T>        the command return type
     * @return OuterCommand of given method name and args
     */
    static <T> OuterCommand<T> of(final String methodName,
                                  final Object... methodArgs) {
        return new OuterCommandOf<>(methodName, methodArgs);
    }

    /**
     * Returns OuterCommand based on {@link SelenideElement#execute(Command)} method
     * of given command (as a first argument) and rest arguments.
     *
     * @param executeMethodFirstArg the execute method first argument
     * @param executeMethodRestArgs the execute method rest arguments
     * @param <T>                   the command return type
     * @return OuterCommand of given command (as a first argument) and rest arguments
     */
    static <T> OuterCommand<T> of(final Command<? extends T> executeMethodFirstArg,
                                  final Object... executeMethodRestArgs) {
        return new OuterCommandOf<>(executeMethodFirstArg, executeMethodRestArgs);
    }
}
