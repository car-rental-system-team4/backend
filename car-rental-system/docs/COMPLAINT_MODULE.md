# Complaint Module - Implementation Documentation

## Overview
The Complaint Module allows users to raise complaints about their rental experience, and admins can resolve them with responses. This provides a structured way to handle customer issues and feedback.

---

## Table of Contents
1. [Architecture](#architecture)
2. [Core Concepts Used](#core-concepts-used)
3. [Design Patterns](#design-patterns)
4. [Backend Implementation](#backend-implementation)
5. [Frontend Implementation](#frontend-implementation)
6. [API Endpoints](#api-endpoints)
7. [How It Works - Step by Step](#how-it-works---step-by-step)
8. [Interview Questions & Answers](#interview-questions--answers)
9. [Security](#security)
10. [Business Rules](#business-rules)
11. [Testing](#testing)

---

## Architecture

### Backend Layers
```
Controller (ComplaintController)
    ↓
Service (ComplaintService → ComplaintServiceImpl)
    ↓
Repository (ComplaintRepository)
    ↓
Entity (Complaint)
    ↓
Database (complaints table)
```

### Frontend Components
```
Pages:
  - ComplaintsPage.jsx (User complaints page)
  - AdminComplaintsPage.jsx (Admin complaint management)

Services:
  - api.js (Complaint API functions)
```

---

## Core Concepts Used

### 1. **Resolution Workflow Pattern**
**What it is**: A process for tracking and resolving customer complaints.

**How it's implemented**:
```java
public enum ComplaintStatus {
    PENDING,    // New complaint
    RESOLVED,   // Admin resolved with response
    CLOSED      // Complaint closed
}
```

**Workflow**:
```
User raises complaint → Status: PENDING
    ↓
Admin reviews
    ↓
Admin resolves with response → Status: RESOLVED
    ↓
(Optional) Close complaint → Status: CLOSED
```

**Why use resolution workflow?**
- **Tracking**: Track complaint lifecycle
- **Accountability**: Admin must respond
- **Audit trail**: Record of resolution

---

### 2. **Admin Response Pattern**
**What it is**: Admin provides resolution text when resolving complaint.

**How it's implemented**:
```java
public ComplaintResponse resolveComplaint(Integer complaintId, 
                                         ComplaintResolutionRequest request) {
    Complaint complaint = complaintRepository.findById(complaintId)
        .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));
    
    complaint.setStatus(ComplaintStatus.RESOLVED);
    complaint.setAdminResponse(request.getAdminResponse());
    complaint.setResolvedAt(LocalDateTime.now());
    
    return convertToResponse(complaintRepository.save(complaint));
}
```

**Why store admin response?**
- **Transparency**: User can see admin's response
- **Documentation**: Record of resolution
- **Accountability**: Admin must provide explanation

---

### 3. **Optional Booking Link**
**What it is**: Complaint can optionally reference a specific booking.

**How it's implemented**:
```java
@Entity
public class Complaint {
    private Integer bookingId;  // Optional, nullable
    // ...
}
```

**Why optional?**
- **Flexibility**: Complaints may not be booking-specific
- **General issues**: User can complain about general service
- **Booking-specific**: Can link to specific booking if relevant

---

## Design Patterns

### 1. **Status-Based Access Control**
**Different data visibility based on status**:
- **User**: Can see all their complaints (all statuses)
- **Admin**: Can see all complaints (all statuses)
- **Public**: No public access (complaints are private)

---

### 2. **Resolution Pattern**
**Admin resolves complaint with response**:
```java
complaint.setStatus(ComplaintStatus.RESOLVED);
complaint.setAdminResponse(adminResponse);
complaint.setResolvedAt(LocalDateTime.now());
```

**Benefits**:
- **Timestamp tracking**: Know when resolved
- **Response storage**: Admin's response is recorded
- **Status update**: Clear status change

---

## How It Works - Step by Step

### Example: User Raises Complaint

**1. Frontend Request**:
```javascript
const complaintData = {
    subject: "Vehicle condition issue",
    description: "The car had scratches that were not mentioned",
    bookingId: 5  // Optional
};
await createComplaint(complaintData);
```

**2. Service Method**:
```java
public ComplaintResponse createComplaint(String userEmail, ComplaintRequest request) {
    // Step 1: Find user
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    
    // Step 2: Create complaint
    Complaint complaint = new Complaint();
    complaint.setUser(user);
    complaint.setSubject(request.getSubject());
    complaint.setDescription(request.getDescription());
    complaint.setBookingId(request.getBookingId());  // Optional
    complaint.setStatus(ComplaintStatus.PENDING);  // Default
    
    // Step 3: Save (@PrePersist sets timestamps)
    Complaint savedComplaint = complaintRepository.save(complaint);
    
    // Step 4: Convert to DTO
    return convertToResponse(savedComplaint);
}
```

**3. Admin Resolves Complaint**:
```java
public ComplaintResponse resolveComplaint(Integer complaintId, 
                                         ComplaintResolutionRequest request) {
    Complaint complaint = complaintRepository.findById(complaintId)
        .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));
    
    // Update status and response
    complaint.setStatus(ComplaintStatus.RESOLVED);
    complaint.setAdminResponse(request.getAdminResponse());
    complaint.setResolvedAt(LocalDateTime.now());
    
    Complaint resolvedComplaint = complaintRepository.save(complaint);
    return convertToResponse(resolvedComplaint);
}
```

---

## Interview Questions & Answers

### Q1: "How does complaint resolution work?"
**Answer**:
1. **User raises complaint**: Status set to PENDING
2. **Admin views complaint**: Admin sees complaint in admin panel
3. **Admin resolves**: Admin provides response text
4. **Status update**: Status changed to RESOLVED
5. **Timestamp**: `resolvedAt` timestamp recorded
6. **User notification**: User can see admin's response

**Code**:
```java
complaint.setStatus(ComplaintStatus.RESOLVED);
complaint.setAdminResponse(adminResponse);
complaint.setResolvedAt(LocalDateTime.now());
```

---

### Q2: "What's the difference between RESOLVED and CLOSED status?"
**Answer**:
- **RESOLVED**: Admin has provided a response/solution
- **CLOSED**: Complaint is closed (may be after user acknowledges resolution)

**Use cases**:
- **RESOLVED**: Admin responded, waiting for user confirmation
- **CLOSED**: User confirmed resolution or complaint is no longer active

**Current implementation**: Uses RESOLVED status. CLOSED can be added for future enhancement.

---

### Q3: "How do users track their complaints?"
**Answer**:
1. **Endpoint**: `GET /api/complaints/user`
2. **Service method**: `getUserComplaints(userEmail)`
3. **Repository**: `findByUserId(userId)`
4. **Response**: Returns all complaints for user (all statuses)

**Why show all statuses?**
- **Transparency**: User can see complaint lifecycle
- **Tracking**: User knows if complaint is resolved
- **History**: User can see past complaints

---

## Backend Implementation

### 1. ComplaintStatus Enum
**File:** `enums/ComplaintStatus.java`

**Values:**
- `PENDING` - New complaint awaiting resolution
- `RESOLVED` - Complaint resolved by admin
- `CLOSED` - Complaint closed

---

### 2. Complaint Entity
**File:** `entity/Complaint.java`

**Fields:**
- `id` - Primary key
- `user` - ManyToOne relationship to User (complainant)
- `subject` - String (required)
- `description` - Text (required)
- `bookingId` - Integer (optional - link to specific booking)
- `status` - ComplaintStatus enum (default: PENDING)
- `adminResponse` - Text (admin's resolution response)
- `resolvedAt` - Timestamp (when resolved)
- `createdAt` - Timestamp
- `updatedAt` - Timestamp

**Relationships:**
- ManyToOne → User (complainant)

---

### 3. ComplaintRepository
**File:** `repository/ComplaintRepository.java`

**Methods:**
- `findByUserId(Integer userId)` - Get all complaints by a user
- `findByStatus(ComplaintStatus status)` - Get complaints by status

---

### 4. Complaint DTOs

#### ComplaintRequest
**File:** `dto/ComplaintRequest.java`

**Fields:**
- `subject` - Required
- `description` - Required
- `bookingId` - Optional

**Validation:**
- Subject and description are required

#### ComplaintResponse
**File:** `dto/ComplaintResponse.java`

**Fields:**
- All complaint details including user information
- Admin response
- Resolution timestamp
- Status information

#### ComplaintResolutionRequest
**File:** `dto/ComplaintResolutionRequest.java`

**Fields:**
- `adminResponse` - Required (admin's resolution text)

---

### 5. ComplaintService
**File:** `service/ComplaintServiceImpl.java`

#### createComplaint()
**Business Logic:**
1. Get authenticated user
2. Create complaint with PENDING status
3. Link optional booking ID if provided
4. Save and return

**Validation:**
- User must exist
- Subject and description required

#### getUserComplaints()
**Business Logic:**
- Returns all complaints for the authenticated user
- Includes all statuses (user can see their own complaints)

#### getAllComplaints()
**Business Logic:**
- Returns all complaints in the system
- Admin-only operation

#### resolveComplaint()
**Business Logic:**
- Admin-only operation
- Sets status to RESOLVED
- Sets adminResponse
- Sets resolvedAt timestamp

---

### 6. ComplaintController
**File:** `controller/ComplaintController.java`

**Endpoints:**

1. **POST /api/complaints**
   - Creates a new complaint
   - Requires authentication
   - Returns ComplaintResponse

2. **GET /api/complaints/user**
   - Gets all complaints for the authenticated user
   - Requires authentication
   - Returns List<ComplaintResponse>

3. **GET /api/complaints**
   - Gets all complaints (admin only)
   - Requires ADMIN role
   - Returns List<ComplaintResponse>

4. **PUT /api/complaints/{id}/resolve**
   - Resolves a complaint with admin response
   - Requires ADMIN role
   - Returns ComplaintResponse

---

## Frontend Implementation

### 1. API Service
**File:** `services/api.js`

**Functions:**
```javascript
export const createComplaint = (complaintData) => api.post("/complaints", complaintData);
export const getUserComplaints = () => api.get("/complaints/user");
export const getAllComplaints = () => api.get("/complaints");
export const resolveComplaint = (complaintId, resolutionData) => api.put(`/complaints/${complaintId}/resolve`, resolutionData);
```

---

### 2. ComplaintsPage
**File:** `pages/Complaints/ComplaintsPage.jsx`

**Features:**
- User can view their own complaints
- "Raise Complaint" form
- Displays complaint status
- Shows admin response when resolved
- Status badges (Pending, Resolved, Closed)
- Optional booking ID linking

**Route:** `/complaints`

**Form Fields:**
- Subject (required)
- Description (required)
- Booking ID (optional)

---

### 3. AdminComplaintsPage
**File:** `pages/Admin/AdminComplaintsPage.jsx`

**Features:**
- Displays all complaints
- Status filtering (ALL, PENDING, RESOLVED, CLOSED)
- Resolve complaint modal
- Admin response textarea
- Shows user information
- Shows booking ID if linked
- Status badges
- Complaint count per status

**Route:** `/admin/complaints`

**Resolve Flow:**
1. Admin clicks "Resolve" button
2. Modal opens with complaint details
3. Admin enters resolution response
4. Complaint status changes to RESOLVED
5. resolvedAt timestamp is set

---

## API Endpoints

### POST /api/complaints
**Purpose:** Create a new complaint

**Request Body:**
```json
{
  "subject": "Vehicle condition issue",
  "description": "The car had scratches that were not mentioned...",
  "bookingId": 5
}
```

**Response:** ComplaintResponse object

**Security:** Requires authentication

---

### GET /api/complaints/user
**Purpose:** Get all complaints for the authenticated user

**Response:** Array of ComplaintResponse objects

**Security:** Requires authentication

---

### GET /api/complaints
**Purpose:** Get all complaints (admin only)

**Response:** Array of ComplaintResponse objects

**Security:** Requires ADMIN role

---

### PUT /api/complaints/{id}/resolve
**Purpose:** Resolve a complaint with admin response

**Request Body:**
```json
{
  "adminResponse": "We have investigated your complaint and will provide a refund..."
}
```

**Response:** Updated ComplaintResponse with status = RESOLVED

**Security:** Requires ADMIN role

---

## Security

### Role-Based Access Control
- ✅ User endpoints require authentication
- ✅ Admin endpoints require ADMIN role
- ✅ Users can only see their own complaints
- ✅ Admins can see all complaints

### Business Rules
- ✅ Complaints default to PENDING status
- ✅ Only admins can resolve complaints
- ✅ Admin response is required for resolution
- ✅ resolvedAt timestamp set automatically

---

## Business Rules

### Complaint Creation
1. User must be authenticated
2. Subject and description are required
3. Booking ID is optional (for linking to specific booking)
4. Complaint is created with PENDING status

### Complaint Resolution
1. Only admins can resolve complaints
2. Admin response is required
3. Status changes to RESOLVED
4. resolvedAt timestamp is set automatically

### Complaint Viewing
1. Users can see all their own complaints
2. Admins can see all complaints
3. Status filtering available for admins

---

## Frontend Integration

### ComplaintsPage
- ✅ User complaint submission form
- ✅ View own complaints
- ✅ Status display
- ✅ Admin response display
- ✅ Resolution timestamp

### AdminComplaintsPage
- ✅ View all complaints
- ✅ Status filtering
- ✅ Resolve complaint modal
- ✅ Admin response input
- ✅ User and booking information

### Navigation
- ✅ "My Complaints" link in user header
- ✅ "Complaints" link in admin sidebar

---

## Database Schema

### complaints Table
```sql
CREATE TABLE complaints (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  subject VARCHAR(255) NOT NULL,
  description TEXT NOT NULL,
  booking_id INT,
  status ENUM('PENDING','RESOLVED','CLOSED') DEFAULT 'PENDING',
  admin_response TEXT,
  resolved_at TIMESTAMP NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

## Summary

The Complaint Module provides:
- ✅ User complaint submission
- ✅ Admin complaint resolution
- ✅ Admin response system
- ✅ Status tracking (PENDING, RESOLVED, CLOSED)
- ✅ Optional booking linking
- ✅ User complaint history
- ✅ Admin complaint management
- ✅ Frontend-backend integration

**Status: ✅ FULLY IMPLEMENTED**

The complaint module is production-ready and fully integrated with the frontend.
