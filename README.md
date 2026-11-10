# EduVault — Java Desktop Suite

EduVault combines authentication, an encrypted credential vault, task management, an in-memory CGPA calculator, and a consent-based blood donor directory.

## Requirements
- JDK 17 or newer
- Maven 3.8+
- MySQL 8+

## Database setup
1. Start MySQL.
2. Open `database/eduvault.sql` in MySQL Workbench or phpMyAdmin and execute it.
3. Configure database access using environment variables:
   - `EDUVAULT_DB_URL` (default: `jdbc:mysql://localhost:3306/eduvault?serverTimezone=UTC`)
   - `EDUVAULT_DB_USER` (default: `root`)
   - `EDUVAULT_DB_PASSWORD` (default: empty)

## OTP Verification
During registration, EduVault generates a secure 6-digit OTP code directly on screen in the verification dialog (and logs it in the system console) with a 5-minute expiry timer. Once verified, the user account is created in MySQL.

## AES key setup
The vault requires a separate 32-byte AES key. Generate one with this Java snippet:
```java
byte[] key = new byte[32];
new java.security.SecureRandom().nextBytes(key);
System.out.println(java.util.Base64.getEncoder().encodeToString(key));
```
Set the printed value as `EDUVAULT_AES_KEY`. Do not put the key in source code, Git, or the database. Back it up securely; losing it means saved vault secrets cannot be decrypted.

## Run
From the project root:
```bash
mvn clean compile
mvn exec:java
```
You can also import the Maven project into IntelliJ IDEA, NetBeans, or VS Code and run `gui.Main`.

## Package map
- `gui`: Java Swing user interface
- `controller`: coordinates UI requests
- `service`: business calculations
- `dao`: SQL persistence
- `model`: data classes
- `util`: hashing, encryption, validation
- `config`: JDBC connection

## Security and limitations
Passwords use salted PBKDF2-HMAC-SHA256 hashes. Vault secrets use AES-GCM authenticated encryption. User-owned credentials and tasks are queried by user ID. Donor contact information is only returned when the donor opted in to sharing. This is an educational starter project; add stronger key management, automated tests, logging, rate limiting, account recovery, and production hardening before real-world deployment.

The CGPA calculator currently calculates from courses entered in the current session. `CourseDAO` is included for future persistent course management.
