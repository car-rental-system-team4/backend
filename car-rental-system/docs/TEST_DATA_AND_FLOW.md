# Test Data and Flow Guide

This document provides comprehensive test data and step-by-step test flows for the Car Rental System. Use this to test the complete system functionality.

---

## Table of Contents
1. [Test Users](#test-users)
2. [Test Vehicles](#test-vehicles)
3. [Test Bookings](#test-bookings)
4. [Test Payments](#test-payments)
5. [Test Reviews](#test-reviews)
6. [Test Complaints](#test-complaints)
7. [Complete Test Flow](#complete-test-flow)
8. [API Testing with cURL](#api-testing-with-curl)
9. [Frontend Testing Flow](#frontend-testing-flow)

---

## Test Users

### 1. Admin User
**Purpose**: Test admin functionality

**Registration API Payload**:
```json
POST /api/auth/register
{
  "name": "Admin User",
  "email": "admin@test.com",
  "password": "admin123",
  "phoneNo": "9876543210",
  "licenseNo": "ADMIN001",
  "aadharNo": "123456789012",
  "houseNo": "101",
  "buildingName": "Admin Building",
  "streetName": "Admin Street",
  "area": "Admin Area",
  "pincode": "400001",
  "role": "ADMIN",
  "gender": "MALE"
}
```

**Login Credentials**:
- Email: `admin@test.com`
- Password: `admin123`
- Role: `ADMIN`
- Status: `APPROVED` (auto-approved)

**Expected Response**:
```json
{
  "token": "eyJhbGci...",
  "role": "ADMIN",
  "name": "Admin User",
  "userId": 1,
  "email": "admin@test.com"
}
```

---

### 2. Vendor User
**Purpose**: Test vendor functionality

**Registration API Payload**:
```json
POST /api/auth/register
{
  "name": "Vendor One",
  "email": "vendor1@test.com",
  "password": "vendor123",
  "phoneNo": "9876543211",
  "licenseNo": "VENDOR001",
  "aadharNo": "123456789013",
  "houseNo": "201",
  "buildingName": "Vendor Building",
  "streetName": "Vendor Street",
  "area": "Vendor Area",
  "pincode": "400002",
  "role": "VENDOR",
  "gender": "MALE"
}
```

**Login Credentials**:
- Email: `vendor1@test.com`
- Password: `vendor123`
- Role: `VENDOR`
- Status: `PENDING` (needs admin approval)

**Note**: Admin must approve this user before vendor can login and add vehicles.

---

### 3. Customer User
**Purpose**: Test customer functionality

**Registration API Payload**:
```json
POST /api/auth/register
{
  "name": "John Customer",
  "email": "customer1@test.com",
  "password": "customer123",
  "phoneNo": "9876543212",
  "licenseNo": "CUSTOMER001",
  "aadharNo": "123456789014",
  "houseNo": "301",
  "buildingName": "Customer Building",
  "streetName": "Customer Street",
  "area": "Customer Area",
  "pincode": "400003",
  "role": "CUSTOMER",
  "gender": "MALE"
}
```

**Login Credentials**:
- Email: `customer1@test.com`
- Password: `customer123`
- Role: `CUSTOMER`
- Status: `PENDING` (needs admin approval)

---

### 4. Additional Test Users

**Vendor 2**:
```json
{
  "name": "Vendor Two",
  "email": "vendor2@test.com",
  "password": "vendor123",
  "phoneNo": "9876543213",
  "licenseNo": "VENDOR002",
  "aadharNo": "123456789015",
  "houseNo": "202",
  "buildingName": "Vendor 2 Building",
  "streetName": "Vendor 2 Street",
  "area": "Vendor 2 Area",
  "pincode": "400004",
  "role": "VENDOR",
  "gender": "FEMALE"
}
```

**Customer 2**:
```json
{
  "name": "Jane Customer",
  "email": "customer2@test.com",
  "password": "customer123",
  "phoneNo": "9876543214",
  "licenseNo": "CUSTOMER002",
  "aadharNo": "123456789016",
  "houseNo": "302",
  "buildingName": "Customer 2 Building",
  "streetName": "Customer 2 Street",
  "area": "Customer 2 Area",
  "pincode": "400005",
  "role": "CUSTOMER",
  "gender": "FEMALE"
}
```

---

## Test Vehicles

### Vehicle 1 - Honda Accord
**Purpose**: Test vehicle CRUD operations

**Add Vehicle API Payload** (Vendor):
```json
POST /api/vehicles
Authorization: Bearer <vendor_token>

{
  "make": "Honda",
  "model": "Accord",
  "year": 2023,
  "color": "White",
  "licensePlate": "MH01AB1234",
  "vin": "1HGBH41JXMN109186",
  "pricePerDay": 3500.00,
  "fuelType": "PETROL",
  "transmission": "AUTOMATIC",
  "seatingCapacity": 5,
  "description": "Premium sedan with excellent comfort and features. Perfect for long drives and business trips.",
  "imageUrl": "Accord.jpg"
}
```

**Expected Response**:
```json
{
  "id": 1,
  "make": "Honda",
  "model": "Accord",
  "year": 2023,
  "color": "White",
  "licensePlate": "MH01AB1234",
  "vin": "1HGBH41JXMN109186",
  "pricePerDay": 3500.00,
  "status": "AVAILABLE",
  "fuelType": "PETROL",
  "transmission": "AUTOMATIC",
  "seatingCapacity": 5,
  "description": "Premium sedan...",
  "imageUrl": "Accord.jpg",
  "vendorId": 2,
  "vendorName": "Vendor One"
}
```

---

### Vehicle 2 - Maruti Swift
**Add Vehicle API Payload**:
```json
POST /api/vehicles
{
  "make": "Maruti",
  "model": "Swift",
  "year": 2024,
  "color": "Red",
  "licensePlate": "MH01CD5678",
  "vin": "MAZ12345678901234",
  "pricePerDay": 1500.00,
  "fuelType": "PETROL",
  "transmission": "MANUAL",
  "seatingCapacity": 5,
  "description": "Compact and fuel-efficient hatchback. Perfect for city driving.",
  "imageUrl": "Swift.jpg"
}
```

---

### Vehicle 3 - Mahindra XUV500
**Add Vehicle API Payload**:
```json
POST /api/vehicles
{
  "make": "Mahindra",
  "model": "XUV500",
  "year": 2023,
  "color": "Black",
  "licensePlate": "MH01EF9012",
  "vin": "MAH98765432109876",
  "pricePerDay": 4500.00,
  "fuelType": "DIESEL",
  "transmission": "AUTOMATIC",
  "seatingCapacity": 7,
  "description": "Spacious 7-seater SUV. Perfect for family trips and group travel.",
  "imageUrl": "XUV500.jpg"
}
```

---

### Vehicle 4 - Toyota Fortuner
**Add Vehicle API Payload**:
```json
POST /api/vehicles
{
  "make": "Toyota",
  "model": "Fortuner",
  "year": 2024,
  "color": "Silver",
  "licensePlate": "MH01GH3456",
  "vin": "TOY11223344556677",
  "pricePerDay": 5000.00,
  "fuelType": "DIESEL",
  "transmission": "AUTOMATIC",
  "seatingCapacity": 7,
  "description": "Premium SUV with excellent off-road capabilities.",
  "imageUrl": "Fortuner.jpg"
}
```

---

### Vehicle 5 - Hyundai Creta
**Add Vehicle API Payload**:
```json
POST /api/vehicles
{
  "make": "Hyundai",
  "model": "Creta",
  "year": 2024,
  "color": "Blue",
  "licensePlate": "MH01IJ7890",
  "vin": "HYU22334455667788",
  "pricePerDay": 3000.00,
  "fuelType": "PETROL",
  "transmission": "AUTOMATIC",
  "seatingCapacity": 5,
  "description": "Modern compact SUV with advanced features.",
  "imageUrl": "creta.jpg"
}
```

---

## Test Bookings

### Booking 1 - Active Booking
**Purpose**: Test active booking flow

**Create Booking API Payload** (Customer):
```json
POST /api/bookings
Authorization: Bearer <customer_token>

{
  "vehicleId": 1,
  "pickupDate": "2025-12-10",
  "returnDate": "2025-12-15",
  "pickupLocation": "Mumbai Central Station",
  "returnLocation": "Mumbai Airport"
}
```

**Expected Response**:
```json
{
  "id": 1,
  "userId": 3,
  "userName": "John Customer",
  "userEmail": "customer1@test.com",
  "vehicleId": 1,
  "vehicleMake": "Honda",
  "vehicleModel": "Accord",
  "pickupDate": "2025-12-10",
  "returnDate": "2025-12-15",
  "pickupLocation": "Mumbai Central Station",
  "returnLocation": "Mumbai Airport",
  "totalAmount": 17500.00,
  "status": "PENDING",
  "createdAt": "2025-01-26T10:00:00"
}
```

**Calculation**: 5 days × ₹3,500/day = ₹17,500

---

### Booking 2 - Completed Booking
**Create Booking API Payload**:
```json
POST /api/bookings
{
  "vehicleId": 2,
  "pickupDate": "2025-01-01",
  "returnDate": "2025-01-05",
  "pickupLocation": "Delhi Airport",
  "returnLocation": "Delhi Railway Station"
}
```

**Calculation**: 4 days × ₹1,500/day = ₹6,000

---

### Booking 3 - Pending Booking
**Create Booking API Payload**:
```json
POST /api/bookings
{
  "vehicleId": 3,
  "pickupDate": "2025-12-20",
  "returnDate": "2025-12-25",
  "pickupLocation": "Bangalore City Center",
  "returnLocation": "Bangalore Airport"
}
```

**Calculation**: 5 days × ₹4,500/day = ₹22,500

---

## Test Payments

### Payment 1 - Completed Payment
**Purpose**: Test payment flow

**Create Payment API Payload** (Customer):
```json
POST /api/payments
Authorization: Bearer <customer_token>

{
  "bookingId": 1,
  "paymentMethod": "UPI",
  "transactionId": "UPI123456789012345"
}
```

**Expected Response**:
```json
{
  "id": 1,
  "bookingId": 1,
  "amount": 17500.00,
  "paymentMethod": "UPI",
  "status": "COMPLETED",
  "transactionId": "UPI123456789012345",
  "paymentDate": "2025-01-26T10:05:00",
  "createdAt": "2025-01-26T10:05:00"
}
```

**Note**: After payment, booking status automatically changes to `CONFIRMED`.

---

### Payment 2 - Completed Payment
**Create Payment API Payload**:
```json
POST /api/payments
{
  "bookingId": 2,
  "paymentMethod": "UPI",
  "transactionId": "UPI987654321098765"
}
```

---

## Test Reviews

### Review 1 - Approved Review
**Purpose**: Test review submission and approval

**Create Review API Payload** (Customer):
```json
POST /api/reviews
Authorization: Bearer <customer_token>

{
  "vehicleId": 1,
  "rating": 5,
  "comment": "Excellent car! Very comfortable and smooth ride. Highly recommended."
}
```

**Expected Response**:
```json
{
  "id": 1,
  "userId": 3,
  "userName": "John Customer",
  "vehicleId": 1,
  "vehicleMake": "Honda",
  "vehicleModel": "Accord",
  "rating": 5,
  "comment": "Excellent car! Very comfortable and smooth ride. Highly recommended.",
  "status": "PENDING",
  "createdAt": "2025-01-26T11:00:00"
}
```

**Admin Approval**:
```json
PUT /api/reviews/1/approve
Authorization: Bearer <admin_token>
```

---

### Review 2 - Approved Review
**Create Review API Payload**:
```json
POST /api/reviews
{
  "vehicleId": 2,
  "rating": 4,
  "comment": "Good car for city driving. Fuel efficient and easy to handle."
}
```

---

### Review 3 - Rejected Review
**Create Review API Payload**:
```json
POST /api/reviews
{
  "vehicleId": 1,
  "rating": 1,
  "comment": "Bad experience"
}
```

**Admin Rejection**:
```json
PUT /api/reviews/3/reject
Authorization: Bearer <admin_token>
```

---

## Test Complaints

### Complaint 1 - Resolved Complaint
**Purpose**: Test complaint submission and resolution

**Create Complaint API Payload** (Customer):
```json
POST /api/complaints
Authorization: Bearer <customer_token>

{
  "subject": "Vehicle condition issue",
  "description": "The car had scratches that were not mentioned in the listing. The interior was also not clean.",
  "bookingId": 1
}
```

**Expected Response**:
```json
{
  "id": 1,
  "userId": 3,
  "userName": "John Customer",
  "subject": "Vehicle condition issue",
  "description": "The car had scratches...",
  "bookingId": 1,
  "status": "PENDING",
  "createdAt": "2025-01-26T12:00:00"
}
```

**Admin Resolution**:
```json
PUT /api/complaints/1/resolve
Authorization: Bearer <admin_token>

{
  "adminResponse": "We apologize for the inconvenience. We have inspected the vehicle and addressed the issues. A discount of ₹500 has been applied to your next booking."
}
```

---

### Complaint 2 - Pending Complaint
**Create Complaint API Payload**:
```json
POST /api/complaints
{
  "subject": "Late vehicle delivery",
  "description": "The vehicle was delivered 2 hours late, causing inconvenience.",
  "bookingId": 2
}
```

---

## Complete Test Flow

### Flow 1: Complete Booking and Payment Flow

**Step 1: Register Users**
```bash
# Register Admin
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Admin User",
    "email": "admin@test.com",
    "password": "admin123",
    "phoneNo": "9876543210",
    "licenseNo": "ADMIN001",
    "aadharNo": "123456789012",
    "houseNo": "101",
    "buildingName": "Admin Building",
    "streetName": "Admin Street",
    "area": "Admin Area",
    "pincode": "400001",
    "role": "ADMIN",
    "gender": "MALE"
  }'

# Register Vendor
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Vendor One",
    "email": "vendor1@test.com",
    "password": "vendor123",
    "phoneNo": "9876543211",
    "licenseNo": "VENDOR001",
    "aadharNo": "123456789013",
    "houseNo": "201",
    "buildingName": "Vendor Building",
    "streetName": "Vendor Street",
    "area": "Vendor Area",
    "pincode": "400002",
    "role": "VENDOR",
    "gender": "MALE"
  }'

# Register Customer
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Customer",
    "email": "customer1@test.com",
    "password": "customer123",
    "phoneNo": "9876543212",
    "licenseNo": "CUSTOMER001",
    "aadharNo": "123456789014",
    "houseNo": "301",
    "buildingName": "Customer Building",
    "streetName": "Customer Street",
    "area": "Customer Area",
    "pincode": "400003",
    "role": "CUSTOMER",
    "gender": "MALE"
  }'
```

**Step 2: Admin Approves Users**
```bash
# Login as Admin
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@test.com",
    "password": "admin123"
  }'
# Save the token from response

# Approve Vendor
curl -X PUT http://localhost:8080/api/admin/users/2/approve \
  -H "Authorization: Bearer <admin_token>"

# Approve Customer
curl -X PUT http://localhost:8080/api/admin/users/3/approve \
  -H "Authorization: Bearer <admin_token>"
```

**Step 3: Vendor Adds Vehicle**
```bash
# Login as Vendor
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "vendor1@test.com",
    "password": "vendor123"
  }'
# Save the token

# Add Vehicle
curl -X POST http://localhost:8080/api/vehicles \
  -H "Authorization: Bearer <vendor_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "make": "Honda",
    "model": "Accord",
    "year": 2023,
    "color": "White",
    "licensePlate": "MH01AB1234",
    "vin": "1HGBH41JXMN109186",
    "pricePerDay": 3500.00,
    "fuelType": "PETROL",
    "transmission": "AUTOMATIC",
    "seatingCapacity": 5,
    "description": "Premium sedan with excellent comfort.",
    "imageUrl": "Accord.jpg"
  }'
```

**Step 4: Customer Creates Booking**
```bash
# Login as Customer
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "customer1@test.com",
    "password": "customer123"
  }'
# Save the token

# Create Booking
curl -X POST http://localhost:8080/api/bookings \
  -H "Authorization: Bearer <customer_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "vehicleId": 1,
    "pickupDate": "2025-12-10",
    "returnDate": "2025-12-15",
    "pickupLocation": "Mumbai Central Station",
    "returnLocation": "Mumbai Airport"
  }'
# Save booking ID from response
```

**Step 5: Customer Makes Payment**
```bash
# Create Payment
curl -X POST http://localhost:8080/api/payments \
  -H "Authorization: Bearer <customer_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "bookingId": 1,
    "paymentMethod": "UPI",
    "transactionId": "UPI123456789012345"
  }'
```

**Step 6: Customer Submits Review**
```bash
# Create Review
curl -X POST http://localhost:8080/api/reviews \
  -H "Authorization: Bearer <customer_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "vehicleId": 1,
    "rating": 5,
    "comment": "Excellent car! Very comfortable and smooth ride."
  }'
```

**Step 7: Admin Approves Review**
```bash
# Approve Review
curl -X PUT http://localhost:8080/api/reviews/1/approve \
  -H "Authorization: Bearer <admin_token>"
```

**Step 8: Customer Submits Complaint**
```bash
# Create Complaint
curl -X POST http://localhost:8080/api/complaints \
  -H "Authorization: Bearer <customer_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "subject": "Vehicle condition issue",
    "description": "The car had scratches that were not mentioned.",
    "bookingId": 1
  }'
```

**Step 9: Admin Resolves Complaint**
```bash
# Resolve Complaint
curl -X PUT http://localhost:8080/api/complaints/1/resolve \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "adminResponse": "We apologize for the inconvenience. We have addressed the issues."
  }'
```

---

## API Testing with cURL

### Authentication Endpoints

**Register User**:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "password123",
    "phoneNo": "9876543210",
    "licenseNo": "TEST001",
    "aadharNo": "123456789012",
    "houseNo": "101",
    "buildingName": "Test Building",
    "streetName": "Test Street",
    "area": "Test Area",
    "pincode": "400001",
    "role": "CUSTOMER",
    "gender": "MALE"
  }'
```

**Login**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

**Get Profile**:
```bash
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer <token>"
```

**Update Profile**:
```bash
curl -X PUT http://localhost:8080/api/auth/profile \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Name",
    "phoneNo": "9876543211"
  }'
```

---

### Vehicle Endpoints

**Get All Available Vehicles**:
```bash
curl -X GET http://localhost:8080/api/vehicles \
  -H "Authorization: Bearer <token>"
```

**Get Vehicle by ID**:
```bash
curl -X GET http://localhost:8080/api/vehicles/1 \
  -H "Authorization: Bearer <token>"
```

**Add Vehicle (Vendor)**:
```bash
curl -X POST http://localhost:8080/api/vehicles \
  -H "Authorization: Bearer <vendor_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "make": "Honda",
    "model": "Accord",
    "year": 2023,
    "color": "White",
    "licensePlate": "MH01AB1234",
    "vin": "1HGBH41JXMN109186",
    "pricePerDay": 3500.00,
    "fuelType": "PETROL",
    "transmission": "AUTOMATIC",
    "seatingCapacity": 5,
    "description": "Premium sedan",
    "imageUrl": "Accord.jpg"
  }'
```

**Update Vehicle (Vendor)**:
```bash
curl -X PUT http://localhost:8080/api/vehicles/1 \
  -H "Authorization: Bearer <vendor_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "make": "Honda",
    "model": "Accord",
    "year": 2024,
    "color": "Black",
    "licensePlate": "MH01AB1234",
    "vin": "1HGBH41JXMN109186",
    "pricePerDay": 4000.00,
    "fuelType": "PETROL",
    "transmission": "AUTOMATIC",
    "seatingCapacity": 5,
    "description": "Updated description",
    "imageUrl": "Accord.jpg"
  }'
```

**Delete Vehicle (Vendor)**:
```bash
curl -X DELETE http://localhost:8080/api/vehicles/1 \
  -H "Authorization: Bearer <vendor_token>"
```

**Update Vehicle Status (Vendor)**:
```bash
curl -X PUT http://localhost:8080/api/vehicles/1/status \
  -H "Authorization: Bearer <vendor_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "UNDER_MAINTENANCE"
  }'
```

---

### Booking Endpoints

**Create Booking (Customer)**:
```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Authorization: Bearer <customer_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "vehicleId": 1,
    "pickupDate": "2025-12-10",
    "returnDate": "2025-12-15",
    "pickupLocation": "Mumbai Central",
    "returnLocation": "Mumbai Airport"
  }'
```

**Get User Bookings (Customer)**:
```bash
curl -X GET http://localhost:8080/api/bookings/user \
  -H "Authorization: Bearer <customer_token>"
```

**Get Booking by ID (Customer)**:
```bash
curl -X GET http://localhost:8080/api/bookings/1 \
  -H "Authorization: Bearer <customer_token>"
```

**Cancel Booking (Customer)**:
```bash
curl -X PUT http://localhost:8080/api/bookings/1/cancel \
  -H "Authorization: Bearer <customer_token>"
```

**Get Vendor Bookings (Vendor)**:
```bash
curl -X GET http://localhost:8080/api/bookings/vendor \
  -H "Authorization: Bearer <vendor_token>"
```

---

### Payment Endpoints

**Create Payment (Customer)**:
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Authorization: Bearer <customer_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "bookingId": 1,
    "paymentMethod": "UPI",
    "transactionId": "UPI123456789012345"
  }'
```

**Get Payment by Booking ID (Customer)**:
```bash
curl -X GET http://localhost:8080/api/payments/1 \
  -H "Authorization: Bearer <customer_token>"
```

**Update Payment Status (Admin)**:
```bash
curl -X PUT http://localhost:8080/api/payments/1/status \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "REFUNDED"
  }'
```

---

### Review Endpoints

**Create Review (Customer)**:
```bash
curl -X POST http://localhost:8080/api/reviews \
  -H "Authorization: Bearer <customer_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "vehicleId": 1,
    "rating": 5,
    "comment": "Excellent car!"
  }'
```

**Get Reviews by Vehicle (Customer)**:
```bash
curl -X GET http://localhost:8080/api/reviews/vehicle/1 \
  -H "Authorization: Bearer <customer_token>"
```

**Approve Review (Admin)**:
```bash
curl -X PUT http://localhost:8080/api/reviews/1/approve \
  -H "Authorization: Bearer <admin_token>"
```

**Reject Review (Admin)**:
```bash
curl -X PUT http://localhost:8080/api/reviews/1/reject \
  -H "Authorization: Bearer <admin_token>"
```

---

### Complaint Endpoints

**Create Complaint (Customer)**:
```bash
curl -X POST http://localhost:8080/api/complaints \
  -H "Authorization: Bearer <customer_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "subject": "Vehicle condition issue",
    "description": "The car had scratches.",
    "bookingId": 1
  }'
```

**Get User Complaints (Customer)**:
```bash
curl -X GET http://localhost:8080/api/complaints/user \
  -H "Authorization: Bearer <customer_token>"
```

**Get All Complaints (Admin)**:
```bash
curl -X GET http://localhost:8080/api/complaints \
  -H "Authorization: Bearer <admin_token>"
```

**Resolve Complaint (Admin)**:
```bash
curl -X PUT http://localhost:8080/api/complaints/1/resolve \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "adminResponse": "We have addressed the issue."
  }'
```

---

### Admin Endpoints

**Get Admin Stats**:
```bash
curl -X GET http://localhost:8080/api/admin/stats \
  -H "Authorization: Bearer <admin_token>"
```

**Get All Users**:
```bash
curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer <admin_token>"
```

**Get Pending Users**:
```bash
curl -X GET http://localhost:8080/api/admin/users/pending \
  -H "Authorization: Bearer <admin_token>"
```

**Approve User**:
```bash
curl -X PUT http://localhost:8080/api/admin/users/2/approve \
  -H "Authorization: Bearer <admin_token>"
```

**Reject User**:
```bash
curl -X PUT http://localhost:8080/api/admin/users/2/reject \
  -H "Authorization: Bearer <admin_token>"
```

**Delete User**:
```bash
curl -X DELETE http://localhost:8080/api/admin/users/2 \
  -H "Authorization: Bearer <admin_token>"
```

**Get All Bookings**:
```bash
curl -X GET http://localhost:8080/api/admin/bookings \
  -H "Authorization: Bearer <admin_token>"
```

**Get All Payments**:
```bash
curl -X GET http://localhost:8080/api/admin/payments \
  -H "Authorization: Bearer <admin_token>"
```

**Get All Vehicles**:
```bash
curl -X GET http://localhost:8080/api/admin/vehicles \
  -H "Authorization: Bearer <admin_token>"
```

**Get Revenue Report**:
```bash
curl -X GET "http://localhost:8080/api/admin/reports/revenue?period=year" \
  -H "Authorization: Bearer <admin_token>"
```

**Get Booking Analytics**:
```bash
curl -X GET http://localhost:8080/api/admin/reports/bookings \
  -H "Authorization: Bearer <admin_token>"
```

**Get Vehicle Performance**:
```bash
curl -X GET http://localhost:8080/api/admin/reports/vehicles \
  -H "Authorization: Bearer <admin_token>"
```

**Get User Analytics**:
```bash
curl -X GET http://localhost:8080/api/admin/reports/users \
  -H "Authorization: Bearer <admin_token>"
```

---

## Frontend Testing Flow

### Test Flow 1: Complete User Journey

**1. Register as Customer**
- Navigate to `/register`
- Fill registration form
- Submit
- Should see "User registered successfully"
- Redirect to `/login`

**2. Admin Approves User**
- Login as admin (`admin@test.com` / `admin123`)
- Navigate to `/admin/users/registration-requests`
- Click "Approve" on pending customer
- Customer can now login

**3. Customer Login**
- Navigate to `/login`
- Enter credentials (`customer1@test.com` / `customer123`)
- Should redirect to `/` (home page)

**4. Browse Vehicles**
- Navigate to `/cars`
- Should see available vehicles
- Apply filters (fuel, transmission, price)
- Click "View Details" on a vehicle

**5. View Car Details**
- Should see vehicle details
- View reviews (if any)
- Click "Book Now"

**6. Create Booking**
- Navigate to `/payment`
- Select pickup date (today or future)
- Select return date (after pickup)
- Enter pickup location
- Enter return location
- See calculated price
- Click "Pay Now"

**7. Make Payment**
- Payment created automatically
- Booking status changes to CONFIRMED
- Redirect to `/bookings`

**8. View Bookings**
- Navigate to `/bookings`
- Should see booking in "Current" tab
- Click "View Details" to see full details
- Can cancel booking if needed

**9. Submit Review**
- Navigate to car details page
- Click "Write a Review"
- Select rating (1-5 stars)
- Enter comment
- Submit review
- Review status: PENDING

**10. Admin Approves Review**
- Login as admin
- Navigate to `/admin/reviews`
- See pending review
- Click "Approve"
- Review now visible to public

**11. Submit Complaint**
- Navigate to `/complaints`
- Click "Submit Complaint"
- Enter subject and description
- Optionally link to booking
- Submit complaint
- Status: PENDING

**12. Admin Resolves Complaint**
- Login as admin
- Navigate to `/admin/complaints`
- See pending complaint
- Click "Resolve"
- Enter admin response
- Submit
- Complaint status: RESOLVED

---

### Test Flow 2: Vendor Journey

**1. Register as Vendor**
- Navigate to `/register`
- Fill form with role: VENDOR
- Submit

**2. Admin Approves Vendor**
- Admin approves vendor
- Vendor can now login

**3. Vendor Login**
- Login as vendor (`vendor1@test.com` / `vendor123`)
- Should redirect to `/vendor/dashboard`

**4. View Dashboard**
- See statistics (cars, bookings, revenue)
- See recent bookings
- See revenue chart

**5. Add Vehicle**
- Navigate to `/vendor/cars`
- Click "Add Vehicle"
- Fill vehicle form
- Select image file
- Submit
- Vehicle added with status: AVAILABLE

**6. View Vehicles**
- See all vendor vehicles
- Filter by status
- Click "View" to see details
- Click "Edit" to update
- Click "Delete" to remove

**7. View Bookings**
- Navigate to `/vendor/bookings`
- See all bookings for vendor's vehicles
- Filter by status
- Click "View Details" to see full booking info

**8. View Revenue**
- Navigate to `/vendor/revenue`
- See revenue statistics
- See revenue chart (6 months)
- See booking history

**9. Update Settings**
- Navigate to `/vendor/settings`
- Update profile information
- Change password
- Delete account (if needed)

---

### Test Flow 3: Admin Journey

**1. Admin Login**
- Login as admin (`admin@test.com` / `admin123`)
- Redirect to `/admin/dashboard`

**2. View Dashboard**
- See system statistics
- See recent bookings
- See pending users alert

**3. Manage Users**
- Navigate to `/admin/users`
- View all users
- Search users
- Approve/reject pending users
- Delete users (if no dependencies)

**4. Manage Bookings**
- Navigate to `/admin/bookings`
- View all bookings
- Filter by status
- View booking details

**5. Manage Vehicles**
- Navigate to `/admin/cars`
- View all vehicles
- Search vehicles
- Filter by status
- View vehicle details

**6. Moderate Reviews**
- Navigate to `/admin/reviews`
- See pending reviews
- Approve or reject reviews
- View approved reviews

**7. Resolve Complaints**
- Navigate to `/admin/complaints`
- See all complaints
- Filter by status
- Resolve complaints with response

**8. View Reports**
- Navigate to `/admin/reports`
- See revenue reports with charts
- See booking analytics
- See vehicle performance
- See user analytics

---

## SQL Insert Statements (Alternative Method)

If you prefer to insert data directly into the database:

### Insert Test Users

```sql
-- Note: Passwords must be BCrypt hashed
-- Use Spring Boot to register users via API, or hash passwords manually

-- Admin User (password: admin123)
-- BCrypt hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
INSERT INTO users (name, email, password_hash, phone_no, license_no, aadhar_no, 
                   house_no, building_name, street_name, area, pincode, role, gender, status)
VALUES ('Admin User', 'admin@test.com', 
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        '9876543210', 'ADMIN001', '123456789012',
        '101', 'Admin Building', 'Admin Street', 'Admin Area', '400001',
        'ADMIN', 'MALE', 'APPROVED');

-- Vendor User (password: vendor123)
INSERT INTO users (name, email, password_hash, phone_no, license_no, aadhar_no,
                   house_no, building_name, street_name, area, pincode, role, gender, status)
VALUES ('Vendor One', 'vendor1@test.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        '9876543211', 'VENDOR001', '123456789013',
        '201', 'Vendor Building', 'Vendor Street', 'Vendor Area', '400002',
        'VENDOR', 'MALE', 'APPROVED');

-- Customer User (password: customer123)
INSERT INTO users (name, email, password_hash, phone_no, license_no, aadhar_no,
                   house_no, building_name, street_name, area, pincode, role, gender, status)
VALUES ('John Customer', 'customer1@test.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        '9876543212', 'CUSTOMER001', '123456789014',
        '301', 'Customer Building', 'Customer Street', 'Customer Area', '400003',
        'CUSTOMER', 'MALE', 'APPROVED');
```

**Note**: The BCrypt hash above is for "password123". For actual testing, use the API to register users so passwords are properly hashed.

---

### Insert Test Vehicles

```sql
-- Vehicle 1 - Honda Accord (Vendor ID: 2)
INSERT INTO vehicles (make, manufacturer, model, year, color, license_plate, vin,
                      price_per_day, status, fuel_type, transmission, seating_capacity,
                      description, image_url, vendor_id, created_at, updated_at)
VALUES ('Honda', 'Honda', 'Accord', 2023, 'White', 'MH01AB1234', '1HGBH41JXMN109186',
        3500.00, 'AVAILABLE', 'PETROL', 'AUTOMATIC', 5,
        'Premium sedan with excellent comfort and features.',
        'Accord.jpg', 2, NOW(), NOW());

-- Vehicle 2 - Maruti Swift (Vendor ID: 2)
INSERT INTO vehicles (make, manufacturer, model, year, color, license_plate, vin,
                      price_per_day, status, fuel_type, transmission, seating_capacity,
                      description, image_url, vendor_id, created_at, updated_at)
VALUES ('Maruti', 'Maruti', 'Swift', 2024, 'Red', 'MH01CD5678', 'MAZ12345678901234',
        1500.00, 'AVAILABLE', 'PETROL', 'MANUAL', 5,
        'Compact and fuel-efficient hatchback.',
        'Swift.jpg', 2, NOW(), NOW());

-- Vehicle 3 - Mahindra XUV500 (Vendor ID: 2)
INSERT INTO vehicles (make, manufacturer, model, year, color, license_plate, vin,
                      price_per_day, status, fuel_type, transmission, seating_capacity,
                      description, image_url, vendor_id, created_at, updated_at)
VALUES ('Mahindra', 'Mahindra', 'XUV500', 2023, 'Black', 'MH01EF9012', 'MAH98765432109876',
        4500.00, 'AVAILABLE', 'DIESEL', 'AUTOMATIC', 7,
        'Spacious 7-seater SUV.',
        'XUV500.jpg', 2, NOW(), NOW());
```

---

### Insert Test Bookings

```sql
-- Booking 1 - Active Booking (User ID: 3, Vehicle ID: 1)
INSERT INTO bookings (user_id, vehicle_id, pickup_date, return_date,
                      pickup_location, return_location, total_amount, status,
                      created_at, updated_at)
VALUES (3, 1, '2025-12-10', '2025-12-15',
        'Mumbai Central Station', 'Mumbai Airport', 17500.00, 'CONFIRMED',
        NOW(), NOW());

-- Booking 2 - Completed Booking (User ID: 3, Vehicle ID: 2)
INSERT INTO bookings (user_id, vehicle_id, pickup_date, return_date,
                      pickup_location, return_location, total_amount, status,
                      created_at, updated_at)
VALUES (3, 2, '2025-01-01', '2025-01-05',
        'Delhi Airport', 'Delhi Railway Station', 6000.00, 'COMPLETED',
        NOW(), NOW());
```

---

### Insert Test Payments

```sql
-- Payment 1 - Completed Payment (Booking ID: 1)
INSERT INTO payments (booking_id, amount, payment_method, status, transaction_id,
                      payment_date, created_at, updated_at)
VALUES (1, 17500.00, 'UPI', 'COMPLETED', 'UPI123456789012345',
        NOW(), NOW(), NOW());

-- Payment 2 - Completed Payment (Booking ID: 2)
INSERT INTO payments (booking_id, amount, payment_method, status, transaction_id,
                      payment_date, created_at, updated_at)
VALUES (2, 6000.00, 'UPI', 'COMPLETED', 'UPI987654321098765',
        NOW(), NOW(), NOW());
```

---

### Insert Test Reviews

```sql
-- Review 1 - Approved Review (User ID: 3, Vehicle ID: 1)
INSERT INTO reviews (user_id, vehicle_id, rating, comment, status,
                     created_at, updated_at)
VALUES (3, 1, 5, 'Excellent car! Very comfortable and smooth ride.',
        'APPROVED', NOW(), NOW());

-- Review 2 - Approved Review (User ID: 3, Vehicle ID: 2)
INSERT INTO reviews (user_id, vehicle_id, rating, comment, status,
                     created_at, updated_at)
VALUES (3, 2, 4, 'Good car for city driving. Fuel efficient.',
        'APPROVED', NOW(), NOW());
```

---

### Insert Test Complaints

```sql
-- Complaint 1 - Resolved Complaint (User ID: 3, Booking ID: 1)
INSERT INTO complaints (user_id, subject, description, booking_id, status,
                        admin_response, resolved_at, created_at, updated_at)
VALUES (3, 'Vehicle condition issue',
        'The car had scratches that were not mentioned.',
        1, 'RESOLVED',
        'We apologize for the inconvenience. We have addressed the issues.',
        NOW(), NOW(), NOW());
```

---

## Quick Test Credentials Summary

### Login Credentials

| Role | Email | Password | Status |
|------|-------|----------|--------|
| Admin | `admin@test.com` | `admin123` | APPROVED |
| Vendor | `vendor1@test.com` | `vendor123` | APPROVED (after admin approval) |
| Customer | `customer1@test.com` | `customer123` | APPROVED (after admin approval) |

---

## Testing Checklist

### Authentication & User Module
- [ ] Register admin user
- [ ] Register vendor user
- [ ] Register customer user
- [ ] Admin approves vendor
- [ ] Admin approves customer
- [ ] Login as admin
- [ ] Login as vendor
- [ ] Login as customer
- [ ] Update profile
- [ ] Change password
- [ ] Delete account

### Vehicle & Vendor Module
- [ ] Vendor adds vehicle
- [ ] Vendor views vehicles
- [ ] Vendor updates vehicle
- [ ] Vendor deletes vehicle
- [ ] Vendor updates vehicle status
- [ ] Customer views available vehicles
- [ ] Customer views vehicle details

### Booking Module
- [ ] Customer creates booking
- [ ] Customer views bookings
- [ ] Customer views booking details
- [ ] Customer cancels booking
- [ ] Vendor views bookings for their vehicles
- [ ] Test date conflict detection

### Payment Module
- [ ] Customer creates payment
- [ ] Customer views payment
- [ ] Admin updates payment status
- [ ] Test one booking → one payment enforcement

### Review Module
- [ ] Customer submits review
- [ ] Admin approves review
- [ ] Admin rejects review
- [ ] Customer views approved reviews

### Complaint Module
- [ ] Customer submits complaint
- [ ] Customer views complaints
- [ ] Admin views all complaints
- [ ] Admin resolves complaint

### Admin Module
- [ ] Admin views dashboard stats
- [ ] Admin views all users
- [ ] Admin views all bookings
- [ ] Admin views all vehicles
- [ ] Admin views reports
- [ ] Admin views analytics

---

## Important Notes

1. **Password Hashing**: Always use API registration endpoint to ensure passwords are properly BCrypt hashed. Don't insert users directly with plain passwords.

2. **User Approval**: Non-admin users need admin approval before they can login. Admin must approve via `/api/admin/users/{id}/approve`.

3. **Date Validation**: 
   - Pickup date must be today or future
   - Return date must be after pickup date
   - Use format: `YYYY-MM-DD`

4. **Status Flow**:
   - Booking: PENDING → CONFIRMED → ACTIVE → COMPLETED
   - Payment: PENDING → COMPLETED
   - Review: PENDING → APPROVED/REJECTED
   - Complaint: PENDING → RESOLVED

5. **Unique Constraints**:
   - Email must be unique
   - Phone number must be unique
   - License number must be unique
   - Aadhar number must be unique
   - Vehicle license plate must be unique
   - Vehicle VIN must be unique

6. **Foreign Key Constraints**:
   - Vehicle must have valid vendor_id
   - Booking must have valid user_id and vehicle_id
   - Payment must have valid booking_id
   - Review must have valid user_id and vehicle_id
   - Complaint must have valid user_id

---

## Troubleshooting

### Common Issues

**Issue**: "User not found" after registration
**Solution**: Admin must approve user before login

**Issue**: "Vehicle not available"
**Solution**: Check vehicle status is AVAILABLE, not BOOKED or UNDER_MAINTENANCE

**Issue**: "Date conflict"
**Solution**: Ensure no other booking exists for same vehicle and overlapping dates

**Issue**: "Payment already exists"
**Solution**: One booking can have only one payment (one-to-one relationship)

**Issue**: "Already reviewed this vehicle"
**Solution**: One user can review a vehicle only once

---

**Last Updated**: January 2026

**Use this guide to test all functionality of the Car Rental System end-to-end.**
