// ***********************************************************
// This example support/index.js is processed and
// loaded automatically before your test files.
// ***********************************************************

import './commands';
import '@cypress/code-coverage/support';

beforeEach(() => {
  cy.clearSession();
});
