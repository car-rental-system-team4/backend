# Vehicle & Vendor Module Documentation

## Overview

The Vehicle & Vendor Module provides complete CRUD operations for vehicle management, vendor-vehicle relationships, availability status management, and vendor-specific dashboard APIs. This module follows the existing codebase patterns and integrates seamlessly with the authentication and authorization system.

## Table of Contents
1. [Architecture](#architecture)
2. [Core Concepts Used](#core-concepts-used)
3. [Design Patterns](#design-patterns)
4. [JPA Relationship Concepts](#jpa-relationship-concepts)
5. [Security Concepts](#security-concepts)
6. [Entity Relationships](#entity-relationships)
7. [API Endpoints](#api-endpoints)
8. [Backend Implementation](#backend-implementation)
9. [Frontend Implementation](#frontend-implementation)
10. [How It Works - Step by Step](#how-it-works---step-by-step)
11. [Interview Questions & Answers](#interview-questions--answers)
12. [Testing](#testing)

---

## Architecture

### Technology Stack
- **Framework**: Spring Boot 3.2.2 with Java 21
- **ORM**: JPA/Hibernate with MySQL
- **Security**: Spring Security with JWT authentication
- **Validation**: Jakarta Validation API
- **Libraries**: Spring Data JPA, Lombok, SpringDoc OpenAPI

### Module Structure

```
com.carrental/
├── entity/
│   └── Vehicle.java
├── enums/
│   └── VehicleStatus.java
├── repository/
│   └── VehicleRepository.java
├── dto/
│   ├── VehicleRequest.java
│   ├── VehicleResponse.java
│   └── VehicleStatusUpdateRequest.java
├── service/
│   ├── VehicleService.java
│   └── VehicleServiceImpl.java
└── controller/
    └── VehicleController.java
```

## Core Concepts Used

### 1. **Many-to-One Relationship**
**What it is**: Multiple vehicles belong to one vendor (User with VENDOR role).

**How it's implemented**:
```java
@Entity
public class Vehicle {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private User vendor;
}
```

**Database representation**:
```sql
vehicles table:
  id INT PRIMARY KEY
  vendor_id INT NOT NULL FOREIGN KEY REFERENCES users(id)
  ...
```

**Why Many-to-One?**
- **Business rule**: One vendor can own multiple vehicles
- **Data normalization**: Avoids duplicating vendor data in each vehicle
- **Referential integrity**: Foreign key ensures vendor exists

**Interview Question**: "Explain the Many-to-One relationship between Vehicle and User."
**Answer**: 
- **Many vehicles** belong to **one vendor** (User with VENDOR role)
- Implemented using `@ManyToOne` annotation on Vehicle entity
- Foreign key `vendor_id` in vehicles table references `users.id`
- LAZY fetching prevents loading vendor unless accessed
- Allows one vendor to manage multiple vehicles efficiently

---

### 2. **Ownership Verification Pattern**
**What it is**: Ensuring vendors can only manage their own vehicles.

**How it's implemented**:
```java
// In updateVehicle()
Vehicle vehicle = vehicleRepository.findById(vehicleId)
    .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

if (!vehicle.getVendor().getId().equals(vendor.getId())) {
    throw new IllegalArgumentException("You can only update your own vehicles");
}
```

**Why verify ownership?**
- **Security**: Prevents vendors from modifying other vendors' vehicles
- **Data integrity**: Ensures data consistency
- **Business rule**: Vendors should only manage their own inventory

**Interview Question**: "How do you ensure vendors only manage their own vehicles?"
**Answer**:
1. **Extract vendor from SecurityContext**: Get authenticated vendor email
2. **Load vendor from database**: Get vendor entity
3. **Load vehicle**: Get vehicle by ID
4. **Compare IDs**: Check if `vehicle.getVendor().getId().equals(vendor.getId())`
5. **Throw exception**: If IDs don't match, throw IllegalArgumentException

---

### 3. **Unique Constraint Validation**
**What it is**: Ensuring license plate and VIN are unique across all vehicles.

**How it's implemented**:
```java
// Check for duplicate license plate
Optional<Vehicle> existingVehicle = vehicleRepository.findByLicensePlate(request.getLicensePlate());
if (existingVehicle.isPresent()) {
    throw new IllegalArgumentException("License plate already exists");
}

// Check for duplicate VIN
Optional<Vehicle> existingVehicle = vehicleRepository.findByVin(request.getVin());
if (existingVehicle.isPresent()) {
    throw new IllegalArgumentException("VIN already exists");
}
```

**Why validate before save?**
- **Better error messages**: Service-level validation provides clear messages
- **Performance**: Avoids database constraint violation (slower)
- **User experience**: Can check before attempting save

**Database constraint** (backup):
```sql
ALTER TABLE vehicles ADD UNIQUE (license_plate);
ALTER TABLE vehicles ADD UNIQUE (vin);
```

**Interview Question**: "Why check for duplicates in service layer when database has unique constraint?"
**Answer**:
1. **Better error messages**: Service can provide "License plate already exists" vs database error code
2. **Performance**: Avoids database round-trip for constraint violation
3. **User experience**: Can validate before attempting save
4. **Defense in depth**: Database constraint is backup if service check fails

---

### 4. **Optional Pattern for Null Safety**
**What it is**: Using `Optional<T>` to handle potentially null values safely.

**How it's used**:
```java
Optional<Vehicle> existingVehicle = vehicleRepository.findByLicensePlate(licensePlate);
if (existingVehicle.isPresent()) {
    // Vehicle exists
    throw new IllegalArgumentException("License plate already exists");
}
// Vehicle doesn't exist, safe to proceed
```

**Why use Optional?**
- **Null safety**: Avoids NullPointerException
- **Explicit**: Makes "may or may not exist" clear
- **Functional style**: Can use `ifPresent()`, `orElse()`, etc.

**Alternative (without Optional)**:
```java
Vehicle existingVehicle = vehicleRepository.findByLicensePlate(licensePlate);
if (existingVehicle != null) {  // Risk of NPE if method returns null
    throw new IllegalArgumentException("License plate already exists");
}
```

---

### 5. **DTO to Entity Conversion**
**What it is**: Converting request DTOs to entities and entities to response DTOs.

**Request → Entity**:
```java
Vehicle vehicle = new Vehicle();
vehicle.setMake(request.getMake());
vehicle.setModel(request.getModel());
vehicle.setYear(request.getYear());
// ... map all fields
vehicle.setVendor(vendor);  // Set relationship
vehicle.setStatus(VehicleStatus.AVAILABLE);  // Set default
```

**Entity → Response**:
```java
VehicleResponse response = new VehicleResponse();
response.setId(vehicle.getId());
response.setMake(vehicle.getMake());
response.setVendorId(vehicle.getVendor().getId());
response.setVendorName(vehicle.getVendor().getName());
// ... map all fields
```

**Why manual mapping?**
- **Control**: Explicit control over what data is mapped
- **No dependencies**: No need for mapping libraries
- **Simple**: For small projects, manual mapping is sufficient

**Future enhancement**: Use MapStruct for compile-time mapping:
```java
@Mapper
public interface VehicleMapper {
    VehicleResponse toResponse(Vehicle vehicle);
    Vehicle toEntity(VehicleRequest request);
}
```

---

## Design Patterns

### 1. **CRUD Pattern**
**What it is**: Standard Create, Read, Update, Delete operations.

**Implementation**:
- **Create**: `addVehicle()` - POST
- **Read**: `getVehicleById()`, `getAllAvailableVehicles()`, `getVendorVehicles()` - GET
- **Update**: `updateVehicle()`, `updateVehicleStatus()` - PUT
- **Delete**: `deleteVehicle()` - DELETE

**Benefits**:
- **Standard**: Well-understood pattern
- **RESTful**: Follows REST conventions
- **Complete**: Covers all data operations

---

### 2. **Repository Pattern**
**Purpose**: Abstraction layer for data access.

**Implementation**:
```java
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    List<Vehicle> findByVendorId(Integer vendorId);
    List<Vehicle> findByStatus(VehicleStatus status);
    Optional<Vehicle> findByLicensePlate(String licensePlate);
    Optional<Vehicle> findByVin(String vin);
}
```

**Benefits**:
- **Testability**: Can mock repository in unit tests
- **Flexibility**: Can switch data sources
- **Query abstraction**: Business logic doesn't know about SQL

---

## JPA Relationship Concepts

### 1. **@ManyToOne with LAZY Fetching**
**Implementation**:
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "vendor_id", nullable = false)
private User vendor;
```

**How it works**:
- **LAZY**: Vendor not loaded until `vehicle.getVendor()` is called
- **Join column**: Foreign key column name
- **Nullable false**: Vehicle must have a vendor

**When vendor is loaded**:
```java
Vehicle vehicle = vehicleRepository.findById(1).get();
// Vendor NOT loaded yet

String vendorName = vehicle.getVendor().getName();
// NOW vendor is loaded (lazy loading triggered)
```

**Interview Question**: "What happens when you access vehicle.getVendor() with LAZY fetching?"
**Answer**:
1. **Proxy object**: Hibernate creates a proxy initially
2. **Access triggers load**: When you call `getVendor().getName()`, Hibernate executes SQL
3. **SQL query**: `SELECT * FROM users WHERE id = ?`
4. **LazyInitializationException**: If accessed outside transaction (common pitfall)

**Solution**: Keep transaction open or use `@Transactional` on service method.

---

### 2. **Bidirectional Relationship (Optional)**
**Current**: Unidirectional (Vehicle → User)

**Bidirectional would be**:
```java
// On User entity
@OneToMany(mappedBy = "vendor")
private List<Vehicle> vehicles;
```

**Why unidirectional?**
- **Simplicity**: Easier to understand
- **Performance**: Don't load vehicles when loading user
- **Sufficient**: Can query vehicles by vendor ID when needed

---

## Security Concepts

### 1. **Role-Based Access Control**
**How it's implemented**:
```java
// SecurityConfig
.requestMatchers(HttpMethod.POST, "/api/vehicles").hasAuthority(UserRole.VENDOR.name())
.requestMatchers(HttpMethod.PUT, "/api/vehicles/{id}").hasAuthority(UserRole.VENDOR.name())
.requestMatchers(HttpMethod.DELETE, "/api/vehicles/{id}").hasAuthority(UserRole.VENDOR.name())
.requestMatchers(HttpMethod.GET, "/api/vehicles/vendor").hasAuthority(UserRole.VENDOR.name())
```

**Two-level security**:
1. **SecurityConfig**: URL-level (first line of defense)
2. **Service method**: Ownership verification (second line of defense)

---

### 2. **Ownership-Based Authorization**
**Pattern**:
```java
// Verify vendor owns the vehicle
if (!vehicle.getVendor().getId().equals(vendor.getId())) {
    throw new IllegalArgumentException("You can only update your own vehicles");
}
```

**Why both URL-level and method-level?**
- **URL-level**: Prevents non-vendors from accessing endpoints
- **Method-level**: Prevents vendors from accessing other vendors' vehicles

---

## Entity Relationships

### Vehicle Entity

The `Vehicle` entity represents a car in the rental system with the following key features:

- **Many-to-One Relationship**: Each vehicle belongs to one vendor (User with VENDOR role)
- **Status Management**: Uses `VehicleStatus` enum for availability tracking
- **Audit Fields**: Automatically tracks `createdAt` and `updatedAt` timestamps
- **Unique Constraints**: `licensePlate` and `vin` must be unique across all vehicles

### Database Schema

```sql
CREATE TABLE vehicles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    make VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    year INT NOT NULL,
    color VARCHAR(255) NOT NULL,
    license_plate VARCHAR(255) NOT NULL UNIQUE,
    vin VARCHAR(255) NOT NULL UNIQUE,
    price_per_day DOUBLE NOT NULL,
    status VARCHAR(50) NOT NULL,
    fuel_type VARCHAR(50) NOT NULL,
    transmission VARCHAR(50) NOT NULL,
    seating_capacity INT NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    vendor_id INT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (vendor_id) REFERENCES users(id)
);
```

### Entity Relationship Diagram

```
┌─────────────┐         ┌──────────────┐
│    User     │         │   Vehicle    │
│  (Vendor)   │◄────────│              │
└─────────────┘         └──────────────┘
     │1                    │*
     │                      │
     └──────────────────────┘
      Many-to-One
      (One vendor has many vehicles)
```

## Vehicle Status Enum

The `VehicleStatus` enum defines the availability states of a vehicle:

- **AVAILABLE**: Vehicle is available for rental
- **RENTED**: Vehicle is currently rented out
- **MAINTENANCE**: Vehicle is under maintenance
- **UNAVAILABLE**: Vehicle is unavailable for other reasons

## API Endpoints

### Base URL
All vehicle endpoints are prefixed with `/api/vehicles`

### 1. Add Vehicle (Vendor Only)

**Endpoint**: `POST /api/vehicles`

**Authorization**: Requires VENDOR role

**Request Body**:
```json
{
  "make": "Toyota",
  "model": "Camry",
  "year": 2023,
  "color": "White",
  "licensePlate": "ABC1234",
  "vin": "1HGBH41JXMN109186",
  "pricePerDay": 1500.00,
  "fuelType": "PETROL",
  "transmission": "AUTOMATIC",
  "seatingCapacity": 5,
  "description": "Comfortable sedan with all features",
  "imageUrl": "https://example.com/camry.jpg"
}
```

**Response**: `200 OK`
```json
{
  "id": 1,
  "make": "Toyota",
  "model": "Camry",
  "year": 2023,
  "color": "White",
  "licensePlate": "ABC1234",
  "vin": "1HGBH41JXMN109186",
  "pricePerDay": 1500.00,
  "status": "AVAILABLE",
  "fuelType": "PETROL",
  "transmission": "AUTOMATIC",
  "seatingCapacity": 5,
  "description": "Comfortable sedan with all features",
  "imageUrl": "https://example.com/camry.jpg",
  "vendorId": 5,
  "vendorName": "John Doe",
  "createdAt": "2026-01-26T10:30:00",
  "updatedAt": "2026-01-26T10:30:00"
}
```

**Error Responses**:
- `400 Bad Request`: Invalid input data, duplicate license plate or VIN
- `401 Unauthorized`: Missing or invalid JWT token
- `403 Forbidden`: User is not a vendor

### 2. Update Vehicle (Vendor Only)

**Endpoint**: `PUT /api/vehicles/{id}`

**Authorization**: Requires VENDOR role and vehicle ownership

**Request Body**: Same as Add Vehicle

**Response**: `200 OK` (VehicleResponse)

**Error Responses**:
- `400 Bad Request`: Invalid input, duplicate license plate/VIN, or not vehicle owner
- `401 Unauthorized`: Missing or invalid JWT token
- `403 Forbidden`: User is not a vendor
- `404 Not Found`: Vehicle not found

### 3. Delete Vehicle (Vendor Only)

**Endpoint**: `DELETE /api/vehicles/{id}`

**Authorization**: Requires VENDOR role and vehicle ownership

**Response**: `200 OK`
```json
"Vehicle deleted successfully"
```

**Error Responses**:
- `400 Bad Request`: Not vehicle owner
- `401 Unauthorized`: Missing or invalid JWT token
- `403 Forbidden`: User is not a vendor
- `404 Not Found`: Vehicle not found

### 4. Get All Available Vehicles

**Endpoint**: `GET /api/vehicles`

**Authorization**: Requires authentication (any role)

**Response**: `200 OK`
```json
[
  {
    "id": 1,
    "make": "Toyota",
    "model": "Camry",
    "year": 2023,
    "color": "White",
    "licensePlate": "ABC1234",
    "vin": "1HGBH41JXMN109186",
    "pricePerDay": 1500.00,
    "status": "AVAILABLE",
    "fuelType": "PETROL",
    "transmission": "AUTOMATIC",
    "seatingCapacity": 5,
    "description": "Comfortable sedan with all features",
    "imageUrl": "https://example.com/camry.jpg",
    "vendorId": 5,
    "vendorName": "John Doe",
    "createdAt": "2026-01-26T10:30:00",
    "updatedAt": "2026-01-26T10:30:00"
  }
]
```

**Note**: Only returns vehicles with status `AVAILABLE`

### 5. Get Vehicle by ID

**Endpoint**: `GET /api/vehicles/{id}`

**Authorization**: Requires authentication (any role)

**Response**: `200 OK` (VehicleResponse)

**Error Responses**:
- `401 Unauthorized`: Missing or invalid JWT token
- `404 Not Found`: Vehicle not found

### 6. Get Vendor's Vehicles

**Endpoint**: `GET /api/vehicles/vendor`

**Authorization**: Requires VENDOR role

**Response**: `200 OK`
```json
[
  {
    "id": 1,
    "make": "Toyota",
    "model": "Camry",
    "status": "AVAILABLE",
    ...
  },
  {
    "id": 2,
    "make": "Honda",
    "model": "Accord",
    "status": "RENTED",
    ...
  }
]
```

**Note**: Returns all vehicles owned by the authenticated vendor, regardless of status

### 7. Update Vehicle Status (Vendor Only)

**Endpoint**: `PUT /api/vehicles/{id}/status`

**Authorization**: Requires VENDOR role and vehicle ownership

**Request Body**:
```json
{
  "status": "MAINTENANCE"
}
```

**Valid Status Values**: `AVAILABLE`, `RENTED`, `MAINTENANCE`, `UNAVAILABLE`

**Response**: `200 OK` (VehicleResponse)

**Error Responses**:
- `400 Bad Request`: Invalid status value, not vehicle owner
- `401 Unauthorized`: Missing or invalid JWT token
- `403 Forbidden`: User is not a vendor
- `404 Not Found`: Vehicle not found

## Security Configuration

The security configuration in `SecurityConfig.java` enforces the following rules:

- **GET `/api/vehicles`**: All authenticated users
- **GET `/api/vehicles/{id}`**: All authenticated users
- **POST `/api/vehicles`**: VENDOR role only
- **PUT `/api/vehicles/{id}`**: VENDOR role only
- **DELETE `/api/vehicles/{id}`**: VENDOR role only
- **PUT `/api/vehicles/{id}/status`**: VENDOR role only
- **GET `/api/vehicles/vendor`**: VENDOR role only

Additionally, the service layer validates:
- Vendor role before allowing operations
- Vehicle ownership before update/delete operations
- Duplicate license plate and VIN checks

## Business Logic

### Vehicle Creation
1. Validates vendor role
2. Checks for duplicate license plate
3. Checks for duplicate VIN
4. Sets default status to `AVAILABLE`
5. Automatically sets `createdAt` and `updatedAt` timestamps
6. Associates vehicle with authenticated vendor

### Vehicle Update
1. Validates vendor role
2. Verifies vehicle ownership
3. Checks for duplicate license plate/VIN (if changed)
4. Updates all provided fields
5. Automatically updates `updatedAt` timestamp

### Vehicle Deletion
1. Validates vendor role
2. Verifies vehicle ownership
3. Permanently deletes vehicle from database

### Status Management
1. Validates vendor role
2. Verifies vehicle ownership
3. Validates status enum value
4. Updates vehicle status
5. Automatically updates `updatedAt` timestamp

## Usage Examples

### Example 1: Vendor Adding a Vehicle

```bash
curl -X POST http://localhost:8080/api/vehicles \
  -H "Authorization: Bearer <vendor_jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "make": "Toyota",
    "model": "Camry",
    "year": 2023,
    "color": "White",
    "licensePlate": "ABC1234",
    "vin": "1HGBH41JXMN109186",
    "pricePerDay": 1500.00,
    "fuelType": "PETROL",
    "transmission": "AUTOMATIC",
    "seatingCapacity": 5,
    "description": "Comfortable sedan",
    "imageUrl": "https://example.com/camry.jpg"
  }'
```

### Example 2: Customer Viewing Available Vehicles

```bash
curl -X GET http://localhost:8080/api/vehicles \
  -H "Authorization: Bearer <customer_jwt_token>"
```

### Example 3: Vendor Updating Vehicle Status

```bash
curl -X PUT http://localhost:8080/api/vehicles/1/status \
  -H "Authorization: Bearer <vendor_jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "MAINTENANCE"
  }'
```

### Example 4: Vendor Viewing Their Vehicles

```bash
curl -X GET http://localhost:8080/api/vehicles/vendor \
  -H "Authorization: Bearer <vendor_jwt_token>"
```

## Validation Rules

### VehicleRequest Validation
- `make`: Required, not blank
- `model`: Required, not blank
- `year`: Required, minimum 1900
- `color`: Required, not blank
- `licensePlate`: Required, not blank, unique
- `vin`: Required, not blank, unique
- `pricePerDay`: Required, positive number
- `fuelType`: Required, not blank
- `transmission`: Required, not blank
- `seatingCapacity`: Required, minimum 1
- `description`: Optional
- `imageUrl`: Optional

### VehicleStatusUpdateRequest Validation
- `status`: Required, must be one of: AVAILABLE, RENTED, MAINTENANCE, UNAVAILABLE

## Error Handling

The module follows consistent error handling patterns:

- **400 Bad Request**: Invalid input, business rule violations (duplicate license plate/VIN, ownership issues)
- **401 Unauthorized**: Missing or invalid JWT token
- **403 Forbidden**: Insufficient permissions (not a vendor)
- **404 Not Found**: Vehicle not found
- **500 Internal Server Error**: Unexpected server errors

All error responses include descriptive messages to help clients understand the issue.

## Database Considerations

### Indexes
The following indexes are automatically created by JPA:
- Primary key on `id`
- Unique index on `license_plate`
- Unique index on `vin`
- Foreign key index on `vendor_id`

### Cascade Behavior
- No cascade delete: Vehicles are not automatically deleted when a vendor is deleted
- This prevents accidental data loss
- Consider soft delete or setting vendor to null if needed

### Performance
- Uses `LAZY` fetching for vendor relationship to avoid N+1 queries
- Repository methods use indexed columns for efficient queries
- Consider pagination for large vehicle lists (future enhancement)

## How It Works - Step by Step

### Example: Vendor Adds a Vehicle

**1. Frontend Request**:
```javascript
const vehicleData = {
    make: "Toyota",
    model: "Camry",
    year: 2023,
    licensePlate: "ABC1234",
    vin: "1HGBH41JXMN109186",
    pricePerDay: 1500.00,
    // ... other fields
};
await addVehicle(vehicleData);
```

**2. Controller**:
```java
@PostMapping
public ResponseEntity<?> addVehicle(@RequestBody VehicleRequest request) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String vendorEmail = auth.getName();
    
    VehicleResponse response = vehicleService.addVehicle(vendorEmail, request);
    return ResponseEntity.ok(response);
}
```

**3. Service Method**:
```java
public VehicleResponse addVehicle(String vendorEmail, VehicleRequest request) {
    // Step 1: Find vendor
    User vendor = userRepository.findByEmail(vendorEmail)
        .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));
    
    // Step 2: Verify vendor role
    if (vendor.getRole() != UserRole.VENDOR) {
        throw new IllegalArgumentException("Only vendors can add vehicles");
    }
    
    // Step 3: Check for duplicate license plate
    if (vehicleRepository.findByLicensePlate(request.getLicensePlate()).isPresent()) {
        throw new IllegalArgumentException("License plate already exists");
    }
    
    // Step 4: Check for duplicate VIN
    if (vehicleRepository.findByVin(request.getVin()).isPresent()) {
        throw new IllegalArgumentException("VIN already exists");
    }
    
    // Step 5: Create vehicle entity
    Vehicle vehicle = new Vehicle();
    vehicle.setMake(request.getMake());
    vehicle.setModel(request.getModel());
    // ... set all fields
    vehicle.setVendor(vendor);  // Set relationship
    vehicle.setStatus(VehicleStatus.AVAILABLE);  // Default status
    
    // Step 6: Save vehicle (@PrePersist sets createdAt, updatedAt)
    Vehicle savedVehicle = vehicleRepository.save(vehicle);
    
    // Step 7: Convert to DTO
    return convertToResponse(savedVehicle);
}
```

**4. Entity Lifecycle**:
```java
@PrePersist
protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
    if (status == null) {
        status = VehicleStatus.AVAILABLE;
    }
}
```

**5. Database Insert**:
```sql
INSERT INTO vehicles (make, model, year, license_plate, vin, vendor_id, status, ...)
VALUES ('Toyota', 'Camry', 2023, 'ABC1234', '1HGBH41JXMN109186', 5, 'AVAILABLE', ...);
```

---

## Interview Questions & Answers

### Q1: "How do you ensure vendors only manage their own vehicles?"
**Answer**: 
1. **Extract vendor from SecurityContext**: Get authenticated vendor email
2. **Load vendor entity**: Get vendor from database
3. **Load vehicle**: Get vehicle by ID
4. **Compare IDs**: Check if `vehicle.getVendor().getId().equals(vendor.getId())`
5. **Throw exception**: If IDs don't match, throw IllegalArgumentException

**Code**:
```java
if (!vehicle.getVendor().getId().equals(vendor.getId())) {
    throw new IllegalArgumentException("You can only update your own vehicles");
}
```

---

### Q2: "Explain the Many-to-One relationship between Vehicle and User."
**Answer**:
- **Relationship**: Many vehicles belong to one vendor (User with VENDOR role)
- **Implementation**: `@ManyToOne` annotation on Vehicle entity
- **Database**: Foreign key `vendor_id` in vehicles table references `users.id`
- **Fetch type**: LAZY (vendor not loaded until accessed)
- **Benefits**: One vendor can manage multiple vehicles, avoids data duplication

**Database representation**:
```
users table:        vehicles table:
id | name           id | make | vendor_id
1  | Vendor1        1  | Toyota | 1
                   2  | Honda | 1
                   3  | BMW | 1
```

---

### Q3: "How do you prevent duplicate license plates and VINs?"
**Answer**:
1. **Service-level check**: Query repository before saving
2. **Database constraint**: UNIQUE constraint on columns (backup)
3. **Exception handling**: Throw clear error message if duplicate found

**Code**:
```java
Optional<Vehicle> existing = vehicleRepository.findByLicensePlate(licensePlate);
if (existing.isPresent()) {
    throw new IllegalArgumentException("License plate already exists");
}
```

**Why both?**
- **Service check**: Better error messages, faster feedback
- **Database constraint**: Final safety net, prevents race conditions

---

### Q4: "What happens when a vendor is deleted?"
**Answer**:
**Current implementation**: No cascade delete
- Vehicles remain in database with `vendor_id` pointing to deleted user
- **Issue**: Orphaned vehicles (foreign key constraint may prevent deletion)

**Solutions**:
1. **Cascade delete**: `@ManyToOne(cascade = CascadeType.REMOVE)` - deletes vehicles when vendor deleted
2. **Soft delete**: Mark vendor as deleted, keep vehicles
3. **Set to null**: Set `vendor_id` to NULL when vendor deleted
4. **Prevent deletion**: Don't allow vendor deletion if vehicles exist

**Best practice**: Use soft delete or prevent deletion if relationships exist.

---

### Q5: "How does vehicle status affect booking availability?"
**Answer**:
**Status values**:
- **AVAILABLE**: Can be booked
- **RENTED**: Currently rented, cannot be booked
- **MAINTENANCE**: Under maintenance, cannot be booked
- **UNAVAILABLE**: Unavailable, cannot be booked

**In booking creation**:
```java
if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
    throw new IllegalArgumentException("Vehicle is not available for booking");
}
```

**Status updates**:
- **Booking created**: Vehicle status → RENTED (or BOOKED)
- **Booking cancelled**: Vehicle status → AVAILABLE
- **Vendor manually**: Can set to MAINTENANCE or UNAVAILABLE

---

### Q6: "Explain the DTO to Entity conversion process."
**Answer**:
**Request DTO → Entity**:
```java
Vehicle vehicle = new Vehicle();
vehicle.setMake(request.getMake());
vehicle.setModel(request.getModel());
vehicle.setVendor(vendor);  // Set relationship
vehicle.setStatus(VehicleStatus.AVAILABLE);  // Set default
```

**Entity → Response DTO**:
```java
VehicleResponse response = new VehicleResponse();
response.setId(vehicle.getId());
response.setMake(vehicle.getMake());
response.setVendorId(vehicle.getVendor().getId());
response.setVendorName(vehicle.getVendor().getName());
```

**Why manual?**
- **Control**: Explicit mapping
- **No dependencies**: No mapping libraries needed
- **Simple**: Sufficient for small projects

**Future**: Use MapStruct for automatic mapping.

---

### Q7: "How do you handle image uploads for vehicles?"
**Answer**:
**Current implementation**: Store image path/URL in `imageUrl` field

**Options**:
1. **Local storage**: Store images in `public/` folder, save filename
2. **Cloud storage**: Upload to S3/Cloudinary, save URL
3. **Base64**: Encode image, store in database (not recommended for large images)

**Frontend**:
```javascript
const formData = new FormData();
formData.append('image', selectedFile);
// Upload to backend endpoint
```

**Backend** (future):
```java
@PostMapping("/vehicles/{id}/image")
public ResponseEntity<?> uploadImage(@PathVariable Integer id, 
                                     @RequestParam("file") MultipartFile file) {
    // Save file to disk or cloud
    // Update vehicle.imageUrl
}
```

---

## Testing Guidelines

### Unit Tests
- Test service layer methods with mocked repositories
- Test validation logic
- Test ownership verification
- Test duplicate detection

### Integration Tests
- Test controller endpoints with Spring Boot Test
- Test security configuration
- Test database operations
- Test error scenarios

### Test Scenarios
1. Vendor can add vehicle
2. Non-vendor cannot add vehicle
3. Vendor can only update/delete own vehicles
4. Duplicate license plate/VIN detection
5. Status transitions
6. Available vehicles filtering

## Future Enhancements

1. **Booking History**: Add endpoint to get booking history for vendor's vehicles (when Booking module is implemented)
2. **Multiple Images**: Support multiple images per vehicle
3. **Search & Filter**: Add search by make, model, price range, etc.
4. **Pagination**: Implement pagination for vehicle listings
5. **Soft Delete**: Implement soft delete instead of hard delete
6. **Vehicle Categories**: Add vehicle categories (SUV, Sedan, Hatchback, etc.)
7. **Location**: Add location/address fields for vehicles
8. **Ratings**: Add vehicle ratings and reviews

## Integration with Other Modules

### Authentication Module
- Uses JWT authentication from existing `JwtFilter`
- Extracts user email from `SecurityContextHolder`
- Validates user roles using `UserRole` enum

### User Module
- References `User` entity for vendor relationship
- Uses `UserRepository` to fetch vendor information
- Validates vendor role before operations

### Future Booking Module
- Vehicle status will be updated to `RENTED` when booked
- Booking history will reference vehicle ID
- Vehicle availability will be checked before booking

## Code Patterns

### Service Layer Pattern
```java
@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    
    // Methods follow pattern:
    // 1. Validate vendor role
    // 2. Verify ownership (if applicable)
    // 3. Perform business logic
    // 4. Convert entity to DTO
    // 5. Return response
}
```

### Controller Pattern
```java
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@CrossOrigin("*")
public class VehicleController {
    // Methods follow pattern:
    // 1. Extract authenticated user from SecurityContext
    // 2. Call service method
    // 3. Handle exceptions
    // 4. Return appropriate HTTP response
}
```

### DTO Pattern
- Request DTOs use Jakarta Validation annotations
- Response DTOs include all relevant fields
- Separate DTOs for different operations (status update)

## Conclusion

The Vehicle & Vendor Module provides a complete, secure, and well-structured solution for vehicle management in the car rental system. It follows Spring Boot best practices, integrates seamlessly with the existing authentication system, and provides a solid foundation for future enhancements.
