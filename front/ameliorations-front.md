# Recommandations de mise à jour du front Angular

## Objectif

Améliorer la qualité du code Angular du front en appliquant les bonnes pratiques de développement :

- typer correctement les méthodes et variables ;
- supprimer les `any` ;
- gérer proprement les flux observables ;
- moderniser les templates vers la nouvelle syntaxe Angular ;
- sécuriser les accès à l’état utilisateur/session ;
- améliorer la lisibilité et la maintenabilité.

---

## Remarque générale

Le projet est fonctionnel, mais il reste plusieurs points à corriger selon les bonnes pratiques Angular actuelles. La priorité est surtout la qualité du typage, la robustesse des composants et la migration vers les nouvelles syntaxes de template.

Angular CLI propose aussi une migration simple vers la nouvelle syntaxe :

```bash
ng generate @angular/core:control-flow
```

Cette commande aide à convertir automatiquement les structures du type `*ngIf` et `*ngFor` vers `@if` et `@for`.

---

## Fichier par fichier : observation et correction à apporter

### 1) [src/app/app.component.ts](src/app/app.component.ts)

- Observation : la méthode `$isLogged()` est bien typée, mais il manque une protection explicite sur l’état de session et une meilleure cohérence des signatures de méthodes.
- Correction à apporter :
  - vérifier la présence de `sessionService` avant de l’utiliser ;
  - garder des signatures claires ;
  - sécuriser l’accès au service dans une logique plus robuste.

```ts
public $isLogged(): Observable<boolean> {
  return this.sessionService.$isLogged();
}

public logout(): void {
  if (!this.sessionService.sessionInformation) {
    this.router.navigate(['/login']);
    return;
  }

  this.sessionService.logOut();
  this.router.navigate(['']);
}
```

### 2) [src/app/app.component.html](src/app/app.component.html)

- Observation : le template utilise encore `*ngIf` et `ng-template`.
- Correction à apporter :
  - remplacer `*ngIf` par `@if` ;
  - remplacer le bloc `ng-template` par un `@else`.

```html
<mat-toolbar color="primary" fxLayout="row" fxLayoutAlign="space-between center">
  <span>Yoga app</span>

  @if ($isLogged() | async) {
    <div>
      <span routerLink="sessions" class="link">Sessions</span>
      <span routerLink="me" class="link">Account</span>
      <span (click)="logout()" class="link">Logout</span>
    </div>
  } @else {
    <div>
      <a routerLink="/login" routerLinkActive="active" class="link">Login</a>
      <a routerLink="/register" routerLinkActive="active" class="link">Register</a>
    </div>
  }
</mat-toolbar>
```

### 3) [src/app/components/me/me.component.ts](src/app/components/me/me.component.ts)

- Observation : la souscription est directe et le code accède à `sessionInformation!` sans garde. C’est un point à corriger pour éviter les erreurs et les risques de `undefined`.
- Correction à apporter :
  - vérifier la présence de `sessionInformation` avant d’utiliser `id` ;
  - sécuriser le flux de chargement de données ;
  - utiliser `takeUntil` ou `DestroyRef` si le composant alimente des flux plus complexes.

```ts
ngOnInit(): void {
  if (!this.sessionService.sessionInformation) {
    this.router.navigate(['/login']);
    return;
  }

  this.userService
    .getById(this.sessionService.sessionInformation.id.toString())
    .subscribe((user: User) => {
      this.user = user;
    });
}

public delete(): void {
  if (!this.sessionService.sessionInformation) {
    this.router.navigate(['/login']);
    return;
  }

  this.userService
    .delete(this.sessionService.sessionInformation.id.toString())
    .subscribe(() => {
      this.matSnackBar.open('Your account has been deleted !', 'Close', { duration: 3000 });
      this.sessionService.logOut();
      this.router.navigate(['/']);
    });
}
```

### 4) [src/app/components/me/me.component.html](src/app/components/me/me.component.html)

- Observation : beaucoup de conditions `*ngIf` sont présentes.
- Correction à apporter :
  - remplacer les `*ngIf` par `@if` ;
  - garder le template plus lisible et conforme aux bonnes pratiques Angular modernes.

