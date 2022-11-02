# Selenide hacks

*The set of Selenide hacks*

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.plugatar/selenide-hacks/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.plugatar/selenide-hacks)
[![Javadoc](https://javadoc.io/badge2/com.plugatar/selenide-hacks/javadoc.svg)](https://javadoc.io/doc/com.plugatar/selenide-hacks)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Hits-of-Code](https://hitsofcode.com/github/evpl/selenide-hacks?branch=master)](https://hitsofcode.com/github/evpl/selenide-hacks/view?branch=master)
[![Lines of code](https://img.shields.io/tokei/lines/github/evpl/selenide-hacks)](https://en.wikipedia.org/wiki/Source_lines_of_code)

## Table of Contents

* [How to use](#How-to-use)
* [API](#API)
    * [CustomArgsCommand](#CustomArgsCommand)
    * [UnsafeSelenideElement](#UnsafeSelenideElement)
    * [OuterCommand](#OuterCommand)

## How to use

Requires Java 8+ version. Just add [Selenide](https://github.com/selenide/selenide) and Selenide hacks dependencies
(versions correspond to the Selenide version).

## API

### CustomArgsCommand

The Selenide `Command` with custom arguments. You can use `CustomArgsCommand.of(Command, Object[])` method or
`CustomArgsCommandOf(Command, Object[])` constructor.

```java
SelenideElement element = driver.$(".class_name");

element.execute(
    CustomArgsCommand.of(new Append(), "text to append"),
    Duration.ofSeconds(20)
);
```

### UnsafeSelenideElement

Represents an object that provides access to `SelenideElement` by method name and method arguments. You can
use `UnsafeSelenideElement.of(SelenideElement)` method or `UnsafeSelenideElementOf(SelenideElement)` constructor.

```java
SelenideElement element = driver.$(".class_name");

/* The first way */
UnsafeSelenideElement.of(element).invoke("append", "text to append");

/* The second way */
UnsafeSelenideElement.of(element).invoke(
    "execute",
    CustomArgsCommand.of(new Append(), "text to append"),
    Duration.ofSeconds(20)
);

/* The third way */
UnsafeSelenideElement.of(element).invoke("append", "text to append", Duration.ofSeconds(20));
```

### OuterCommand

```java
SelenideElement element = driver.$(".class_name");

/* The first way */
OuterCommand.of("append", "text to append").executeOn(element);

/* The second way */
OuterCommand.of(
    "execute",
    CustomArgsCommand.of(new Append(), "text to append"),
    Duration.ofSeconds(20)
).executeOn(element);

/* The third way */
OuterCommand.of("append", "text to append", Duration.ofSeconds(20)).executeOn(element);
```
