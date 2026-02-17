# Module Étudiant - SGLE Backend

## Vue d'ensemble

Le module Étudiant fournit toutes les fonctionnalités nécessaires pour que les étudiants puissent gérer leur vie résidentielle : rechercher des logements, soumettre des demandes, gérer leur logement attribué, signaler des incidents, suivre leurs paiements et gérer leurs documents.

## Architecture

### DTOs (`dto/etudiant/`)

| DTO | Description |
|-----|-------------|
| `CompleterProfilRequest` | Données pour compléter/mettre à jour le profil étudiant |
| `ProfilEtudiantDto` | Profil complet de l'étudiant avec informations personnelles |
| `RechercheLogementRequest` | Critères de recherche de logements |
| `LogementCatalogueDto` | Vue simplifiée d'un logement pour le catalogue |
| `LogementDetailsDto` | Détails complets d'un logement avec équipements |
| `SoumettreDemandeRequest` | Formulaire de soumission de demande de logement |
| `SuiviDemandeDto` | Suivi d'une demande avec statut et progression |
| `MonLogementDto` | Informations du logement attribué à l'étudiant |
| `SignalerIncidentRequest` | Formulaire de signalement d'incident |
| `SuiviIncidentDto` | Suivi d'un incident avec historique |
| `PaiementEtudiantDto` | Détail d'un paiement |
| `RecapitulatifPaiementsDto` | Récapitulatif global des paiements |
| `NotificationEtudiantDto` | Notification formatée pour l'étudiant |
| `DocumentEtudiantSimpleDto` | Document simplifié de l'étudiant |
| `UploadDocumentRequest` | Requête d'upload de document |

### Service (`EtudiantService`)

Service central regroupant toute la logique métier des fonctionnalités étudiantes.

### Controllers (`controller/etudiant/`)

| Controller | Base Path | Description |
|------------|-----------|-------------|
| `EtudiantProfilController` | `/api/etudiant/profil` | Gestion du profil |
| `EtudiantRechercheController` | `/api/etudiant/logements` | Recherche de logements |
| `EtudiantDemandeController` | `/api/etudiant/demandes` | Demandes de logement |
| `EtudiantLogementController` | `/api/etudiant/mon-logement` | Logement attribué |
| `EtudiantIncidentController` | `/api/etudiant/incidents` | Signalements et support |
| `EtudiantPaiementController` | `/api/etudiant/paiements` | Paiements et reçus |
| `EtudiantNotificationController` | `/api/etudiant/notifications` | Notifications |
| `EtudiantDocumentController` | `/api/etudiant/documents` | Documents personnels |

---

## Endpoints API

### 🔐 Profil Étudiant

#### GET `/api/etudiant/profil`
Récupère le profil complet de l'étudiant connecté.

**Réponse :**
```json
{
  "id": 1,
  "nom": "Diop",
  "prenom": "Mamadou",
  "email": "mamadou.diop@ucad.edu.sn",
  "telephone": "+221 77 123 45 67",
  "dateNaissance": "2000-05-15",
  "adresse": "Dakar, Sénégal",
  "matricule": "2021000123",
  "etablissement": "Université Cheikh Anta Diop",
  "filiere": "Informatique",
  "niveau": "Master 2",
  "anneeUniversitaire": "2024-2025",
  "profilComplet": true,
  "documentsManquants": [],
  "logementActuel": {
    "id": 5,
    "numeroLogement": "B-102",
    "residenceNom": "Cité Universitaire UCAD"
  }
}
```

#### PUT `/api/etudiant/profil`
Met à jour le profil de l'étudiant.

**Corps de la requête :**
```json
{
  "nom": "Diop",
  "prenom": "Mamadou",
  "telephone": "+221 77 123 45 67",
  "dateNaissance": "2000-05-15",
  "adresse": "Dakar, Sénégal",
  "contactUrgenceNom": "Fatou Diop",
  "contactUrgenceTelephone": "+221 77 987 65 43",
  "contactUrgenceLien": "Mère"
}
```

#### POST `/api/etudiant/profil/changer-mot-de-passe`
Change le mot de passe de l'étudiant.

