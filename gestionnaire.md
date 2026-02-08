# Fonctionnalités GESTIONNAIRE - API Documentation

## Vue d'ensemble

Ce document décrit les fonctionnalités disponibles pour les utilisateurs ayant le rôle **GESTIONNAIRE** ou **ADMIN**. Chaque fonctionnalité est expliquée avec son flux de travail et ses cas d'utilisation.

---

## 1. Gestion des Logements (`/api/gestionnaire/logements`)

### Comment ça marche

Le gestionnaire peut gérer l'ensemble du parc immobilier : créer, modifier, archiver et consulter les logements.

#### Lister les logements
```
GET /api/gestionnaire/logements?page=0&taille=20&type=STUDIO&statut=DISPONIBLE&ville=Dakar&recherche=texte
```
**Fonctionnement :**
1. Le système récupère tous les logements de la base de données
2. Applique les filtres optionnels (type, statut, ville, recherche par code/description)
3. Pagine les résultats (par défaut page 0, 20 éléments)
4. Retourne une `Page<LogementResponseDto>` avec les informations de pagination

**Paramètres optionnels :**
- `page` : Numéro de page (défaut: 0)
- `taille` : Nombre d'éléments par page (défaut: 20)
- `type` : TypeLogementEnum (STUDIO, T1, T2, T3, CHAMBRE_UNIVERSITAIRE, etc.)
- `statut` : StatutLogementEnum (DISPONIBLE, OCCUPE, EN_MAINTENANCE, ARCHIVE)
- `ville` : Nom de la ville
- `recherche` : Texte libre pour recherche dans code/description

#### Créer un logement
```
POST /api/gestionnaire/logements
Content-Type: application/json

{
  "type": "STUDIO",
  "capacite": 1,
  "superficie": 25.5,
  "prixMensuel": 150000,
  "description": "Studio meublé proche campus",
  "meuble": true,
  "adresseRue": "Avenue Cheikh Anta Diop",
  "adresseNumero": "12",
  "adresseCodePostal": "10000",
  "adresseVille": "Dakar",
  "adresseQuartier": "Point E",
  "adresseRegion": "Dakar",
  "photos": ["url1.jpg", "url2.jpg"]
}
```
**Fonctionnement :**
1. Valide les données reçues
2. Génère automatiquement un code unique (ex: `ST-2026-00001` pour un studio)
3. Crée l'adresse embedded
4. Initialise le statut à `DISPONIBLE`
5. Enregistre et retourne le logement créé

#### Modifier un logement
```
PUT /api/gestionnaire/logements/{id}
```
**Fonctionnement :**
1. Vérifie que le logement existe
2. Met à jour uniquement les champs fournis (mise à jour partielle)
3. Met à jour la date de modification
4. Retourne le logement modifié

#### Archiver un logement
```
PUT /api/gestionnaire/logements/{id}/archiver
```
**Fonctionnement :**
1. Vérifie que le logement n'est pas occupé
2. Change le statut en `ARCHIVE`
3. Le logement n'apparaît plus dans les recherches standards

