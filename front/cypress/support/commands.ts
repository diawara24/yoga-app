/// <reference types="cypress" />

declare namespace Cypress {
  interface Chainable {
    clearSession(): Chainable<void>;
    loginAsAdmin(): Chainable<void>;
    loginAsUser(): Chainable<void>;
  }
}

Cypress.Commands.add('clearSession', () => {
  cy.clearLocalStorage();
});

Cypress.Commands.add('loginAsAdmin', () => {
  cy.intercept('POST', '**/api/auth/login', { fixture: 'session.json' }).as('login');
  cy.intercept({ method: 'GET', url: /\/api\/session$/ }, { fixture: 'sessions.json' }).as('sessions');

  cy.visit('/login', {
    onBeforeLoad(win) {
      win.localStorage.clear();
    },
  });
  cy.get('input[formControlName=email]').type('yoga@studio.com');
  cy.get('input[formControlName=password]').type('test!1234');
  cy.get('button[type=submit]').click();

  cy.wait('@login');
  cy.url().should('include', '/sessions');
  cy.wait('@sessions');
});

Cypress.Commands.add('loginAsUser', () => {
  cy.intercept('POST', '**/api/auth/login', { fixture: 'session-user.json' }).as('login');
  cy.intercept({ method: 'GET', url: /\/api\/session$/ }, { fixture: 'sessions.json' }).as('sessions');

  cy.visit('/login', {
    onBeforeLoad(win) {
      win.localStorage.clear();
    },
  });
  cy.get('input[formControlName=email]').type('user@studio.com');
  cy.get('input[formControlName=password]').type('test!1234');
  cy.get('button[type=submit]').click();

  cy.wait('@login');
  cy.url().should('include', '/sessions');
  cy.wait('@sessions');
});
