# Guide Complet des Fonctionnalités Admin - SGLE Backend

## Vue d'ensemble

Le système de gestion des logements étudiants (SGLE) expose une API REST complète pour l'administration. Toutes les routes admin sont protégées et nécessitent le rôle `ADMIN`.

**Base URL**: `/api/admin`  
**Port**: `8081`  
**Authentification**: JWT Bearer Token avec rôle ADMIN

---

## Table des matières

1. [Dashboard](#1-dashboard)
2. [Gestion des Utilisateurs](#2-gestion-des-utilisateurs)
3. [Gestion des Logements](#3-gestion-des-logements)
4. [Gestion des Attributions](#4-gestion-des-attributions)
5. [Gestion des Paiements](#5-gestion-des-paiements)
6. [Gestion des Rapports](#6-gestion-des-rapports)
7. [Processus Métier](#7-processus-métier)
8. [Données de Test](#8-données-de-test)

---

## 1. Dashboard (`/api/admin/dashboard`)

Tableau de bord central pour la supervision du système.

### Endpoints

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/statistiques` | GET | Statistiques globales du système |
| `/metriques` | GET | Métriques de performance |
| `/alertes` | GET | Alertes actives (retards, incidents critiques) |
| `/resume` | GET | Résumé complet (stats + métriques + alertes) |

### Comment ça se déroule

1. **Accès au Dashboard**
   - L'admin se connecte avec ses identifiants
   - Il accède au tableau de bord via `/api/admin/dashboard/resume`
   - Le système agrège toutes les données en temps réel

2. **Consultation des Statistiques**
   ```
   GET /api/admin/dashboard/statistiques
   → Retourne: logements, utilisateurs, demandes, attributions, finances, incidents
   ```

3. **Surveillance des Alertes**
   - Le système détecte automatiquement:
     - Paiements en retard > 30 jours
     - Incidents de niveau CRITIQUE non résolus
     - Attributions expirant dans 30 jours
     - Logements en maintenance depuis > 7 jours

### Détails des statistiques retournées
- **Logements**: Total, disponibles, occupés, en maintenance, taux d'occupation
- **Utilisateurs**: Total par rôle, actifs, nouveaux ce mois
- **Demandes**: Total, en attente, approuvées, rejetées, taux d'approbation
- **Attributions**: Actives, expirant bientôt, terminées ce mois
- **Financier**: Revenus mois/année, impayés, taux de recouvrement
- **Incidents**: Ouverts, critiques, temps moyen de résolution

---

## 2. Gestion des Utilisateurs (`/api/admin/utilisateurs`)

Gestion complète de tous les comptes utilisateurs du système.

### Endpoints

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/` | POST | Créer un utilisateur |
| `/` | GET | Lister tous les utilisateurs |
| `/{id}` | GET | Obtenir un utilisateur par ID |
| `/{id}` | PUT | Modifier un utilisateur |
| `/{id}` | DELETE | Supprimer un utilisateur |
| `/{id}/role` | PATCH | Changer le rôle |
| `/{id}/activer` | PATCH | Activer le compte |
| `/{id}/desactiver` | PATCH | Désactiver le compte |
| `/{id}/reset-password` | PATCH | Réinitialiser le mot de passe |
| `/statistiques` | GET | Statistiques des utilisateurs |

### Rôles disponibles

| Rôle | Description | Permissions |
|------|-------------|-------------|
| `ADMIN` | Administrateur système | Accès total |
| `GESTIONNAIRE` | Gestionnaire de logements | Gestion logements, attributions, incidents |
| `ETUDIANT` | Étudiant | Consultation, demandes, paiements |
| `INVITE` | Invité | Consultation limitée |

### Comment ça se déroule

#### Création d'un utilisateur
```
1. POST /api/admin/utilisateurs
   Body: { email, motDePasse, role }
   
2. Le système:
   - Vérifie que l'email n'existe pas déjà
   - Hash le mot de passe (BCrypt)
   - Crée le compte avec statut actif=true
   
3. Réponse: Utilisateur créé avec son ID
```

#### Changement de rôle
```
1. PATCH /api/admin/utilisateurs/{id}/role?nouveauRole=GESTIONNAIRE

2. Le système:
   - Vérifie que l'utilisateur existe
   - Met à jour le rôle
   - Les nouvelles permissions sont effectives immédiatement

3. Réponse: Utilisateur mis à jour
```

#### Désactivation d'un compte
```
1. PATCH /api/admin/utilisateurs/{id}/desactiver

2. Le système:
   - Met actif=false
   - L'utilisateur ne peut plus se connecter
   - Ses données sont conservées

3. Pour réactiver: PATCH /api/admin/utilisateurs/{id}/activer
```

---

## 3. Gestion des Logements (`/api/admin/logements`)

Gestion du parc immobilier étudiant.

### Endpoints

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/` | POST | Créer un logement |
| `/` | GET | Lister tous les logements |
| `/{id}` | GET | Obtenir par ID |
| `/code/{code}` | GET | Obtenir par code unique |
| `/{id}` | PUT | Modifier un logement |
| `/{id}` | DELETE | Supprimer (si pas d'attributions) |
| `/{id}/archiver` | PATCH | Archiver un logement |
| `/{id}/maintenance` | POST | Planifier une maintenance |
| `/statistiques` | GET | Statistiques des logements |

### Types de logement

| Type | Description | Capacité typique |
|------|-------------|------------------|
| `CHAMBRE_UNIVERSITAIRE` | Chambre en cité U | 1 personne |
| `STUDIO` | Studio individuel | 1 personne |
| `T1` | Appartement 1 pièce | 1-2 personnes |
| `T2` | Appartement 2 pièces | 2-3 personnes |
| `T3` | Appartement 3 pièces | 3-4 personnes |
| `RESIDENCE_ETUDIANTE` | Chambre en résidence | 1 personne |
| `APPARTEMENT_PARTAGE` | Colocation | Variable |

### Statuts de logement

| Statut | Description | Transition possible vers |
|--------|-------------|-------------------------|
| `DISPONIBLE` | Prêt à être attribué | OCCUPE, RESERVE, EN_MAINTENANCE |
| `OCCUPE` | Actuellement habité | DISPONIBLE (fin attribution) |
| `RESERVE` | Réservé pour attribution | OCCUPE, DISPONIBLE |
| `EN_MAINTENANCE` | Travaux en cours | DISPONIBLE, HORS_SERVICE |
| `HORS_SERVICE` | Indisponible | EN_MAINTENANCE, ARCHIVE |
| `ARCHIVE` | Retiré du parc | - |

### Comment ça se déroule

#### Création d'un logement
```
1. POST /api/admin/logements
   Body: {
     type: "STUDIO",
     adresse: { rue, numero, ville, quartier, codePostal },
     capacite: 1,
     superficie: 25,
     prixMensuel: 75000,
     meuble: true,
     description: "Studio meublé proche campus"
   }

2. Le système:
   - Génère automatiquement un code unique (ex: ST-001)
   - Initialise le statut à DISPONIBLE
   - Assigne un gestionnaire si fourni

3. Réponse: Logement créé avec ID et code
```

#### Mise en maintenance
```
1. POST /api/admin/logements/{id}/maintenance
   Body: { motif: "Réparation plomberie", dureeEstimee: 3 }

2. Le système:
   - Change le statut à EN_MAINTENANCE
   - Crée un enregistrement de maintenance
   - Si occupé: notifie l'étudiant

3. Réponse: Maintenance planifiée
```

#### Archivage d'un logement
```
1. PATCH /api/admin/logements/{id}/archiver

2. Le système vérifie:
   - Pas d'attribution active
   - Pas de demandes en attente pour ce logement
   
3. Si OK:
   - Change le statut à ARCHIVE
   - Le logement n'apparaît plus dans les recherches

4. Réponse: Logement archivé
```

---

## 4. Gestion des Attributions (`/api/admin/attributions`)

Gestion des contrats de location entre étudiants et logements.

### Endpoints

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/` | POST | Créer une attribution |
| `/` | GET | Lister les attributions |
| `/{id}` | GET | Obtenir par ID |
| `/contrat/{numero}` | GET | Obtenir par numéro de contrat |
| `/{id}` | PUT | Modifier une attribution |
| `/{id}/revoquer` | PATCH | Révoquer (avec motif) |
| `/{id}/terminer` | PATCH | Terminer normalement |
| `/{id}/prolonger` | PATCH | Prolonger la durée |
| `/{id}/checkin` | PATCH | Enregistrer l'entrée |
| `/{id}/checkout` | PATCH | Enregistrer la sortie |
| `/expirant` | GET | Attributions expirant bientôt |
| `/statistiques` | GET | Statistiques des attributions |

### Statuts d'attribution

| Statut | Description |
|--------|-------------|
| `ACTIVE` | Contrat en cours |
| `EXPIREE` | Fin normale du contrat |
| `RESILIEE` | Contrat résilié avant terme |
| `SUSPENDUE` | Contrat temporairement suspendu |
| `EN_COURS_RENOUVELLEMENT` | Renouvellement en traitement |

### Comment ça se déroule

#### Processus complet d'attribution

```
┌─────────────────────────────────────────────────────────────────┐
│                    PROCESSUS D'ATTRIBUTION                       │
└─────────────────────────────────────────────────────────────────┘

Étape 1: DEMANDE DE L'ÉTUDIANT
─────────────────────────────
L'étudiant soumet une demande via /api/demandes
  → Type de logement souhaité
  → Budget maximum
  → Date de début souhaitée
  → Préférences (quartier, meublé, etc.)

Étape 2: TRAITEMENT DE LA DEMANDE
─────────────────────────────────
Le gestionnaire/admin examine la demande
  → Vérifie l'éligibilité de l'étudiant
  → Recherche les logements correspondants
  → Peut: APPROUVER ou REJETER

Étape 3: CRÉATION DE L'ATTRIBUTION
──────────────────────────────────
POST /api/admin/attributions
{
  "etudiantId": "uuid-etudiant",
  "logementId": "uuid-logement",
  "dateDebut": "2026-03-01",
  "dateFin": "2027-02-28",
  "montantLoyer": 75000,
  "montantCaution": 150000
}

Le système:
  ✓ Génère un numéro de contrat (CONT-2026-XXX)
  ✓ Vérifie la disponibilité du logement
  ✓ Vérifie que l'étudiant n'a pas déjà une attribution active
  ✓ Change le statut du logement à OCCUPE
  ✓ Lie la demande à l'attribution

Étape 4: CHECK-IN
─────────────────
PATCH /api/admin/attributions/{id}/checkin
  → Enregistre la date d'entrée
  → Génère l'état des lieux d'entrée
  → Active les paiements mensuels

Étape 5: VIE DU CONTRAT
───────────────────────
Pendant la durée du contrat:
  → Paiements mensuels suivis
  → Incidents signalés et traités
  → Possibilité de prolonger si besoin

Étape 6: FIN DU CONTRAT
───────────────────────
Option A - Fin normale:
  PATCH /api/admin/attributions/{id}/terminer
  → Enregistre la date de fin
  → Déclenche le checkout

Option B - Résiliation:
  PATCH /api/admin/attributions/{id}/revoquer
  Body: { motif: "Non-paiement répété" }
  → Résilie le contrat immédiatement

Étape 7: CHECK-OUT
──────────────────
PATCH /api/admin/attributions/{id}/checkout
  → Enregistre la date de sortie
  → Génère l'état des lieux de sortie
  → Calcule le solde final (caution - dommages)
  → Libère le logement (statut → DISPONIBLE)
```

#### Prolongation d'une attribution
```
1. PATCH /api/admin/attributions/{id}/prolonger
   Query: nouvelleDateFin=2027-08-31

2. Le système:
   - Vérifie que le contrat est ACTIVE
   - Vérifie que la nouvelle date > ancienne date
   - Met à jour la date de fin
   - Génère les nouveaux paiements mensuels

3. Réponse: Attribution prolongée
```

---

## 5. Gestion des Paiements (`/api/admin/paiements`)

Suivi financier complet des loyers.

### Endpoints

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/` | POST | Enregistrer un paiement |
| `/` | GET | Lister tous les paiements |
| `/{id}` | GET | Obtenir par ID |
| `/numero/{numero}` | GET | Obtenir par numéro |
| `/{id}/payer` | PATCH | Marquer comme payé |
| `/{id}/annuler` | PATCH | Annuler un paiement |
| `/{id}/rembourser` | PATCH | Rembourser |
| `/attribution/{id}` | GET | Paiements d'une attribution |
| `/attribution/{id}/generer` | POST | Générer paiements mensuels |
| `/attribution/{id}/solde` | GET | Calculer le solde |
| `/en-retard` | GET | Paiements en retard |
| `/rappels` | POST | Envoyer des rappels |
| `/statistiques` | GET | Statistiques financières |

### Statuts de paiement

| Statut | Description |
|--------|-------------|
| `EN_ATTENTE` | Paiement attendu |
| `PAYE` | Paiement effectué |
| `EN_RETARD` | Échéance dépassée |
| `ANNULE` | Paiement annulé |
| `REMBOURSE` | Paiement remboursé |

### Modes de paiement

- `ESPECES` - Paiement en espèces
- `VIREMENT_BANCAIRE` - Virement bancaire
- `CARTE_BANCAIRE` - Carte bancaire
- `CHEQUE` - Paiement par chèque
- `MOBILE_MONEY` - Orange Money, Wave, etc.
- `AUTRE` - Autre mode

### Comment ça se déroule

#### Cycle de vie d'un paiement

```
┌─────────────────────────────────────────────────────────────────┐
│                    CYCLE DE VIE D'UN PAIEMENT                    │
└─────────────────────────────────────────────────────────────────┘

1. GÉNÉRATION AUTOMATIQUE
─────────────────────────
Lors de la création d'une attribution, le système peut générer
automatiquement les échéances:

POST /api/admin/paiements/attribution/{id}/generer?nombreMois=12

Résultat: 12 paiements créés avec:
  - Statut: EN_ATTENTE
  - Date d'échéance: 5 de chaque mois
  - Montant: loyer mensuel

2. SUIVI DES ÉCHÉANCES
──────────────────────
Chaque mois:
  - Le système vérifie les paiements à échéance
  - Si non payé après la date: passage à EN_RETARD

3. ENREGISTREMENT DU PAIEMENT
─────────────────────────────
Quand l'étudiant paie:

PATCH /api/admin/paiements/{id}/payer
Query: modePaiement=MOBILE_MONEY&reference=WAV-123456

Le système:
  ✓ Change le statut à PAYE
  ✓ Enregistre la date de paiement
  ✓ Sauvegarde la référence bancaire
  ✓ Peut générer un reçu automatique

4. GESTION DES RETARDS
──────────────────────
GET /api/admin/paiements/en-retard
  → Liste tous les paiements en retard

POST /api/admin/paiements/rappels
  → Envoie des notifications aux étudiants concernés

5. CALCUL DU SOLDE
──────────────────
GET /api/admin/paiements/attribution/{id}/solde

Retourne:
{
  "totalDu": 900000,
  "totalPaye": 675000,
  "soldeRestant": 225000,
  "nombreImpayés": 3,
  "penalites": 15000
}
```

#### Cas particuliers

**Annulation d'un paiement:**
```
PATCH /api/admin/paiements/{id}/annuler
Query: motif=Erreur de saisie

→ Statut passe à ANNULE
→ Un nouveau paiement peut être créé
```

**Remboursement:**
```
PATCH /api/admin/paiements/{id}/rembourser
Query: motif=Départ anticipé

→ Statut passe à REMBOURSE
→ Montant déduit des revenus
```

---

## 6. Gestion des Rapports (`/api/admin/rapports`)

Génération de rapports et analyses.

### Endpoints

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/` | POST | Générer un rapport complet |
| `/` | GET | Lister les rapports |
| `/{id}` | GET | Obtenir un rapport |
| `/{id}` | DELETE | Supprimer un rapport |
| `/export` | GET | Exporter des données |
| `/occupation` | GET | Rapport d'occupation rapide |
| `/financier` | GET | Rapport financier rapide |
| `/demandes` | GET | Rapport des demandes |
| `/performance` | GET | Rapport de performance |

### Types de rapport

| Type | Contenu |
|------|---------|
| `OCCUPATION` | Taux d'occupation par type, période, quartier |
| `FINANCIER` | Revenus, impayés, prévisions |
| `DEMANDES` | Analyse des demandes, délais de traitement |
| `ETUDIANTS` | Profil des étudiants, répartition |
| `INCIDENTS` | Incidents par type, temps de résolution |
| `MAINTENANCE` | Coûts, fréquence, logements concernés |
| `PERFORMANCE` | KPIs globaux du système |

### Formats d'export

- `PDF` - Rapport formaté avec graphiques
- `CSV` - Données brutes exportables
- `EXCEL` - Fichier Excel avec onglets
- `JSON` - Données structurées pour intégration

### Comment ça se déroule

```
1. GÉNÉRATION D'UN RAPPORT
──────────────────────────
POST /api/admin/rapports
Body: {
  "type": "FINANCIER",
  "dateDebut": "2026-01-01",
  "dateFin": "2026-01-31",
  "format": "PDF"
}

Le système:
  → Collecte les données de la période
  → Calcule les métriques
  → Génère le fichier au format demandé
  → Sauvegarde le rapport

2. RAPPORTS RAPIDES
───────────────────
Pour un aperçu instantané:

GET /api/admin/rapports/occupation
→ Retourne le taux d'occupation actuel

GET /api/admin/rapports/financier
→ Retourne un résumé financier du mois

3. EXPORT DE DONNÉES
────────────────────
GET /api/admin/rapports/export?type=PAIEMENTS&format=CSV&dateDebut=2026-01-01

→ Télécharge un fichier CSV des paiements
```

---

## 7. Processus Métier

### A. Processus d'inscription d'un étudiant

```
┌─────────────────────────────────────────────────────────────────┐
│              INSCRIPTION D'UN NOUVEL ÉTUDIANT                    │
└─────────────────────────────────────────────────────────────────┘

1. Création du compte
   POST /api/auth/register
   → Email + mot de passe
   → Rôle automatique: ETUDIANT
   → Compte actif par défaut

2. Première connexion
   POST /api/auth/login
   → Obtention du token JWT

3. L'étudiant peut ensuite:
   → Consulter les logements disponibles
   → Soumettre une demande de logement
   → Suivre sa demande
```

### B. Processus de traitement d'une demande

```
┌─────────────────────────────────────────────────────────────────┐
│              TRAITEMENT COMPLET D'UNE DEMANDE                    │
└─────────────────────────────────────────────────────────────────┘

ÉTUDIANT                        ADMIN/GESTIONNAIRE
────────                        ──────────────────
    │                                   │
    │  1. Soumet demande               │
    │──────────────────────────────────>│
    │                                   │
    │                           2. Examine la demande
    │                              - Vérifie éligibilité
    │                              - Recherche logement
    │                                   │
    │  3. Notification                  │
    │<──────────────────────────────────│
    │     (Approuvé/Rejeté)             │
    │                                   │
    │                           4. Si approuvé:
    │                              Crée l'attribution
    │                                   │
    │  5. Reçoit le contrat            │
    │<──────────────────────────────────│
    │                                   │
    │  6. Signe et paie caution        │
    │──────────────────────────────────>│
    │                                   │
    │                           7. Enregistre check-in
    │                                   │
    │  8. Emménage                      │
    │<──────────────────────────────────│
```

### C. Processus de gestion des incidents

```
┌─────────────────────────────────────────────────────────────────┐
│                  GESTION D'UN INCIDENT                           │
└─────────────────────────────────────────────────────────────────┘

1. SIGNALEMENT
   L'étudiant signale un incident (fuite d'eau, panne...)
   → Type, description, urgence, photos

2. RÉCEPTION
   Le gestionnaire reçoit l'alerte
   → Incident créé avec statut OUVERT

3. ASSIGNATION
   Le gestionnaire assigne un technicien
   → Statut passe à EN_COURS

4. INTERVENTION
   Le technicien intervient et résout le problème

5. CLÔTURE
   Le gestionnaire clôture l'incident
   → Ajoute un commentaire de résolution
   → Statut passe à RESOLU puis FERME

6. SUIVI
   Si critique: alerte sur le dashboard admin
   Temps de résolution enregistré pour statistiques
```

---

## 8. Données de Test

### Comptes disponibles

| Type | Email | Mot de passe |
|------|-------|--------------|
| Admin | `admin@sgle.sn` | `Admin@123` |
| Admin | `superadmin@sgle.sn` | `Admin@123` |
| Gestionnaire | `gestionnaire1@sgle.sn` | `Gestionnaire@123` |
| Gestionnaire | `gestionnaire2@sgle.sn` | `Gestionnaire@123` |
| Gestionnaire | `moussa.diop@sgle.sn` | `Gestionnaire@123` |
| Étudiant | `aminata.fall@ucad.edu.sn` | `Etudiant@123` |
| Étudiant | `ousmane.ndiaye@ucad.edu.sn` | `Etudiant@123` |
| Étudiant | `fatou.diallo@ucad.edu.sn` | `Etudiant@123` |
| Invité | `invite@sgle.sn` | `Invite@123` |

### Volumes de données de test

| Entité | Quantité |
|--------|----------|
| Utilisateurs | 14 |
| Logements | 13 |
| Demandes | 7 |
| Attributions | 5 |
| Paiements | 25 |
| Incidents | 6 |

### Logements de test

| Code | Type | Quartier | Prix | Statut |
|------|------|----------|------|--------|
| CU-001 | Chambre Universitaire | Point E | 25 000 FCFA | Disponible |
| CU-002 | Chambre Universitaire | Point E | 25 000 FCFA | Occupé |
| ST-001 | Studio | Fann | 75 000 FCFA | Disponible |
| ST-002 | Studio | Fann | 80 000 FCFA | Occupé |
| T1-001 | T1 | Sicap Liberté | 120 000 FCFA | Disponible |
| T2-001 | T2 | Plateau | 180 000 FCFA | Disponible |
| RE-001 | Résidence Étudiante | UCAD | 35 000 FCFA | Disponible |

---

## Authentification

### Connexion

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@sgle.sn",
  "motDePasse": "Admin@123"
}
```

### Réponse

```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "type": "Bearer",
    "email": "admin@sgle.sn",
    "role": "ADMIN"
  }
}
```

### Utilisation du Token

```http
GET /api/admin/dashboard/statistiques
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

---

## Format des Réponses

### Succès

```json
{
  "success": true,
  "message": "Opération réussie",
  "data": { ... },
  "timestamp": "2026-02-06T12:00:00"
}
```

### Erreur

```json
{
  "success": false,
  "message": "Description de l'erreur",
  "errors": ["Détail 1", "Détail 2"],
  "timestamp": "2026-02-06T12:00:00"
}
```

### Codes HTTP

| Code | Description |
|------|-------------|
| 200 | Succès |
| 201 | Ressource créée |
| 400 | Données invalides |
| 401 | Non authentifié |
| 403 | Accès refusé |
| 404 | Non trouvé |
| 409 | Conflit |
| 500 | Erreur serveur |

---

## Configuration Technique

| Paramètre | Valeur |
|-----------|--------|
| Port | 8081 |
| Base URL | http://localhost:8081 |
| Base de données | H2 (dev) / PostgreSQL (prod) |
| Console H2 | http://localhost:8081/h2-console |
| Swagger UI | http://localhost:8081/swagger-ui.html |
| API Docs | http://localhost:8081/v3/api-docs |

---

*Documentation mise à jour le 6 février 2026*
