# Bank Transfer Microservice POC

A Backend Transfer Microservice Proof of Concept built with **Java 25** and **Spring Boot 4.0**.

## Project Overview

This POC demonstrates a fully functional banking transfer microservice capable of handling:
- Account registration and management
- Session-based authentication
- Intra-bank transfers (within the same bank)
- Inter-bank transfers (to external banks via simulated API integration)
- Name enquiry verification before transfers

## Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 25 | Programming Language |
| Spring Boot | 4.0.2 | Application Framework |
| Spring Data JPA | 4.x | Data Persistence |
| MySQL | 8.x | Database |
| Flyway | Latest | Database Migrations |
| Lombok | Latest | Boilerplate Reduction |
| Maven | 3.x | Build Tool |

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        REST API Layer                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │   Account   │  │    Auth     │  │       Transfer          │  │
│  │  Controller │  │  Controller │  │       Controller        │  │
│  └──────┬──────┘  └──────┬──────┘  └───────────┬─────────────┘  │
├─────────┼────────────────┼─────────────────────┼────────────────┤
│         │                │                     │                │
│  ┌──────▼──────┐  ┌──────▼──────┐  ┌───────────▼─────────────┐  │
│  │   Account   │  │    Auth     │  │       Transfer          │  │
│  │   Service   │  │   Service   │  │       Service           │  │
│  └──────┬──────┘  └─────────────┘  └───────────┬─────────────┘  │
│         │                                      │                │
│         │         ┌────────────────────────────┤                │
│         │         │                            │                │
│  ┌──────▼─────────▼──────┐          ┌──────────▼──────────┐     │
│  │   Account Repository  │          │  External Bank API  │     │
│  │      (JPA/MySQL)      │          │     (Simulated)     │     │
│  └───────────────────────┘          └─────────────────────┘     │
└─────────────────────────────────────────────────────────────────┘
```

## Project Structure

```
src/main/java/com/bank_app/bank_app/
├── account/                    # Account Module
│   ├── dto/
│   │   ├── requests/          # CreateAccountRequest, UpdateBalanceRequest
│   │   └── responses/         # AccountResponse
│   ├── impl/                  # AccountServiceImpl
│   ├── Account.java           # Entity
│   ├── AccountController.java
│   ├── AccountRepository.java
│   └── AccountService.java
│
├── auth/                       # Authentication Module
│   ├── dto/                   # LoginRequest, AuthResponse
│   ├── impl/                  # AuthServiceImpl
│   ├── AuthController.java
│   └── AuthService.java
│
├── transfer/                   # Transfer Module
│   ├── dto/
│   │   ├── requests/          # TransferRequest, NameEnquiryRequest
│   │   └── responses/         # TransferResponse, NameEnquiryResponse, BankDTO
│   ├── impl/                  # TransferServiceImpl
│   ├── Transfer.java          # Entity
│   ├── TransferController.java
│   ├── TransferRepository.java
│   ├── TransferService.java
│   ├── TransferType.java      # Enum: INTRA_BANK, INTER_BANK
│   ├── TransferStatus.java    # Enum: PENDING, COMPLETED, FAILED
│   └── ExternalBankService.java
│
├── config/                     # Configuration
│   ├── APIResponse.java       # Standardized API response wrapper
│   ├── PasswordConfig.java    # BCrypt password encoder
│   ├── RestClientConfig.java  # RestTemplate bean
│   ├── SessionConfig.java
│   └── SessionAuthFilter.java # Authentication filter
│
├── exception/                  # Exception Handling
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
│
└── external/                   # External API Demo
    ├── ExternalApiController.java
    └── ExternalApiService.java
```

## Features

### 1. Account Management
- Create bank accounts with BVN validation
- Retrieve account details
- Credit/Debit operations with balance validation
- Name enquiry functionality

### 2. Authentication
- Session-based authentication (30-minute timeout)
- Secure password hashing with BCrypt
- Protected endpoints via SessionAuthFilter

### 3. Transfer Operations

#### Intra-Bank Transfer
Transfers between accounts within the same bank with:
- Real-time balance updates
- Transaction reference generation
- Pessimistic locking to prevent race conditions

#### Inter-Bank Transfer
Transfers to external banks featuring:
- Bank code validation
- Name enquiry before transfer
- Simulated external API integration
- Automatic reversal on failure

### 4. External API Integration
Demonstrates real HTTP calls using RestTemplate with JSONPlaceholder API.

## API Endpoints

### Account Endpoints
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/accounts` | Create new account | No |
| GET | `/accounts` | Get all accounts | Yes |
| GET | `/accounts/{accountNumber}` | Get account by number | Yes |
| PATCH | `/accounts/{accountNumber}/balance` | Update balance | Yes |
| GET | `/accounts/{accountNumber}/name-enquiry` | Get account name | Yes |

### Authentication Endpoints
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/login` | Login | No |
| POST | `/auth/logout` | Logout | Yes |
| GET | `/auth/me` | Get current user | Yes |

### Transfer Endpoints
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/transfer` | Initiate transfer | Yes |
| GET | `/transfer/reference/{refId}` | Get transfer by reference | Yes |
| GET | `/transfer/history/{accountNumber}` | Get transfer history | Yes |
| POST | `/transfer/name-enquiry` | Verify account | Yes |
| GET | `/transfer/banks` | List supported banks | Yes |

### External API Demo Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/external/posts` | Fetch all posts |
| GET | `/external/posts/{id}` | Fetch single post |
| POST | `/external/posts` | Create post |

## API Response Format

All endpoints return a standardized response:

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { }
}
```

## Sample Requests

### Create Account
```bash
POST /accounts
Content-Type: application/json

{
  "accountName": "John Doe",
  "bvn": "12345678901",
  "password": "securePassword123"
}
```





## Setup & Installation

### Prerequisites
- Java 25
- Maven 3.x
- MySQL 8.x

### Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd bank_app
   ```

2. **Configure database**

   Create a MySQL database:
   ```sql
   CREATE DATABASE bank_db;
   ```

3. **Update application.properties**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/bank_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the API**
   ```
   http://localhost:8080
   ```

## Key Technical Implementations

### 1. Pessimistic Locking
Prevents race conditions during concurrent balance updates:
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
Optional<Account> findByAccountNumber(Long accountNumber);
```

### 2. Global Exception Handling
Centralized error handling with consistent responses:
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse<Object>> handleNotFoundException(...) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(APIResponse.error(ex.getMessage()));
    }
}
```

## Testing the External Bank Simulation

### Valid Account Numbers (10 digits, ends in 1-8)
- `1234567891` - Success
- `1234567892` - Success

### Invalid Scenarios
- `1234567890` - Account not found (ends in 0)
- `1234567899` - Account inactive (ends in 9)
- `123456789` - Invalid format (9 digits)

## Future Enhancements

- [ ] JWT-based authentication
- [ ] Integration with real banking APIs (Paystack, Flutterwave)
- [ ] Transaction history with pagination
- [ ] Email/SMS notifications
- [ ] Rate limiting
- [ ] API documentation with Swagger/OpenAPI
- [ ] Unit and integration tests
- [ ] Docker containerization

## Author

**Nafisat Sanusi**

---

*This project was developed as a Proof of Concept for the Backend Transfer Microservice KPI deliverable.*
