# Récapitulatif des Fonctionnalités Admin - SGLE Backend

## Vue d'ensemble

Le système de gestion des logements étudiants (SGLE) expose une API REST complète pour l'administration. Toutes les routes admin sont protégées et nécessitent le rôle `ADMIN`.

**Base URL**: `/api/admin`

**Authentification**: JWT Bearer Token avec rôle ADMIN

---

## 1. Dashboard (`/api/admin/dashboard`)

Tableau de bord et statistiques globales du système.

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/statistiques` | GET | Statistiques globales (logements, étudiants, paiements, incidents) |
| `/metriques` | GET | Métriques de performance du système |
| `/alertes` | GET | Alertes système actives (paiements en retard, incidents critiques, etc.) |
| `/resume` | GET | Résumé complet du dashboard (statistiques + métriques + alertes) |

### Détails des statistiques
- **Logements**: Total, disponibles, occupés, en maintenance, archivés, taux d'occupation
- **Étudiants**: Total, actifs, en attente, rejetés, nouveaux ce mois
- **Demandes**: Total, en attente, approuvées, rejetées, taux d'approbation
- **Attributions**: Total, actives, expirant bientôt, terminées
- **Financier**: Revenus année/mois, impayés, paiements en retard, taux de recouvrement
- **Incidents**: Total, ouverts, critiques
- **Maintenance**: Planifiées, en cours, terminées
- **Utilisateurs**: Total, actifs, nouveaux ce mois

---

## 2. Utilisateurs (`/api/admin/utilisateurs`)

Gestion complète des comptes utilisateurs (Admins, Gestionnaires, Étudiants).

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/` | POST | Créer un utilisateur |
| `/` | GET | Lister tous les utilisateurs |
| `/{id}` | GET | Obtenir un utilisateur par ID |
| `/{id}` | PUT | Modifier un utilisateur |
| `/{id}` | DELETE | Supprimer un utilisateur |
| `/{id}/role` | PATCH | Changer le rôle d'un utilisateur |
| `/{id}/activer` | PATCH | Activer un compte utilisateur |
| `/{id}/desactiver` | PATCH | Désactiver un compte utilisateur |
| `/{id}/reset-password` | PATCH | Réinitialiser le mot de passe |

### Rôles disponibles
- `ADMIN` - Administrateur système
- `GESTIONNAIRE` - Gestionnaire de logements
- `ETUDIANT` - Étudiant
- `INVITE` - Invité (accès limité)

---

## 3. Logements (`/api/admin/logements`)

Gestion complète du parc immobilier.

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/` | POST | Créer un logement (code auto-généré: LOG-YYYY-XXX) |
| `/` | GET | Lister tous les logements avec pagination |
| `/{id}` | GET | Obtenir un logement par ID |
| `/code/{code}` | GET | Obtenir un logement par code unique |
| `/{id}` | PUT | Modifier un logement |
| `/{id}` | DELETE | Supprimer un logement (impossible si attributions actives) |
| `/{id}/archiver` | PATCH | Archiver un logement |
| `/{id}/maintenance` | POST | Planifier une maintenance |

### Types de logement
- `STUDIO`, `T1`, `T2`, `T3`
- `CHAMBRE_UNIVERSITAIRE`
- `APPARTEMENT_PARTAGE`
- `RESIDENCE_ETUDIANTE`

### Statuts de logement
- `DISPONIBLE` - Prêt à être attribué
- `OCCUPE` - Actuellement occupé
- `EN_MAINTENANCE` - En cours de maintenance
- `HORS_SERVICE` - Temporairement indisponible
- `RESERVE` - Réservé
- `ARCHIVE` - Archivé

---

## 4. Étudiants (`/api/admin/etudiants`)

Gestion administrative des étudiants.

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/` | POST | Créer un étudiant (matricule auto-généré si non fourni) |
| `/` | GET | Lister tous les étudiants avec pagination |
| `/{id}` | GET | Obtenir un étudiant par ID |
| `/matricule/{matricule}` | GET | Obtenir un étudiant par matricule |
| `/{id}` | PUT | Modifier un étudiant |
| `/{id}` | DELETE | Supprimer un étudiant (impossible si attributions actives) |
| `/{id}/valider` | PATCH | Valider le dossier d'inscription |
| `/{id}/rejeter` | PATCH | Rejeter le dossier (avec motif optionnel) |
| `/{id}/statut` | PATCH | Changer le statut |
| `/{id}/activer` | PATCH | Activer le compte |
| `/{id}/desactiver` | PATCH | Désactiver le compte |
| `/statistiques` | GET | Statistiques globales des étudiants |

### Statuts étudiant
- `ACTIF` - Compte actif
- `EN_ATTENTE_VALIDATION` - Dossier en attente
- `SUSPENDU` - Compte suspendu
- `DIPLOME` - Étudiant diplômé
- `EXCLU` - Étudiant exclu

### Niveaux d'études
- `LICENCE_1`, `LICENCE_2`, `LICENCE_3`
- `MASTER_1`, `MASTER_2`
- `DOCTORAT`
- `BTS_1`, `BTS_2`

---

## 5. Attributions (`/api/admin/attributions`)