**Paramètres :**
- `ancienMotDePasse` (String) : Mot de passe actuel
- `nouveauMotDePasse` (String) : Nouveau mot de passe

---

### 🔍 Recherche de Logements

#### GET `/api/etudiant/logements`
Catalogue des logements disponibles.

**Paramètres optionnels :**
- `page` (int) : Numéro de page (défaut: 0)
- `taille` (int) : Taille de page (défaut: 10)

**Réponse :**
```json
{
  "content": [
    {
      "id": 1,
      "numeroLogement": "A-101",
      "residenceNom": "Cité Universitaire UCAD",
      "typeLogement": "CHAMBRE_SIMPLE",
      "typeLogementLibelle": "Chambre simple",
      "loyerMensuel": 25000,
      "loyerFormate": "25 000 FCFA",
      "superficie": 12,
      "disponible": true,
      "photoUrl": "/api/media/logements/1/photo.jpg",
      "nbEquipements": 5,
      "ville": "Dakar"
    }
  ],
  "totalElements": 50,
  "totalPages": 5
}
```

#### POST `/api/etudiant/logements/recherche`
Recherche avec filtres avancés.

**Corps de la requête :**
```json
{
  "typeLogement": "CHAMBRE_SIMPLE",
  "loyerMin": 20000,
  "loyerMax": 50000,
  "superficieMin": 10,
  "ville": "Dakar",
  "residenceId": 1,
  "equipementsRequis": ["WIFI", "CLIMATISATION"],
  "disponibleUniquement": true
}
```

#### GET `/api/etudiant/logements/{logementId}`
Détails complets d'un logement.

**Réponse :**
```json
{
  "id": 1,
  "numeroLogement": "A-101",
  "residence": {
    "id": 1,
    "nom": "Cité Universitaire UCAD",
    "adresse": "Avenue Cheikh Anta Diop",
    "ville": "Dakar"
  },
  "typeLogement": "CHAMBRE_SIMPLE",
  "typeLogementLibelle": "Chambre simple",
  "etage": 1,
  "superficie": 12,
  "loyerMensuel": 25000,
  "loyerFormate": "25 000 FCFA",
  "disponible": true,
  "description": "Chambre individuelle avec vue sur le campus",
  "equipements": [
    {"nom": "Lit", "description": "Lit simple 90x190"},
    {"nom": "Bureau", "description": "Bureau de travail"},
    {"nom": "WiFi", "description": "Connexion haut débit"}
  ],
  "photos": ["/api/media/logements/1/photo1.jpg"],
  "reglesLogement": "Non-fumeur, pas d'animaux",
  "capaciteMax": 1,
  "contactGestionnaire": {
    "nom": "M. Fall",
    "telephone": "+221 33 123 45 67",
    "email": "gestionnaire@ucad.edu.sn"
  }
}
```

---

### 📋 Demandes de Logement

#### POST `/api/etudiant/demandes`
Soumet une nouvelle demande de logement.

**Corps de la requête :**
```json
{
  "logementId": 1,
  "message": "Je suis étudiant en Master 2 et recherche un logement proche du campus.",
  "dateEntreeSouhaitee": "2024-09-01",
  "dureeSouhaitee": 12,
  "documentsJoints": [1, 2, 3]
}
```

**Réponse :**
```json
{
  "id": 10,
  "logementNumero": "A-101",
  "residenceNom": "Cité Universitaire UCAD",
  "statut": "EN_ATTENTE",
  "statutLibelle": "En attente de traitement",
  "dateCreation": "2024-08-15T10:30:00",
  "message": "Votre demande a été soumise avec succès."
}
```

#### GET `/api/etudiant/demandes`
Liste les demandes de l'étudiant.

**Paramètres optionnels :**
- `statut` (StatutDemandeEnum) : Filtrer par statut

**Réponse :**
```json
[
  {
    "id": 10,
    "logementNumero": "A-101",
    "residenceNom": "Cité Universitaire UCAD",
    "statut": "EN_ATTENTE",
    "statutLibelle": "En attente de traitement",
    "dateCreation": "2024-08-15T10:30:00",
    "progression": 25,
    "etapeSuivante": "Examen des documents"
  }
]
```

