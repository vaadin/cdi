# Vaadin CDI

This is the official CDI integration for [Vaadin Flow](https://github.com/vaadin/flow).

This branch is Vaadin 10.0 (Flow 1.0) compatible. See other branches for other Vaadin versions:

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

## Issue tracking

If you find an issue, please report it in the [GitHub issue tracker](https://github.com/vaadin/cdi/issues).

## Contributions

Contributions to the project can be done through pull requests in GitHub.

---

Copyright 2012-2018 Vaadin Ltd.

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
