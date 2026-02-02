# Swagger API Test Payloads

## Base URL
```
http://localhost:8080/api
```

## Authentication

### 1. Register a Vendor
**Endpoint:** `POST /api/auth/register`

**Payload:**
```json
{
  "name": "John Doe",
  "email": "vendor@example.com",
  "password": "vendor123",
  "phoneNo": "9876543210",
  "licenseNo": "DL1234567890",
  "aadharNo": "123456789012",
  "houseNo": "123",
  "buildingName": "ABC Building",
  "streetName": "Main Street",
  "area": "Downtown",
  "pincode": "400001",
  "role": "VENDOR",
  "gender": "MALE"
}
```

### 2. Register a Customer
**Endpoint:** `POST /api/auth/register`

**Payload:**
```json
{
  "name": "Jane Smith",
  "email": "customer@example.com",
  "password": "customer123",
  "phoneNo": "9876543211",
  "licenseNo": "DL1234567891",
  "aadharNo": "123456789013",
  "houseNo": "456",
  "buildingName": "XYZ Apartment",
  "streetName": "Park Avenue",
  "area": "Uptown",
  "pincode": "400002",
  "role": "CUSTOMER",
  "gender": "FEMALE"
}
```

### 3. Login (Vendor)
**Endpoint:** `POST /api/auth/login`

**Payload:**
```json
{
  "email": "vendor@example.com",
  "password": "vendor123"
}
```

**Response:** Copy the `token` from response for Authorization header

### 4. Login (Customer)
**Endpoint:** `POST /api/auth/login`

**Payload:**
```json
{
  "email": "customer@example.com",
  "password": "customer123"
}
```

---

## Vehicle APIs

### 5. Add Vehicle (Vendor Only)
**Endpoint:** `POST /api/vehicles`

**Headers:**
```
Authorization: Bearer <vendor_jwt_token>
Content-Type: application/json
```

**Payload Example 1 - Sedan:**
```json
{
  "make": "Toyota",
  "model": "Camry",
  "year": 2023,
  "color": "White",
  "licensePlate": "MH01AB1234",
  "vin": "1HGBH41JXMN109186",
  "pricePerDay": 1500.00,
  "fuelType": "PETROL",
  "transmission": "AUTOMATIC",
  "seatingCapacity": 5,
  "description": "Comfortable sedan with all modern features. Perfect for city driving.",
  "imageUrl": "https://example.com/images/toyota-camry.jpg"
}
```

**Payload Example 2 - SUV:**
```json
{
  "make": "Mahindra",
  "model": "XUV500",
  "year": 2022,
  "color": "Black",
  "licensePlate": "MH02CD5678",
  "vin": "1HGBH41JXMN109187",
  "pricePerDay": 3200.00,
  "fuelType": "DIESEL",
  "transmission": "AUTOMATIC",
  "seatingCapacity": 7,
  "description": "Spacious 7-seater SUV with premium features. Great for family trips.",
  "imageUrl": "https://example.com/images/xuv500.jpg"
}
```

**Payload Example 3 - Hatchback:**
```json
{
  "make": "Maruti",
  "model": "Swift",
  "year": 2024,
  "color": "Red",
  "licensePlate": "MH03EF9012",
  "vin": "1HGBH41JXMN109188",
  "pricePerDay": 1200.00,
  "fuelType": "PETROL",
  "transmission": "MANUAL",
  "seatingCapacity": 5,
  "description": "Compact and fuel-efficient hatchback. Ideal for city commuting.",
  "imageUrl": "https://example.com/images/swift.jpg"
}
```

**Payload Example 4 - Electric:**
```json
{
  "make": "Tata",
  "model": "Nexon EV",
  "year": 2023,
  "color": "Blue",
  "licensePlate": "MH04GH3456",
  "vin": "1HGBH41JXMN109189",
  "pricePerDay": 2200.00,
  "fuelType": "ELECTRIC",
  "transmission": "AUTOMATIC",
  "seatingCapacity": 5,
  "description": "Eco-friendly electric vehicle with long range. Zero emissions.",
  "imageUrl": "https://example.com/images/nexon-ev.jpg"
}
```

