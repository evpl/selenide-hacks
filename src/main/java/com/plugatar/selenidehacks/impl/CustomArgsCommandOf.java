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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;

/**
 * Command with custom arguments.
 *
 * @param <T> the command return type
 */
@ParametersAreNonnullByDefault
public class CustomArgsCommandOf<T> implements Command<T> {
    private final Command<? extends T> originCommand;
    private final Object[] commandArgs;

    /**
     * Ctor.
     *
     * @param originCommand the origin command
     * @param commandArgs   the command arguments
     * @throws NullPointerException if {@code originCommand} is null
     */
    public CustomArgsCommandOf(final Command<? extends T> originCommand,
                               final @Nullable Object... commandArgs) {
        this.originCommand = Objects.requireNonNull(originCommand, "originCommand arg is null");
        this.commandArgs = commandArgs;
    }

    @Override
    public final T execute(final SelenideElement proxy,
                           final WebElementSource locator,
                           final @Nullable Object[] ignoredArgs) throws IOException {
        return this.originCommand.execute(proxy, locator, this.commandArgs);
    }
}
