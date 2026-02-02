# Enum Values Reference - Database Schema

This document lists all enum values as they exist in the database. All backend and frontend code must use these exact values.

## Database Enum Values

### 1. Users Table

#### `role` ENUM
**Database Values:**
- `ADMIN`
- `CUSTOMER`
- `VENDOR`

**Backend Enum:** `UserRole.java`
```java
public enum UserRole {
    ADMIN, VENDOR, CUSTOMER
}
```

**Frontend Usage:**
- Use uppercase: `'ADMIN'`, `'VENDOR'`, `'CUSTOMER'`
- Case-insensitive comparison: `user.role?.toUpperCase() === 'VENDOR'`

---

#### `gender` ENUM
**Database Values:**
- `FEMALE`
- `MALE`
- `OTHER`

**Backend Enum:** `Gender.java`
```java
public enum Gender {
    MALE, FEMALE, OTHER
}
```

**Frontend Usage:**
- Use uppercase: `'MALE'`, `'FEMALE'`, `'OTHER'`
- Form options: `<option value="MALE">Male</option>`

---

### 2. Vehicles Table

#### `status` ENUM
**Database Values:**
- `AVAILABLE`
- `BOOKED`
- `UNDER_MAINTENANCE`
- `DEACTIVATED`

**Backend Enum:** `VehicleStatus.java`
```java
public enum VehicleStatus {
    AVAILABLE, BOOKED, UNDER_MAINTENANCE, DEACTIVATED
}
```

**Frontend Usage:**
- Use uppercase: `'AVAILABLE'`, `'BOOKED'`, `'UNDER_MAINTENANCE'`, `'DEACTIVATED'`
- Display mapping:
  - `AVAILABLE` → "Available"
  - `BOOKED` → "Booked"
  - `UNDER_MAINTENANCE` → "Under Maintenance"
  - `DEACTIVATED` → "Deactivated"

---

## Important Notes

### Case Sensitivity
- **Database stores values in UPPERCASE**
- **Backend enums use UPPERCASE**
- **Frontend must use UPPERCASE when comparing**
- **Frontend can display in Title Case for user-friendly labels**

### Comparison Best Practices

**✅ Good:**
```javascript
// Case-insensitive comparison
if (user.role?.toUpperCase() === 'VENDOR') { ... }

// Direct comparison (if you're sure it's uppercase)
if (vehicle.status === 'AVAILABLE') { ... }
```

**❌ Bad:**
```javascript
// Case-sensitive lowercase comparison
if (user.role === 'vendor') { ... }

// Mixed case
if (user.role === 'Vendor') { ... }
```

### Frontend Display Mapping

When displaying enum values to users, map uppercase database values to user-friendly labels:

```javascript
const getStatusDisplayName = (status) => {
  const statusMap = {
    'AVAILABLE': 'Available',
    'BOOKED': 'Booked',
    'UNDER_MAINTENANCE': 'Under Maintenance',
    'DEACTIVATED': 'Deactivated'
  }
  return statusMap[status] || status
}
```

---

## Verification

To verify enum values in database:
```sql
SELECT COLUMN_NAME, COLUMN_TYPE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'car_rental_db' 
AND COLUMN_TYPE LIKE 'enum%';
```

Current database enums:
- `users.role`: enum('ADMIN','CUSTOMER','VENDOR')
- `users.gender`: enum('FEMALE','MALE','OTHER')
- `vehicles.status`: enum('AVAILABLE','BOOKED','UNDER_MAINTENANCE','DEACTIVATED')

---

## Summary

| Table | Field | Database Values | Backend Enum | Frontend Values |
|-------|-------|----------------|--------------|-----------------|
| users | role | ADMIN, CUSTOMER, VENDOR | UserRole | 'ADMIN', 'CUSTOMER', 'VENDOR' |
| users | gender | FEMALE, MALE, OTHER | Gender | 'MALE', 'FEMALE', 'OTHER' |
| vehicles | status | AVAILABLE, BOOKED, UNDER_MAINTENANCE, DEACTIVATED | VehicleStatus | 'AVAILABLE', 'BOOKED', 'UNDER_MAINTENANCE', 'DEACTIVATED' |

**All values must match exactly (case-sensitive) between database, backend, and frontend API calls.**
