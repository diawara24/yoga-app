describe('Login', () => {
  it('should login successfully as admin', () => {
    cy.intercept('POST', '**/api/auth/login', { fixture: 'session.json' }).as('login');
    cy.intercept({ method: 'GET', url: /\/api\/session$/ }, { fixture: 'sessions.json' }).as('sessions');

    cy.visit('/login');

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234');
    cy.get('button[type=submit]').click();

    cy.wait('@login');
    cy.url().should('include', '/sessions');
    cy.wait('@sessions');

    cy.contains('Sessions').should('be.visible');
    cy.contains('Account').should('be.visible');
    cy.contains('Logout').should('be.visible');
    cy.contains('Sessions available').should('be.visible');
    cy.contains('Create').should('be.visible');
  });

  it('should display an error message on login failure', () => {
    cy.intercept('POST', '**/api/auth/login', {
      statusCode: 401,
      body: { detail: 'Identifiants incorrects ou session expirée.' },
    }).as('loginError');

    cy.visit('/login');

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('wrong-password');
    cy.get('button[type=submit]').click();

    cy.wait('@loginError');
    cy.url().should('include', '/login');
    cy.get('.error').should('contain', 'Identifiants incorrects ou session expirée.');
  });

  it('should keep submit disabled when form is invalid', () => {
    cy.visit('/login');

    cy.get('button[type=submit]').should('be.disabled');
    cy.get('input[formControlName=email]').type('not-an-email');
    cy.get('button[type=submit]').should('be.disabled');
  });
});