**Payload Example 5 - Luxury:**
```json
{
  "make": "Toyota",
  "model": "Fortuner",
  "year": 2023,
  "color": "Silver",
  "licensePlate": "MH05IJ7890",
  "vin": "1HGBH41JXMN109190",
  "pricePerDay": 4500.00,
  "fuelType": "DIESEL",
  "transmission": "AUTOMATIC",
  "seatingCapacity": 7,
  "description": "Premium SUV with 4x4 capability. Perfect for off-road adventures.",
  "imageUrl": "https://example.com/images/fortuner.jpg"
}
```

**Payload Example 6 - Hybrid:**
```json
{
  "make": "Honda",
  "model": "City Hybrid",
  "year": 2024,
  "color": "White",
  "licensePlate": "MH06KL1234",
  "vin": "1HGBH41JXMN109191",
  "pricePerDay": 1800.00,
  "fuelType": "HYBRID",
  "transmission": "AUTOMATIC",
  "seatingCapacity": 5,
  "description": "Fuel-efficient hybrid sedan combining petrol and electric power.",
  "imageUrl": "https://example.com/images/city-hybrid.jpg"
}
```

**Payload Example 7 - Minimal (Required Fields Only):**
```json
{
  "make": "Hyundai",
  "model": "Creta",
  "year": 2023,
  "color": "Black",
  "licensePlate": "MH07MN5678",
  "vin": "1HGBH41JXMN109192",
  "pricePerDay": 2800.00,
  "fuelType": "DIESEL",
  "transmission": "AUTOMATIC",
  "seatingCapacity": 5
}
```

---

### 6. Update Vehicle (Vendor Only)
**Endpoint:** `PUT /api/vehicles/{id}`

**Headers:**
```
Authorization: Bearer <vendor_jwt_token>
Content-Type: application/json
```

**Payload:**
```json
{
  "make": "Toyota",
  "model": "Camry",
  "year": 2023,
  "color": "Pearl White",
  "licensePlate": "MH01AB1234",
  "vin": "1HGBH41JXMN109186",
  "pricePerDay": 1600.00,
  "fuelType": "PETROL",
  "transmission": "AUTOMATIC",
  "seatingCapacity": 5,
  "description": "Updated description: Premium sedan with leather seats and sunroof.",
  "imageUrl": "https://example.com/images/toyota-camry-updated.jpg"
}
```

---

### 7. Update Vehicle Status (Vendor Only)
**Endpoint:** `PUT /api/vehicles/{id}/status`

**Headers:**
```
Authorization: Bearer <vendor_jwt_token>
Content-Type: application/json
```

**Payload Example 1 - Set to Booked:**
```json
{
  "status": "BOOKED"
}
```

**Payload Example 2 - Set to Under Maintenance:**
```json
{
  "status": "UNDER_MAINTENANCE"
}
```

**Payload Example 3 - Set to Available:**
```json
{
  "status": "AVAILABLE"
}
```

**Payload Example 4 - Set to Deactivated:**
```json
{
  "status": "DEACTIVATED"
}
```

**Valid Status Values:**
- `AVAILABLE` - Vehicle is available for rental
- `BOOKED` - Vehicle is currently booked/rented
- `UNDER_MAINTENANCE` - Vehicle is under maintenance
- `DEACTIVATED` - Vehicle is deactivated/unavailable

---

### 8. Get All Available Vehicles (User/Customer)
**Endpoint:** `GET /api/vehicles`

**Headers:**
```
Authorization: Bearer <customer_jwt_token>
```

**No payload required** - Returns all vehicles with status `AVAILABLE`

---

### 9. Get Vehicle by ID (User/Customer)
**Endpoint:** `GET /api/vehicles/{id}`

**Headers:**
```
Authorization: Bearer <customer_jwt_token>
```

**No payload required** - Replace `{id}` with actual vehicle ID (e.g., `/api/vehicles/1`)

---

### 10. Get Vendor's Vehicles (Vendor Only)
**Endpoint:** `GET /api/vehicles/vendor`

**Headers:**
```
Authorization: Bearer <vendor_jwt_token>
```

**No payload required** - Returns all vehicles owned by the authenticated vendor

---

### 11. Delete Vehicle (Vendor Only)
**Endpoint:** `DELETE /api/vehicles/{id}`

**Headers:**
```
Authorization: Bearer <vendor_jwt_token>
```

**No payload required** - Replace `{id}` with actual vehicle ID (e.g., `/api/vehicles/1`)

