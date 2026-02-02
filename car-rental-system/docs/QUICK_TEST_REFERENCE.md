# Quick Test Reference Guide

Quick reference for testing the Car Rental System.

---

## Quick Test Credentials

| Role | Email | Password | Use Case |
|------|-------|----------|----------|
| **Admin** | `admin@test.com` | `admin123` | Admin operations, approvals |
| **Vendor** | `vendor1@test.com` | `vendor123` | Vehicle management |
| **Customer** | `customer1@test.com` | `customer123` | Booking, payments, reviews |

---

## Quick Test Flow

### 1. Setup (One-time)
```bash
# 1. Register Admin
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

# 2. Register Vendor
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

# 3. Register Customer
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

### 2. Admin Approves Users
```bash
# Login as Admin first
POST /api/auth/login
{
  "email": "admin@test.com",
  "password": "admin123"
}
# Save token

# Approve Vendor
PUT /api/admin/users/2/approve
Authorization: Bearer <admin_token>

# Approve Customer
PUT /api/admin/users/3/approve
Authorization: Bearer <admin_token>
```

### 3. Vendor Adds Vehicle
```bash
# Login as Vendor
POST /api/auth/login
{
  "email": "vendor1@test.com",
  "password": "vendor123"
}
# Save token

# Add Vehicle
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
  "description": "Premium sedan",
  "imageUrl": "Accord.jpg"
}
# Save vehicle ID
```

### 4. Customer Creates Booking
```bash
# Login as Customer
POST /api/auth/login
{
  "email": "customer1@test.com",
  "password": "customer123"
}
# Save token

# Create Booking
POST /api/bookings
Authorization: Bearer <customer_token>
{
  "vehicleId": 1,
  "pickupDate": "2025-12-10",
  "returnDate": "2025-12-15",
  "pickupLocation": "Mumbai Central",
  "returnLocation": "Mumbai Airport"
}
# Save booking ID
```

### 5. Customer Makes Payment
```bash
POST /api/payments
Authorization: Bearer <customer_token>
{
  "bookingId": 1,
  "paymentMethod": "UPI",
  "transactionId": "UPI123456789012345"
}
```

### 6. Customer Submits Review
```bash
POST /api/reviews
Authorization: Bearer <customer_token>
{
  "vehicleId": 1,
  "rating": 5,
  "comment": "Excellent car!"
}
# Save review ID
```

### 7. Admin Approves Review
```bash
PUT /api/reviews/1/approve
Authorization: Bearer <admin_token>
```

### 8. Customer Submits Complaint
```bash
POST /api/complaints
Authorization: Bearer <customer_token>
{
  "subject": "Vehicle condition issue",
  "description": "The car had scratches.",
  "bookingId": 1
}
# Save complaint ID
```

### 9. Admin Resolves Complaint
```bash
PUT /api/complaints/1/resolve
Authorization: Bearer <admin_token>
{
  "adminResponse": "We have addressed the issue."
}
```

---

## Test Data Summary

### Users
- **Admin**: admin@test.com / admin123 (Auto-approved)
- **Vendor**: vendor1@test.com / vendor123 (Needs approval)
- **Customer**: customer1@test.com / customer123 (Needs approval)

### Vehicles
- **Honda Accord**: ₹3,500/day, Petrol, Automatic, 5 seats
- **Maruti Swift**: ₹1,500/day, Petrol, Manual, 5 seats
- **Mahindra XUV500**: ₹4,500/day, Diesel, Automatic, 7 seats

### Bookings
- **Booking 1**: 5 days, ₹17,500 (Honda Accord)
- **Booking 2**: 4 days, ₹6,000 (Maruti Swift)

### Payments
- **Payment 1**: ₹17,500, UPI, COMPLETED
- **Payment 2**: ₹6,000, UPI, COMPLETED

---

## Common Test Scenarios

### Scenario 1: New User Registration Flow
1. Register → Status: PENDING
2. Admin approves → Status: APPROVED
3. User can login

### Scenario 2: Complete Booking Flow
1. Customer views vehicles
2. Customer creates booking → Status: PENDING
3. Customer makes payment → Booking: CONFIRMED, Payment: COMPLETED
4. Customer submits review → Status: PENDING
5. Admin approves review → Status: APPROVED

### Scenario 3: Complaint Resolution Flow
1. Customer submits complaint → Status: PENDING
2. Admin views complaint
3. Admin resolves with response → Status: RESOLVED

---

## Date Format
- Use format: `YYYY-MM-DD`
- Example: `2025-12-10`
- Pickup date: Today or future
- Return date: After pickup date

---

## Status Values

### User Status
- `PENDING` - Awaiting approval
- `APPROVED` - Can login
- `REJECTED` - Registration rejected

### Vehicle Status
- `AVAILABLE` - Can be booked
- `BOOKED` - Currently booked
- `UNDER_MAINTENANCE` - Not available
- `DEACTIVATED` - Not available

### Booking Status
- `PENDING` - Created, awaiting payment
- `CONFIRMED` - Payment completed
- `ACTIVE` - Currently active
- `COMPLETED` - Returned
- `CANCELLED` - Cancelled

### Payment Status
- `PENDING` - Created
- `COMPLETED` - Payment successful
- `FAILED` - Payment failed
- `REFUNDED` - Payment refunded

### Review Status
- `PENDING` - Awaiting approval
- `APPROVED` - Visible to public
- `REJECTED` - Hidden from public

### Complaint Status
- `PENDING` - Awaiting resolution
- `RESOLVED` - Admin resolved
- `CLOSED` - Closed

---

**For detailed test data and flows, see [TEST_DATA_AND_FLOW.md](./TEST_DATA_AND_FLOW.md)**
