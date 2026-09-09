describe('Register', () => {
  it('should register successfully and redirect to login', () => {
    cy.intercept('POST', '**/api/auth/register', {
      statusCode: 200,
      body: { message: 'User registered successfully!' },
    }).as('register');

    cy.visit('/register');

    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('john.doe@test.com');
    cy.get('input[formControlName=password]').type('password');
    cy.get('button[type=submit]').click();

    cy.wait('@register');
    cy.url().should('include', '/login');
  });

  it('should display an error message when email is already taken', () => {
    cy.intercept('POST', '**/api/auth/register', {
      statusCode: 400,
      body: { detail: 'Cet email est déjà utilisé.' },
    }).as('registerError');

    cy.visit('/register');

    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('password');
    cy.get('button[type=submit]').click();

    cy.wait('@registerError');
    cy.url().should('include', '/register');
    cy.get('.error').should('contain', 'Cet email est déjà utilisé.');
  });
});
