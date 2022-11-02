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
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link CustomArgsCommand}.
 */
final class CustomArgsCommandTest {

    @Test
    void ofMethodThrowsExceptionForNullOriginCommandParam() {
        assertThatCode(() -> CustomArgsCommand.of((Command<Object>) null, new Object[0]))
            .isInstanceOf(NullPointerException.class);
    }
}
