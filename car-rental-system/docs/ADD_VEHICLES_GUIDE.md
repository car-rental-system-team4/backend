# Guide to Add Multiple Vehicles to Database

This guide provides multiple methods to add test vehicles to the database.

---

## Method 1: Using API Script (Recommended)

### Step 1: Register and Approve Vendor

**1.1. Register a Vendor User:**
```bash
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
```

**1.2. Login as Admin:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@test.com",
    "password": "admin123"
  }'
```
**Save the admin token from response.**

**1.3. Approve Vendor (replace `<admin_token>` and `<vendor_id>`):**
```bash
curl -X PUT http://localhost:8080/api/admin/users/2/approve \
  -H "Authorization: Bearer <admin_token>"
```

**1.4. Login as Vendor:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "vendor1@test.com",
    "password": "vendor123"
  }'
```
**Save the vendor token from response.**

### Step 2: Run the Script

**2.1. Make script executable:**
```bash
chmod +x add_test_vehicles.sh
```

**2.2. Run the script with vendor token:**
```bash
./add_test_vehicles.sh <vendor_token>
```

**Example:**
```bash
./add_test_vehicles.sh eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

The script will add 8 vehicles:
1. Honda Accord - ₹3,500/day
2. Maruti Swift - ₹1,500/day
3. Mahindra XUV500 - ₹4,500/day
4. Toyota Fortuner - ₹5,000/day
5. Hyundai Creta - ₹3,000/day
6. Tata Nexon - ₹2,800/day
7. Maruti Ertiga - ₹2,500/day
8. Honda City - ₹3,200/day

---

## Method 2: Using SQL Script

### Step 1: Find Vendor ID

Connect to your MySQL database and run:
```sql
SELECT id, name, email, role, status 
FROM users 
WHERE role = 'VENDOR' AND status = 'APPROVED';
```

**Note the vendor ID** (e.g., `2`).

### Step 2: Update SQL Script

Open `add_vehicles_sql.sql` and replace all occurrences of `<VENDOR_ID>` with the actual vendor ID.

**Example:**
```sql
-- Replace this:
'Accord.jpg', <VENDOR_ID>, NOW(), NOW()

-- With this (if vendor ID is 2):
'Accord.jpg', 2, NOW(), NOW()
```

### Step 3: Run SQL Script

**Using MySQL command line:**
```bash
mysql -u D4_92619_Pratik -p car_rental_db < add_vehicles_sql.sql
```

**Or using MySQL Workbench:**
1. Open MySQL Workbench
2. Connect to your database
3. Open `add_vehicles_sql.sql`
4. Replace `<VENDOR_ID>` with actual vendor ID
5. Execute the script

---

## Method 3: Manual API Calls

If you prefer to add vehicles one by one, use these API calls:

### Add Honda Accord
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
    "description": "Premium sedan with excellent comfort and features.",
    "imageUrl": "Accord.jpg"
  }'
```

### Add Maruti Swift
```bash
curl -X POST http://localhost:8080/api/vehicles \
  -H "Authorization: Bearer <vendor_token>" \
  -H "Content-Type: application/json" \
  -d '{
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
    "description": "Compact and fuel-efficient hatchback.",
    "imageUrl": "Swift.jpg"
  }'
```

### Add More Vehicles

See `TEST_DATA_AND_FLOW.md` for more vehicle payloads.

---

## Method 4: Using Frontend (Vendor Portal)

1. **Login as Vendor** at `http://localhost:5173/login`
2. **Navigate to** `/vendor/cars`
3. **Click "Add Vehicle"**
4. **Fill in the form** with vehicle details
5. **Submit**

Repeat for each vehicle.

---

## Verify Vehicles Were Added

### Check via API:
```bash
curl -X GET http://localhost:8080/api/vehicles \
  -H "Authorization: Bearer <vendor_token>"
```

### Check via Frontend:
1. Navigate to `http://localhost:5173/cars`
2. You should see all added vehicles

### Check via Database:
```sql
SELECT id, make, model, license_plate, price_per_day, status 
FROM vehicles 
ORDER BY id;
```

---

## Vehicle Details Summary

| Vehicle | Make | Model | Price/Day | Fuel | Transmission | Seats | Image |
|---------|------|-------|-----------|------|--------------|-------|-------|
| 1 | Honda | Accord | ₹3,500 | Petrol | Automatic | 5 | Accord.jpg |
| 2 | Maruti | Swift | ₹1,500 | Petrol | Manual | 5 | Swift.jpg |
| 3 | Mahindra | XUV500 | ₹4,500 | Diesel | Automatic | 7 | XUV500.jpg |
| 4 | Toyota | Fortuner | ₹5,000 | Diesel | Automatic | 7 | Fortuner.jpg |
| 5 | Hyundai | Creta | ₹3,000 | Petrol | Automatic | 5 | creta.jpg |
| 6 | Tata | Nexon | ₹2,800 | Petrol | Automatic | 5 | Nexon.jpg |
| 7 | Maruti | Ertiga | ₹2,500 | Petrol | Manual | 7 | Maruti.jpg |
| 8 | Honda | City | ₹3,200 | Petrol | Automatic | 5 | Accord.jpg |

---

## Troubleshooting

### Issue: "Vendor not found"
**Solution**: Make sure vendor user is registered and approved by admin.

### Issue: "Duplicate license plate"
**Solution**: Each vehicle must have a unique license plate. Change the `licensePlate` value.

### Issue: "Duplicate VIN"
**Solution**: Each vehicle must have a unique VIN. Change the `vin` value.

### Issue: "Image not showing"
**Solution**: 
1. Ensure image files exist in `/frontend/public/vehicle-images/`
2. Check that `imageUrl` matches the filename exactly (case-sensitive)

### Issue: "No vehicles visible"
**Solution**:
1. Check vehicle status is `AVAILABLE`
2. Verify vendor is approved
3. Check API response for errors
4. Verify frontend is calling correct endpoint

---

## Quick Start (All-in-One)

If you want to quickly set up everything:

```bash
# 1. Register vendor
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Vendor One","email":"vendor1@test.com","password":"vendor123","phoneNo":"9876543211","licenseNo":"VENDOR001","aadharNo":"123456789013","houseNo":"201","buildingName":"Vendor Building","streetName":"Vendor Street","area":"Vendor Area","pincode":"400002","role":"VENDOR","gender":"MALE"}'

# 2. Login as admin (get token)
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@test.com","password":"admin123"}' | jq -r '.token')

# 3. Approve vendor (assuming vendor ID is 2)
curl -X PUT http://localhost:8080/api/admin/users/2/approve \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# 4. Login as vendor (get token)
VENDOR_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"vendor1@test.com","password":"vendor123"}' | jq -r '.token')

# 5. Run script
./add_test_vehicles.sh $VENDOR_TOKEN
```

---

**For more details, see [TEST_DATA_AND_FLOW.md](./docs/TEST_DATA_AND_FLOW.md)**