#### GET `/api/etudiant/demandes/{demandeId}`
Détails d'une demande spécifique.

#### DELETE `/api/etudiant/demandes/{demandeId}`
Annule une demande en attente.

---

### 🏠 Mon Logement

#### GET `/api/etudiant/mon-logement`
Informations du logement actuellement attribué.

**Réponse :**
```json
{
  "id": 5,
  "numeroLogement": "B-102",
  "residence": {
    "nom": "Cité Universitaire UCAD",
    "adresse": "Avenue Cheikh Anta Diop",
    "ville": "Dakar"
  },
  "typeLogement": "CHAMBRE_DOUBLE",
  "etage": 2,
  "superficie": 18,
  "loyerMensuel": 35000,
  "loyerFormate": "35 000 FCFA",
  "dateAttribution": "2024-09-01",
  "dateFinContrat": "2025-06-30",
  "joursRestants": 285,
  "contratActif": true,
  "equipements": ["Lit", "Bureau", "WiFi", "Climatisation"],
  "reglesLogement": "Horaires de visite: 8h-22h",
  "contactGestionnaire": {
    "nom": "M. Fall",
    "telephone": "+221 33 123 45 67"
  },
  "prochainPaiement": {
    "montant": 35000,
    "dateEcheance": "2024-10-01",
    "joursRestants": 15
  }
}
```

#### GET `/api/etudiant/mon-logement/historique`
Historique des logements précédents.

#### GET `/api/etudiant/mon-logement/contrat`
Télécharge le contrat de location au format PDF.

#### GET `/api/etudiant/mon-logement/contact-gestionnaire`
Coordonnées du gestionnaire de la résidence.

---

### 💬 Signalements et Support

#### POST `/api/etudiant/incidents`
Signale un nouvel incident.

**Corps de la requête :**
```json
{
  "titre": "Fuite d'eau dans la salle de bain",
  "description": "Une fuite importante sous le lavabo depuis ce matin.",
  "typeIncident": "PLOMBERIE",
  "priorite": "HAUTE",
  "localisation": "Salle de bain, sous le lavabo",
  "photosUrls": ["/uploads/incident_photo1.jpg"]
}
```

**Réponse :**
```json
{
  "id": 15,
  "titre": "Fuite d'eau dans la salle de bain",
  "statut": "OUVERT",
  "statutLibelle": "Ouvert - En attente de prise en charge",
  "dateSignalement": "2024-09-20T14:30:00",
  "numeroReference": "INC-2024-0015",
  "message": "Votre signalement a été enregistré. Un technicien interviendra sous 48h."
}
```

#### GET `/api/etudiant/incidents`
Liste les incidents signalés par l'étudiant.

**Paramètres optionnels :**
- `statut` (StatutIncidentEnum) : Filtrer par statut

#### GET `/api/etudiant/incidents/{incidentId}`
Détails et suivi d'un incident.

**Réponse :**
```json
{
  "id": 15,
  "titre": "Fuite d'eau dans la salle de bain",
  "description": "Une fuite importante sous le lavabo depuis ce matin.",
  "typeIncident": "PLOMBERIE",
  "priorite": "HAUTE",
  "statut": "EN_COURS",
  "statutLibelle": "En cours de traitement",
  "dateSignalement": "2024-09-20T14:30:00",
  "dateResolutionPrevue": "2024-09-22",
  "progression": 50,
  "historiqueStatuts": [
    {"statut": "OUVERT", "date": "2024-09-20T14:30:00"},
    {"statut": "EN_COURS", "date": "2024-09-21T09:00:00"}
  ],
  "commentaireGestionnaire": "Un technicien interviendra demain matin."
}
```

#### POST `/api/etudiant/incidents/{incidentId}/photos`
Ajoute des photos à un incident existant.

---

### 💳 Paiements

#### GET `/api/etudiant/paiements/recapitulatif`
Récapitulatif global des paiements.

