# SDLE Backend — CI Pipeline

## Description
Backend de l'application SDLE — Service d'authentification basé sur **Spring Boot 4** (Java 21) avec **PostgreSQL**.

## Technologies
- Java 21
- Spring Boot 4.0.1
- PostgreSQL 16
- Maven 3.9
- Docker

## Architecture CI/CD
Ce repository contient le pipeline **CI (Continuous Integration)** :

```
Pipeline Jenkins :
├── Checkout du code
├── Build (mvn compile)
├── Tests unitaires (mvn test)
├── Scan de sécurité (Trivy - code source)
├── Package (mvn package)
├── Build image Docker
├── Scan de sécurité (Trivy - image Docker)
└── Push vers Docker Hub
```

## Image Docker
- **Docker Hub** : `yadex34/sdle-backend`
- **Port exposé** : `8081`

## Variables d'environnement
| Variable | Description |
|----------|-------------|
| `SPRING_DATASOURCE_URL` | URL de connexion PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Utilisateur BDD |
| `SPRING_DATASOURCE_PASSWORD` | Mot de passe BDD |
| `JWT_SECRET` | Clé secrète JWT |
| `JWT_EXPIRATION` | Durée de vie du token (ms) |

## Lancer en local
```bash
# Avec Docker Compose
docker compose up -d

# Sans Docker
mvn spring-boot:run
```

## Prérequis Jenkins
- Plugin Docker Pipeline
- Credentials Docker Hub (`dockerhub-credentials`)
- Maven configuré (`Maven-3.9`)
- Trivy installé sur l'agent
