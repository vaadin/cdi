vaadin-cdi
==========

Vaadin-CDI is official CDI integration for Vaadin framework version 7.


Changes in 1.0.0.alpha3
-----------------------

- Reintroduce conventions for mapping of views and UIs
  - This requires updating @CDIUI and @CDIView parameter values in existing
    applications.
  - see http://dev.vaadin.com/ticket/12385 for details
- @UIScoped is inherited by subclasses of an annotated UI
- Support non-JEE containers with Weld (BeanManager look-up)
- Use BeanManager.resolve() to support @Alternative UIs
- Partial passivation support
- Automatically injected servlet has async-supported enabled
- Reduced unnecessary logging
- See http://dev.vaadin.com/query?status=closed&milestone=Vaadin+CDI+1.0.0.alpha3
  for the complete list of changes.


Copyright 2012-2014 Vaadin Ltd.

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.