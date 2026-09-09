describe('Account and logout', () => {
  it('should display account information', () => {
    cy.intercept('GET', '**/api/user/1', { fixture: 'user.json' }).as('me');

    cy.loginAsAdmin();
    cy.contains('Account').click();

    cy.wait('@me');
    cy.url().should('include', '/me');
    cy.contains('User information').should('be.visible');
    cy.contains('Name: Admin ADMIN').should('be.visible');
    cy.contains('Email: yoga@studio.com').should('be.visible');
    cy.contains('You are admin').should('be.visible');
  });

  it('should logout and show login/register links', () => {
    cy.loginAsAdmin();

    cy.contains('Logout').click();

    cy.url().should('include', '/login');
    cy.contains('a', 'Login').should('be.visible');
    cy.contains('a', 'Register').should('be.visible');
    cy.contains('Sessions').should('not.exist');
    cy.contains('Account').should('not.exist');
  });
});