**Réponse :**
```json
{
  "soldeActuel": 0,
  "soldeDu": 35000,
  "prochainPaiement": {
    "montant": 35000,
    "dateEcheance": "2024-10-01",
    "joursRestants": 15
  },
  "totalPayeAnnee": 105000,
  "nombrePaiementsAnnee": 3,
  "paiementsEnRetard": [],
  "paiementsAVenir": [
    {"mois": "Octobre 2024", "montant": 35000, "dateEcheance": "2024-10-01"}
  ]
}
```

#### GET `/api/etudiant/paiements/historique`
Historique des paiements.

**Paramètres optionnels :**
- `annee` (int) : Filtrer par année
- `statut` (StatutPaiementEnum) : Filtrer par statut

**Réponse :**
```json
[
  {
    "id": 1,
    "montant": 35000,
    "montantFormate": "35 000 FCFA",
    "dateEcheance": "2024-09-01",
    "datePaiement": "2024-08-28",
    "statut": "PAYE",
    "statutLibelle": "Payé",
    "modePaiement": "VIREMENT",
    "reference": "PAY-2024-0001",
    "periode": "Septembre 2024"
  }
]
```

#### GET `/api/etudiant/paiements/{paiementId}/recu`
Télécharge le reçu de paiement au format PDF.

---

### 🔔 Notifications

#### GET `/api/etudiant/notifications`
Liste les notifications de l'étudiant.

**Paramètres optionnels :**
- `nonLuesUniquement` (boolean) : Afficher uniquement les non lues
- `page` (int), `taille` (int) : Pagination

**Réponse :**
```json
{
  "content": [
    {
      "id": 1,
      "type": "RAPPEL_PAIEMENT",
      "typeLibelle": "Rappel paiement",
      "titre": "Rappel de paiement",
      "message": "Votre loyer d'octobre est dû dans 5 jours.",
      "lue": false,
      "dateEnvoi": "2024-09-26T10:00:00",
      "tempsDepuisEnvoi": "il y a 2 heures",
      "nouvelle": true
    }
  ],
  "nombreNonLues": 3
}
```

#### POST `/api/etudiant/notifications/{notificationId}/lue`
Marque une notification comme lue.

#### POST `/api/etudiant/notifications/marquer-toutes-lues`
Marque toutes les notifications comme lues.

#### GET `/api/etudiant/notifications/non-lues/count`
Nombre de notifications non lues.

---

### 📄 Documents

#### GET `/api/etudiant/documents`
Liste les documents de l'étudiant.

**Paramètres optionnels :**
- `type` (TypeDocumentEnum) : Filtrer par type

**Réponse :**
```json
[
  {
    "id": 1,
    "nom": "carte_etudiant_2024.pdf",
    "type": "CARTE_ETUDIANT",
    "typeLibelle": "Carte étudiante",
    "tailleFichier": 524288,
    "tailleFormatee": "512.0 Ko",
    "dateUpload": "2024-08-01",
    "dateExpiration": "2025-08-31",
    "valide": true,
    "expire": false,
    "joursAvantExpiration": 340,
    "expireBientot": false
  }
]
```

#### POST `/api/etudiant/documents/upload`
Upload un nouveau document.

**Corps de la requête (multipart/form-data) :**
- `fichier` : Le fichier à uploader
- `type` : Type de document (CARTE_ETUDIANT, PIECE_IDENTITE, etc.)
- `nom` : Nom du document (optionnel)
- `dateExpiration` : Date d'expiration (optionnel)

#### GET `/api/etudiant/documents/{documentId}/download`
Télécharge un document.

#### DELETE `/api/etudiant/documents/{documentId}`
Supprime un document.

#### GET `/api/etudiant/documents/requis`
Liste les documents requis et leur statut.

**Réponse :**
```json
[
  {
    "type": "CARTE_ETUDIANT",
    "typeLibelle": "Carte étudiante",
    "obligatoire": true,
    "fourni": true,
    "valide": true,
    "documentId": 1
  },
  {
    "type": "PIECE_IDENTITE",
    "typeLibelle": "Pièce d'identité",
    "obligatoire": true,
    "fourni": false,
    "valide": false,
    "documentId": null
  }
]
```

