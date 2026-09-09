describe('Sessions', () => {
  it('should list sessions for an admin and show create/edit actions', () => {
    cy.loginAsAdmin();

    cy.contains('Sessions available').should('be.visible');
    cy.contains('Yoga doux').should('be.visible');
    cy.contains('Yoga dynamique').should('be.visible');
    cy.contains('Create').should('be.visible');
    cy.contains('Edit').should('be.visible');
  });

  it('should list sessions for a non-admin without create/edit', () => {
    cy.loginAsUser();

    cy.contains('Sessions available').should('be.visible');
    cy.contains('Yoga doux').should('be.visible');
    cy.contains('Create').should('not.exist');
    cy.contains('Edit').should('not.exist');
  });

  it('should open session detail', () => {
    cy.intercept({ method: 'GET', url: /\/api\/session\/1$/ }, { fixture: 'session-detail.json' }).as('sessionDetail');
    cy.intercept({ method: 'GET', url: /\/api\/teacher\/1$/ }, { fixture: 'teacher.json' }).as('teacher');

    cy.loginAsAdmin();

    cy.contains('mat-card.item', 'Yoga doux').within(() => {
      cy.contains('button', 'Detail').click();
    });

    cy.wait('@sessionDetail');
    cy.wait('@teacher');
    cy.url().should('include', '/sessions/detail/1');
    cy.contains('h1', 'Yoga Doux').should('be.visible');
    cy.contains('Marie DUPONT').should('be.visible');
    cy.contains('Delete').should('be.visible');
  });

  it('should allow a non-admin to participate', () => {
    cy.intercept({ method: 'GET', url: /\/api\/session\/1$/ }, { fixture: 'session-detail.json' }).as('sessionDetail');
    cy.intercept({ method: 'GET', url: /\/api\/teacher\/1$/ }, { fixture: 'teacher.json' }).as('teacher');
    cy.intercept('POST', '**/api/session/1/participate/2', {
      body: { message: 'Participation confirmée' },
    }).as('participate');

    const sessionAfterParticipate = {
      id: 1,
      name: 'Yoga doux',
      description: 'Session pour débutants',
      date: '2026-03-15T10:00:00.000+00:00',
      teacher_id: 1,
      users: [2],
      createdAt: '2026-01-01T10:00:00.000+00:00',
      updatedAt: '2026-01-01T10:00:00.000+00:00',
    };

    cy.loginAsUser();

    cy.contains('mat-card.item', 'Yoga doux').within(() => {
      cy.contains('button', 'Detail').click();
    });

    cy.wait('@sessionDetail');
    cy.wait('@teacher');

    cy.intercept({ method: 'GET', url: /\/api\/session\/1$/ }, { body: sessionAfterParticipate }).as('sessionAfterParticipate');
    cy.contains('button', 'Participate').click();
    cy.wait('@participate');
    cy.wait('@sessionAfterParticipate');
    cy.contains('button', 'Do not participate').should('be.visible');
  });
});