```html
@if (user) {
  <div fxLayout="column" fxLayoutAlign="start center">
    <p>Name: {{ user.firstName }} {{ user.lastName | uppercase }}</p>
    <p>Email: {{ user.email }}</p>

    @if (user.admin) {
      <p class="my2">You are admin</p>
    } @else {
      <div fxLayout="column" class="my2" fxLayoutAlign="center center">
        <p>Delete my account:</p>
        <button mat-raised-button color="warn" (click)="delete()">
          <mat-icon>delete</mat-icon>
          <span class="ml1">Detail</span>
        </button>
      </div>
    }
  </div>
}
```

### 5) [src/app/core/service/session-api.service.ts](src/app/core/service/session-api.service.ts)

- Observation : la méthode `delete()` retourne `Observable<any>`. C’est un vrai point de non-conformité avec le typage strict.
- Correction à apporter :
  - remplacer `Observable<any>` par `Observable<void>` ou un type métier exact ;
  - garder les mêmes conventions dans tous les services HTTP.

```ts
public delete(id: string): Observable<void> {
  return this.httpClient.delete<void>(`${this.pathService}/${id}`);
}

public create(session: Session): Observable<Session> {
  return this.httpClient.post<Session>(this.pathService, session);
}
```

### 6) [src/app/core/service/user.service.ts](src/app/core/service/user.service.ts)

- Observation : la suppression retourne `Observable<any>`, ce qui est une faiblesse de typage.
- Correction à apporter :
  - remplacer `Observable<any>` par `Observable<void>` ;
  - sécuriser le contrat du service.

```ts
public delete(id: string): Observable<void> {
  return this.httpClient.delete<void>(`${this.pathService}/${id}`);
}
```

### 7) [src/app/core/service/teacher.service.ts](src/app/core/service/teacher.service.ts)

- Observation : le service est correctement typé, mais il faut rester cohérent avec la convention globale du projet.
- Correction à apporter :
  - garder les signatures homogènes ;
  - éviter les écarts de style entre services.

```ts
public all(): Observable<Teacher[]> {
  return this.httpClient.get<Teacher[]>(this.pathService);
}

public detail(id: string): Observable<Teacher> {
  return this.httpClient.get<Teacher>(`${this.pathService}/${id}`);
}
```

### 8) [src/app/core/service/session.service.ts](src/app/core/service/session.service.ts)

- Observation : le service d’état de session est centralisé, mais il faut sécuriser les accès à `sessionInformation` et éviter l’usage massif de `!`.
- Correction à apporter :
  - vérifier l’existence de `sessionInformation` avant utilisation ;
  - garder une logique d’authentification claire et typée ;
  - éviter les assertions non null abusives.

```ts
public get isLogged(): boolean {
  return !!this.sessionInformation;
}

public logIn(sessionInformation: SessionInformation): void {
  this.sessionInformation = sessionInformation;
  this.isLoggedSubject.next(true);
}
```

### 9) [src/app/pages/login/login.component.ts](src/app/pages/login/login.component.ts)

- Observation : la souscription est correcte, mais il manque un état plus explicite pour le chargement et l’erreur.
- Correction à apporter :
  - ajouter `isSubmitting` ;
  - gérer les erreurs HTTP dans un bloc `error` clair ;
  - éviter les doubles soumissions.

```ts
public isSubmitting = false;

public submit(): void {
  if (this.form.invalid) {
    return;
  }

  this.isSubmitting = true;
  const loginRequest = this.form.value as LoginRequest;

  this.authService.login(loginRequest).subscribe({
    next: (response: SessionInformation) => {
      this.sessionService.logIn(response);
      this.isSubmitting = false;
      this.router.navigate(['/sessions']);
    },
    error: () => {
      this.onError = true;
      this.isSubmitting = false;
    }
  });
}
```

### 10) [src/app/pages/login/login.component.html](src/app/pages/login/login.component.html)

- Observation : le template contient `*ngIf` pour le message d’erreur.
- Correction à apporter :
  - remplacer par `@if` ;
  - garder le rendu plus moderne.

```html
@if (onError) {
  <p class="error">An error occurred</p>
}
```

### 11) [src/app/pages/register/register.component.ts](src/app/pages/register/register.component.ts)

- Observation : la logique de soumission fonctionne, mais elle manque de robustesse sur les états UI et le typage explicite.
- Correction à apporter :
  - ajouter `isSubmitting` ;
  - sécuriser le `RegisterRequest` avant envoi ;
  - typer correctement les callbacks.