---

## Testing Workflow

### Step 1: Register and Login as Vendor
1. Register a vendor using payload #1
2. Login using payload #3
3. Copy the `token` from response

### Step 2: Add Vehicles
1. Use payload #5 (any example) to add vehicles
2. Note the vehicle IDs from responses

### Step 3: Test Vendor Operations
1. Get vendor's vehicles using endpoint #10
2. Update a vehicle using endpoint #6
3. Update vehicle status using endpoint #7 (use: AVAILABLE, BOOKED, UNDER_MAINTENANCE, or DEACTIVATED)
4. Get vehicle by ID using endpoint #9

### Step 4: Test Customer Operations
1. Register and login as customer (payloads #2 and #4)
2. Get all available vehicles using endpoint #8
3. Get vehicle by ID using endpoint #9

### Step 5: Test Delete
1. Delete a vehicle using endpoint #11 (as vendor)

---

## Expected Responses

### Success Response (Add/Update Vehicle):
```json
{
  "id": 1,
  "make": "Toyota",
  "model": "Camry",
  "year": 2023,
  "color": "White",
  "licensePlate": "MH01AB1234",
  "vin": "1HGBH41JXMN109186",
  "pricePerDay": 1500.00,
  "status": "AVAILABLE",
  "fuelType": "PETROL",
  "transmission": "AUTOMATIC",
  "seatingCapacity": 5,
  "description": "Comfortable sedan with all modern features.",
  "imageUrl": "https://example.com/images/toyota-camry.jpg",
  "vendorId": 1,
  "vendorName": "John Doe",
  "createdAt": "2026-01-26T10:30:00",
  "updatedAt": "2026-01-26T10:30:00"
}
```

### Error Response (Duplicate License Plate):
```json
"License plate already exists"
```

### Error Response (Not Vehicle Owner):
```json
"You can only update your own vehicles"
```

### Error Response (Invalid Status):
```json
"Invalid vehicle status: INVALID_STATUS"
```

---

## Common Issues & Solutions

### Issue 1: 401 Unauthorized
**Solution:** Make sure you're using a valid JWT token in the Authorization header:
```
Authorization: Bearer <your_token_here>
```

### Issue 2: 403 Forbidden
**Solution:** Make sure you're logged in as a VENDOR for vendor-only endpoints

### Issue 3: 400 Bad Request - Duplicate License Plate
**Solution:** Use a unique license plate for each vehicle

### Issue 4: 400 Bad Request - Duplicate VIN
**Solution:** Use a unique VIN for each vehicle

### Issue 5: 404 Not Found
**Solution:** Check if the vehicle ID exists and belongs to you

---

## Quick Test Sequence

1. **Register Vendor:**
   ```json
   POST /api/auth/register
   {
     "name": "Test Vendor",
     "email": "testvendor@test.com",
     "password": "test123",
     "phoneNo": "9999999999",
     "role": "VENDOR"
   }
   ```

2. **Login:**
   ```json
   POST /api/auth/login
   {
     "email": "testvendor@test.com",
     "password": "test123"
   }
   ```
   Copy token: `eyJhbGciOiJIUzI1NiJ9...`

3. **Add Vehicle:**
   ```json
   POST /api/vehicles
   Authorization: Bearer <token>
   {
     "make": "Test",
     "model": "Car",
     "year": 2024,
     "color": "Red",
     "licensePlate": "TEST1234",
     "vin": "TESTVIN123456",
     "pricePerDay": 1000,
     "fuelType": "PETROL",
     "transmission": "MANUAL",
     "seatingCapacity": 5
   }
   ```

4. **Get Vendor Vehicles:**
   ```
   GET /api/vehicles/vendor
   Authorization: Bearer <token>
   ```

5. **Update Status:**
   ```json
   PUT /api/vehicles/1/status
   Authorization: Bearer <token>
   {
     "status": "BOOKED"
   }
   ```

---

## Status Values Reference

The database uses these status enum values:
- `AVAILABLE` - Vehicle is available for rental
- `BOOKED` - Vehicle is currently booked/rented  
- `UNDER_MAINTENANCE` - Vehicle is under maintenance
- `DEACTIVATED` - Vehicle is deactivated/unavailable

**Note:** The frontend and API use these exact values. Make sure to use uppercase and underscores as shown.
