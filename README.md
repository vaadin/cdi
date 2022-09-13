[![Published on Vaadin  Directory](https://img.shields.io/badge/Vaadin%20Directory-published-00b4f0.svg)](https://vaadin.com/directory/component/vaadin-cdi)
[![Stars on Vaadin Directory](https://img.shields.io/vaadin-directory/star/vaadin-cdi.svg)](https://vaadin.com/directory/component/vaadin-cdi)

# Vaadin CDI

This is the official CDI integration for [Vaadin Flow](https://github.com/vaadin/flow).

This branch is compatible with upcoming Vaadin platform versions. See other branches for other Vaadin versions:

* 15.0 for upcoming Vaadin 24
* 14.1 for upcoming Vaadin 23.3
* 14.0 for Vaadin 23
* 13.0 for Vaadin 22
* 11.3 for Vaadin 14.8
* 10.0 for Vaadin 10
* 3.0 for Vaadin 8.2+
* 2.0 for Vaadin Framework 8.0...8.1 versions
* 1.0 for Vaadin Framework 7 versions

## Using with Vaadin 10

To use CDI with Vaadin 10, you need to add the following dependency to your pom.xml:
```xml
<dependency>
  <groupdId>com.vaadin</groupId>
  <artifactId>vaadin-cdi</artifactId>
  <version>10.0.0.beta1</version> <!-- Or the LATEST version -->
</dependency>
```

Since the current release version is a prerelease, you need to also include the prerelease Maven repository:

```xml
<repositories>
  <repository>
    <id>Vaadin prereleases</id>
    <url>https://maven.vaadin.com/vaadin-prereleases</url>
  </repository>
<repositories>
```

## Getting started

**NOTE: This is still WIP.** The easiest way for starting a project is to go to [vaadin.com/start](https://vaadin.com/start) and select the _Project Base with CDI_ to get an empty project with everything setup ready for you.

**NOTE: This is still WIP.** There is a tutorial also available in https://github.com/vaadin/flow-cdi-tutorial that helps you get started with Vaadin 10 and CDI.

## Building the project

Execute `mvn clean install` in the root directory to build vaadin-cdi.

## Run integration tests

Execute `mvn -pl vaadin-cdi-itest -Ptomee verify` in the root directory to run integration tests.

## Issue tracking

If you find an issue, please report it in the [GitHub issue tracker](https://github.com/vaadin/cdi/issues).

## Contributions

The contributing docs can be found here: https://vaadin.com/docs-beta/latest/guide/contributing/overview/
