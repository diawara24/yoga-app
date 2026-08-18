# Note d’architecture – backend Yoga App

## 1. Problèmes identifiés

### Gestion des exceptions
- Les controllers utilisent des blocs `try { ... } catch { ... }` pour gérer des exceptions comme `NumberFormatException`.
- Cette logique est dupliquée dans plusieurs classes.
- Le code est plus difficile à maintenir, car la gestion des erreurs n’est pas centralisée.
- La gestion des exceptions doit être externalisée pour éviter de répéter la même logique dans chaque controller.

La solution recommandée est d’utiliser un `@ControllerAdvice` (ou une autre stratégie équivalente proposée par Spring Boot), afin de centraliser la gestion des exceptions HTTP et de réduire la duplication.

### Découpage des couches
Le projet doit respecter l’architecture suivante :
- controller → service → repository

Le problème actuel est que certains controllers manipulent directement des repositories ou réalisent eux-mêmes des validations métier. Cela va à l’encontre des bonnes pratiques.

Un controller doit :
- recevoir la requête HTTP,
- déléguer le traitement métier au service,
- appeler le service qui fait appel au repository si nécessaire,
- retourner une réponse adaptée.

### Logique métier dans les controllers
Plusieurs traitements métier se retrouvent dans les controllers, par exemple :
- vérification de l’existence d’un utilisateur,
- recherche d’une session puis suppression,
- validation de participation à une session,
- contrôle d’authentification sur la suppression d’un utilisateur.

Ces vérifications doivent être transférées dans les services métier, où elles sont plus naturellement gérées.

---

## 2. Recommandations d’architecture

### 2.1 Centraliser les exceptions
Créer une classe de type `@ControllerAdvice` qui gère les exceptions globalement, par exemple :
- `NotFoundException` → HTTP 404
- `BadRequestException` → HTTP 400
- `UnauthorizedException` → HTTP 401
- `NumberFormatException` → HTTP 400
- ...

Cela permet de supprimer les `try/catch` redondants présents dans les controllers.

### 2.2 Respecter le bon découpage
Le bon flux est :
- Controller : réception de la requête, validation technique simple, appel du service
- Service : logique métier, validations métier, orchestration
- Repository : accès aux données

### 2.3 Déplacer la logique métier dans les services
Les services doivent contenir les règles suivantes :
- recherche d’un élément par identifiant,
- vérification de son existence,
- gestion des cas de mauvaise demande,
- gestion des cas de non autorisation,
- gestion de la suppression/ajout de relations métiers.

Exemples de traitements à déplacer :
- vérification si un utilisateur existe avant suppression,
- vérification si une session existe avant suppression,
- contrôle si un utilisateur participe déjà à une séance,
- contrôle si un utilisateur n’est pas autorisé à supprimer son compte.

---

## 3. Exemple de bon découpage attendu

### Controller
Le controller ne doit pas faire de logique métier lourde. Il se contente d’appeler un service, par exemple :
- `sessionController.getById(id)`
- `sessionService.getById(id)`

### Service
Le service assure la logique métier :
- vérifier que la session existe,
- vérifier que l’utilisateur existe,
- vérifier que la relation de participation est correcte,
- lancer des exceptions métier adaptées.

### Repository
Le repository ne contient que des accès CRUD / requêtes JPA.

---

## 4. Contrôleurs à réviser
Les classes concernées par le mauvais découpage et la duplication de logique sont principalement :
- `SessionController`
- `UserController`
- `TeacherController`
- `AuthController`

Les règles métier qu’ils contiennent ou délèguent doivent être déplacées vers les services correspondants.

---

## 5. Conclusion
Le backend est globalement structuré comme un projet Spring Boot classique, mais il présente un défaut architectural important : la logique métier n’est pas complètement centralisée dans les services. La correction principale consiste à :

1. mettre en place un `@ControllerAdvice` pour les exceptions,
2. supprimer les `try/catch` de duplication dans les controllers,
3. déléguer la logique métier vers les services,
4. laisser les repositories uniquement pour l’accès aux données.

Cela améliore la maintenabilité, la lisibilité et la testabilité du code.
