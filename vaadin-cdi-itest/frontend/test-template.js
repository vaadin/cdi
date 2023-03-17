/*
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
import {html} from '@polymer/polymer/lib/utils/html-tag.js';
import {PolymerElement} from '@polymer/polymer/polymer-element.js';

class TestTemplate extends PolymerElement {
  static get template() {
    return html`
        <div>
        AAAAAAAAAAAAAAAAAAAAAAAAA
            <input id="input"/>
            <label id="label"/>
        </div>
    `;
  }

  static get is() {
    return 'test-template'
  }
}

window.customElements.define(TestTemplate.is, TestTemplate);
