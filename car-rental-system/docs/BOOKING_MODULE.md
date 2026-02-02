# Booking Module - Implementation Documentation

## Overview
The Booking Module provides complete booking management functionality for the car rental system, allowing users to create, view, and cancel bookings, while vendors can view bookings for their vehicles.

---

## Table of Contents
1. [Architecture](#architecture)
2. [Core Concepts Used](#core-concepts-used)
3. [Design Patterns](#design-patterns)
4. [Spring Framework Concepts](#spring-framework-concepts)
5. [JPA/Hibernate Concepts](#jpahibernate-concepts)
6. [Transaction Management](#transaction-management)
7. [Date/Time Handling](#datetime-handling)
8. [Database Schema](#database-schema)
9. [Backend Implementation](#backend-implementation)
10. [Frontend Implementation](#frontend-implementation)
11. [API Endpoints](#api-endpoints)
12. [Business Rules](#business-rules)
13. [How It Works - Step by Step](#how-it-works---step-by-step)
14. [Interview Questions & Answers](#interview-questions--answers)
15. [Code Walkthrough](#code-walkthrough)
16. [Testing](#testing)

---

## Architecture

### Backend Layers
```
Controller (BookingController)
    ↓
Service (BookingService → BookingServiceImpl)
    ↓
Repository (BookingRepository)
    ↓
Entity (Booking)
    ↓
Database (bookings table)
```

### Frontend Components
```
Pages:
  - BookingsPage.jsx (User bookings)
  - VendorBookings.jsx (Vendor bookings)
  - PaymentPage.jsx (Booking creation)
  - CarDetailsPage.jsx (Booking initiation)

Services:
  - api.js (API functions)
```

---

## Core Concepts Used

### 1. **@Transactional Annotation**
**What it is**: Declarative transaction management in Spring.

**How it's used in Booking Module**:
```java
@Override
@Transactional
public BookingResponse createBooking(String userEmail, BookingRequest request) {
    // Multiple database operations
    vehicleRepository.save(vehicle);  // Update vehicle status
    bookingRepository.save(booking);   // Create booking
    // If any operation fails, entire transaction rolls back
}
```

**Why use @Transactional?**
- **ACID Properties**: Ensures all operations succeed or all fail (atomicity)
- **Data Consistency**: Prevents partial updates (e.g., booking created but vehicle not updated)
- **Isolation**: Prevents concurrent booking conflicts

**Interview Question**: "What happens if an exception occurs in a @Transactional method?"
**Answer**: Spring automatically rolls back the transaction. All database changes made within the method are undone. This ensures data consistency - either all operations succeed or none do.

---

### 2. **JPQL (Java Persistence Query Language)**
**What it is**: Object-oriented query language for JPA entities.

**How it's used for conflict detection**:
```java
@Query("SELECT b FROM Booking b WHERE b.vehicle.id = :vehicleId AND " +
       "b.status IN :statuses AND " +
       "((b.pickupDate <= :returnDate AND b.returnDate >= :pickupDate))")
List<Booking> findConflictingBookings(
    @Param("vehicleId") Integer vehicleId,
    @Param("pickupDate") LocalDate pickupDate,
    @Param("returnDate") LocalDate returnDate,
    @Param("statuses") List<BookingStatus> statuses
);
```

**How it works**:
- **Entity-based**: Uses entity names (`Booking`) not table names (`bookings`)
- **Type-safe**: Compile-time checking
- **Parameter binding**: `:parameterName` prevents SQL injection

**Generated SQL**:
```sql
SELECT * FROM bookings b 
WHERE b.vehicle_id = ? 
  AND b.status IN (?, ?, ?)
  AND (b.pickup_date <= ? AND b.return_date >= ?)
```

**Interview Question**: "What's the difference between JPQL and native SQL?"
**Answer**: 
- **JPQL**: Works with entities, database-agnostic, type-safe
- **Native SQL**: Works with tables, database-specific, more control
- Use JPQL for portability, native SQL for complex queries or database-specific features

---

### 3. **Date Overlap Algorithm**
**What it is**: Algorithm to detect if two date ranges overlap.

**Mathematical Logic**:
Two date ranges overlap if:
```
Range1: [pickup1, return1]
Range2: [pickup2, return2]

Overlap if: pickup1 <= return2 AND return1 >= pickup2
```

**Implementation in Query**:
```java
(b.pickupDate <= :returnDate AND b.returnDate >= :pickupDate)
```

**Visual Example**:
```
Existing:    [----Booking1----]
New:              [----Booking2----]
                  ↑ Overlap detected

Existing:    [----Booking1----]
New:                            [----Booking2----]
                                ↑ No overlap
```

**Interview Question**: "How do you detect date conflicts in bookings?"
**Answer**: Use the overlap algorithm: `pickup1 <= return2 AND return1 >= pickup2`. In our code, we check if any existing booking's pickup date is before or equal to the new return date, AND the existing return date is after or equal to the new pickup date. This covers all overlap scenarios.

---

### 4. **ChronoUnit for Date Calculations**
**What it is**: Java 8+ API for date/time calculations.

**How it's used**:
```java
long days = ChronoUnit.DAYS.between(pickupDate, returnDate);
Double totalAmount = vehicle.getPricePerDay() * days;
```

**Why use ChronoUnit?**
- **Precise**: Handles leap years, month boundaries correctly
- **Readable**: `DAYS.between()` is clearer than manual calculation
- **Type-safe**: Returns `long` (not `int`)

**Alternative approaches**:
```java
// Manual calculation (error-prone)
long days = returnDate.toEpochDay() - pickupDate.toEpochDay();

// Period (for years/months/days)
Period period = Period.between(pickupDate, returnDate);
```

**Interview Question**: "Why use ChronoUnit.DAYS.between() instead of manual date subtraction?"
**Answer**: 
1. **Correctness**: Handles edge cases (leap years, DST) automatically
2. **Readability**: Clear intent - calculating days between dates
3. **Maintainability**: Less error-prone than manual calculations
4. **Type safety**: Returns appropriate type (`long` for days)

---

### 5. **State Machine Pattern (Implicit)**
**What it is**: Pattern for managing object state transitions.

**How it appears in Booking Module**:
```java
public enum BookingStatus {
    PENDING,    // Initial state
    CONFIRMED,  // After payment
    ACTIVE,     // When pickup date arrives
    COMPLETED,  // After return
    CANCELLED   // User cancellation
}
```

**State Transitions**:
```
PENDING → CONFIRMED (after payment)
PENDING → CANCELLED (user cancels)
CONFIRMED → ACTIVE (pickup date arrives)
CONFIRMED → CANCELLED (user cancels)
ACTIVE → COMPLETED (return date passes)
```

**Validation in cancelBooking()**:
```java
if (booking.getStatus() == BookingStatus.CANCELLED) {
    throw new IllegalArgumentException("Already cancelled");
}
if (booking.getStatus() == BookingStatus.COMPLETED) {
    throw new IllegalArgumentException("Cannot cancel completed");
}
```

**Interview Question**: "How do you ensure valid state transitions in the booking system?"
**Answer**: 
1. **Enum-based states**: Use enum to restrict possible states
2. **Validation logic**: Check current state before allowing transition
3. **Business rules**: Define which transitions are allowed (e.g., can't cancel completed)
4. **Future enhancement**: Could use State Machine library (like Spring State Machine) for complex workflows

---

## Design Patterns

### 1. **Repository Pattern**
**Purpose**: Abstraction layer for data access.

**Implementation**:
```java
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUserId(Integer userId);
    List<Booking> findConflictingBookings(...);
}
```

**Benefits**:
- **Testability**: Can mock repository in unit tests
- **Flexibility**: Can switch data sources without changing service code
- **Query abstraction**: Business logic doesn't know about SQL

---

### 2. **DTO Pattern**
**Purpose**: Separate API contract from database schema.

**Example**:
```java
// Entity (internal)
@Entity
public class Booking {
    @ManyToOne
    private User user;  // Full entity relationship
    // ...
}

// DTO (external)
public class BookingResponse {
    private Integer userId;      // Only ID
    private String userName;     // Only name
    // No sensitive data
}
```

**Why use DTOs?**
- **Security**: Don't expose entity relationships
- **Performance**: Avoid lazy loading issues
- **Versioning**: Can change DTO without changing entity

---

### 3. **Template Method Pattern**
**How it appears**: JpaRepository provides template methods.

```java
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    // Template methods from JpaRepository:
    // - save(), findAll(), findById(), delete()
    
    // Custom methods (steps in template):
    List<Booking> findByUserId(Integer userId);
}
```

---

## Spring Framework Concepts

### 1. **@Transactional with Propagation**
**Default behavior**: `REQUIRED` - Join existing transaction or create new.

```java
@Transactional
public BookingResponse createBooking(...) {
    // Transaction starts here
    vehicleRepository.save(vehicle);
    bookingRepository.save(booking);
    // Transaction commits here (or rolls back on exception)
}
```

**Transaction Isolation**:
- **Default**: `READ_COMMITTED` - Prevents dirty reads
- **Important for booking**: Prevents double-booking in concurrent scenarios

**Interview Question**: "How does @Transactional prevent double-booking?"
**Answer**: 
1. **Isolation**: `READ_COMMITTED` ensures we see committed data only
2. **Locking**: Database locks rows during transaction
3. **Conflict detection**: Query checks for conflicts within transaction
4. **Atomicity**: Either entire booking succeeds or fails (no partial state)

**Future enhancement**: Use `@Transactional(isolation = Isolation.SERIALIZABLE)` for stricter isolation, or use pessimistic locking:
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT b FROM Booking b WHERE ...")
```

---

### 2. **Custom Query Methods**
**How Spring Data JPA generates queries**:

```java
// Method name → Query
findByUserId(Integer userId)
// → SELECT * FROM bookings WHERE user_id = ?

findByIdAndUserId(Integer id, Integer userId)
// → SELECT * FROM bookings WHERE id = ? AND user_id = ?
```

**Query creation rules**:
- `findBy` + `FieldName` → `WHERE fieldName = ?`
- `And` → `AND`
- `Or` → `OR`
- `In` → `IN (?, ?, ?)`

**Interview Question**: "How would you write a method to find bookings by status and date range?"
**Answer**:
```java
List<Booking> findByStatusAndPickupDateBetween(
    BookingStatus status, 
    LocalDate startDate, 
    LocalDate endDate
);
// Generated: WHERE status = ? AND pickup_date BETWEEN ? AND ?
```

---

### 3. **Parameter Binding in @Query**
**How it works**:
```java
@Query("SELECT b FROM Booking b WHERE b.vehicle.id = :vehicleId")
List<Booking> findByVehicleId(@Param("vehicleId") Integer vehicleId);
```

**Why use `@Param`?**
- **Named parameters**: Clear and readable
- **Position-independent**: Order doesn't matter
- **Type-safe**: Compile-time checking

**Alternative (positional)**:
```java
@Query("SELECT b FROM Booking b WHERE b.vehicle.id = ?1")
List<Booking> findByVehicleId(Integer vehicleId);
```

---

## JPA/Hibernate Concepts

### 1. **@ManyToOne Relationship**
**What it is**: Many bookings belong to one user/vehicle.

**Implementation**:
```java
@Entity
public class Booking {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
}
```

**How it works**:
- **LAZY loading**: User/Vehicle not loaded until accessed
- **Join column**: Foreign key column name
- **Bidirectional**: Can add `@OneToMany` on User/Vehicle side

**Interview Question**: "Why use LAZY fetching for @ManyToOne?"
**Answer**:
1. **Performance**: Don't load related entities unless needed
2. **Memory**: Saves memory by not loading unnecessary data
3. **N+1 problem prevention**: Only loads when explicitly accessed
4. **Default**: LAZY is default for @ManyToOne (EAGER is default for @OneToMany)

---

### 2. **@PrePersist and @PreUpdate**
**What they are**: JPA lifecycle callbacks.

**How they're used**:
```java
@Entity
public class Booking {
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = BookingStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

**When they're called**:
- `@PrePersist`: Before entity is first saved (INSERT)
- `@PreUpdate`: Before entity is updated (UPDATE)

**Benefits**:
- **Automatic**: No need to manually set timestamps
- **Consistent**: Always set, can't forget
- **Default values**: Can set default status

**Interview Question**: "What's the difference between @PrePersist and @PreUpdate?"
**Answer**:
- **@PrePersist**: Called only on first save (INSERT), use for setting creation timestamp and default values
- **@PreUpdate**: Called on every update (UPDATE), use for setting modification timestamp
- **@PrePersist** is called once, **@PreUpdate** is called every time entity is modified

---

### 3. **Enum Mapping**
**How enums are stored**:
```java
@Enumerated(EnumType.STRING)
@Column(name = "status")
private BookingStatus status;
```

**EnumType.STRING vs EnumType.ORDINAL**:
- **STRING**: Stores enum name ("PENDING", "CONFIRMED") - readable, but can't reorder enum
- **ORDINAL**: Stores index (0, 1, 2) - compact, but breaks if enum order changes

**Why use STRING?**
- **Readability**: Can read database directly
- **Stability**: Adding new enum values doesn't break existing data
- **Debugging**: Easier to debug

---

## Transaction Management

### 1. **ACID Properties in Booking Creation**
**Atomicity**: All operations succeed or all fail
```java
@Transactional
public BookingResponse createBooking(...) {
    vehicleRepository.save(vehicle);  // Operation 1
    bookingRepository.save(booking);   // Operation 2
    // If either fails, both roll back
}
```

**Consistency**: Data remains valid (vehicle status matches booking)
```java
// Business rule: If booking created, vehicle must be BOOKED
vehicle.setStatus(VehicleStatus.BOOKED);
booking.setStatus(BookingStatus.PENDING);
// Both must succeed together
```

**Isolation**: Concurrent bookings don't interfere
```java
// Transaction 1 and Transaction 2 both check conflicts
// Only one succeeds, other sees the committed change
```

**Durability**: Once committed, changes persist
```java
// After transaction commits, booking is permanently saved
```

**Interview Question**: "How do you ensure ACID properties in booking creation?"
**Answer**:
1. **@Transactional**: Ensures atomicity (all or nothing)
2. **Business logic validation**: Ensures consistency (status matches)
3. **Isolation level**: Prevents dirty reads (concurrent bookings)
4. **Database commit**: Ensures durability (persisted to disk)

---

### 2. **Rollback Scenarios**
**Automatic rollback**:
```java
@Transactional
public BookingResponse createBooking(...) {
    // If exception thrown here, transaction rolls back
    if (conflictingBookings.isEmpty() == false) {
        throw new IllegalArgumentException("Conflict");  // Rolls back
    }
    // ...
}
```

**Manual rollback**:
```java
@Transactional
public BookingResponse createBooking(...) {
    try {
        // operations
    } catch (Exception e) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        throw e;
    }
}
```

---

## Date/Time Handling

### 1. **LocalDate vs LocalDateTime**
**Why use LocalDate for booking dates?**:
```java
private LocalDate pickupDate;   // Date only (no time)
private LocalDate returnDate;    // Date only (no time)
```

**Benefits**:
- **Simplicity**: Bookings are date-based, not time-based
- **Clarity**: No confusion about time zones
- **Comparison**: Easy to compare dates

**When to use LocalDateTime**:
- For timestamps (createdAt, updatedAt)
- For precise time tracking

---

### 2. **Date Validation**
**Jakarta Validation**:
```java
public class BookingRequest {
    @NotNull
    @FutureOrPresent  // Pickup can be today or future
    private LocalDate pickupDate;
    
    @NotNull
    @Future  // Return must be future
    private LocalDate returnDate;
}
```

**Service-level validation**:
```java
// Additional business rule
if (returnDate.isBefore(pickupDate) || returnDate.isEqual(pickupDate)) {
    throw new IllegalArgumentException("Return date must be after pickup date");
}
```

**Why both?**
- **@FutureOrPresent**: Basic validation (framework level)
- **Service validation**: Business rule (return > pickup)

---

## How It Works - Step by Step

### Example: User Creates a Booking

**1. Frontend Request**:
```javascript
const bookingData = {
    vehicleId: 1,
    pickupDate: "2025-12-10",
    returnDate: "2025-12-15",
    pickupLocation: "Mumbai Central",
    returnLocation: "Mumbai Airport"
};
await createBooking(bookingData);
```

**2. Controller Receives Request**:
```java
@PostMapping("/bookings")
public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
    // Extract user email from SecurityContext
    String userEmail = getCurrentUserEmail();
    // Call service
    BookingResponse response = bookingService.createBooking(userEmail, request);
    return ResponseEntity.ok(response);
}
```

**3. Service Method (Transactional)**:
```java
@Transactional  // Transaction starts
public BookingResponse createBooking(String userEmail, BookingRequest request) {
    // Step 1: Find user
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    
    // Step 2: Find vehicle
    Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
        .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
    
    // Step 3: Validate vehicle available
    if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
        throw new IllegalArgumentException("Vehicle not available");
    }
    
    // Step 4: Validate dates
    if (request.getReturnDate().isBefore(request.getPickupDate())) {
        throw new IllegalArgumentException("Invalid dates");
    }
    
    // Step 5: Check conflicts (JPQL query)
    List<Booking> conflicts = bookingRepository.findConflictingBookings(...);
    if (!conflicts.isEmpty()) {
        throw new IllegalArgumentException("Date conflict");
    }
    
    // Step 6: Calculate amount
    long days = ChronoUnit.DAYS.between(pickupDate, returnDate);
    Double totalAmount = vehicle.getPricePerDay() * days;
    
    // Step 7: Create booking entity
    Booking booking = new Booking();
    booking.setUser(user);
    booking.setVehicle(vehicle);
    booking.setPickupDate(request.getPickupDate());
    booking.setReturnDate(request.getReturnDate());
    booking.setTotalAmount(totalAmount);
    booking.setStatus(BookingStatus.PENDING);  // @PrePersist sets this
    
    // Step 8: Update vehicle status
    vehicle.setStatus(VehicleStatus.BOOKED);
    vehicleRepository.save(vehicle);
    
    // Step 9: Save booking
    Booking savedBooking = bookingRepository.save(booking);
    
    // Step 10: Convert to DTO
    return convertToResponse(savedBooking);
    // Transaction commits (or rolls back if exception)
}
```

**4. Conflict Detection Query Execution**:
```sql
SELECT * FROM bookings 
WHERE vehicle_id = 1 
  AND status IN ('PENDING', 'CONFIRMED', 'ACTIVE')
  AND (pickup_date <= '2025-12-15' AND return_date >= '2025-12-10')
```

**5. Response Serialization**:
- Spring converts `BookingResponse` to JSON
- Returns HTTP 200 with booking details

---

## Interview Questions & Answers

### Q1: "Explain the booking creation flow."
**Answer**: 
1. **Validation**: Check vehicle exists and is AVAILABLE
2. **Date validation**: Ensure return date > pickup date
3. **Conflict detection**: Query for overlapping bookings with active statuses
4. **Amount calculation**: Days between dates × price per day
5. **Entity creation**: Create Booking entity with PENDING status
6. **Vehicle update**: Change vehicle status to BOOKED
7. **Transaction commit**: Both operations succeed or both fail

**Key concepts**: Transaction management, conflict detection, state management

---

### Q2: "How do you prevent double-booking?"
**Answer**: 
1. **Conflict detection query**: Check for overlapping dates with active statuses
2. **Transaction isolation**: READ_COMMITTED prevents dirty reads
3. **Status filtering**: Only check PENDING, CONFIRMED, ACTIVE bookings
4. **Date overlap algorithm**: `pickup1 <= return2 AND return1 >= pickup2`

**Future enhancement**: Use pessimistic locking or database-level constraints

---

### Q3: "Why use @Transactional in createBooking()?"
**Answer**:
1. **Atomicity**: Both vehicle update and booking creation must succeed together
2. **Consistency**: Ensures vehicle status matches booking state
3. **Isolation**: Prevents concurrent booking conflicts
4. **Rollback**: If any step fails, all changes are undone

**Example**: If booking save fails after vehicle update, vehicle status rolls back to AVAILABLE

---

### Q4: "Explain the date overlap detection algorithm."
**Answer**: 
Two date ranges overlap if:
```
Range A: [pickupA, returnA]
Range B: [pickupB, returnB]

Overlap if: pickupA <= returnB AND returnA >= pickupB
```

**In our query**:
```java
(b.pickupDate <= :returnDate AND b.returnDate >= :pickupDate)
```

This checks if existing booking's pickup is before new return AND existing return is after new pickup.

**Visual**:
```
Existing: [----A----]
New:         [----B----]
            ↑ Overlap (A.pickup <= B.return AND A.return >= B.pickup)
```

---

### Q5: "How does the booking status state machine work?"
**Answer**:
**States**: PENDING → CONFIRMED → ACTIVE → COMPLETED
**Cancellation**: PENDING/CONFIRMED/ACTIVE → CANCELLED

**Transitions**:
- **PENDING → CONFIRMED**: After payment
- **CONFIRMED → ACTIVE**: When pickup date arrives (scheduled job)
- **ACTIVE → COMPLETED**: When return date passes (scheduled job)
- **Any → CANCELLED**: User cancels (with validation)

**Validation**: Can't cancel CANCELLED or COMPLETED bookings

---

### Q6: "What happens to vehicle status when booking is cancelled?"
**Answer**:
```java
if (currentStatus == BookingStatus.CONFIRMED || currentStatus == BookingStatus.ACTIVE) {
    vehicle.setStatus(VehicleStatus.AVAILABLE);
}
```

**Logic**:
- **PENDING booking cancelled**: Vehicle already AVAILABLE (wasn't confirmed yet)
- **CONFIRMED/ACTIVE cancelled**: Vehicle set back to AVAILABLE (was booked)
- **COMPLETED**: Can't cancel, so vehicle already returned

**Why this logic?**: Only confirmed/active bookings actually reserved the vehicle

---

## Code Walkthrough

### Complete Flow: Create Booking

**1. Frontend** (`PaymentPage.jsx`):
```javascript
const handlePayment = async () => {
    const bookingData = {
        vehicleId: vehicle.id,
        pickupDate: pickupDate,
        returnDate: returnDate,
        pickupLocation: pickupLocation,
        returnLocation: returnLocation
    };
    
    try {
        const response = await createBooking(bookingData);
        // Success - redirect to bookings page
    } catch (error) {
        // Handle error
    }
};
```

**2. Controller** (`BookingController.java`):
```java
@PostMapping("/bookings")
public ResponseEntity<?> createBooking(@RequestBody @Valid BookingRequest request) {
    // @Valid triggers Jakarta Validation
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userEmail = auth.getName();
    
    try {
        BookingResponse response = bookingService.createBooking(userEmail, request);
        return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
```

**3. Service** (`BookingServiceImpl.java`):
```java
@Transactional  // Transaction boundary
public BookingResponse createBooking(String userEmail, BookingRequest request) {
    // Validation and business logic (see step-by-step above)
    // ...
    return convertToResponse(savedBooking);
}
```

**4. Repository Query** (`BookingRepository.java`):
```java
@Query("SELECT b FROM Booking b WHERE b.vehicle.id = :vehicleId " +
       "AND b.status IN :statuses AND " +
       "((b.pickupDate <= :returnDate AND b.returnDate >= :pickupDate))")
List<Booking> findConflictingBookings(...);
```

**5. Entity Lifecycle** (`Booking.java`):
```java
@PrePersist
protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
    if (status == null) {
        status = BookingStatus.PENDING;  // Default status
    }
}
```

---

## Database Schema

### Bookings Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | INT | PRIMARY KEY, AUTO_INCREMENT | Booking ID |
| `user_id` | INT | NOT NULL, FOREIGN KEY | User who made the booking |
| `vehicle_id` | INT | NOT NULL, FOREIGN KEY | Vehicle being booked |
| `pickup_date` | DATE | NOT NULL | Pickup date |
| `return_date` | DATE | NOT NULL | Return date |
| `pickup_location` | VARCHAR(255) | NOT NULL | Pickup location |
| `return_location` | VARCHAR(255) | NOT NULL | Return location |
| `total_amount` | DOUBLE | NOT NULL | Total booking amount |
| `status` | ENUM | NOT NULL | PENDING, CONFIRMED, ACTIVE, COMPLETED, CANCELLED |
| `created_at` | DATETIME(6) | NOT NULL | Creation timestamp |
| `updated_at` | DATETIME(6) | NOT NULL | Update timestamp |

### Relationships
- **Booking → User**: Many-to-One (Many bookings belong to one user)
- **Booking → Vehicle**: Many-to-One (Many bookings for one vehicle)
- **Vehicle → User (Vendor)**: Many-to-One (Many vehicles belong to one vendor)

---

## Backend Implementation

### 1. BookingStatus Enum
**File:** `enums/BookingStatus.java`

```java
public enum BookingStatus {
    PENDING, CONFIRMED, ACTIVE, COMPLETED, CANCELLED
}
```

**Status Flow:**
- `PENDING` → Initial status when booking is created
- `CONFIRMED` → After payment is confirmed
- `ACTIVE` → When pickup date arrives
- `COMPLETED` → After return date passes
- `CANCELLED` → When user cancels booking

---

### 2. Booking Entity
**File:** `entity/Booking.java`

**Key Features:**
- Automatic timestamp management (`@PrePersist`, `@PreUpdate`)
- Default status set to `PENDING` on creation
- Relationships with User and Vehicle entities
- Enum-based status management

**Fields:**
- `id` - Primary key
- `user` - ManyToOne relationship with User
- `vehicle` - ManyToOne relationship with Vehicle
- `pickupDate` - LocalDate for pickup
- `returnDate` - LocalDate for return
- `pickupLocation` - String
- `returnLocation` - String
- `totalAmount` - Calculated amount
- `status` - BookingStatus enum
- `createdAt` - Timestamp
- `updatedAt` - Timestamp

---

### 3. Booking Repository
**File:** `repository/BookingRepository.java`

**Custom Query Methods:**
- `findByUserId(Integer userId)` - Get all bookings for a user
- `findByVehicleId(Integer vehicleId)` - Get all bookings for a vehicle
- `findByStatus(BookingStatus status)` - Get bookings by status
- `findByIdAndUserId(Integer id, Integer userId)` - Get booking by ID and user (ownership check)
- `findByVendorId(Integer vendorId)` - Get all bookings for vendor's vehicles (custom query)
- `findConflictingBookings(...)` - Check for date conflicts

**Conflict Detection Query:**
```java
@Query("SELECT b FROM Booking b WHERE b.vehicle.id = :vehicleId AND b.status IN :statuses AND " +
       "((b.pickupDate <= :returnDate AND b.returnDate >= :pickupDate))")
List<Booking> findConflictingBookings(...)
```

This query finds bookings that overlap with the requested dates for active statuses.

---

### 4. Booking DTOs

#### BookingRequest
**File:** `dto/BookingRequest.java`

**Fields:**
- `vehicleId` - Integer (required, positive)
- `pickupDate` - LocalDate (required, future or present)
- `returnDate` - LocalDate (required, future)
- `pickupLocation` - String (required, not blank)
- `returnLocation` - String (required, not blank)

**Validation:**
- Jakarta Validation annotations
- Date validation (pickup must be today or future, return must be future)

#### BookingResponse
**File:** `dto/BookingResponse.java`

**Fields:**
- All booking details
- User information (id, name, email)
- Vehicle information (id, make, model, year, license plate, price)
- Vendor information (id, name)
- Calculated fields (total amount, dates, locations)

---

### 5. Booking Service
**File:** `service/BookingServiceImpl.java`

#### createBooking()
**Business Logic:**
1. Find user by email
2. Find vehicle by ID
3. Validate vehicle is AVAILABLE
4. Validate dates (return > pickup)
5. Check for conflicting bookings
6. Calculate total amount (days × price per day)
7. Create booking with PENDING status
8. Update vehicle status to BOOKED
9. Save booking and vehicle

**Conflict Detection:**
- Checks for bookings with statuses: PENDING, CONFIRMED, ACTIVE
- Validates date overlap using query
- Throws exception if conflict found

**Amount Calculation:**
```java
long days = ChronoUnit.DAYS.between(pickupDate, returnDate);
Double totalAmount = vehicle.getPricePerDay() * days;
```

#### getBookingById()
- Finds booking by ID
- Verifies ownership (user must own the booking)
- Returns booking details

#### getUserBookings()
- Gets all bookings for authenticated user
- Returns list of BookingResponse

#### cancelBooking()
**Business Logic:**
1. Find user and booking
2. Verify ownership
3. Check if booking can be cancelled (not already cancelled/completed)
4. Update booking status to CANCELLED
5. If booking was CONFIRMED or ACTIVE, update vehicle status back to AVAILABLE
6. Save changes

#### getVendorBookings()
- Gets all bookings for vendor's vehicles
- Uses custom query to find bookings by vendor ID
- Returns list of BookingResponse

---

### 6. Booking Controller
**File:** `controller/BookingController.java`

**Endpoints:**

1. **POST /api/bookings**
   - Creates a new booking
   - Requires authentication
   - Returns BookingResponse

2. **GET /api/bookings/{id}**
   - Gets booking by ID
   - Requires authentication
   - User must own the booking

3. **GET /api/bookings/user**
   - Gets all bookings for authenticated user
   - Requires authentication

4. **PUT /api/bookings/{id}/cancel**
   - Cancels a booking
   - Requires authentication
   - User must own the booking

5. **GET /api/bookings/vendor**
   - Gets all bookings for vendor's vehicles
   - Requires VENDOR role
   - Returns bookings for all vendor's vehicles

**Error Handling:**
- 400 Bad Request - Validation errors, business rule violations
- 401 Unauthorized - Missing or invalid token
- 403 Forbidden - Insufficient permissions
- 404 Not Found - Booking not found
- 500 Internal Server Error - Server errors

---

### 7. Security Configuration
**File:** `configuration/SecurityConfig.java`

**Booking Endpoints:**
```java
.requestMatchers(HttpMethod.POST, "/api/bookings").authenticated()
.requestMatchers(HttpMethod.GET, "/api/bookings/{id}").authenticated()
.requestMatchers(HttpMethod.GET, "/api/bookings/user").authenticated()
.requestMatchers(HttpMethod.PUT, "/api/bookings/{id}/cancel").authenticated()
.requestMatchers(HttpMethod.GET, "/api/bookings/vendor").hasAuthority(UserRole.VENDOR.name())
```

---

## Frontend Implementation

### 1. API Service
**File:** `services/api.js`

**Functions:**
```javascript
export const createBooking = (bookingData) => api.post("/bookings", bookingData);
export const getBookingById = (id) => api.get(`/bookings/${id}`);
export const getUserBookings = () => api.get("/bookings/user");
export const cancelBooking = (id) => api.put(`/bookings/${id}/cancel`);
export const getVendorBookings = () => api.get("/bookings/vendor");
```

---

### 2. BookingsPage (User)
**File:** `pages/Bookings/BookingsPage.jsx`

**Features:**
- Fetches user bookings from backend
- Displays bookings in tabs (Current/Past)
- Shows booking details with vehicle information
- Cancel booking functionality
- Image display with fallback
- Status badges
- Loading states
- Error handling

**State Management:**
- `bookings` - List of bookings
- `loading` - Loading state
- `activeTab` - Current tab (current/past)

**Functions:**
- `fetchBookings()` - Fetches bookings from backend
- `handleCancelBooking()` - Cancels a booking
- `calculateDays()` - Calculates days between dates
- `getStatusBadge()` - Returns status badge component
- `getImagePath()` - Resolves image path

---

### 3. VendorBookings (Vendor)
**File:** `pages/Vendor/VendorBookings.jsx`

**Features:**
- Fetches vendor bookings from backend
- Filter by status (All, PENDING, CONFIRMED, ACTIVE, COMPLETED, CANCELLED)
- Grid and table views
- Booking details modal
- Status mapping (backend enum to display)

**State Management:**
- `bookings` - List of bookings
- `loading` - Loading state
- `filteredStatus` - Current filter
- `selectedBooking` - Selected booking for modal

**Functions:**
- `fetchBookings()` - Fetches vendor bookings
- `getDisplayStatus()` - Maps backend status to display
- `getStatusColor()` - Returns badge color for status

---

### 4. PaymentPage (Booking Creation)
**File:** `pages/Payment/PaymentPage.jsx`

**Features:**
- Date selection (pickup and return)
- Location input (pickup and return)
- Payment method selection
- Card details form
- Price calculation
- Booking creation on payment

**Updated Flow:**
1. User selects dates and locations
2. User enters payment details
3. On "Pay" button click:
   - Creates booking via API
   - Shows success message
   - Redirects to bookings page

**Integration:**
- Uses `createBooking()` API function
- Passes vehicle data from CarDetailsPage
- Handles errors and validation

---

### 5. CarDetailsPage (Booking Initiation)
**File:** `pages/Cars/CarDetailsPage.jsx`

**Features:**
- Fetches vehicle from backend by ID
- Displays vehicle details
- "Book Now" button navigates to PaymentPage
- Passes vehicle data to PaymentPage

**Integration:**
- Uses `getVehicleById()` API function
- Maps backend vehicle data to frontend format
- Handles loading and error states

---

## API Endpoints

### POST /api/bookings
**Purpose:** Create a new booking

**Request Body:**
```json
{
  "vehicleId": 1,
  "pickupDate": "2025-12-10",
  "returnDate": "2025-12-15",
  "pickupLocation": "Mumbai Central",
  "returnLocation": "Mumbai Airport"
}
```

**Response:**
```json
{
  "id": 1,
  "userId": 5,
  "userName": "John Doe",
  "userEmail": "john@example.com",
  "vehicleId": 1,
  "vehicleMake": "Honda",
  "vehicleModel": "Accord",
  "pickupDate": "2025-12-10",
  "returnDate": "2025-12-15",
  "pickupLocation": "Mumbai Central",
  "returnLocation": "Mumbai Airport",
  "totalAmount": 17500.0,
  "status": "PENDING",
  "createdAt": "2025-01-26T23:00:00",
  "updatedAt": "2025-01-26T23:00:00"
}
```

**Validation:**
- Vehicle must exist
- Vehicle must be AVAILABLE
- Dates must be valid (return > pickup)
- No conflicting bookings
- Locations must be provided

---

### GET /api/bookings/{id}
**Purpose:** Get booking by ID

**Response:** BookingResponse object

**Security:**
- User must own the booking

---

### GET /api/bookings/user
**Purpose:** Get all bookings for authenticated user

**Response:** Array of BookingResponse objects

---

### PUT /api/bookings/{id}/cancel
**Purpose:** Cancel a booking

**Response:** Updated BookingResponse object

**Business Rules:**
- Cannot cancel already cancelled booking
- Cannot cancel completed booking
- Vehicle status updated to AVAILABLE if booking was CONFIRMED or ACTIVE

---

### GET /api/bookings/vendor
**Purpose:** Get all bookings for vendor's vehicles

**Response:** Array of BookingResponse objects

**Security:**
- Requires VENDOR role

---

## Business Rules

### 1. Booking Creation
- ✅ Vehicle must be AVAILABLE
- ✅ Pickup date must be today or in the future
- ✅ Return date must be after pickup date
- ✅ No conflicting bookings for the same vehicle and dates
- ✅ Total amount calculated as: days × price per day
- ✅ Vehicle status updated to BOOKED on creation

### 2. Booking Cancellation
- ✅ User can only cancel their own bookings
- ✅ Cannot cancel already cancelled booking
- ✅ Cannot cancel completed booking
- ✅ Vehicle status updated to AVAILABLE if booking was active

### 3. Date Validation
- ✅ Pickup date: `@FutureOrPresent`
- ✅ Return date: `@Future`
- ✅ Return date must be after pickup date (service-level validation)

### 4. Conflict Detection
- ✅ Checks for bookings with statuses: PENDING, CONFIRMED, ACTIVE
- ✅ Validates date overlap
- ✅ Prevents double booking

### 5. Amount Calculation
- ✅ Calculated as: `(returnDate - pickupDate) × pricePerDay`
- ✅ Minimum 1 day rental
- ✅ Stored in `totalAmount` field

---

## Testing

### Test Scenarios

1. **Create Booking:**
   - ✅ Valid booking creation
   - ✅ Invalid vehicle ID
   - ✅ Vehicle not available
   - ✅ Date conflicts
   - ✅ Invalid dates

2. **Get Booking:**
   - ✅ Get own booking
   - ✅ Get other user's booking (should fail)
   - ✅ Invalid booking ID

3. **Cancel Booking:**
   - ✅ Cancel pending booking
   - ✅ Cancel confirmed booking
   - ✅ Cancel already cancelled (should fail)
   - ✅ Cancel completed (should fail)

4. **Vendor Bookings:**
   - ✅ Vendor can see all bookings for their vehicles
   - ✅ Non-vendor cannot access vendor endpoint

---

## Error Handling

### Backend Errors
- **400 Bad Request:** Validation errors, business rule violations
- **401 Unauthorized:** Missing or invalid token
- **403 Forbidden:** Insufficient permissions
- **404 Not Found:** Booking/Vehicle not found
- **500 Internal Server Error:** Server errors

### Frontend Errors
- Network errors handled with try-catch
- User-friendly error messages
- Loading states
- Fallback UI for missing data

---

## Integration Points

### With Vehicle Module
- Updates vehicle status to BOOKED on booking creation
- Updates vehicle status to AVAILABLE on cancellation
- Checks vehicle availability before booking

### With Payment Module (Future)
- Booking created with PENDING status
- Payment will update booking to CONFIRMED
- One booking → One payment relationship

### With User Module
- User authentication required
- User ownership verification
- User information in booking response

---

## Summary

The Booking Module provides:
- ✅ Complete CRUD operations for bookings
- ✅ Business rule enforcement
- ✅ Date conflict detection
- ✅ Vehicle status management
- ✅ User and vendor access control
- ✅ Frontend-backend integration
- ✅ Error handling and validation
- ✅ Transaction management

**Status: ✅ FULLY IMPLEMENTED AND TESTED**

The booking module is production-ready and fully integrated with the vehicle and user modules.