```ts
public isSubmitting = false;

public submit(): void {
  if (this.form.invalid) {
    return;
  }

  this.isSubmitting = true;
  const registerRequest = this.form.value as RegisterRequest;

  this.authService.register(registerRequest).subscribe({
    next: () => {
      this.isSubmitting = false;
      this.router.navigate(['/login']);
    },
    error: () => {
      this.onError = true;
      this.isSubmitting = false;
    }
  });
}
```

### 12) [src/app/pages/register/register.component.html](src/app/pages/register/register.component.html)

- Observation : utilisation de `*ngIf` pour afficher l’erreur.
- Correction à apporter :
  - remonter vers la nouvelle syntaxe `@if`.

```html
@if (onError) {
  <span class="error ml2">An error occurred</span>
}
```

### 13) [src/app/pages/sessions/components/detail/detail.component.ts](src/app/pages/sessions/components/detail/detail.component.ts)

- Observation : plusieurs souscriptions, nombreux accès non sécurisés et un `(_: any)` qui viole les bonnes pratiques.
- Correction à apporter :
  - supprimer le `any` ;
  - sécuriser `sessionInformation` ;
  - utiliser un type explicite pour les callbacks ;
  - diviser les responsabilités si nécessaire.

```ts
public delete(): void {
  if (!this.sessionService.sessionInformation) {
    this.router.navigate(['/login']);
    return;
  }

  this.sessionApiService.delete(this.sessionId).subscribe(() => {
    this.matSnackBar.open('Session deleted !', 'Close', { duration: 3000 });
    this.router.navigate(['sessions']);
  });
}

private fetchSession(): void {
  this.sessionApiService.detail(this.sessionId).subscribe((session: Session) => {
    this.session = session;
    this.isParticipate = session.users.includes(this.sessionService.sessionInformation!.id);

    this.teacherService.detail(session.teacher_id.toString()).subscribe((teacher: Teacher) => {
      this.teacher = teacher;
    });
  });
}
```

### 14) [src/app/pages/sessions/components/detail/detail.component.html](src/app/pages/sessions/components/detail/detail.component.html)

- Observation : plusieurs conditions `*ngIf` sur le template.
- Correction à apporter :
  - remplacer par `@if` ;
  - simplifier la structure conditionnelle.

```html
@if (session) {
  <mat-card>
    <mat-card-title>
      <div fxLayout="row" fxLayoutAlign="space-between center">
        <div fxLayout="row" fxLayoutAlign="start center">
          <button mat-icon-button (click)="back()">
            <mat-icon>arrow_back</mat-icon>
          </button>
          <h1>{{ session.name | titlecase }}</h1>
        </div>

        <div>
          @if (isAdmin) {
            <button mat-raised-button color="warn" (click)="delete()">Delete</button>
          } @else {
            @if (!isParticipate) {
              <button mat-raised-button color="primary" (click)="participate()">Participate</button>
            } @else {
              <button mat-raised-button color="warn" (click)="unParticipate()">Do not participate</button>
            }
          }
        </div>
      </div>
    </mat-card-title>
  </mat-card>
}
```

### 15) [src/app/pages/sessions/components/form/form.component.ts](src/app/pages/sessions/components/form/form.component.ts)

- Observation : le composant gère trop de responsabilités et utilise une vérification `sessionInformation!` non sécurisée.
- Correction à apporter :
  - sécuriser `sessionInformation` avant de lire `admin` ;
  - typer `sessionForm` correctement ;
  - garder les méthodes internes nettes et lisibles.

```ts
ngOnInit(): void {
  if (!this.sessionService.sessionInformation || !this.sessionService.sessionInformation.admin) {
    this.router.navigate(['/sessions']);
    return;
  }

  const url = this.router.url;

  if (url.includes('update')) {
    this.onUpdate = true;
    this.id = this.route.snapshot.paramMap.get('id') ?? undefined;

    if (this.id) {
      this.sessionApiService.detail(this.id).subscribe((session: Session) => this.initForm(session));
      return;
    }
  }

  this.initForm();
}
```

### 16) [src/app/pages/sessions/components/form/form.component.html](src/app/pages/sessions/components/form/form.component.html)

- Observation : `*ngIf` et `*ngFor` sont encore utilisés.
- Correction à apporter :
  - migrer vers `@if` et `@for` ;
  - simplifier la lecture du template.