#### Supprimer un logement
```
DELETE /api/gestionnaire/logements/{id}
```
**Fonctionnement :**
1. Vérifie que le logement n'est pas occupé
2. Supprime définitivement le logement
3. À utiliser avec précaution (préférer l'archivage)

#### Statistiques
```
GET /api/gestionnaire/logements/statistiques
```
**Retourne :**
```json
{
  "total": 150,
  "disponibles": 45,
  "occupes": 95,
  "enMaintenance": 5,
  "archives": 5,
  "tauxOccupation": 63.33
}
```

---

## 2. Gestion des Demandes (`/api/gestionnaire/demandes`)

### Comment ça marche

Le gestionnaire traite les demandes de logement des étudiants. Le flux typique est :
1. Étudiant soumet une demande → statut `EN_ATTENTE`
2. Gestionnaire prend en charge → statut `EN_COURS_TRAITEMENT`
3. Gestionnaire approuve/rejette → statut `APPROUVEE` ou `REJETEE`

#### Lister les demandes en attente
```
GET /api/gestionnaire/demandes/en-attente
```
**Fonctionnement :**
1. Récupère toutes les demandes avec statut `EN_ATTENTE`
2. Trie par date de demande (les plus anciennes d'abord)
3. Permet au gestionnaire de traiter par ordre d'arrivée

#### Consulter une demande
```
GET /api/gestionnaire/demandes/{id}
```
**Retourne :**
```json
{
  "id": "uuid",
  "numeroReference": "DEM-2026-00001",
  "etudiantId": "uuid",
  "etudiantEmail": "etudiant@ucad.edu.sn",
  "typeLogementSouhaite": "STUDIO",
  "budgetMaximum": 150000,
  "dateDebutSouhaitee": "2026-03-01",
  "dureeSouhaitee": 12,
  "preferences": "Proche campus, wifi inclus",
  "statut": "EN_ATTENTE",
  "priorite": "NORMALE",
  "dateDemande": "2026-02-01T10:30:00",
  "commentaires": null
}
```

#### Prendre en charge une demande
```
PUT /api/gestionnaire/demandes/{id}/en-traitement
```
**Fonctionnement :**
1. Vérifie que la demande est en `EN_ATTENTE`
2. Change le statut en `EN_COURS_TRAITEMENT`
3. Associe le gestionnaire connecté à la demande
4. La demande apparaît dans "Mes demandes" du gestionnaire

#### Approuver une demande
```
PUT /api/gestionnaire/demandes/{id}/approuver
```
**Fonctionnement :**
1. Vérifie que la demande est en `EN_ATTENTE` ou `EN_COURS_TRAITEMENT`
2. Change le statut en `APPROUVEE`
3. Enregistre la date de traitement
4. L'étudiant peut ensuite recevoir une attribution

#### Rejeter une demande
```
PUT /api/gestionnaire/demandes/{id}/rejeter?motif=Pas de logement disponible correspondant
```
**Fonctionnement :**
1. Vérifie que la demande est en `EN_ATTENTE` ou `EN_COURS_TRAITEMENT`
2. Change le statut en `REJETEE`
3. Enregistre le motif de rejet (obligatoire)
4. L'étudiant est notifié avec le motif

#### Changer la priorité
```
PUT /api/gestionnaire/demandes/{id}/priorite/HAUTE
```
**Valeurs possibles :** `BASSE`, `NORMALE`, `HAUTE`, `URGENTE`

**Cas d'utilisation :**
- Étudiant boursier en situation d'urgence → `URGENTE`
- Demande standard → `NORMALE`
- Demande pour l'année suivante → `BASSE`

#### Statistiques
```
GET /api/gestionnaire/demandes/statistiques
```
**Retourne :**
```json
{
  "total": 200,
  "enAttente": 35,
  "enTraitement": 10,
  "approuvees": 140,
  "rejetees": 12,
  "annulees": 3,
  "tauxApprobation": 70.0
}
```

---

## 3. Gestion des Attributions (`/api/gestionnaire/attributions`)

### Comment ça marche

Une attribution lie un étudiant à un logement pour une période définie. Le flux est :
1. Demande approuvée → Créer attribution
2. Attribution active → Check-in de l'étudiant
3. Fin de contrat → Check-out et terminaison

#### Créer une attribution
```
POST /api/gestionnaire/attributions
Content-Type: application/json

{
  "etudiantId": "uuid-etudiant",
  "logementId": "uuid-logement",
  "dateDebut": "2026-03-01",
  "dateFin": "2027-02-28",
  "montantLoyer": 150000,
  "montantCaution": 300000,
  "commentaires": "Attribution suite à demande DEM-2026-00001"
}
```
**Fonctionnement :**
1. Vérifie que le logement est `DISPONIBLE`
2. Vérifie que l'étudiant n'a pas d'attribution active
3. Vérifie que les dates sont cohérentes
4. Génère un numéro de contrat unique (ex: `CONT-2026-00001`)
5. Change le statut du logement en `OCCUPE`
6. Crée l'attribution avec statut `ACTIVE`

#### Enregistrer le check-in
```
PUT /api/gestionnaire/attributions/{id}/check-in
```
**Fonctionnement :**
1. Vérifie que le check-in n'a pas déjà été fait
2. Enregistre la date/heure du check-in
3. Confirme que l'étudiant a pris possession du logement

**Cas d'utilisation :**
- L'étudiant arrive avec ses affaires
- Le gestionnaire vérifie l'état des lieux
- Valide le check-in dans le système

#### Enregistrer le check-out
```
PUT /api/gestionnaire/attributions/{id}/check-out
```
**Fonctionnement :**
1. Vérifie que le check-in a été fait
2. Vérifie que le check-out n'a pas déjà été fait
3. Enregistre la date/heure du check-out

**Cas d'utilisation :**
- L'étudiant quitte le logement
- État des lieux de sortie effectué
- Remise des clés

#### Prolonger une attribution
```
PUT /api/gestionnaire/attributions/{id}/prolonger?nouvelleDateFin=2027-08-31
```
**Fonctionnement :**
1. Vérifie que la nouvelle date est postérieure à l'ancienne
2. Met à jour la date de fin
3. Ajoute un commentaire de prolongation dans l'historique

**Cas d'utilisation :**
- Étudiant souhaite rester une année supplémentaire
- Prolongation pour fin de cursus

#### Révoquer une attribution
```
PUT /api/gestionnaire/attributions/{id}/revoquer?motif=Non-paiement répété du loyer
```
**Fonctionnement :**
1. Change le statut en `RESILIEE`
2. Enregistre le motif de révocation
3. Libère le logement (statut `DISPONIBLE`)

**Cas d'utilisation :**
- Non-respect du règlement
- Impayés de loyer
- Comportement inapproprié

#### Terminer une attribution
```
PUT /api/gestionnaire/attributions/{id}/terminer
```
**Fonctionnement :**
1. Change le statut en `EXPIREE`
2. Libère le logement (statut `DISPONIBLE`)

**Cas d'utilisation :**
- Fin normale du contrat
- L'étudiant a effectué son check-out

#### Alertes d'expiration
```
GET /api/gestionnaire/attributions/expirant/30
```
**Fonctionnement :**
1. Recherche les attributions actives dont la date de fin est dans les X prochains jours
2. Permet d'anticiper les renouvellements ou libérations

**Cas d'utilisation :**
- Contacter les étudiants 30 jours avant expiration
- Planifier la remise en location

#### Statistiques
```
GET /api/gestionnaire/attributions/statistiques
```
**Retourne :**
```json
{
  "total": 300,
  "actives": 95,
  "expirees": 180,
  "resiliees": 25,
  "expirantBientot": 12
}
```

---

## 4. Tableau de Bord (`/api/gestionnaire/dashboard`)

### Comment ça marche

Le dashboard fournit une vue d'ensemble en temps réel de toutes les activités.

#### Vue globale
```
GET /api/gestionnaire/dashboard/statistiques
```
**Retourne les statistiques complètes :**
- Logements (total, disponibles, occupés, taux d'occupation)
- Étudiants (total, actifs)
- Demandes (total, en attente, taux d'approbation)
- Attributions (actives, expirant bientôt)
- Finances (revenus mois/année, impayés)
- Incidents (ouverts, critiques)
- Maintenance (planifiées, en cours)

#### Alertes actives
```
GET /api/gestionnaire/dashboard/alertes
```
**Types d'alertes générées automatiquement :**
```json
[
  {
    "type": "PAIEMENT_RETARD",
    "titre": "Paiements en retard",
    "message": "15 paiement(s) en retard nécessite(nt) votre attention",
    "priorite": "HAUTE",
    "actionRecommandee": "Envoyer des rappels aux étudiants concernés"
  },
  {
    "type": "ATTRIBUTION_EXPIRE_BIENTOT",
    "titre": "Attributions expirant bientôt",
    "message": "12 attribution(s) expire(nt) dans les 30 prochains jours",
    "priorite": "MOYENNE",
    "actionRecommandee": "Contacter les étudiants pour renouvellement"
  },
  {
    "type": "INCIDENT_CRITIQUE",
    "titre": "Incidents critiques",
    "message": "3 incident(s) critique(s) en attente de traitement",
    "priorite": "CRITIQUE",
    "actionRecommandee": "Intervenir immédiatement"
  }
]
```

#### Résumé du gestionnaire
```
GET /api/gestionnaire/dashboard/resume
```
**Retourne un objet consolidé avec :**
- Statistiques demandes
- Statistiques incidents
- Statistiques attributions
- Toutes les alertes
- Nombre d'incidents urgents
- Nombre d'attributions expirant

#### Mon portefeuille
```
GET /api/gestionnaire/dashboard/mon-portefeuille
```
**Fonctionnement :**
1. Identifie le gestionnaire connecté via le token JWT
2. Récupère les demandes qu'il gère
3. Récupère les incidents qui lui sont assignés
4. Retourne son portefeuille personnel

**Retourne :**
```json
{
  "demandes": [...],
  "nombreDemandes": 5,
  "incidents": [...],
  "nombreIncidents": 3
}
```

---

## 5. Gestion des Incidents (`/api/gestionnaire/incidents`)

### Comment ça marche

Les incidents sont des signalements de problèmes dans les logements. Le flux est :
1. Étudiant signale un incident → statut `OUVERT`
2. Gestionnaire prend en charge → statut `EN_COURS`
3. Technicien intervient et résout → statut `RESOLU`
4. Gestionnaire ferme l'incident → statut `FERME`

#### Créer un incident
```
POST /api/gestionnaire/incidents
Content-Type: application/json

{
  "etudiantId": "uuid-etudiant",
  "logementId": "uuid-logement",
  "type": "PLOMBERIE",
  "description": "Fuite d'eau sous l'évier de la cuisine",
  "urgence": "HAUTE",
  "photos": ["photo-fuite1.jpg", "photo-fuite2.jpg"]
}
```
**Fonctionnement :**
1. Génère un numéro de ticket unique (ex: `INC-2026-00001`)
2. Initialise le statut à `OUVERT`
3. Enregistre la date de signalement
4. Associe l'étudiant et le logement concernés

#### Consulter les incidents urgents
```
GET /api/gestionnaire/incidents/urgents
```
**Fonctionnement :**
1. Récupère les incidents avec urgence `CRITIQUE` ou `HAUTE`
2. Exclut les incidents déjà résolus ou fermés
3. Permet de prioriser les interventions

#### S'assigner un incident
```
PUT /api/gestionnaire/incidents/{id}/assigner-gestionnaire
```
**Fonctionnement :**
1. Identifie le gestionnaire connecté via le token JWT
2. Associe le gestionnaire à l'incident
3. Change le statut en `EN_COURS` si était `OUVERT`
4. L'incident apparaît dans "Mes incidents"

#### Assigner un technicien
```
PUT /api/gestionnaire/incidents/{id}/assigner-technicien/{technicienId}
```
**Fonctionnement :**
1. Recherche le technicien par son ID
2. Enregistre le nom/email du technicien sur l'incident
3. Change le statut en `EN_COURS` si était `OUVERT`

**Cas d'utilisation :**
- Plomberie → Assigner le plombier de service
- Électricité → Assigner l'électricien
- Serrurerie → Assigner le serrurier

#### Résoudre un incident
```
PUT /api/gestionnaire/incidents/{id}/resoudre?commentaireResolution=Fuite réparée, joints remplacés
```
**Fonctionnement :**
1. Vérifie que l'incident n'est pas déjà résolu/fermé
2. Change le statut en `RESOLU`
3. Enregistre le commentaire de résolution
4. Enregistre la date de résolution

#### Fermer un incident
```
PUT /api/gestionnaire/incidents/{id}/fermer
```
**Fonctionnement :**
1. Change le statut en `FERME`
2. Finalise le cycle de vie de l'incident
3. L'incident reste consultable dans l'historique

#### Changer le niveau d'urgence
```
PUT /api/gestionnaire/incidents/{id}/urgence/CRITIQUE
```
**Cas d'utilisation :**
- Fuite d'eau aggravée → Passer de `HAUTE` à `CRITIQUE`
- Problème mineur → Passer de `NORMALE` à `BASSE`

#### Statistiques
```
GET /api/gestionnaire/incidents/statistiques
```
**Retourne :**
```json
{
  "total": 150,
  "ouverts": 12,
  "enCours": 8,
  "resolus": 110,
  "fermes": 20,
  "tauxResolution": 86.67
}
```

---

## Workflow Complet : De la Demande à l'Attribution

```
┌─────────────────────────────────────────────────────────────────┐
│                         WORKFLOW COMPLET                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. DEMANDE                                                      │
│  ┌─────────┐    ┌─────────────────┐    ┌───────────┐           │
│  │Étudiant │───▶│ Demande créée   │───▶│ EN_ATTENTE│           │
│  └─────────┘    │ DEM-2026-00001  │    └─────┬─────┘           │
│                 └─────────────────┘          │                   │
│                                              ▼                   │
│  2. TRAITEMENT                        ┌─────────────────┐       │
│  ┌───────────┐                        │EN_COURS_TRAITEMENT│     │
│  │Gestionnaire│◀─────────────────────│ (Gestionnaire    │       │
│  │  prend en │                        │  assigné)       │       │
│  │  charge   │                        └───────┬─────────┘       │
│  └───────────┘                                │                  │
│                          ┌────────────────────┴─────────────┐   │
│                          ▼                                  ▼    │
│                   ┌───────────┐                     ┌──────────┐│
│                   │ APPROUVEE │                     │ REJETEE  ││
│                   └─────┬─────┘                     │ + motif  ││
│                         │                           └──────────┘│
│  3. ATTRIBUTION         ▼                                       │
│  ┌─────────────────────────────────────────┐                   │
│  │ Création Attribution                     │                   │
│  │ CONT-2026-00001                          │                   │
│  │ - Étudiant + Logement                   │                   │
│  │ - Dates début/fin                       │                   │
│  │ - Montant loyer/caution                 │                   │
│  │ Statut: ACTIVE                          │                   │
│  │ Logement: DISPONIBLE → OCCUPE           │                   │
│  └─────────────────────┬───────────────────┘                   │
│                        │                                        │
│  4. CHECK-IN           ▼                                        │
│  ┌─────────────────────────────────────────┐                   │
│  │ Étudiant arrive                         │                   │
│  │ → État des lieux entrée                │                   │
│  │ → Remise des clés                      │                   │
│  │ → dateCheckIn enregistrée              │                   │
│  └─────────────────────┬───────────────────┘                   │
│                        │                                        │
│  5. VIE DU CONTRAT     │                                        │
│  ┌─────────────────────┴───────────────────┐                   │
│  │ - Paiements mensuels                    │                   │
│  │ - Incidents éventuels                   │                   │
│  │ - Possibilité de prolongation           │                   │
│  └─────────────────────┬───────────────────┘                   │
│                        │                                        │
│  6. FIN                ▼                                        │
│  ┌─────────────────────────────────────────┐                   │
│  │ CHECK-OUT                               │                   │
│  │ → État des lieux sortie                │                   │
│  │ → Remise des clés                      │                   │
│  │ → dateCheckOut enregistrée             │                   │
│  │                                         │                   │
│  │ TERMINER ATTRIBUTION                    │                   │
│  │ Statut: ACTIVE → EXPIREE               │                   │
│  │ Logement: OCCUPE → DISPONIBLE          │                   │
│  └─────────────────────────────────────────┘                   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Enums Utilisés

### StatutDemandeEnum

- `EN_ATTENTE` - Demande en attente de traitement
- `EN_COURS_TRAITEMENT` - Demande en cours de traitement
- `APPROUVEE` - Demande approuvée
- `REJETEE` - Demande rejetée
- `ANNULEE` - Demande annulée

### PrioriteEnum

- `BASSE` - Priorité basse
- `NORMALE` - Priorité normale
- `HAUTE` - Priorité haute
- `URGENTE` - Priorité urgente

### StatutIncidentEnum

- `OUVERT` - Incident ouvert
- `EN_COURS` - Incident en cours de traitement
- `RESOLU` - Incident résolu
- `FERME` - Incident fermé

### TypeIncidentEnum

- `PLOMBERIE` - Problème de plomberie
- `ELECTRICITE` - Problème électrique
- `SERRURERIE` - Problème de serrurerie
- `CHAUFFAGE` - Problème de chauffage
- `CLIMATISATION` - Problème de climatisation
- `NUISIBLES` - Nuisibles
- `DEGAT_DES_EAUX` - Dégât des eaux
- `BRUIT` - Nuisance sonore
- `SECURITE` - Problème de sécurité
- `AUTRE` - Autre type

### UrgenceEnum

- `BASSE` - Urgence basse
- `NORMALE` - Urgence normale
- `HAUTE` - Urgence haute
- `CRITIQUE` - Urgence critique

### StatutAttributionEnum

- `ACTIVE` - Attribution active
- `EXPIREE` - Attribution expirée
- `RESILIEE` - Attribution résiliée

### StatutLogementEnum

- `DISPONIBLE` - Logement disponible
- `OCCUPE` - Logement occupé
- `EN_MAINTENANCE` - Logement en maintenance
- `ARCHIVE` - Logement archivé

### TypeLogementEnum

- `STUDIO` - Studio
- `T1` - T1
- `T2` - T2
- `T3` - T3
- `CHAMBRE_UNIVERSITAIRE` - Chambre universitaire
- `APPARTEMENT_PARTAGE` - Appartement partagé
- `RESIDENCE_ETUDIANTE` - Résidence étudiante

---

## Sécurité

Tous les endpoints sont protégés et nécessitent:

1. Un token JWT valide dans le header `Authorization: Bearer {token}`
2. Un rôle **ADMIN** ou **GESTIONNAIRE**

---
