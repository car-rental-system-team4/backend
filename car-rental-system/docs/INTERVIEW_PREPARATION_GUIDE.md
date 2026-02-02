# Car Rental System - Interview Preparation Guide

This comprehensive guide covers all concepts, patterns, and implementation details across all modules. Use this for interview preparation and understanding the complete system.

---

## Table of Contents
1. [System Overview](#system-overview)
2. [Architecture Patterns](#architecture-patterns)
3. [Core Technologies](#core-technologies)
4. [Module-by-Module Concepts](#module-by-module-concepts)
5. [Common Interview Questions](#common-interview-questions)
6. [Code Examples](#code-examples)
7. [Best Practices](#best-practices)

---

## System Overview

### Technology Stack
- **Backend**: Spring Boot 3.2.2, Java 21
- **Database**: MySQL 8.0
- **ORM**: JPA/Hibernate
- **Security**: Spring Security + JWT
- **Frontend**: React 19, Vite, React Router
- **Build Tools**: Maven (backend), npm (frontend)

### Architecture
- **Layered Architecture**: Controller → Service → Repository → Entity
- **RESTful APIs**: Stateless, resource-based
- **Microservices-ready**: Can be split into services if needed

---

## Architecture Patterns

### 1. **Layered Architecture**
```
┌─────────────────┐
│   Controller    │  ← HTTP layer, validation, authentication
├─────────────────┤
│    Service      │  ← Business logic, orchestration
├─────────────────┤
│   Repository    │  ← Data access abstraction
├─────────────────┤
│     Entity      │  ← Domain models
├─────────────────┤
│    Database     │  ← Persistent storage
└─────────────────┘
```

**Benefits**:
- Separation of concerns
- Testability
- Maintainability
- Scalability

---

### 2. **Repository Pattern**
**Purpose**: Abstract data access layer

**Implementation**:
```java
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    List<Vehicle> findByVendorId(Integer vendorId);
    Optional<Vehicle> findByLicensePlate(String licensePlate);
}
```

**Benefits**:
- Testability (can mock)
- Flexibility (can switch databases)
- Query abstraction

---

### 3. **Service Layer Pattern**
**Purpose**: Encapsulate business logic

**Implementation**:
```java
@Service
public class BookingServiceImpl implements BookingService {
    public BookingResponse createBooking(...) {
        // Business logic here
    }
}
```

**Benefits**:
- Reusability
- Testability
- Single responsibility

---

### 4. **DTO Pattern**
**Purpose**: Transfer data between layers

**Why use DTOs?**
- Security (don't expose sensitive fields)
- Performance (avoid lazy loading issues)
- Versioning (can change without affecting entities)
- Decoupling (frontend independent of database schema)

---

## Core Technologies

### Spring Boot Concepts

#### 1. **Dependency Injection**
**How it works**:
```java
@Service
@RequiredArgsConstructor
public class BookingServiceImpl {
    private final BookingRepository bookingRepository;  // Injected by Spring
}
```

**Types**:
- **Constructor injection** (preferred): Via constructor
- **Setter injection**: Via setter methods
- **Field injection**: Via @Autowired (not recommended)

**Interview Q**: "How does Spring Boot dependency injection work?"
**A**: Spring's IoC container scans for `@Component`, `@Service`, `@Repository`, creates singleton beans, and injects dependencies via constructor/setter/field. `@RequiredArgsConstructor` generates constructor for final fields.

---

#### 2. **@Transactional**
**What it does**: Manages database transactions

**Usage**:
```java
@Transactional
public BookingResponse createBooking(...) {
    vehicleRepository.save(vehicle);
    bookingRepository.save(booking);
    // If any fails, both roll back
}
```

**Properties**:
- **propagation**: How transaction propagates (default: REQUIRED)
- **isolation**: Transaction isolation level (default: READ_COMMITTED)
- **rollbackFor**: Exceptions that trigger rollback

**Interview Q**: "What happens if an exception occurs in @Transactional method?"
**A**: Spring automatically rolls back the transaction. All database changes are undone. This ensures ACID properties - either all operations succeed or none do.

---

#### 3. **Spring Security**
**Components**:
- **JwtFilter**: Validates JWT tokens
- **SecurityConfig**: Configures endpoint security
- **SecurityContextHolder**: Thread-local storage for authentication

**Flow**:
```
Request → JwtFilter (validates token) → SecurityContext (stores auth) → 
SecurityConfig (checks authority) → Controller
```

---

### JPA/Hibernate Concepts

#### 1. **Entity Relationships**

**@ManyToOne** (Vehicle → User/Vendor):
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "vendor_id")
private User vendor;
```

**@OneToOne** (Payment → Booking):
```java
@OneToOne
@JoinColumn(name = "booking_id", unique = true)
private Booking booking;
```

**Fetch Types**:
- **LAZY**: Load when accessed (default for @ManyToOne, @OneToOne)
- **EAGER**: Load immediately (default for @OneToMany, @ManyToMany)

**Interview Q**: "What's the difference between LAZY and EAGER fetching?"
**A**: 
- **LAZY**: Related entity loaded only when accessed (better performance)
- **EAGER**: Related entity loaded immediately (can cause N+1 queries)
- **LAZY preferred** to avoid performance issues

---

#### 2. **Lifecycle Callbacks**
**@PrePersist**: Called before entity is first saved
```java
@PrePersist
protected void onCreate() {
    createdAt = LocalDateTime.now();
    if (status == null) {
        status = BookingStatus.PENDING;
    }
}
```

**@PreUpdate**: Called before entity is updated
```java
@PreUpdate
protected void onUpdate() {
    updatedAt = LocalDateTime.now();
}
```

---

#### 3. **Custom Queries**
**JPQL** (Java Persistence Query Language):
```java
@Query("SELECT b FROM Booking b WHERE b.vehicle.id = :vehicleId AND " +
       "b.status IN :statuses AND " +
       "((b.pickupDate <= :returnDate AND b.returnDate >= :pickupDate))")
List<Booking> findConflictingBookings(...);
```

**Method name queries**:
```java
List<Booking> findByUserId(Integer userId);
// Generated: SELECT * FROM bookings WHERE user_id = ?
```

---

### Java 8+ Features

#### 1. **Stream API**
**Operations**:
- **Intermediate**: `filter()`, `map()`, `sorted()` - return new stream, lazy
- **Terminal**: `collect()`, `sum()`, `forEach()` - trigger execution

**Example**:
```java
Double totalRevenue = payments.stream()
    .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
    .mapToDouble(Payment::getAmount)
    .sum();
```

---

#### 2. **Optional**
**Purpose**: Null-safe operations

**Usage**:
```java
Optional<Vehicle> vehicle = vehicleRepository.findByLicensePlate(plate);
if (vehicle.isPresent()) {
    // Vehicle exists
}
```

---

#### 3. **LocalDate/LocalDateTime**
**Purpose**: Modern date/time API

**Usage**:
```java
LocalDate pickupDate = LocalDate.of(2025, 12, 10);
long days = ChronoUnit.DAYS.between(pickupDate, returnDate);
```

---

## Module-by-Module Concepts

### Authentication & User Module
**Key Concepts**:
- JWT token generation and validation
- BCrypt password hashing and verification
- Spring Security filter chain
- UserDetails interface implementation
- Role-based access control
- User registration approval workflow
- Custom JWT filter

**Interview Questions**:
- Explain the complete JWT authentication flow
- How does BCrypt password hashing work?
- How does Spring Security filter chain work?
- What's the difference between authentication and authorization?
- How does SecurityContextHolder work in multi-threaded environment?

---

### Admin Module
**Key Concepts**:
- Data aggregation with Stream API
- Statistics calculation
- Role-based access control (two-level)
- DTO conversion

**Interview Questions**:
- How do you calculate total revenue?
- Why use DTOs instead of entities?
- How do you ensure only admins access admin endpoints?

---

### Booking Module
**Key Concepts**:
- Date overlap algorithm for conflict detection
- Transaction management (@Transactional)
- State machine (booking status transitions)
- JPQL for complex queries

**Interview Questions**:
- How do you prevent double-booking?
- Explain the date overlap detection algorithm
- Why use @Transactional in createBooking()?

---

### Payment Module
**Key Concepts**:
- One-to-One relationship enforcement
- UUID generation for transaction IDs
- Status synchronization (Payment → Booking)
- Idempotency pattern

**Interview Questions**:
- How do you enforce one booking → one payment?
- How do payment and booking statuses stay in sync?
- How do you generate transaction IDs?

---

### Vehicle & Vendor Module
**Key Concepts**:
- Many-to-One relationship (Vehicle → Vendor)
- Ownership verification
- Unique constraint validation
- CRUD operations

**Interview Questions**:
- How do you ensure vendors only manage their own vehicles?
- How does vehicle status affect booking availability?
- How do you prevent duplicate license plates?

---

### Review Module
**Key Concepts**:
- Approval workflow (PENDING → APPROVED/REJECTED)
- Unique constraint (one review per user per vehicle)
- Status-based filtering
- Content moderation

**Interview Questions**:
- How do you prevent duplicate reviews?
- How does the approval workflow work?
- Why filter reviews by status?

---

### Complaint Module
**Key Concepts**:
- Resolution workflow (PENDING → RESOLVED)
- Admin response pattern
- Optional booking link
- Status tracking

**Interview Questions**:
- How does complaint resolution work?
- What's the difference between RESOLVED and CLOSED?
- How do users track their complaints?

---

### Reports Module
**Key Concepts**:
- Data aggregation with Stream API
- Time-series data handling (YearMonth)
- Top N queries
- Complex DTOs with nested classes

**Interview Questions**:
- How do you generate monthly revenue reports?
- How do you calculate top performing vehicles?
- Explain Stream API operations in report generation

---

## Common Interview Questions

### General Questions

**Q: "Explain the layered architecture used in this project."**
**A**: 
- **Controller**: Handles HTTP, validation, authentication
- **Service**: Business logic, orchestration
- **Repository**: Data access abstraction
- **Entity**: Domain models
- **Benefits**: Separation of concerns, testability, maintainability

---

**Q: "How does JWT authentication work?"**
**A**:
1. User logs in → Backend validates credentials
2. Backend generates JWT (contains user info, role, expiration)
3. Frontend stores token
4. Frontend sends token in `Authorization: Bearer <token>` header
5. JWT Filter validates token, extracts user info
6. Sets Authentication in SecurityContext
7. SecurityConfig checks authorities for endpoint access

---

**Q: "What's the difference between @OneToOne and @ManyToOne?"**
**A**:
- **@OneToOne**: One entity → exactly one instance (Payment → Booking)
- **@ManyToOne**: Many entities → one instance (Booking → User)
- **@OneToMany**: One entity → many instances (User → Bookings)
- **@ManyToMany**: Many entities → many instances

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
- **Intermediate operations**: `filter()`, `map()`, `sorted()` - lazy, return new stream
- **Terminal operations**: `collect()`, `sum()`, `forEach()` - trigger execution
- **Benefits**: Functional style, readable, chainable
- **Example**: `payments.stream().filter(p -> p.getStatus() == COMPLETED).mapToDouble(Payment::getAmount).sum()`

---

**Q: "How do you handle exceptions in Spring Boot?"**
**A**:
1. **Try-catch in controller**: Basic error handling
2. **@ControllerAdvice**: Global exception handler (better)
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

### Module-Specific Questions

#### Admin Module
**Q: "How do you calculate total revenue?"**
**A**: 
1. Get all payments from repository
2. Filter for COMPLETED status using Stream API
3. Extract amounts using `mapToDouble(Payment::getAmount)`
4. Sum all amounts using `sum()`

**Code**:
```java
Double totalRevenue = paymentRepository.findAll().stream()
    .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
    .mapToDouble(Payment::getAmount)
    .sum();
```

---

#### Booking Module
**Q: "How do you prevent double-booking?"**
**A**:
1. **Conflict detection query**: Check for overlapping dates with active statuses
2. **Date overlap algorithm**: `pickup1 <= return2 AND return1 >= pickup2`
3. **Transaction isolation**: READ_COMMITTED prevents dirty reads
4. **Status filtering**: Only check PENDING, CONFIRMED, ACTIVE bookings

**Code**:
```java
@Query("SELECT b FROM Booking b WHERE b.vehicle.id = :vehicleId AND " +
       "b.status IN :statuses AND " +
       "((b.pickupDate <= :returnDate AND b.returnDate >= :pickupDate))")
List<Booking> findConflictingBookings(...);
```

---

#### Payment Module
**Q: "How do you enforce one booking → one payment?"**
**A**:
1. **Database constraint**: UNIQUE constraint on `booking_id`
2. **JPA annotation**: `@JoinColumn(unique = true)`
3. **Service-level check**: Verify payment doesn't exist before creating
4. **Exception**: Throw if duplicate payment attempted

---

#### Vehicle Module
**Q: "How do you ensure vendors only manage their own vehicles?"**
**A**:
1. Extract vendor email from SecurityContext
2. Load vendor entity from database
3. Load vehicle by ID
4. Compare IDs: `vehicle.getVendor().getId().equals(vendor.getId())`
5. Throw exception if IDs don't match

---

#### Review Module
**Q: "How do you prevent duplicate reviews?"**
**A**:
1. **Repository method**: `findByUserIdAndVehicleId()` checks existence
2. **Service validation**: Throw exception if review exists
3. **Database constraint**: Optional UNIQUE constraint on (user_id, vehicle_id)

---

#### Reports Module
**Q: "How do you generate monthly revenue reports?"**
**A**:
1. Get all completed payments
2. Group by month using `YearMonth.from(paymentDate)`
3. Sum amounts for each month
4. Sort chronologically
5. Return MonthlyRevenue DTOs

---

## Code Examples

### Complete Flow: Create Booking

```java
@Transactional
public BookingResponse createBooking(String userEmail, BookingRequest request) {
    // 1. Find user
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    
    // 2. Find vehicle
    Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
        .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
    
    // 3. Validate vehicle available
    if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
        throw new IllegalArgumentException("Vehicle not available");
    }
    
    // 4. Validate dates
    if (request.getReturnDate().isBefore(request.getPickupDate())) {
        throw new IllegalArgumentException("Invalid dates");
    }
    
    // 5. Check conflicts
    List<Booking> conflicts = bookingRepository.findConflictingBookings(...);
    if (!conflicts.isEmpty()) {
        throw new IllegalArgumentException("Date conflict");
    }
    
    // 6. Calculate amount
    long days = ChronoUnit.DAYS.between(pickupDate, returnDate);
    Double totalAmount = vehicle.getPricePerDay() * days;
    
    // 7. Create booking
    Booking booking = new Booking();
    booking.setUser(user);
    booking.setVehicle(vehicle);
    booking.setTotalAmount(totalAmount);
    booking.setStatus(BookingStatus.PENDING);
    
    // 8. Update vehicle status
    vehicle.setStatus(VehicleStatus.BOOKED);
    vehicleRepository.save(vehicle);
    
    // 9. Save booking
    Booking savedBooking = bookingRepository.save(booking);
    
    // 10. Convert to DTO
    return convertToResponse(savedBooking);
}
```

**Key Concepts Demonstrated**:
- Transaction management
- Validation
- Conflict detection
- Date calculations
- State management
- Entity relationships

---

## Best Practices

### 1. **Security**
- ✅ Always verify authentication
- ✅ Verify role in both SecurityConfig and Controller
- ✅ Verify ownership for user-specific resources
- ✅ Use HTTPS in production
- ✅ Validate all inputs

### 2. **Error Handling**
- ✅ Use appropriate HTTP status codes
- ✅ Provide clear error messages
- ✅ Use @ControllerAdvice for global handling
- ✅ Log errors for debugging

### 3. **Performance**
- ✅ Use LAZY fetching for relationships
- ✅ Use pagination for large datasets
- ✅ Use database indexes
- ✅ Cache frequently accessed data
- ✅ Use aggregation queries instead of loading all data

### 4. **Code Quality**
- ✅ Use DTOs instead of entities in responses
- ✅ Validate inputs at multiple levels
- ✅ Use meaningful variable names
- ✅ Add comments for complex logic
- ✅ Follow consistent naming conventions

---

## Quick Reference

### HTTP Status Codes
- **200 OK**: Success
- **201 Created**: Resource created
- **400 Bad Request**: Invalid input
- **401 Unauthorized**: Not authenticated
- **403 Forbidden**: Not authorized
- **404 Not Found**: Resource doesn't exist
- **500 Internal Server Error**: Server error

### Common Annotations
- **@Service**: Service layer component
- **@Repository**: Data access component
- **@RestController**: REST controller
- **@Transactional**: Transaction management
- **@ManyToOne**: Many-to-one relationship
- **@OneToOne**: One-to-one relationship
- **@Query**: Custom query
- **@PrePersist**: Before first save
- **@PreUpdate**: Before update

### Stream API Operations
- **filter()**: Filter elements
- **map()**: Transform elements
- **collect()**: Collect to collection
- **sum()**: Sum numeric values
- **groupingBy()**: Group by key
- **sorted()**: Sort elements
- **limit()**: Limit number of elements

---

## Study Checklist

### Must Know:
- [ ] Spring Boot architecture and layers
- [ ] JPA relationships and fetch types
- [ ] JWT authentication flow
- [ ] Transaction management
- [ ] Stream API operations
- [ ] Design patterns used
- [ ] Security implementation
- [ ] Exception handling

### Module-Specific:
- [ ] Authentication: JWT flow, BCrypt, Spring Security
- [ ] Admin: Statistics calculation
- [ ] Booking: Conflict detection
- [ ] Payment: One-to-One enforcement
- [ ] Vehicle: Ownership verification
- [ ] Review: Approval workflow
- [ ] Complaint: Resolution process
- [ ] Reports: Data aggregation

---

**Last Updated**: January 2026

**Use this guide along with individual module documentation files for comprehensive interview preparation.**
