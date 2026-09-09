# Yoga App — Front

Application Angular 19 (Yoga sessions) — projet OpenClassrooms *Testez et améliorez une application full-stack*.

## Prérequis

- Node.js / npm
- Backend démarré (API disponible via le proxy)

## Installation

```bash
cd front
npm install
```

## Démarrer le front

```bash
npm start
```

L’application est disponible sur [http://localhost:4200](http://localhost:4200).

---

## Tests

### Tests unitaires (Jest)

Lancer les tests :

```bash
npm test
```

Mode watch :

```bash
npm run test:watch
```

Tests + rapport de couverture :

```bash
npm run test:coverage
```

Rapport HTML Jest :

> `coverage/jest/index.html`

### Tests E2E (Cypress)

Les specs se trouvent dans `cypress/e2e/` :

- `login.cy.ts` — connexion (succès / erreur / formulaire invalide)
- `register.cy.ts` — inscription
- `sessions.cy.ts` — liste, détail, participation
- `account.cy.ts` — profil et logout

**Important :** démarrer le front avant (`npm start`).

Lancer Cypress en headless :

```bash
npm run test:e2e
```

Ouvrir l’interface Cypress :

```bash
npm run test:e2e:open
```

Équivalents :

```bash
npm run cypress:run
npm run cypress:open
```

Via Angular CLI (avec instrumentation coverage) :

```bash
npm run e2e
npm run e2e:ci
```

Générer le rapport de couverture E2E (après un run e2e instrumenté) :

```bash
npm run e2e:coverage
```

Rapport HTML E2E :

> `coverage/lcov-report/index.html`

---

## Structure utile

| Dossier / fichier | Rôle |
|---|---|
| `src/app/` | Code applicatif Angular |
| `src/app/**/*.spec.ts` | Tests unitaires Jest |
| `cypress/e2e/` | Tests end-to-end Cypress |
| `cypress/fixtures/` | Données mockées pour Cypress |
| `cypress/support/commands.ts` | Helpers (`loginAsAdmin`, `loginAsUser`, …) |
| `RAPPORT_ANALYSE_BONNES_PRATIQUES.md` | Analyse et corrections des bonnes pratiques |

---

## Scripts npm (résumé)

| Script | Description |
|---|---|
| `npm start` | Serveur de dev Angular |
| `npm test` | Tests unitaires Jest |
| `npm run test:watch` | Jest en mode watch |
| `npm run test:coverage` | Jest + couverture |
| `npm run test:e2e` | Cypress headless |
| `npm run test:e2e:open` | Cypress UI |
| `npm run e2e` | Cypress via Angular CLI (coverage) |
| `npm run e2e:coverage` | Rapport coverage E2E (nyc) |
