# Car Rental System - Modules Concept Summary

This document provides a quick reference guide to all concepts used across different modules in the car rental system. Use this for interview preparation and understanding the technical stack.

---

## Table of Contents
1. [Core Concepts Across All Modules](#core-concepts-across-all-modules)
2. [Module-Specific Concepts](#module-specific-concepts)
3. [Common Interview Questions](#common-interview-questions)

---

## Core Concepts Across All Modules

### 1. **Spring Boot Architecture**
- **Layered Architecture**: Controller → Service → Repository → Entity → Database
- **Dependency Injection**: `@RequiredArgsConstructor`, constructor injection
- **Component Scanning**: `@Service`, `@Repository`, `@Controller`, `@RestController`
- **Configuration**: `@Configuration`, `@Bean`

### 2. **JPA/Hibernate**
- **Entity Relationships**: `@OneToOne`, `@ManyToOne`, `@OneToMany`
- **Fetch Types**: `LAZY` (default for @ManyToOne), `EAGER` (default for @OneToMany)
- **Lifecycle Callbacks**: `@PrePersist`, `@PreUpdate`
- **Query Methods**: Method name → SQL query generation
- **Custom Queries**: `@Query` with JPQL

### 3. **Security (Spring Security)**
- **JWT Authentication**: Token-based stateless authentication
- **Role-Based Access Control (RBAC)**: `hasAuthority()`, `hasRole()`
- **SecurityContextHolder**: Thread-local storage for authentication
- **JWT Filter**: Custom filter for token validation

### 4. **Design Patterns**
- **Repository Pattern**: Data access abstraction
- **Service Layer Pattern**: Business logic encapsulation
- **DTO Pattern**: Data transfer objects
- **Strategy Pattern**: Role-based strategies (implicit)
- **State Machine Pattern**: Status transitions

### 5. **Transaction Management**
- **@Transactional**: Declarative transaction management
- **ACID Properties**: Atomicity, Consistency, Isolation, Durability
- **Isolation Levels**: READ_COMMITTED (default)
- **Rollback**: Automatic on exceptions

### 6. **Java 8+ Features**
- **Stream API**: `filter()`, `map()`, `collect()`, `sum()`
- **Method References**: `Payment::getAmount`
- **Optional**: Null-safe operations
- **LocalDate/LocalDateTime**: Date/time handling
- **ChronoUnit**: Date calculations

### 7. **Validation**
- **Jakarta Validation**: `@NotNull`, `@NotBlank`, `@Future`, `@Positive`
- **Service-Level Validation**: Business rule enforcement
- **Custom Validators**: For complex validation

---

## Module-Specific Concepts

### Authentication & User Module
**Key Concepts**:
- **JWT Authentication**: Token-based stateless authentication
- **BCrypt Password Hashing**: Secure password storage with salt
- **Spring Security**: Filter chain, AuthenticationManager, SecurityContextHolder
- **UserDetails Interface**: Spring Security user representation
- **Role-Based Access Control**: ADMIN, VENDOR, CUSTOMER roles
- **User Registration Approval**: PENDING → APPROVED workflow
- **Custom JWT Filter**: Token validation and SecurityContext setup

**Interview Questions**:
- How does JWT authentication work?
- How does BCrypt password hashing work?
- Explain Spring Security filter chain
- What's the difference between authentication and authorization?
- How does SecurityContextHolder work?

---

### Admin Module
**Key Concepts**:
- **Data Aggregation**: Stream API for filtering and summing
- **Statistics Calculation**: Count operations, revenue calculation
- **Role Verification**: Two-level security (SecurityConfig + Controller)
- **DTO Conversion**: Entity to DTO mapping

**Interview Questions**:
- How do you calculate total revenue?
- Why use DTOs instead of entities?
- How do you ensure only admins access admin endpoints?

---

### Booking Module
**Key Concepts**:
- **Date Overlap Algorithm**: Conflict detection
- **ChronoUnit**: Date calculations
- **JPQL Queries**: Custom conflict detection queries
- **State Machine**: Booking status transitions
- **Transaction Management**: Atomic booking creation

**Interview Questions**:
- How do you prevent double-booking?
- Explain the date overlap detection algorithm
- Why use @Transactional in createBooking()?

---

### Payment Module
**Key Concepts**:
- **One-to-One Relationship**: Payment → Booking
- **UUID Generation**: Transaction ID generation
- **Status Synchronization**: Payment status → Booking status
- **Ownership Verification**: User can only pay for own bookings
- **Idempotency**: Prevent duplicate payments

**Interview Questions**:
- How do you enforce one booking → one payment?
- How do payment and booking statuses stay in sync?
- How do you generate transaction IDs?

---

### Vehicle & Vendor Module
**Key Concepts**:
- **Many-to-One Relationship**: Vehicle → Vendor (User)
- **CRUD Operations**: Create, Read, Update, Delete
- **Status Management**: Vehicle availability states
- **Ownership Verification**: Vendor can only manage own vehicles
- **File Upload**: Image handling

**Interview Questions**:
- How do you ensure vendors only manage their own vehicles?
- How does vehicle status affect booking availability?
- How do you handle image uploads?

---

### Review Module
**Key Concepts**:
- **Approval Workflow**: PENDING → APPROVED/REJECTED
- **Unique Constraint**: One review per user per vehicle
- **Status Filtering**: Only approved reviews visible to public
- **Many-to-One Relationships**: Review → User, Review → Vehicle

**Interview Questions**:
- How do you prevent duplicate reviews?
- How does the approval workflow work?
- Why filter reviews by status?

---

### Complaint Module
**Key Concepts**:
- **Complaint Resolution Workflow**: PENDING → RESOLVED/CLOSED
- **Admin Resolution**: Admin can resolve complaints
- **User Complaint Tracking**: Users can view their complaints
- **Status Management**: Complaint lifecycle

**Interview Questions**:
- How does complaint resolution work?
- How do users track their complaints?
- What's the difference between RESOLVED and CLOSED?

---

### Reports Module
**Key Concepts**:
- **Data Aggregation**: Revenue, bookings, vehicles, users
- **Time-Based Grouping**: Monthly, yearly reports
- **Stream API**: Complex data transformations
- **YearMonth**: Time-series data handling
- **DTOs for Reports**: Structured response objects

**Interview Questions**:
- How do you generate monthly revenue reports?
- How do you aggregate booking analytics?
- How do you calculate vehicle performance metrics?

---

## Common Interview Questions

### General Questions

**Q: "Explain the layered architecture used in this project."**
**A**: 
- **Controller**: Handles HTTP requests/responses, validation, authentication
- **Service**: Contains business logic, orchestrates operations
- **Repository**: Data access layer, abstracts database operations
- **Entity**: Domain models, JPA entities
- **Benefits**: Separation of concerns, testability, maintainability

---

**Q: "How does Spring Boot dependency injection work?"**
**A**:
1. Spring scans for `@Component`, `@Service`, `@Repository`, `@Controller`
2. Creates singleton beans
3. Injects dependencies via constructor (preferred), setter, or field
4. `@RequiredArgsConstructor` (Lombok) generates constructor for final fields
5. Spring provides dependencies automatically

---

**Q: "Explain JWT authentication flow."**
**A**:
1. User logs in → Backend validates credentials
2. Backend generates JWT token (contains user info, role, expiration)
3. Frontend stores token (localStorage)
4. Frontend sends token in `Authorization: Bearer <token>` header
5. JWT Filter validates token, extracts user info
6. Sets Authentication in SecurityContext
7. SecurityConfig checks authorities for endpoint access

---

**Q: "What's the difference between @OneToOne and @ManyToOne?"**
**A**:
- **@OneToOne**: One entity associated with exactly one instance (e.g., Payment → Booking)
- **@ManyToOne**: Many entities associated with one instance (e.g., Booking → User)
- **@OneToMany**: One entity associated with many instances (e.g., User → Bookings)
- **@ManyToMany**: Many entities associated with many instances

---

**Q: "Why use @Transactional?"**
**A**:
- **Atomicity**: All operations succeed or all fail
- **Consistency**: Data remains valid
- **Isolation**: Prevents concurrent access issues
- **Durability**: Changes persist after commit
- **Automatic rollback**: On exceptions

---

**Q: "Explain the Stream API usage."**
**A**:
- **Intermediate operations**: `filter()`, `map()`, `sorted()` - return new stream, lazy
- **Terminal operations**: `collect()`, `forEach()`, `sum()` - trigger execution
- **Benefits**: Functional style, readable, chainable
- **Example**: `payments.stream().filter(p -> p.getStatus() == COMPLETED).mapToDouble(Payment::getAmount).sum()`

---

**Q: "How do you handle exceptions in Spring Boot?"**
**A**:
1. **Try-catch in controller**: Basic error handling
2. **@ControllerAdvice**: Global exception handler (better approach)
3. **@ExceptionHandler**: Method-level exception handling
4. **ResponseEntity**: Return appropriate HTTP status codes
5. **Custom exceptions**: Business-specific exceptions

---

**Q: "What's the difference between LAZY and EAGER fetching?"**
**A**:
- **LAZY**: Load related entity only when accessed (default for @ManyToOne, @OneToOne)
- **EAGER**: Load related entity immediately (default for @OneToMany, @ManyToMany)
- **LAZY preferred**: Better performance, prevents N+1 queries
- **EAGER risks**: Can cause performance issues, loads entire object graph

---

**Q: "How do you ensure data consistency in transactions?"**
**A**:
1. **@Transactional**: Ensures atomicity
2. **Isolation levels**: Prevent dirty reads, phantom reads
3. **Business logic validation**: Enforce rules in service
4. **Database constraints**: Foreign keys, unique constraints
5. **Optimistic locking**: Version fields for concurrent updates

---

## Quick Reference: Concepts by Module

| Module | Key Concepts |
|--------|-------------|
| **Authentication & User** | JWT, BCrypt, Spring Security, UserDetails, RBAC, Filter chain |
| **Admin** | Data aggregation, Stream API, Statistics, Role verification |
| **Booking** | Date overlap, Conflict detection, State machine, Transactions |
| **Payment** | One-to-One relationship, UUID generation, Status sync |
| **Vehicle** | CRUD operations, Many-to-One, Status management, File upload |
| **Review** | Approval workflow, Unique constraints, Status filtering |
| **Complaint** | Resolution workflow, Status management, Admin actions |
| **Reports** | Data aggregation, Time-series, Stream transformations |

---

## Study Guide for Interviews

### Must Know Concepts:
1. ✅ Spring Boot architecture and layers
2. ✅ JPA relationships and fetch types
3. ✅ JWT authentication flow
4. ✅ Transaction management
5. ✅ Stream API operations
6. ✅ Design patterns used
7. ✅ Security implementation
8. ✅ Exception handling

### Module-Specific Deep Dives:
- **Admin Module**: How statistics are calculated
- **Booking Module**: Conflict detection algorithm
- **Payment Module**: One-to-One enforcement
- **Vehicle Module**: CRUD with ownership
- **Review Module**: Approval workflow
- **Complaint Module**: Resolution process
- **Reports Module**: Data aggregation

---

**Last Updated**: January 2026