```html
@if (sessionForm) {
  <form class="mt2" fxLayout="column" [formGroup]="sessionForm" (ngSubmit)="submit()">
    <mat-form-field appearance="outline" fxFlex>
      <mat-label>Name</mat-label>
      <input matInput formControlName="name">
    </mat-form-field>

    <mat-form-field appearance="outline">
      <mat-label>Teacher</mat-label>
      <mat-select ngDefaultControl formControlName="teacher_id">
        @for (teacher of teachers$ | async; track teacher.id) {
          <mat-option [value]="teacher.id">
            {{ teacher.firstName }} {{ teacher.lastName }}
          </mat-option>
        }
      </mat-select>
    </mat-form-field>
  </form>
}
```

### 17) [src/app/pages/sessions/components/list/list.component.ts](src/app/pages/sessions/components/list/list.component.ts)

- Observation : le getter `user` dépend d’un état de session non protégé.
- Correction à apporter :
  - sécuriser l’accès à `sessionInformation` ;
  - garder le typage coherent sur l’état utilisateur.

```ts
get user(): SessionInformation | undefined {
  return this.sessionService.sessionInformation ?? undefined;
}
```

### 18) [src/app/pages/sessions/components/list/list.component.html](src/app/pages/sessions/components/list/list.component.html)

- Observation : présence de `*ngIf` et `*ngFor`.
- Correction à apporter :
  - utiliser `@if` et `@for` avec `track` ;
  - rendre le template conforme aux bonnes pratiques Angular récentes.

```html
<mat-card>
  <mat-card-header fxLayout="row" fxLayoutAlign="space-between center">
    <mat-card-title class="m0">Rentals available</mat-card-title>

    @if (user?.admin) {
      <button mat-raised-button color="primary" routerLink="create">
        <mat-icon>add</mat-icon>
        <span class="ml1">Create</span>
      </button>
    }
  </mat-card-header>

  <div class="items mt2" fxLayout="row wrap" fxLayout.lt-md="column">
    @for (session of sessions$ | async; track session.id) {
      <mat-card class="item" fxFlex>
        <mat-card-title>{{ session.name }}</mat-card-title>
      </mat-card>
    }
  </div>
</mat-card>
```

---

## Points de correction globaux à appliquer sur l’ensemble du projet

### 1. Les `any`

- Remplacer tous les `any` par des types concrets.
- Utiliser les interfaces du dossier [src/app/core/models](src/app/core/models).

### 2. Les souscriptions observables

- `HttpClient` se désabonne automatiquement ; ne pas forcer `unsubscribe()` sur les appels HTTP.
- Utiliser `takeUntil`, `first`, `take(1)` ou `DestroyRef` uniquement pour les observables créés localement et non gérés par Angular.

### 3. La syntaxe Angular moderne

- Migrer vers `@if` et `@for`.

```bash
ng generate @angular/core:control-flow
```

### 4. Le typage

- Ajouter des retours explicites : `void`, `Observable<T>`, `boolean`, `string`, etc.
- Supprimer les accès non sécurisés à `sessionInformation`.

### 5. Les états UI

- Ajouter `loading` ou `submitting` sur les formulaires.
- Rendre les messages d’erreur plus explicites.

---

## Priorité des corrections

### Priorité haute

1. Supprimer tous les `any`
2. Sécuriser les accès à `sessionInformation`
3. Migrer vers `@if` et `@for`
4. Typage strict des méthodes et propriétés
5. Nettoyer les flux observables et les souscriptions

### Priorité moyenne

6. Ajouter les états de chargement et d’erreur
7. Harmoniser les services HTTP
8. Clarifier la séparation des responsabilités

### Priorité basse

9. Nettoyer les imports et le style
10. Réduire la duplication de code

---

## Conclusion

Le front Angular est fonctionnel, mais il reste des corrections importantes sur le typage, la sécurité des états, la gestion des observables et la modernisation des templates. Les fichiers les plus impactés sont notamment les composants de session, les services HTTP et les vues conditionnelles.

Les bonnes pratiques à respecter sont simples et claires :

- `HttpClient` se désabonne seul ;
- les observables créés manuellement demandent une gestion explicite ;
- les templates doivent être migrés vers `@if` / `@for` ;
- les `any` doivent disparaître.

### Recommandations

- remplacer `Observable<any>` par `Observable<void>`, `Observable<unknown>` ou un type métier exact ;
- remonter les types métier déjà définis dans le dossier `src/app/core/models` ;
- éviter les callbacks anonymes avec type implicite faible ;
- utiliser une signature explicite dans les callbacks HTTP :

```ts
.subscribe((response: Session) => {
  // logique...
});
```

### À traiter