Gestion des attributions de logements aux étudiants.

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/` | POST | Créer une attribution (contrat auto-généré: CTR-YYYY-XXX) |
| `/` | GET | Lister toutes les attributions avec pagination |
| `/{id}` | GET | Obtenir une attribution par ID |
| `/contrat/{numeroContrat}` | GET | Obtenir par numéro de contrat |
| `/{id}` | PUT | Modifier une attribution |
| `/{id}/revoquer` | PATCH | Révoquer une attribution (avec motif) |
| `/{id}/terminer` | PATCH | Terminer normalement une attribution |
| `/{id}/prolonger` | PATCH | Prolonger la durée (nouvelle date de fin) |
| `/{id}/checkin` | PATCH | Enregistrer l'entrée dans le logement |
| `/{id}/checkout` | PATCH | Enregistrer la sortie du logement |
| `/expirant` | GET | Lister les attributions expirant dans N jours |
| `/statistiques` | GET | Statistiques globales des attributions |

### Statuts d'attribution
- `ACTIVE` - Attribution en cours
- `EXPIREE` - Attribution terminée normalement
- `RESILIEE` - Attribution résiliée
- `SUSPENDUE` - Attribution suspendue
- `EN_COURS_RENOUVELLEMENT` - Renouvellement en cours

---

## 6. Paiements (`/api/admin/paiements`)

Gestion financière et suivi des paiements.

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/` | POST | Enregistrer un paiement |
| `/` | GET | Lister tous les paiements avec pagination |
| `/{id}` | GET | Obtenir un paiement par ID |
| `/numero/{numeroPaiement}` | GET | Obtenir par numéro de paiement |
| `/{id}/payer` | PATCH | Marquer comme payé (avec référence bancaire) |
| `/{id}/annuler` | PATCH | Annuler un paiement (avec motif) |
| `/{id}/rembourser` | PATCH | Rembourser un paiement (avec motif) |
| `/attribution/{id}` | GET | Lister les paiements d'une attribution |
| `/attribution/{id}/generer` | POST | Générer les paiements mensuels (N mois) |
| `/attribution/{id}/solde` | GET | Calculer le solde et pénalités |
| `/en-retard` | GET | Lister tous les paiements en retard |
| `/rappels` | POST | Envoyer des rappels pour paiements en retard |
| `/statistiques` | GET | Statistiques globales des paiements |

### Statuts de paiement
- `EN_ATTENTE` - Paiement attendu
- `PAYE` - Paiement effectué
- `EN_RETARD` - Paiement en retard
- `ANNULE` - Paiement annulé
- `REMBOURSE` - Paiement remboursé

### Modes de paiement
- `ESPECES`
- `CARTE_BANCAIRE`
- `VIREMENT_BANCAIRE`
- `CHEQUE`
- `MOBILE_MONEY`
- `AUTRE`

---

## 7. Rapports (`/api/admin/rapports`)

Génération de rapports et export de données.

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/` | POST | Générer un rapport (type, période, format) |
| `/` | GET | Lister tous les rapports générés |
| `/{id}` | GET | Obtenir un rapport par ID |
| `/{id}` | DELETE | Supprimer un rapport |
| `/export` | GET | Exporter des données (CSV, JSON, PDF, Excel) |
| `/occupation` | GET | Rapport d'occupation rapide |
| `/financier` | GET | Rapport financier rapide |
| `/demandes` | GET | Rapport des demandes rapide |

### Types de rapport
- `OCCUPATION` - Taux d'occupation des logements
- `FINANCIER` - Revenus et paiements
- `DEMANDES` - Analyse des demandes
- `ETUDIANTS` - Statistiques étudiants
- `INCIDENTS` - Analyse des incidents
- `MAINTENANCE` - Rapport de maintenance
- `PERFORMANCE` - Métriques de performance

### Formats d'export
- `PDF` - Document PDF
- `CSV` - Fichier CSV
- `EXCEL` - Fichier Excel (.xlsx)

---

## Authentification

### Connexion Admin

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

## Utilisateurs par défaut

| Email | Mot de passe | Rôle |
|-------|--------------|------|
| admin@sgle.sn | Admin@123 | ADMIN |
| gestionnaire@sgle.sn | Gestionnaire@123 | GESTIONNAIRE |
| etudiant@ucad.edu.sn | Etudiant@123 | ETUDIANT |

---

## Codes de réponse HTTP

| Code | Description |
|------|-------------|
| 200 | Succès |
| 201 | Ressource créée |
| 400 | Données invalides |
| 401 | Non authentifié |
| 403 | Accès refusé (rôle insuffisant) |
| 404 | Ressource non trouvée |
| 409 | Conflit (doublon, contrainte violée) |
| 500 | Erreur serveur |

---

## Format des réponses

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

### Pagination

```json
{
  "success": true,
  "data": {
    "content": [...],
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 150,
    "totalPages": 8,
    "first": true,
    "last": false
  }
}
```

---

## Configuration

- **Port**: 8081
- **Base de données**: H2 (dev) / PostgreSQL (prod)
- **Console H2**: http://localhost:8081/h2-console
- **Swagger UI**: http://localhost:8081/swagger-ui.html

---

*Documentation générée le 6 février 2026*
