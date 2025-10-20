# GenomeBank API 🧬

> A comprehensive REST API for managing genomic data, including species, genomes, chromosomes, genes, and their biological functions.

Developed for **Breaze Laboratories** - A biotechnology and bioinformatics platform for genomic data management.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Authentication & Authorization](#authentication--authorization)
- [Database Schema](#database-schema)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Contributors](#contributors)

---

## Overview

GenomeBank API is a bioinformatics platform that allows researchers to:
- Store and manage genomic information for multiple species
- Track genome assemblies and their versions
- Organize chromosomal data with DNA sequences
- Catalog genes with their positions and sequences
- Associate genes with biological functions (Gene Ontology)
- Perform genomic analysis (sequence statistics, gene ranges)

The system implements role-based access control (RBAC) with two main roles:
- **ADMIN**: Full CRUD operations on all resources
- **USER**: Read-only access to genomic data

---

## Features

### Core Functionality
- **Species Management**: Register and manage biological species
- **Genome Versioning**: Track multiple genome assemblies per species
- **Chromosome Data**: Store chromosomal information with DNA sequences
- **Gene Cataloging**: Manage genes with positional and sequence data
- **Function Annotation**: Associate genes with biological functions (GO terms)
- **Sequence Management**: Store and retrieve DNA sequences efficiently

### Advanced Features
- **Gene Range Queries**: Find genes within specific genomic regions
- **Sequence Statistics**: Calculate GC content and nucleotide composition
- **Flexible Filtering**: Query by species, genome, chromosome, symbol, code, category
- **Evidence Tracking**: Track evidence types for gene-function associations

### Security & Architecture
- **JWT Authentication**: Secure token-based authentication
- **Role-Based Access Control**: ADMIN and USER roles
- **DTO Pattern**: Clean separation between entities and API responses
- **Service Layer**: Business logic isolation
- **Validation**: Input validation with Jakarta Bean Validation
- **Exception Handling**: Proper HTTP status codes and error messages

---

## Tech Stack

### Backend
- **Java 17+**
- **Spring Boot 3.x**
- **Spring Security 6.x** (JWT-based authentication)
- **Spring Data JPA** (ORM)
- **Hibernate** (JPA implementation)

### Database
- **MySQL 8.0+** (Primary database)

### Security
- **jjwt (Java JWT)** - JWT token generation and validation
- **BCrypt** - Password encryption

### Build & Development
- **Maven** - Dependency management
- **Lombok** - Boilerplate code reduction
- **Jakarta Validation** - Bean validation

---

## Architecture

### Layered Architecture

```
┌─────────────────────────────────────┐
│         Controllers                 │  ← REST Endpoints
├─────────────────────────────────────┤
│         DTOs (In/Out/Update)        │  ← Data Transfer Objects
├─────────────────────────────────────┤
│         Services (Interfaces)       │  ← Business Logic Interface
├─────────────────────────────────────┤
│         Services (Impl)             │  ← Business Logic Implementation
├─────────────────────────────────────┤
│         Repositories                │  ← Data Access Layer
├─────────────────────────────────────┤
│         Entities                    │  ← JPA Entities
├─────────────────────────────────────┤
│         Database (MySQL)            │  ← Persistence Layer
└─────────────────────────────────────┘
```

### Security Flow

```
Request → JWT Filter → Validate Token → Extract Roles → 
Controller (@PreAuthorize) → Service → Repository → Database
```

---

## Getting Started

### Prerequisites

- Java JDK 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Postman (for API testing)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/genomebank-api.git
   cd genomebank-api
   ```

2. **Create MySQL database**
   ```sql
   CREATE DATABASE genomebank;
   USE genomebank;
   ```

3. **Configure application.properties**
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:mysql://localhost:3306/genomebank
   spring.datasource.username=root
   spring.datasource.password=your_password
   server.servlet.context-path=/genomebank_db
   spring.jpa.show-sql=true
   
   # JWT Configuration
   jwt.secret=MySuperSecretKeyForJWTs1234567890!@#
   jwt.exp-min=60
   ```

4. **Install dependencies**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   The API will be available at: `http://localhost:8080`

6. **Initialize roles (First time only)**
   ```sql
   INSERT INTO rol (nombre) VALUES ('USER'), ('ADMIN');
   ```

---

## API Documentation

### Base URL
```
http://localhost:8080
```

### Authentication Endpoints

#### Register a New User
```http
POST /auth/register
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123",
  "roles": ["ADMIN"]  // Optional, defaults to ["USER"]
}

Response: 201 Created
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9...",
  "token_type": "Bearer",
  "roles": ["ADMIN"]
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

Response: 200 OK
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9...",
  "token_type": "Bearer",
  "roles": ["ADMIN"]
}
```

---

### Species Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/species` | Yes | List all species |
| GET | `/species/{id}` | Yes | Get species by ID |
| POST | `/species` | ADMIN | Create species |
| PUT | `/species/{id}` | ADMIN | Update species |
| DELETE | `/species/{id}` | ADMIN | Delete species |

**Example: Create Species**
```http
POST /species
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "scientificName": "Drosophila melanogaster",
  "commonName": "Fruit fly",
  "description": "Common fruit fly used in genetic research"
}
```

---

### Genome Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/genomes` | Yes | List all genomes |
| GET | `/genomes?speciesId={id}` | Yes | Filter by species |
| GET | `/genomes/{id}` | Yes | Get genome by ID |
| POST | `/genomes` | ADMIN | Create genome |
| PUT | `/genomes/{id}` | ADMIN | Update genome |
| DELETE | `/genomes/{id}` | ADMIN | Delete genome |

**Example: Create Genome**
```http
POST /genomes
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "speciesId": 1,
  "version": "BDGP6.32",
  "description": "Drosophila melanogaster genome assembly version 6.32"
}
```

---

### Chromosome Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/chromosomes` | Yes | List all chromosomes |
| GET | `/chromosomes?genomeId={id}` | Yes | Filter by genome |
| GET | `/chromosomes/{id}` | Yes | Get chromosome by ID |
| POST | `/chromosomes` | ADMIN | Create chromosome |
| PUT | `/chromosomes/{id}` | ADMIN | Update chromosome |
| DELETE | `/chromosomes/{id}` | ADMIN | Delete chromosome |
| GET | `/chromosomes/{id}/sequence` | Yes | Get full sequence |
| GET | `/chromosomes/{id}/sequence/range?start=X&end=Y` | Yes | Get sequence range |
| PUT | `/chromosomes/{id}/sequence` | ADMIN | Update sequence |

**Example: Create Chromosome**
```http
POST /chromosomes
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "genomeId": 1,
  "name": "2L",
  "length": 150,
  "sequence": "ACGTACGTACGT..."
}
```

---

### Gene Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/genes` | Yes | List all genes |
| GET | `/genes?chromosomeId={id}` | Yes | Filter by chromosome |
| GET | `/genes?symbol={symbol}` | Yes | Search by symbol |
| GET | `/genes?chromosomeId={id}&start={x}&end={y}` | Yes | Genes in range |
| GET | `/genes/{id}` | Yes | Get gene by ID |
| POST | `/genes` | ADMIN | Create gene |
| PUT | `/genes/{id}` | ADMIN | Update gene |
| DELETE | `/genes/{id}` | ADMIN | Delete gene |
| GET | `/genes/{id}/sequence` | Yes | Get gene sequence |
| PUT | `/genes/{id}/sequence` | ADMIN | Update sequence |

**Example: Create Gene**
```http
POST /genes
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "chromosomeId": 1,
  "symbol": "BRCA1",
  "startPosition": 100,
  "endPosition": 200,
  "strand": "+",
  "sequence": "ACGTACGT..."
}
```

---

### Function Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/functions` | Yes | List all functions |
| GET | `/functions?code={code}` | Yes | Search by code |
| GET | `/functions?category={cat}` | Yes | Filter by category |
| GET | `/functions/{id}` | Yes | Get function by ID |
| POST | `/functions` | ADMIN | Create function |
| PUT | `/functions/{id}` | ADMIN | Update function |
| DELETE | `/functions/{id}` | ADMIN | Delete function |

**Categories:** `BP` (Biological Process), `MF` (Molecular Function), `CC` (Cellular Component)

**Example: Create Function**
```http
POST /functions
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "code": "GO0003700",
  "name": "DNA-binding transcription factor activity",
  "category": "MF",
  "description": "Molecular function related to transcription"
}
```

---

### Gene-Function Association Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/genes/{id}/functions` | Yes | List functions for gene |
| POST | `/genes/{id}/functions/{functionId}` | ADMIN | Associate function |
| DELETE | `/genes/{id}/functions/{functionId}` | ADMIN | Remove association |

**Example: Associate Function to Gene**
```http
POST /genes/1/functions/1
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "evidence": "experimental"
}
```

---

### Analysis Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/analysis/genes?chromosomeId={id}&start={x}&end={y}` | Yes | Genes in range |
| GET | `/analysis/sequence/stats?chromosomeId={id}` | Yes | Sequence statistics |

**Example Response: Sequence Statistics**
```json
{
  "chromosomeId": 1,
  "chromosomeName": "2L",
  "sequenceLength": 150,
  "geneCount": 4,
  "gcPercentage": 50.0,
  "aCount": 37,
  "cCount": 38,
  "gCount": 37,
  "tCount": 38,
  "nCount": 0
}
```

---

## Authentication & Authorization

### JWT Token Structure

The API uses JWT (JSON Web Tokens) for authentication. Each token contains:
- **Subject**: Username
- **Roles**: Array of user roles
- **Issued At**: Token creation timestamp
- **Expiration**: Token expiry time (60 minutes by default)

### Using Tokens

Include the token in the `Authorization` header:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Role Permissions

| Role | Permissions |
|------|-------------|
| **ADMIN** | Full CRUD on all resources |
| **USER** | Read-only access to all resources |

### Protected Endpoints

- **Public**: `/auth/login`, `/auth/register`
- **Authenticated**: All GET endpoints (both roles)
- **ADMIN Only**: All POST, PUT, DELETE endpoints

---

## Database Schema

### Entity Relationships

```
Species (1) ────→ (N) Genomes
Genomes (1) ────→ (N) Chromosomes
Chromosomes (1) ────→ (N) Genes
Genes (N) ────→ (N) Functions (via GeneFunction junction table)
Users (N) ────→ (N) Roles (via usuario_rol junction table)
```

### Key Tables

#### species
- `species_id` (PK)
- `scientific_name` (UNIQUE)
- `common_name`
- `description`
- `created_at`

#### genomes
- `genome_id` (PK)
- `species_id` (FK)
- `version`
- `description`
- `created_at`

#### chromosomes
- `chromosome_id` (PK)
- `genome_id` (FK)
- `name`
- `length`
- `sequence` (TEXT)
- `created_at`

#### genes
- `gene_id` (PK)
- `chromosome_id` (FK)
- `symbol`
- `start_position`
- `end_position`
- `strand` ('+' or '-')
- `sequence` (TEXT)
- `created_at`

#### functions
- `function_id` (PK)
- `code` (UNIQUE)
- `name`
- `category` (BP/MF/CC)
- `description`
- `created_at`

#### gene_function (Junction Table)
- `gene_id` (PK, FK)
- `function_id` (PK, FK)
- `evidence`
- `created_at`

#### users
- `user_id` (PK)
- `username` (UNIQUE)
- `email` (UNIQUE)
- `password` (BCrypt encrypted)
- `activo` (Boolean)
- `created_at`

#### rol
- `id` (PK)
- `nombre` (UNIQUE)

---

## Testing

### Using Postman

1. **Import the collection** (if provided) or create requests manually
2. **Register an ADMIN user**
   ```http
   POST /auth/register
   {
     "username": "admin",
     "password": "admin123",
     "roles": ["ADMIN"]
   }
   ```
3. **Save the access_token** from the response
4. **Add token to requests**
   - Go to Authorization tab
   - Select "Bearer Token"
   - Paste your token
5. **Test endpoints** in this order:
   - Species → Genomes → Chromosomes → Genes → Functions → Associations

### Sample Test Data

See the [Test Data Guide](docs/TEST_DATA.md) for comprehensive sample data including:
- 7 species (Drosophila, Human, Mouse, etc.)
- 10 genomes (multiple versions)
- 22 chromosomes (distributed across genomes)
- 13 genes (including BRCA1, TP53, eve, etc.)
- 15 functions (5 BP, 5 MF, 5 CC)

### Validation Rules

- Chromosome `length` must match `sequence` length
- Gene positions must be within chromosome bounds
- Gene `startPosition` < `endPosition`
- Gene sequence length = `endPosition - startPosition`
- Strand must be '+' or '-'
- Function codes must be unique
- Sequence must contain only A, C, G, T, N characters

---

## Project Structure

```
genomebank/
├── src/
│   ├── main/
│   │   ├── java/com/backEnd/genomebank/
│   │   │   ├── auth/                    # Authentication & Security
│   │   │   │   ├── AuthConfig.java
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── JwtAuthFilter.java
│   │   │   │   ├── JwtService.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controllers/             # REST Controllers
│   │   │   │   ├── SpeciesController.java
│   │   │   │   ├── GenomeController.java
│   │   │   │   ├── ChromosomeController.java
│   │   │   │   ├── GeneController.java
│   │   │   │   ├── FunctionController.java
│   │   │   │   ├── GeneFunctionController.java
│   │   │   │   └── AnalysisController.java
│   │   │   ├── dto/                     # Data Transfer Objects
│   │   │   │   ├── species/
│   │   │   │   │   ├── SpeciesInDTO.java
│   │   │   │   │   ├── SpeciesOutDTO.java
│   │   │   │   │   └── SpeciesUpdateDTO.java
│   │   │   │   ├── genome/
│   │   │   │   ├── chromosome/
│   │   │   │   ├── gene/
│   │   │   │   ├── function/
│   │   │   │   ├── genefunction/
│   │   │   │   └── analysis/
│   │   │   ├── entities/                # JPA Entities
│   │   │   │   ├── Species.java
│   │   │   │   ├── Genome.java
│   │   │   │   ├── Chromosome.java
│   │   │   │   ├── Gene.java
│   │   │   │   ├── Function.java
│   │   │   │   ├── GeneFunction.java
│   │   │   │   ├── GeneFunctionId.java
│   │   │   │   ├── User.java
│   │   │   │   └── Rol.java
│   │   │   ├── repositories/            # Data Access Layer
│   │   │   │   ├── SpeciesRepository.java
│   │   │   │   ├── GenomeRepository.java
│   │   │   │   ├── ChromosomeRepository.java
│   │   │   │   ├── GeneRepository.java
│   │   │   │   ├── FunctionRepository.java
│   │   │   │   ├── GeneFunctionRepository.java
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── RolRepository.java
│   │   │   └── services/                # Business Logic
│   │   │       ├── ISpeciesService.java
│   │   │       ├── IGenomeService.java
│   │   │       ├── IChromosomeService.java
│   │   │       ├── IGeneService.java
│   │   │       ├── IFunctionService.java
│   │   │       ├── IGeneFunctionService.java
│   │   │       ├── IAnalysisService.java
│   │   │       └── impl/                # Service Implementations
│   │   │           ├── SpeciesServiceImpl.java
│   │   │           ├── GenomeServiceImpl.java
│   │   │           ├── ChromosomeServiceImpl.java
│   │   │           ├── GeneServiceImpl.java
│   │   │           ├── FunctionServiceImpl.java
│   │   │           ├── GeneFunctionServiceImpl.java
│   │   │           └── AnalysisServiceImpl.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
└── pom.xml
```

---

## Configuration

### application.properties

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/genomebank?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# JWT Configuration
jwt.secret=MySuperSecretKeyForJWTs1234567890!@#
jwt.exp-min=60

# Logging
logging.level.org.springframework.security=DEBUG
logging.level.com.backEnd.genomebank=DEBUG
```

### Environment Variables (Optional)

For production, use environment variables:
```bash
export DB_URL=jdbc:mysql://localhost:3306/genomebank
export DB_USERNAME=root
export DB_PASSWORD=secure_password
export JWT_SECRET=your_production_secret_key_here
export JWT_EXPIRATION=60
```

---

## Common Issues & Solutions

### Issue 1: "Usuario no encontrado" on login
**Solution**: Ensure User entity field name matches repository method (`username` vs `name`)

### Issue 2: 403 Forbidden
**Solution**: Verify you're using an ADMIN token for protected endpoints

### Issue 3: "Length must match sequence length"
**Solution**: Ensure `length` field equals actual sequence string length

### Issue 4: "Gene positions exceed chromosome length"
**Solution**: Verify gene `endPosition` ≤ chromosome `length`

### Issue 5: JWT Token Expired
**Solution**: Login again to get a new token (tokens expire after 60 minutes by default)

---

## API Status Codes

| Code | Description |
|------|-------------|
| 200 | OK - Request successful |
| 201 | Created - Resource created successfully |
| 204 | No Content - Deletion successful |
| 400 | Bad Request - Validation error |
| 401 | Unauthorized - Missing or invalid token |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource doesn't exist |
| 409 | Conflict - Duplicate resource |

---

## Best Practices

### Security
- Never commit `application.properties` with real credentials
- Use environment variables for sensitive data
- Rotate JWT secrets regularly
- Implement rate limiting for production

### Data Integrity
- Always validate sequence lengths match positions
- Use transactions for related operations
- Implement cascade deletes carefully
- Back up database regularly

### API Usage
- Use DTOs, never expose entities directly
- Implement pagination for large datasets
- Cache frequently accessed data
- Use proper HTTP methods (GET, POST, PUT, DELETE)

---

## 👥 Contributors

- **Santiago Duque Robledo** - Initial work and development
- **Jeronimo Narvaez Montoya** - Creation of entities, repositories and some Interfaces
- **Cesar David Arias Posada** - Creation of the sql database and its relations

---

## License

This project is developed for **Breaze Laboratories** as part of a biotechnology and bioinformatics initiative.

---

**Version:** 1.0.0  
**Last Updated:** October 2025  
**Status:**  Production Ready

---