- `delete(id: string): Observable<any>` dans le service de sessions ;
- `subscribe((_: any) => ...)` dans le composant de détail ;
- tous autres cas de `any` détectés par le projet.

---

## 3. Typage strict des méthodes et variables

### Problème

Le code contient des méthodes non typées ou des propriétés typées de façon imprécise. Cela réduit la qualité de l’éditeur et la robustesse du code.

### Fichiers concernés

- [src/app/app.component.ts](src/app/app.component.ts)
- [src/app/components/me/me.component.ts](src/app/components/me/me.component.ts)
- [src/app/pages/sessions/components/detail/detail.component.ts](src/app/pages/sessions/components/detail/detail.component.ts)
- [src/app/pages/sessions/components/form/form.component.ts](src/app/pages/sessions/components/form/form.component.ts)
- [src/app/pages/login/login.component.ts](src/app/pages/login/login.component.ts)
- [src/app/pages/register/register.component.ts](src/app/pages/register/register.component.ts)

### Recommandations

- typer les méthodes renvoyant une valeur : `Observable<boolean>`, `void`, `string`, etc. ;
- typer les variables d’état comme `Session | undefined`, `User | null`, `FormGroup | null` selon le contexte ;
- éviter d’utiliser `!` sans justification claire ;
- ne pas laisser des méthodes sans retour explicite lorsque le code peut clairement être typé.

### Bonnes pratiques

```ts
public back(): void {
  window.history.back();
}

private fetchSession(): void {
  // logique
}
```

---

## 4. Supprimer les directives structurelles obsolètes

### Problème

Le frontend utilise encore les anciennes directives `*ngIf` et `*ngFor`, qui sont considérées comme legacy par rapport aux nouvelles pratiques Angular.

### Fichiers concernés

- [src/app/app.component.html](src/app/app.component.html)
- [src/app/components/me/me.component.html](src/app/components/me/me.component.html)
- [src/app/pages/login/login.component.html](src/app/pages/login/login.component.html)
- [src/app/pages/register/register.component.html](src/app/pages/register/register.component.html)
- [src/app/pages/sessions/components/detail/detail.component.html](src/app/pages/sessions/components/detail/detail.component.html)
- [src/app/pages/sessions/components/form/form.component.html](src/app/pages/sessions/components/form/form.component.html)
- [src/app/pages/sessions/components/list/list.component.html](src/app/pages/sessions/components/list/list.component.html)

### Recommandations

Remplacer :

- `*ngIf` par `@if`
- `*ngFor` par `@for`
- `ng-template` par les blocs Angular modernes `@else` et `@else if`

### Exemple

Ancien code :

```html
<div *ngIf="user">
  <p *ngIf="user.admin">You are admin</p>
</div>
```

Version moderne :

```html
@if (user) {
  @if (user.admin) {
    <p>You are admin</p>
  }
}
```

Et pour la liste :

```html
@for (session of sessions$ | async; track session.id) {
  <mat-card>
    {{ session.name }}
  </mat-card>
}
```

---

## 5. Sécuriser l’accès aux données utilisateur/session

### Problème

Le code utilise souvent `sessionInformation!`, ce qui suppose que l’objet existe. Cela est risqué lors d’un état non initialisé ou d’un accès avant connexion.

### Fichiers concernés

- [src/app/components/me/me.component.ts](src/app/components/me/me.component.ts)
- [src/app/pages/sessions/components/detail/detail.component.ts](src/app/pages/sessions/components/detail/detail.component.ts)
- [src/app/pages/sessions/components/form/form.component.ts](src/app/pages/sessions/components/form/form.component.ts)
- [src/app/pages/sessions/components/list/list.component.ts](src/app/pages/sessions/components/list/list.component.ts)

### Recommandations

- vérifier explicitement la présence de `sessionInformation` avant usage ;
- gérer les cas `undefined` dans les composants ;
- centraliser la logique utilisateur dans un service dédié ;
- éviter les assertions non null (`!`) systématiques ;
- protéger les vues qui dépendent d’un accès utilisateur authentifié.

### Exemple de garde

```ts
if (!this.sessionService.sessionInformation) {
  this.router.navigate(['/login']);
  return;
}
```

---

## 6. Améliorer la gestion des erreurs et des états UI

### Problème

Les formulaires et les appels HTTP gèrent peu explicitement les cas d’erreur ou d’état de chargement.

### Fichiers concernés

