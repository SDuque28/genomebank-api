# GenomeBank API - HELP ME

> A comprehensive troubleshooting guide for the most common issues you'll encounter while developing and testing the GenomeBank API.

---

## Table of Contents

1. [Application Won't Start](#application-wont-start)
2. [Database Connection Issues](#database-connection-issues)
3. [Authentication Problems](#authentication-problems)
4. [Authorization Errors (403 Forbidden)](#authorization-errors-403-forbidden)
5. [Entity and Repository Issues](#entity-and-repository-issues)
6. [Validation Errors](#validation-errors)
7. [Sequence Management Issues](#sequence-management-issues)
8. [Relationship and Foreign Key Errors](#relationship-and-foreign-key-errors)
9. [JWT Token Problems](#jwt-token-problems)
10. [Postman Testing Issues](#postman-testing-issues)
11. [DTO Mapping Errors](#dto-mapping-errors)
12. [Cascade and Deletion Issues](#cascade-and-deletion-issues)

---

##  Application Won't Start

### Issue 1: "Failed to configure a DataSource"

*Error Message:*

Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.


*Cause:* Missing or incorrect database configuration in application.properties

*Solution:*

1. Open src/main/resources/application.properties
2. Add or verify these properties:

properties
spring.datasource.url=jdbc:mysql://localhost:3306/genomebank?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver


3. *Make sure MySQL is running:*
   bash
   # Windows
   net start MySQL80

   # Mac
   brew services start mysql

   # Linux
   sudo systemctl start mysql


4. *Test database connection manually:*
   bash
   mysql -u root -p
   # Enter your password
   CREATE DATABASE genomebank;


---

### Issue 2: "Port 8080 is already in use"

*Error Message:*

Web server failed to start. Port 8080 was already in use.


*Cause:* Another application is using port 8080

*Solution 1 - Change Port:*

Add to application.properties:
properties
server.port=8081


*Solution 2 - Kill the Process:*

bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Mac/Linux
lsof -i :8080
kill -9 <PID>


---

### Issue 3: "Table doesn't exist" after starting

*Error Message:*

Table 'genomebank.species' doesn't exist


*Cause:* hibernate.ddl-auto not set correctly

*Solution:*

In application.properties, set:
properties
spring.jpa.hibernate.ddl-auto=update


*Options:*
- create - Drop and recreate tables (LOSE ALL DATA)
- create-drop - Drop tables when app stops
- update - Update schema (RECOMMENDED for development)
- validate - Only validate schema
- none - Do nothing

*If tables still don't create, check:*
1. Entity classes have @Entity annotation
2. Entity classes are in the correct package
3. No syntax errors in entity definitions

---

### Issue 4: Bean Creation Error

*Error Message:*

Error creating bean with name 'speciesController': Unsatisfied dependency expressed through field 'speciesService'


*Cause:* Missing @Service, @Repository, or @Component annotations

*Solution:*

1. *Check Service Implementation has @Service:*
   java
   @Service  // ← Make sure this is here
   @RequiredArgsConstructor
   public class SpeciesServiceImpl implements ISpeciesService {


2. *Check Repository has @Repository:*
   java
   @Repository  // ← Make sure this is here
   public interface SpeciesRepository extends JpaRepository<Species, Long> {


3. *Check all services in impl package are annotated*

4. *Rebuild project:*
   bash
   mvn clean install


---

## 🗄 Database Connection Issues

### Issue 5: "Access denied for user"

*Error Message:*

Access denied for user 'root'@'localhost' (using password: YES)


*Cause:* Wrong database password

*Solution:*

1. *Reset MySQL root password:*
   bash
   # Stop MySQL
   # Windows: net stop MySQL80
   # Mac: brew services stop mysql
   # Linux: sudo systemctl stop mysql

   # Start in safe mode
   mysqld_safe --skip-grant-tables &

   # Connect without password
   mysql -u root

   # Reset password
   FLUSH PRIVILEGES;
   ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';

   # Restart MySQL normally


2. *Update application.properties with correct password*

---

### Issue 6: "Communications link failure"

*Error Message:*

Communications link failure
The last packet sent successfully to the server was 0 milliseconds ago.


*Cause:* MySQL server is not running

*Solution:*

1. *Start MySQL:*
   bash
   # Windows
   net start MySQL80

   # Mac
   brew services start mysql

   # Linux
   sudo systemctl start mysql


2. *Verify MySQL is running:*
   bash
   mysql -u root -p
   # Should connect successfully


3. *Check MySQL port (default 3306):*
   bash
   netstat -an | grep 3306


---

### Issue 7: "Unknown database 'genomebank'"

*Error Message:*

Unknown database 'genomebank'


*Cause:* Database doesn't exist

*Solution:*

*Option 1 - Auto-create (Add to URL):*
properties
spring.datasource.url=jdbc:mysql://localhost:3306/genomebank?createDatabaseIfNotExist=true


*Option 2 - Create manually:*
sql
mysql -u root -p
CREATE DATABASE genomebank;
USE genomebank;


---

## Authentication Problems

### Issue 8: "Usuario no encontrado" on Login

*Error Message:*
json
{
"error": "Usuario no encontrado"
}


*Cause:* Mismatch between User entity field name and repository method

*The Problem:*
java
// User.java
private String name;  // ← Field is 'name'

// UserRepository.java
Optional<User> findByUsername(String username);  // ← Looking for 'username'


*Solution 1 - Change Entity Field:*

In User.java:
java
@Column(nullable = false, unique = true, length = 100)
private String username;  // ← Changed from 'name' to 'username'

@Override
public String getUsername() {
return username;  // ← Changed from 'name'
}


*Solution 2 - Change Repository Method:*

In UserRepository.java:
java
Optional<User> findByName(String name);  // ← Changed to match entity field


And in AuthConfig.java:
java
@Bean
public UserDetailsService userDetailsService() {
return username -> userRepository.findByName(username)  // ← Changed
.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
}


*Verify with:*
sql
SELECT * FROM users;
-- Check column name: 'name' or 'username'?


---

### Issue 9: Can't Register First User

*Error Message:*

Cannot add or update a child row: a foreign key constraint fails


*Cause:* Roles table is empty

*Solution:*

sql
USE genomebank;

-- Check if roles exist
SELECT * FROM rol;

-- If empty, insert roles
INSERT INTO rol (nombre) VALUES ('USER');
INSERT INTO rol (nombre) VALUES ('ADMIN');

-- Verify
SELECT * FROM rol;


*In AuthController.java*, verify role creation logic:
java
@PostMapping("/register")
@ResponseStatus(HttpStatus.CREATED)
public Map<String, Object> register(@RequestBody RegisterRequest req) {
// ... validation ...

    List<String> roleNames = (req.getRoles() == null || req.getRoles().isEmpty())
            ? List.of("USER")
            : req.getRoles();

    Set<Rol> rolEntities = new HashSet<>();
    for (String roleName : roleNames) {
        Rol rol = rolRepo.findByNombre(roleName).orElseGet(() -> {
            Rol newRol = new Rol();
            newRol.setNombre(roleName);
            return rolRepo.save(newRol);  // ← Creates role if doesn't exist
        });
        rolEntities.add(rol);
    }
    // ...
}


---

### Issue 10: Password Not Encrypting

*Error Message:* Login works with plain text password in database

*Cause:* Password encoder not being used

*Solution:*

In AuthController.java register method:
java
@PostMapping("/register")
public Map<String, Object> register(@RequestBody RegisterRequest req) {
// ...
User user = new User();
user.setUsername(req.getUsername());
user.setPassword(passwordEncoder.encode(req.getPassword()));  // ← MUST encode!
user.setRoles(rolEntities);

    usuarioRepo.save(user);
    // ...
}


*Verify in database:*
sql
SELECT username, password FROM users;
-- Password should look like: $2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
-- NOT like: admin123


---

## Authorization Errors (403 Forbidden)

### Issue 11: 403 Forbidden on POST/PUT/DELETE

*Error Message:*

Status: 403 Forbidden


*Cause:* User doesn't have ADMIN role

*Solution:*

1. *Check your token's role:*
    - Go to https://jwt.io/
    - Paste your token
    - Look in payload for "roles": ["USER"] or "roles": ["ADMIN"]

2. *If role is USER, register/login as ADMIN:*
   json
   POST /auth/register
   {
   "username": "admin",
   "password": "admin123",
   "roles": ["ADMIN"]
   }


3. *Use the ADMIN token for protected endpoints*

4. *Verify user's role in database:*
   sql
   SELECT u.username, r.nombre
   FROM users u
   JOIN usuario_rol ur ON u.user_id = ur.user_id
   JOIN rol r ON ur.rol_id = r.id
   WHERE u.username = 'admin';


---

### Issue 12: 403 on GET Requests

*Error Message:*

Status: 403 Forbidden (even on GET)


*Cause:* SecurityConfig not configured correctly

*Solution:*

In SecurityConfig.java, verify:
java
@Bean
SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
return http
.csrf(csrf -> csrf.disable())
.cors(cors -> cors.configurationSource(corsConfigurationSource()))
.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
.authorizeHttpRequests(auth -> auth
.requestMatchers("/auth/login", "/auth/register").permitAll()  // ← Public
.anyRequest().authenticated()  // ← All others need auth (not ADMIN only)
)
.authenticationProvider(authenticationProvider())
.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
.build();
}


*Controllers should use:*
java
// GET - Any authenticated user
@GetMapping
public ResponseEntity<List<SpeciesOutDTO>> obtenerTodasSpecies() { ... }

// POST/PUT/DELETE - ADMIN only
@PostMapping
@PreAuthorize("hasRole('ADMIN')")  // ← This restricts to ADMIN
public ResponseEntity<SpeciesOutDTO> crearSpecies(...) { ... }


---

### Issue 13: @PreAuthorize Not Working

*Error Message:* Can create/delete with USER role (shouldn't be possible)

*Cause:* @EnableMethodSecurity not added

*Solution:*

In SecurityConfig.java:
java
@Configuration
@EnableMethodSecurity  // ← MUST HAVE THIS!
@RequiredArgsConstructor
public class SecurityConfig {
// ...
}


*Without this annotation, @PreAuthorize is ignored!*

---

## Entity and Repository Issues

### Issue 14: "No property 'xxx' found for type"

*Error Message:*

No property 'username' found for type 'User'


*Cause:* Repository method name doesn't match entity field

*Solution:*

Match repository method to entity field:

java
// User.java
private String name;  // ← Field name

// UserRepository.java
Optional<User> findByName(String name);  // ← Must match exactly


*Naming Convention:*
- findBy + FieldName (capitalized)
- Examples:
    - Field: scientificName → Method: findByScientificName
    - Field: code → Method: findByCode
    - Field: category → Method: findByCategory

---

### Issue 15: LazyInitializationException

*Error Message:*

org.hibernate.LazyInitializationException: could not initialize proxy - no Session


*Cause:* Accessing lazy-loaded relationship outside transaction

*Solution:*

*Option 1 - Use EAGER fetching:*
java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "species_id", nullable = false)
private Species species;


*Option 2 - Add @Transactional:*
java
@Service
@RequiredArgsConstructor
public class GenomeServiceImpl implements IGenomeService {

    @Override
    @Transactional(readOnly = true)  // ← Add this
    public Optional<GenomeOutDTO> obtenerGenomePorId(Long id) {
        return genomeRepository.findById(id)
                .map(this::convertToOutDTO);
    }
}


*Option 3 - Load in DTO conversion (inside @Transactional method):*
java
private GenomeOutDTO convertToOutDTO(Genome genome) {
GenomeOutDTO dto = new GenomeOutDTO();
// Access relationships here (inside transaction)
dto.setSpecies(convertSpeciesToDTO(genome.getSpecies()));
return dto;
}


---

### Issue 16: Embedded ID Not Working

*Error Message:*

Composite-id class must implement Serializable


*Cause:* Composite key class doesn't implement Serializable

*Solution:*

In GeneFunctionId.java:
java
@Data
@Embeddable
public class GeneFunctionId implements Serializable {  // ← Must implement Serializable
private static final long serialVersionUID = 1L;  // ← Add this

    private Long geneId;
    private Long functionId;
    
    // Lombok generates equals() and hashCode()
    // If not using Lombok, you MUST implement them manually
}


*Must also have:*
- equals() method
- hashCode() method

---

##  Validation Errors

### Issue 17: "Length must match sequence length"

*Error Message:*

Status: 400 Bad Request
{
"error": "Length must match sequence length"
}


*Cause:* Chromosome/Gene length field doesn't match actual sequence string length

*Solution:*

java
// WRONG
{
"length": 100,
"sequence": "ACGTACGT"  // ← Only 8 characters, but length says 100
}

// CORRECT
{
"length": 8,
"sequence": "ACGTACGT"  // ← Matches!
}


*Quick Check:*
javascript
// In your text editor or browser console
"ACGTACGT".length  // Returns: 8


*For long sequences, use:*
bash
# Count characters in a file
wc -m sequence.txt

# Or in a string
echo "ACGTACGT" | wc -m


---

### Issue 18: "Gene positions exceed chromosome length"

*Error Message:*

Status: 400 Bad Request
{
"error": "Gene positions exceed chromosome length"
}


*Cause:* Gene endPosition is greater than chromosome length

*Solution:*

java
// If chromosome length is 150:

// WRONG
{
"chromosomeId": 1,
"startPosition": 100,
"endPosition": 200,  // ← 200 > 150 (chromosome length)
"strand": "+"
}

// CORRECT
{
"chromosomeId": 1,
"startPosition": 100,
"endPosition": 140,  // ← 140 < 150 ✓
"strand": "+"
}


*Verify chromosome length:*
http
GET /chromosomes/1
Response: { "length": 150, ... }


---

### Issue 19: "Start position must be less than end position"

*Error Message:*

Status: 400 Bad Request
{
"error": "Start position must be less than end position"
}


*Cause:* Gene startPosition ≥ endPosition

*Solution:*

java
// WRONG
{
"startPosition": 200,
"endPosition": 100  // ← 200 > 100
}

// WRONG
{
"startPosition": 100,
"endPosition": 100  // ← Equal (no length)
}

// CORRECT
{
"startPosition": 100,
"endPosition": 200  // ← 100 < 200 ✓
}


---

### Issue 20: "Strand must be '+' or '-'"

*Error Message:*

Validation failed: strand must be '+' or '-'


*Cause:* Invalid strand value

*Solution:*

java
// WRONG
{
"strand": "positive"  // ← Invalid
}
{
"strand": "1"  // ← Invalid
}
{
"strand": "forward"  // ← Invalid
}

// CORRECT
{
"strand": "+"  // ← Valid
}
{
"strand": "-"  // ← Valid
}


*Note:* Must be a single character string, not a boolean or number.

---

## Sequence Management Issues

### Issue 21: Sequence Returns null

*Error Message:*
json
{
"sequence": null
}


*Cause:* Sequence was never set or saved

*Solution:*

1. *Check if sequence was included when creating:*
   json
   POST /chromosomes
   {
   "genomeId": 1,
   "name": "2L",
   "length": 50,
   "sequence": "ACGTACGT..."  // ← Must include!
   }


2. *Or update sequence after creation:*
   json
   PUT /chromosomes/1/sequence
   {
   "sequence": "ACGTACGT..."
   }


3. *Verify in database:*
   sql
   SELECT name, LENGTH(sequence) as seq_length,
   SUBSTRING(sequence, 1, 50) as first_50_chars
   FROM chromosomes
   WHERE chromosome_id = 1;


---

### Issue 22: "Sequence length must match gene length"

*Error Message:*

Status: 400 Bad Request
{
"error": "Sequence length must match gene length (end - start)"
}


*Cause:* When updating gene sequence, length doesn't match (endPosition - startPosition)

*Solution:*

java
// Gene has: start=100, end=120 (length = 20)

// WRONG
PUT /genes/1/sequence
{
"sequence": "ACGTACGT"  // ← Only 8 characters, need 20
}

// CORRECT
PUT /genes/1/sequence
{
"sequence": "ACGTACGTACGTACGTACGT"  // ← Exactly 20 characters
}


*Calculate required length:*

Required Length = endPosition - startPosition
Example: 120 - 100 = 20 base pairs


---

##  Relationship and Foreign Key Errors

### Issue 23: "Cannot add or update a child row"

*Error Message:*

Cannot add or update a child row: a foreign key constraint fails


*Cause:* Referenced entity doesn't exist

*Solution:*

java
// WRONG - Creating genome with non-existent species
POST /genomes
{
"speciesId": 999,  // ← Species 999 doesn't exist
"version": "1.0"
}

// CORRECT - First verify species exists
GET /species
// Response shows species 1, 2, 3 exist

POST /genomes
{
"speciesId": 1,  // ← Use existing species ID
"version": "1.0"
}


*Always create in order:*
1. Species
2. Genome (needs species)
3. Chromosome (needs genome)
4. Gene (needs chromosome)

---

### Issue 24: "Species not found" when creating Genome

*Error Message:*

Status: 404 Not Found
{
"error": "Species not found"
}


*Cause:* Invalid speciesId in request

*Solution:*

1. *List all species:*
   http
   GET /species
   Response: [
   { "id": 1, "scientificName": "..." },
   { "id": 2, "scientificName": "..." }
   ]


2. *Use correct ID:*
   json
   POST /genomes
   {
   "speciesId": 1,  // ← Use ID from above list
   "version": "1.0",
   "description": "..."
   }


3. *Or create species first:*
   json
   POST /species
   {
   "scientificName": "New species",
   "commonName": "New",
   "description": "..."
   }
   // Save the returned ID


---

### Issue 25: Can't Delete - Has Dependent Records

*Error Message:*

Cannot delete or update a parent row: a foreign key constraint fails


*Cause:* Trying to delete entity that has related records

*Solution:*

*Option 1 - Delete in reverse order:*

1. Delete genes first
2. Then delete chromosome
3. Then delete genome
4. Finally delete species


*Option 2 - Use CASCADE in entities:*

In Species.java:
java
@OneToMany(mappedBy = "species",
cascade = CascadeType.ALL,  // ← Add this
orphanRemoval = true)        // ← And this
private Set<Genome> genomes = new HashSet<>();


*Warning:* CASCADE will delete ALL related records automatically!

---

## JWT Token Problems

### Issue 26: Token Expired

*Error Message:*

Status: 401 Unauthorized
Token expired


*Cause:* JWT token has exceeded expiration time (default 60 minutes)

*Solution:*

1. *Login again to get new token:*
   json
   POST /auth/login
   {
   "username": "admin",
   "password": "admin123"
   }


2. *Copy new access_token and update in Postman*

3. *To increase expiration time (in application.properties):*
   properties
   jwt.exp-min=120  # 2 hours instead of 1


---

### Issue 27: "JWT signature does not match"

*Error Message:*

JWT signature does not match locally computed signature


*Cause:* JWT secret was changed after token was issued

*Solution:*

1. *Check JWT secret in application.properties:*
   properties
   jwt.secret=MySuperSecretKeyForJWTs1234567890!@#


2. *If you changed the secret, ALL TOKENS are invalid*

3. *Login again to get new token with current secret*

4. *Never change JWT secret in production without a migration plan*

---

### Issue 28: "Malformed JWT"

*Error Message:*

Malformed JWT token


*Cause:* Token is corrupted or incomplete

*Solution:*

1. *Check token format:*

   Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWI...
   ^^^^^^ ^^^^^^^^^^^^^^^^^^^^^^^^^^^
   Prefix Must have 3 parts separated by dots


2. *Common mistakes:*
    - Missing "Bearer " prefix
    - Extra spaces: Bearer  eyJ... (two spaces)
    - Token cut off: Bearer eyJhbGc... (incomplete)
    - Wrong quotes: Bearer "eyJ..."

3. *Correct format:*

   Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJv...


---

### Issue 29: Token Not Being Read

*Error Message:*

Status: 401 Unauthorized
Full authentication is required


*Cause:* Authorization header missing or wrong format

*Solution:*

*In Postman:*

1. Go to *Authorization* tab
2. Type: Select Bearer Token
3. Token: Paste token (without "Bearer" prefix)

*OR in Headers tab:*

Key: Authorization
Value: Bearer eyJhbGciOiJIUzI1NiJ9...
^^^^^^ Note the space after Bearer


*Verify in JwtAuthFilter.java:*
java
String header = req.getHeader("Authorization");
if (header == null || !header.startsWith("Bearer ")) {
chain.doFilter(req, res);
return;  // ← No token found
}


---

## Postman Testing Issues

### Issue 30: "Cannot read property 'xxx' of undefined"

*Error Message:* Postman shows error in Tests tab

*Cause:* Trying to access property that doesn't exist in response

*Solution:*

1. *Check actual response first:*
    - Click on response body
    - Verify structure

2. *Example - Safe access in Postman Tests:*
   javascript
   // WRONG
   pm.test("Has access token", function () {
   var jsonData = pm.response.json();
   pm.expect(jsonData.access_token).to.exist;
   });

   // CORRECT
   pm.test("Has access token", function () {
   if (pm.response.code === 200 || pm.response.code === 201) {
   var jsonData = pm.response.json();
   pm.expect(jsonData).to.have.property('access_token');
   }
   });


---

### Issue 31: 400 Bad Request with no message

*Error Message:*

Status: 400 Bad Request
(empty body)


*Cause:* JSON syntax error or wrong Content-Type

*Solution:*

1. *Check JSON syntax:*
    - Use JSON validator (https://jsonlint.com/)
    - Look for missing commas, brackets, quotes

2. *Common JSON errors:*
   json
   // WRONG - Missing comma
   {
   "username": "admin"
   "password": "admin123"
   }

   // WRONG - Trailing comma
   {
   "username": "admin",
   "password": "admin123",
   }

   // CORRECT
   {
   "username": "admin",
   "password": "admin123"
   }


3. *Verify Content-Type header:*

   Content-Type: application/json


---

### Issue 32: Postman Shows HTML Instead of JSON

*Error Message:* Response shows HTML page instead of JSON

*Cause:* Wrong URL or Spring Boot error page

*Solution:*

1. *Check URL is correct:*

   ✓ http://localhost:8080/species
   ✗ http://localhost:8080/api/species  (if /api not configured)


2. *Check application is running:*
    - Look for "Started GenomebankApplication in X seconds" in console

3. *Check for Spring Boot errors:*
    - HTML response usually means 404 or 500 error
    - Look at browser/Postman preview to see error page

4. *Verify endpoint exists:*
   bash
   # Check console logs when app starts:
   Mapped "{[/species],methods=[GET]}" onto public ...


---

## DTO Mapping Errors

### Issue 33: NullPointerException in DTO conversion

*Error Message:*

java.lang.NullPointerException
at SpeciesServiceImpl.convertToOutDTO(SpeciesServiceImpl.java:45)


*Cause:* Trying to access null object in DTO mapping

*Solution:*

java
// WRONG - Can throw NPE
private GenomeOutDTO convertToOutDTO(Genome genome) {
GenomeOutDTO dto = new GenomeOutDTO();
dto.setSpeciesId(genome.getSpecies().getId());  // ← NPE if species is null
return dto;
}

// CORRECT - Null-safe
private GenomeOutDTO convertToOutDTO(Genome genome) {
GenomeOutDTO dto = new GenomeOutDTO();
if (genome.getSpecies() != null) {
SpeciesOutDTO speciesDTO = new SpeciesOutDTO();
speciesDTO.setId(genome.getSpecies().getId());
speciesDTO.setScientificName(genome.getSpecies().getScientificName());
dto.setSpecies(speciesDTO);
}
return dto;
}


---

### Issue 34: Infinite Recursion / StackOverflowError

*Error Message:*

java.lang.StackOverflowError
Could not write JSON: Infinite recursion


*Cause:* Bidirectional relationship in entities causes infinite loop

*Solution:*

*DON'T return entities directly in controllers:*

java
// WRONG - Can cause infinite recursion
@GetMapping("/{id}")
public Species getSpecies(@PathVariable Long id) {
return speciesRepository.findById(id).orElseThrow();  // ← Returns entity
}

// CORRECT - Use DTOs
@GetMapping("/{id}")
public ResponseEntity<SpeciesOutDTO> getSpecies(@PathVariable Long id) {
return speciesService.obtenerSpeciesPorId(id)
.map(ResponseEntity::ok)  // ← Returns DTO
.orElse(ResponseEntity.notFound().build());
}


*In DTOs, don't include circular references:*
java
// SpeciesOutDTO - Don't include full genome objects
public class SpeciesOutDTO {
private Long id;
private String scientificName;
// NO: private List<GenomeOutDTO> genomes;  ← Avoid this
}


---

## Cascade and Deletion Issues

### Issue 35: Orphaned Records After Deletion

*Error Message:* Records remain in database after parent deletion

*Cause:* Missing cascade or orphanRemoval settings

*Solution:*

In parent entity:
java
@OneToMany(mappedBy = "species",
cascade = CascadeType.ALL,      // ← Add this
orphanRemoval = true)           // ← And this
private Set<Genome> genomes = new HashSet<>();