---

## Types et Énumérations

### StatutDemandeEnum
- `EN_ATTENTE` : En attente de traitement
- `EN_TRAITEMENT` : En cours d'examen
- `EN_COURS_TRAITEMENT` : En cours de traitement
- `APPROUVEE` : Demande approuvée
- `REJETEE` : Demande rejetée
- `ANNULEE` : Demande annulée
- `EXPIREE` : Demande expirée

### StatutIncidentEnum
- `OUVERT` : Signalé, en attente de prise en charge
- `EN_COURS` : En cours de traitement
- `RESOLU` : Résolu
- `FERME` : Fermé
- `ANNULE` : Annulé

### StatutPaiementEnum
- `EN_ATTENTE` : Paiement en attente
- `PAYE` : Payé
- `EN_RETARD` : En retard

### TypeDocumentEnum
- `CARTE_ETUDIANT` : Carte étudiante
- `PIECE_IDENTITE` : Pièce d'identité
- `CERTIFICAT_SCOLARITE` : Certificat de scolarité
- `ATTESTATION_BOURSE` : Attestation de bourse
- `PHOTO_IDENTITE` : Photo d'identité
- `CONTRAT_LOCATION` : Contrat de location
- `RECU_PAIEMENT` : Reçu de paiement
- `PHOTO_LOGEMENT` : Photo de logement
- `JUSTIFICATIF_DOMICILE` : Justificatif de domicile
- `AUTRE` : Autre document

### TypeNotificationEnum
- `ATTRIBUTION_LOGEMENT` : Attribution de logement
- `DEMANDE_APPROUVEE` : Demande approuvée
- `DEMANDE_REJETEE` : Demande rejetée
- `RAPPEL_PAIEMENT` : Rappel de paiement
- `INCIDENT_RESOLU` : Incident résolu
- `REPONSE_INCIDENT` : Réponse à un incident
- `FIN_CONTRAT_PROCHE` : Fin de contrat proche
- `MAINTENANCE_PLANIFIEE` : Maintenance planifiée
- `MAINTENANCE_PROGRAMMEE` : Maintenance programmée
- `MESSAGE_GESTIONNAIRE` : Message du gestionnaire
- `INFORMATION_GENERALE` : Information générale
- `DOCUMENT_EXPIRE` : Document expiré
- `BIENVENUE` : Message de bienvenue
- `ALERTE_SYSTEME` : Alerte système

---

## Sécurité

Tous les endpoints du module étudiant sont protégés par :
- Authentification JWT obligatoire
- Rôle `ETUDIANT` requis (`@PreAuthorize("hasRole('ETUDIANT')")`)

Les étudiants ne peuvent accéder qu'à leurs propres données.

---

## Test avec Swagger

1. Accéder à Swagger UI : `http://localhost:8081/swagger-ui.html`
2. S'authentifier via `/api/auth/login` avec un compte étudiant
3. Copier le token JWT de la réponse
4. Cliquer sur "Authorize" et entrer : `Bearer <votre_token>`
5. Tester les endpoints du module "Étudiant"

---

## Exemples d'utilisation avec cURL

### Récupérer son profil
```bash
curl -X GET "http://localhost:8081/api/etudiant/profil" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

### Rechercher des logements
```bash
curl -X POST "http://localhost:8081/api/etudiant/logements/recherche" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "typeLogement": "CHAMBRE_SIMPLE",
    "loyerMax": 50000,
    "ville": "Dakar"
  }'
```

### Soumettre une demande de logement
```bash
curl -X POST "http://localhost:8081/api/etudiant/demandes" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "logementId": 1,
    "message": "Je souhaite ce logement pour l année universitaire.",
    "dateEntreeSouhaitee": "2024-09-01"
  }'
```

### Signaler un incident
```bash
curl -X POST "http://localhost:8081/api/etudiant/incidents" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "titre": "Problème électrique",
    "description": "La prise ne fonctionne plus",
    "typeIncident": "ELECTRICITE",
    "priorite": "MOYENNE"
  }'
```