- [src/app/pages/login/login.component.ts](src/app/pages/login/login.component.ts)
- [src/app/pages/register/register.component.ts](src/app/pages/register/register.component.ts)
- [src/app/pages/sessions/components/form/form.component.ts](src/app/pages/sessions/components/form/form.component.ts)

### Recommandations

- créer des états de chargement pour les formulaires et les boutons de soumission ;
- ajouter des messages d’erreur plus explicites ;
- gérer les erreurs HTTP avec des messages utilisateur compréhensibles ;
- empêcher les soumissions multiples pendant le chargement.

### À faire

- `isSubmitting` sur les boutons de soumission ;
- `isLoading` sur les pages de chargement de données ;
- message d’erreur plus clair en cas d’échec login/register/session.

---

## 7. Améliorer les services HTTP

### Fichiers concernés

- [src/app/core/service/session-api.service.ts](src/app/core/service/session-api.service.ts)
- [src/app/core/service/user.service.ts](src/app/core/service/user.service.ts)
- [src/app/core/service/teacher.service.ts](src/app/core/service/teacher.service.ts)
- [src/app/core/service/auth.service.ts](src/app/core/service/auth.service.ts)

### Recommandations

- homogénéiser les signatures de méthodes ;
- typer précisément les réponses HTTP ;
- éviter les `any` dans les retours de méthodes HTTP ;
- renommer les méthodes si besoin pour plus de cohérence (`getAll`, `getById`, `deleteById`, etc.) ;
- centraliser la logique de gestion des erreurs côté service.

### Exemple attendu

```ts
public delete(id: string): Observable<void> {
  return this.httpClient.delete<void>(`${this.pathService}/${id}`);
}
```

---

## 8. Améliorer la clarté et la maintenabilité des composants

### Problème

Certains composants font trop de choses en même temps : récupération de données, logique métier, navigation, gestion d’erreurs, affichage de message.

### Fichiers concernés

- [src/app/pages/sessions/components/detail/detail.component.ts](src/app/pages/sessions/components/detail/detail.component.ts)
- [src/app/pages/sessions/components/form/form.component.ts](src/app/pages/sessions/components/form/form.component.ts)
- [src/app/components/me/me.component.ts](src/app/components/me/me.component.ts)

### Recommandations

- séparer la logique de récupération des données et la logique d’interaction utilisateur ;
- organiser les méthodes par rôle (fetch, delete, submit, exitPage) ;
- limiter la charge du composant ;
- garder les composants concentrés sur l’affichage et l’interaction directe.

---

## 9. Nettoyer les imports et les conventions de style

### Problèmes détectés

- imports non triés ou peu cohérents ;
- mélange de guillemets simples et doubles ;
- formats de code peu homogènes selon les fichiers.

### Fichiers concernés

- plusieurs composants et services dans [src/app](src/app)

### Recommandations

- standardiser les imports Angular/TypeScript ;
- utiliser des guillemets cohérents selon la configuration du projet ;
- formater le code de façon uniforme ;
- suivre les règles ESLint/Prettier du projet.

---

## 10. Réduction de la duplication logique

### Problème

Les composants utilisent des comportements répétitifs : navigation, snack bar, vérification de droits d’admin, récupération de données utilisateur.

### Recommandations

- centraliser les logique de navigation et de messages utilisateur ;
- créer des helpers ou services utilitaires si nécessaire ;
- réduire la répétition dans les composants et les templates.

---

## Priorisation des actions

### Priorité haute

1. Supprimer tous les `any`
2. Gérer les désabonnements RxJS
3. Typage strict des méthodes et variables
4. Remplacer `*ngIf` / `*ngFor` par `@if` / `@for`
5. Sécuriser `sessionInformation!`

### Priorité moyenne

6. Ajouter des états de chargement et d’erreur
7. Améliorer les services HTTP et leurs signatures
8. Clarifier la séparation des responsabilités

### Priorité basse

9. Nettoyer imports et style
10. Réduire la duplication et homogénéiser les conventions

---

## Conclusion

Le front Angular est fonctionnel mais il n’est pas encore totalement conforme aux bonnes pratiques modernes. Les améliorations les plus importantes concernent :

- la sécurité TypeScript ;
- la gestion des flux RxJS ;
- la modernisation des templates Angular ;
- la robustesse des composants et des services.

C’est sur ces points qu’il faut travailler en priorité pour obtenir une application plus lisible, plus fiable et plus simple à maintenir.
