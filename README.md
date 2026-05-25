# Vaadin CDI

Vaadin CDI is the official CDI integration for [Vaadin Framework](https://github.com/vaadin/framework).
This branch is compatible with Vaadin 8 Extended Maintenance versions 8.29.0 and up using a Jakarta runtime.
See other branches for other framework versions:

* master for Vaadin 10
* 4.0 for Vaadin 8 Extended Maintenance with Jakarta
* 3.0 for Vaadin 8.2+
* 2.0 for Vaadin Framework 8.0...8.1 versions
* 1.0 for Vaadin Framework 7 versions

## Migrating from an earlier release

* Replace mentions of `javax.*` in your POMs and Java source files with the `jakarta` equivalents.
* Replace your `vaadin-server` dependency with `vaadin-server-mpr-jakarta`, and your `vaadin-push` dependency with `vaadin-push-jakarta`.

## Download release

Official releases of this add-on are available at Vaadin Directory. For Maven instructions, download and reviews, go to [Vaadin CDI](https://vaadin.com/addon/vaadin-cdi).

## Building the project

Execute `mvn clean install` in the root directory to build vaadin-cdi.

## Issue tracking

If you find an issue, please report it in the [GitHub issue tracker](https://github.com/vaadin/cdi/issues).

## Contributions

Contributions to the project can be done through pull requests in GitHub.

---

## Copyright 2012-2026 Vaadin Ltd

This program is available under Vaadin Commercial License and Service Terms.

See https://vaadin.com/commercial-license-and-service-terms for the full license.
