[![Published on Vaadin  Directory](https://img.shields.io/badge/Vaadin%20Directory-published-00b4f0.svg)](https://vaadin.com/directory/component/vaadin-cdi)
[![Stars on Vaadin Directory](https://img.shields.io/vaadin-directory/star/vaadin-cdi.svg)](https://vaadin.com/directory/component/vaadin-cdi)

# Vaadin CDI

The official CDI integration for [Vaadin Flow](https://github.com/vaadin/flow).

## Development has moved to vaadin/flow

From **Vaadin 25.4** onwards `vaadin-cdi` is developed and released as part of
Flow, in the [vaadin/flow](https://github.com/vaadin/flow) repository:

* the library: [`vaadin-cdi/`](https://github.com/vaadin/flow/tree/main/vaadin-cdi)
* the integration tests: [`flow-tests/vaadin-cdi-tests/`](https://github.com/vaadin/flow/tree/main/flow-tests/vaadin-cdi-tests)

It follows Flow's version numbering there, so the release that goes with Vaadin
25.4 is `vaadin-cdi` 25.4.0 rather than a 16.x one, and a new `vaadin-cdi` is
published with every Flow release. The history of this repository was carried
over with the move, so `git log` and `git blame` on those files still reach the
commits made here.

**Please open issues and pull requests for Vaadin 25.4 and later in
[vaadin/flow](https://github.com/vaadin/flow/issues).** This tracker stays open
for the versions listed below.

## Branches for earlier Vaadin versions

Vaadin 25.3 and earlier are still served from this repository:

* `main` holds the 16.1 line, for Vaadin 25.2 and 25.3
* 16.0 for Vaadin 25.0 and 25.1
* 15.2 for Vaadin 24.8
* 15.1 for Vaadin 24.4
* 15.0 for Vaadin 24
* 14.1 for Vaadin 23.3
* 13.1 for Vaadin 22.1
* 11.3 for Vaadin 14.10
* 10.0 for Vaadin 10
* 4.0 for Vaadin 8 Extended Maintenance with Jakarta
* 3.0 for Vaadin 8.2+
* 2.0 for Vaadin Framework 8.0...8.1 versions
* 1.0 for Vaadin Framework 7 versions

## Using with Vaadin

On Vaadin 25.4 or later the platform manages the version for you, so add the
dependency without one:

```xml
<dependency>
  <groupId>com.vaadin</groupId>
  <artifactId>vaadin-cdi</artifactId>
</dependency>
```

On Vaadin 25.3 or earlier, give the version that matches your platform version
from the list above:

```xml
<dependency>
  <groupId>com.vaadin</groupId>
  <artifactId>vaadin-cdi</artifactId>
  <version>16.1.2</version>
</dependency>
```

If the version you are using is a prerelease, include the prerelease repository
as well:

```xml
<repositories>
  <repository>
    <id>Vaadin prereleases</id>
    <url>https://maven.vaadin.com/vaadin-prereleases</url>
  </repository>
</repositories>
```

## Getting started

Start a project from [vaadin.com/start](https://vaadin.com/start) and pick the
_Project Base with CDI_ to get an empty project set up for you.

## Building the branches kept here

These instructions apply to this repository, i.e. to Vaadin 25.3 and earlier.
For 25.4 and later, build the module in
[vaadin/flow](https://github.com/vaadin/flow) instead.

Execute `mvn clean install -DskipTests` in the root directory to build
vaadin-cdi.

### Run integration tests

Execute `mvn -pl vaadin-cdi-itest -Ptomee verify` in the root directory.

Tests can be executed against the following containers, activating the specific
profile:

* Wildfly Jakarta EE 10: `-Pwildfly`
* OpenLiberty Jakarta EE 10: `-Pliberty`
* Payara Jakarta EE 10: `-Ppayara`
* TomEE Jakarta EE 10: `-Ptomee`

## Contributions

The contributing docs can be found here: https://vaadin.com/docs-beta/latest/guide/contributing/overview/
