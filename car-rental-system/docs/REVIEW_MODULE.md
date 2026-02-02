# Review Module - Implementation Documentation

## Overview
The Review Module allows users to submit reviews for vehicles they've rented, with admin approval/rejection workflow. Only approved reviews are visible to the public.

---

## Table of Contents
1. [Architecture](#architecture)
2. [Core Concepts Used](#core-concepts-used)
3. [Design Patterns](#design-patterns)
4. [JPA Concepts](#jpa-concepts)
5. [Backend Implementation](#backend-implementation)
6. [Frontend Implementation](#frontend-implementation)
7. [API Endpoints](#api-endpoints)
8. [How It Works - Step by Step](#how-it-works---step-by-step)
9. [Interview Questions & Answers](#interview-questions--answers)
10. [Security](#security)
11. [Business Rules](#business-rules)
12. [Testing](#testing)

---

## Architecture

### Backend Layers
```
Controller (ReviewController)
    ↓
Service (ReviewService → ReviewServiceImpl)
    ↓
Repository (ReviewRepository)
    ↓
Entity (Review)
    ↓
Database (reviews table)
```

### Frontend Components
```
Pages:
  - CarDetailsPage.jsx (Integrated reviews section)
  - ReviewsPage.jsx (Standalone reviews page)
  - AdminReviewsPage.jsx (Admin approval page)

Services:
  - api.js (Review API functions)
```

---

## Core Concepts Used

### 1. **Approval Workflow Pattern**
**What it is**: A process where content requires approval before being publicly visible.

**How it's implemented**:
```java
public enum ReviewStatus {
    PENDING,    // Awaiting approval
    APPROVED,   // Approved and visible
    REJECTED    // Rejected and hidden
}
```

**Workflow**:
```
User submits review → Status: PENDING
    ↓
Admin reviews
    ↓
    ├─→ APPROVED (visible to public)
    └─→ REJECTED (hidden from public)
```

**Why use approval workflow?**
- **Content moderation**: Prevent spam, inappropriate content
- **Quality control**: Ensure reviews are genuine
- **Business protection**: Filter negative reviews if needed

**Interview Question**: "Why implement an approval workflow for reviews?"
**Answer**:
1. **Content quality**: Ensures reviews are genuine and appropriate
2. **Spam prevention**: Prevents fake or malicious reviews
3. **Business control**: Allows moderation of content
4. **User trust**: Approved reviews build trust

---

### 2. **Unique Constraint Enforcement**
**What it is**: Ensuring one user can only review a vehicle once.

**How it's implemented**:
```java
// Check if user already reviewed this vehicle
Optional<Review> existingReview = reviewRepository
    .findByUserIdAndVehicleId(user.getId(), vehicle.getId());

if (existingReview.isPresent()) {
    throw new IllegalArgumentException("You have already reviewed this vehicle");
}
```

**Why enforce uniqueness?**
- **Data integrity**: One review per user per vehicle
- **Fairness**: Prevents users from spamming reviews
- **Accuracy**: More accurate average ratings

**Database constraint** (optional):
```sql
ALTER TABLE reviews ADD UNIQUE (user_id, vehicle_id);
```

**Interview Question**: "How do you prevent duplicate reviews?"
**Answer**:
1. **Service-level check**: Query repository before creating
2. **Repository method**: `findByUserIdAndVehicleId()` checks existence
3. **Exception**: Throw if review already exists
4. **Database constraint**: UNIQUE constraint as backup (optional)

---

### 3. **Status-Based Filtering**
**What it is**: Filtering data based on status values.

**How it's used**:
```java
// Public endpoint - only show approved reviews
public List<ReviewResponse> getReviewsByVehicleId(Integer vehicleId) {
    List<Review> reviews = reviewRepository
        .findByVehicleIdAndStatus(vehicleId, ReviewStatus.APPROVED);
    return reviews.stream()
        .map(this::convertToResponse)
        .collect(Collectors.toList());
}

// Admin endpoint - show all reviews
public List<ReviewResponse> getAllReviews() {
    return reviewRepository.findAll().stream()
        .map(this::convertToResponse)
        .collect(Collectors.toList());
}
```

**Why filter by status?**
- **Public view**: Only show approved reviews
- **Admin view**: Show all reviews for moderation
- **Data privacy**: Hide rejected reviews from public

**Interview Question**: "How do you ensure only approved reviews are visible to users?"
**Answer**:
1. **Repository method**: `findByVehicleIdAndStatus(vehicleId, APPROVED)`
2. **Service filtering**: Filter by status in service method
3. **Separate endpoints**: Public endpoint filters, admin endpoint shows all
4. **Database query**: Efficient filtering at database level

---

### 4. **Rating Validation**
**What it is**: Ensuring ratings are within valid range (1-5).

**How it's implemented**:
```java
// DTO validation
public class ReviewRequest {
    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;
}
```

**Service-level validation**:
```java
if (request.getRating() < 1 || request.getRating() > 5) {
    throw new IllegalArgumentException("Rating must be between 1 and 5");
}
```

**Why validate?**
- **Data integrity**: Ensure valid ratings
- **User experience**: Prevent invalid input
- **Business logic**: Ratings must be meaningful (1-5 stars)

---

## Design Patterns

### 1. **Workflow Pattern**
**What it is**: Managing state transitions through a defined workflow.

**Review Workflow**:
```
PENDING → APPROVED (admin approves)
PENDING → REJECTED (admin rejects)
```

**Implementation**:
```java
public ReviewResponse approveReview(Integer reviewId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("Review not found"));
    
    if (review.getStatus() != ReviewStatus.PENDING) {
        throw new IllegalArgumentException("Only pending reviews can be approved");
    }
    
    review.setStatus(ReviewStatus.APPROVED);
    return convertToResponse(reviewRepository.save(review));
}
```

---

### 2. **Repository Query Pattern**
**Custom queries for different use cases**:
```java
// Public: Approved reviews only
findByVehicleIdAndStatus(vehicleId, APPROVED)

// Admin: All reviews
findAll()

// Admin: Reviews by status
findByStatus(status)

// Validation: Check duplicate
findByUserIdAndVehicleId(userId, vehicleId)
```

---

## JPA Concepts

### 1. **Many-to-One Relationships (Multiple)**
**Review has two Many-to-One relationships**:
```java
@Entity
public class Review {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
}
```

**Why LAZY for both?**
- **Performance**: Don't load user/vehicle unless needed
- **Efficiency**: Avoids unnecessary joins
- **N+1 prevention**: Only loads when accessed

---

### 2. **Composite Query Methods**
**Querying by multiple fields**:
```java
findByUserIdAndVehicleId(Integer userId, Integer vehicleId)
// Generated SQL: WHERE user_id = ? AND vehicle_id = ?

findByVehicleIdAndStatus(Integer vehicleId, ReviewStatus status)
// Generated SQL: WHERE vehicle_id = ? AND status = ?
```

**Benefits**:
- **Type-safe**: Compile-time checking
- **Readable**: Method name describes query
- **Efficient**: Single query, not multiple

---

## How It Works - Step by Step

### Example: User Submits Review

**1. Frontend Request**:
```javascript
const reviewData = {
    vehicleId: 1,
    rating: 5,
    comment: "Great car, very comfortable!"
};
await createReview(reviewData);
```

**2. Service Method**:
```java
public ReviewResponse createReview(String userEmail, ReviewRequest request) {
    // Step 1: Find user
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    
    // Step 2: Find vehicle
    Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
        .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
    
    // Step 3: Check for duplicate review
    Optional<Review> existingReview = reviewRepository
        .findByUserIdAndVehicleId(user.getId(), vehicle.getId());
    if (existingReview.isPresent()) {
        throw new IllegalArgumentException("You have already reviewed this vehicle");
    }
    
    // Step 4: Create review
    Review review = new Review();
    review.setUser(user);
    review.setVehicle(vehicle);
    review.setRating(request.getRating());
    review.setComment(request.getComment());
    review.setStatus(ReviewStatus.PENDING);  // Default status
    
    // Step 5: Save (@PrePersist sets timestamps)
    Review savedReview = reviewRepository.save(review);
    
    // Step 6: Convert to DTO
    return convertToResponse(savedReview);
}
```

**3. Admin Approves Review**:
```java
public ReviewResponse approveReview(Integer reviewId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("Review not found"));
    
    // Validate status
    if (review.getStatus() != ReviewStatus.PENDING) {
        throw new IllegalArgumentException("Only pending reviews can be approved");
    }
    
    // Update status
    review.setStatus(ReviewStatus.APPROVED);
    Review updatedReview = reviewRepository.save(review);
    
    return convertToResponse(updatedReview);
}
```

**4. Public Views Reviews**:
```java
public List<ReviewResponse> getReviewsByVehicleId(Integer vehicleId) {
    // Only fetch APPROVED reviews
    List<Review> reviews = reviewRepository
        .findByVehicleIdAndStatus(vehicleId, ReviewStatus.APPROVED);
    
    return reviews.stream()
        .map(this::convertToResponse)
        .collect(Collectors.toList());
}
```

---

## Interview Questions & Answers

### Q1: "How does the review approval workflow work?"
**Answer**:
1. **User submits review**: Status set to PENDING automatically
2. **Admin reviews**: Admin sees pending reviews in admin panel
3. **Admin approves/rejects**: Status changed to APPROVED or REJECTED
4. **Public visibility**: Only APPROVED reviews visible to users
5. **Status filtering**: Repository method filters by status

**Code flow**:
```java
// User creates review
review.setStatus(ReviewStatus.PENDING);

// Admin approves
review.setStatus(ReviewStatus.APPROVED);

// Public query
findByVehicleIdAndStatus(vehicleId, APPROVED);
```

---

### Q2: "How do you prevent duplicate reviews?"
**Answer**:
1. **Repository method**: `findByUserIdAndVehicleId()` checks if review exists
2. **Service validation**: Throw exception if review already exists
3. **Database constraint**: Optional UNIQUE constraint on (user_id, vehicle_id)

**Code**:
```java
Optional<Review> existing = reviewRepository
    .findByUserIdAndVehicleId(userId, vehicleId);
if (existing.isPresent()) {
    throw new IllegalArgumentException("Already reviewed");
}
```

---

### Q3: "Why filter reviews by status in the public endpoint?"
**Answer**:
1. **Content moderation**: Only show approved reviews
2. **User experience**: Users see quality, moderated content
3. **Business control**: Hide rejected/inappropriate reviews
4. **Performance**: Efficient query with status filter

**Implementation**:
```java
// Public: Only approved
findByVehicleIdAndStatus(vehicleId, APPROVED)

// Admin: All reviews
findAll()
```

---

## Backend Implementation

### 1. ReviewStatus Enum
**File:** `enums/ReviewStatus.java`

**Values:**
- `PENDING` - New review awaiting admin approval
- `APPROVED` - Review approved and visible to public
- `REJECTED` - Review rejected by admin

---

### 2. Review Entity
**File:** `entity/Review.java`

**Fields:**
- `id` - Primary key
- `user` - ManyToOne relationship to User (reviewer)
- `vehicle` - ManyToOne relationship to Vehicle
- `rating` - Integer (1-5 stars)
- `comment` - Text (optional)
- `status` - ReviewStatus enum (default: PENDING)
- `createdAt` - Timestamp
- `updatedAt` - Timestamp

**Relationships:**
- ManyToOne → User (reviewer)
- ManyToOne → Vehicle (reviewed vehicle)

---

### 3. ReviewRepository
**File:** `repository/ReviewRepository.java`

**Methods:**
- `findByVehicleId(Integer vehicleId)` - Get all reviews for a vehicle
- `findByUserId(Integer userId)` - Get all reviews by a user
- `findByStatus(ReviewStatus status)` - Get reviews by status
- `findByVehicleIdAndStatus(Integer vehicleId, ReviewStatus status)` - Get approved reviews for a vehicle
- `findByUserIdAndVehicleId(Integer userId, Integer vehicleId)` - Check if user already reviewed vehicle

---

### 4. Review DTOs

#### ReviewRequest
**File:** `dto/ReviewRequest.java`

**Fields:**
- `vehicleId` - Required
- `rating` - Required (1-5)
- `comment` - Optional

**Validation:**
- Rating must be between 1 and 5
- Vehicle ID is required

#### ReviewResponse
**File:** `dto/ReviewResponse.java`

**Fields:**
- All review details including user and vehicle information
- Status information

---

### 5. ReviewService
**File:** `service/ReviewServiceImpl.java`

#### createReview()
**Business Logic:**
1. Get authenticated user
2. Get vehicle by ID
3. Check if user has already reviewed this vehicle (prevent duplicates)
4. Create review with PENDING status
5. Save and return

**Validation:**
- User must exist
- Vehicle must exist
- User cannot review same vehicle twice

#### getReviewsByVehicleId()
**Business Logic:**
- Returns only APPROVED reviews for public viewing
- Filters by vehicle ID and status = APPROVED

#### approveReview()
**Business Logic:**
- Admin-only operation
- Changes status from PENDING → APPROVED
- Review becomes visible to public

#### rejectReview()
**Business Logic:**
- Admin-only operation
- Changes status from PENDING → REJECTED
- Review is hidden from public

---

### 6. ReviewController
**File:** `controller/ReviewController.java`

**Endpoints:**

1. **POST /api/reviews**
   - Creates a new review
   - Requires authentication
   - Returns ReviewResponse

2. **GET /api/reviews/vehicle/{vehicleId}**
   - Gets approved reviews for a vehicle
   - Public access (authenticated users)
   - Returns List<ReviewResponse>

3. **PUT /api/reviews/{id}/approve**
   - Approves a pending review
   - Requires ADMIN role
   - Returns ReviewResponse

4. **PUT /api/reviews/{id}/reject**
   - Rejects a pending review
   - Requires ADMIN role
   - Returns ReviewResponse

---

### 7. AdminService Integration
**File:** `service/AdminServiceImpl.java`

**Additional Methods:**
- `getAllReviews()` - Get all reviews (for admin)
- `getReviewsByStatus(ReviewStatus status)` - Get reviews filtered by status

**Admin Endpoints:**
- `GET /api/admin/reviews` - Get all reviews
- `GET /api/admin/reviews/status/{status}` - Get reviews by status

---

## Frontend Implementation

### 1. API Service
**File:** `services/api.js`

**Functions:**
```javascript
export const createReview = (reviewData) => api.post("/reviews", reviewData);
export const getReviewsByVehicleId = (vehicleId) => api.get(`/reviews/vehicle/${vehicleId}`);
export const approveReview = (reviewId) => api.put(`/reviews/${reviewId}/approve`);
export const rejectReview = (reviewId) => api.put(`/reviews/${reviewId}/reject`);
```

---

### 2. CarDetailsPage Integration
**File:** `pages/Cars/CarDetailsPage.jsx`

**Features:**
- Fetches and displays approved reviews for the vehicle
- "Add Review" button for authenticated users
- Review form with rating (1-5 stars) and comment
- Real-time review submission
- Shows review count and average rating

**Review Submission:**
- Submits review via API
- Shows success message
- Refreshes reviews list
- Review appears after admin approval

---

### 3. ReviewsPage
**File:** `pages/Reviews/ReviewsPage.jsx`

**Features:**
- Standalone page for viewing vehicle reviews
- Review submission form
- Displays all approved reviews
- Star rating display
- User-friendly layout

**Route:** `/reviews/vehicle/:vehicleId`

---

### 4. AdminReviewsPage
**File:** `pages/Admin/AdminReviewsPage.jsx`

**Features:**
- Displays all reviews with status filtering
- Tabs: ALL, PENDING, APPROVED, REJECTED
- Approve/Reject buttons for pending reviews
- Shows review details (user, vehicle, rating, comment)
- Status badges
- Review count per status

**Route:** `/admin/reviews`

---

## API Endpoints

### POST /api/reviews
**Purpose:** Create a new review

**Request Body:**
```json
{
  "vehicleId": 1,
  "rating": 5,
  "comment": "Excellent car!"
}
```

**Response:** ReviewResponse object

**Security:** Requires authentication

---

### GET /api/reviews/vehicle/{vehicleId}
**Purpose:** Get approved reviews for a vehicle

**Response:** Array of ReviewResponse objects (only APPROVED status)

**Security:** Requires authentication

---

### PUT /api/reviews/{id}/approve
**Purpose:** Approve a pending review

**Response:** Updated ReviewResponse with status = APPROVED

**Security:** Requires ADMIN role

---

### PUT /api/reviews/{id}/reject
**Purpose:** Reject a pending review

**Response:** Updated ReviewResponse with status = REJECTED

**Security:** Requires ADMIN role

---

### GET /api/admin/reviews
**Purpose:** Get all reviews (admin only)

**Response:** Array of all ReviewResponse objects

**Security:** Requires ADMIN role

---

### GET /api/admin/reviews/status/{status}
**Purpose:** Get reviews filtered by status (admin only)

**Response:** Array of ReviewResponse objects with specified status

**Security:** Requires ADMIN role

---

## Security

### Role-Based Access Control
- ✅ User endpoints require authentication
- ✅ Admin endpoints require ADMIN role
- ✅ Review approval/rejection restricted to admins
- ✅ Public viewing shows only APPROVED reviews

### Business Rules
- ✅ One review per user per vehicle
- ✅ Reviews default to PENDING status
- ✅ Only APPROVED reviews visible to public
- ✅ Rating must be 1-5

---

## Business Rules

### Review Creation
1. User must be authenticated
2. Vehicle must exist
3. User cannot review same vehicle twice
4. Rating must be between 1 and 5
5. Review is created with PENDING status

### Review Approval
1. Only admins can approve/reject reviews
2. Approved reviews become visible to public
3. Rejected reviews remain hidden

### Review Display
1. Only APPROVED reviews shown to public
2. PENDING and REJECTED reviews hidden from users
3. Admins can see all reviews regardless of status

---

## Frontend Integration

### CarDetailsPage
- ✅ Integrated review section at bottom
- ✅ Fetches real reviews from backend
- ✅ Submit review form
- ✅ Displays approved reviews only
- ✅ Shows review count

### AdminReviewsPage
- ✅ Status filtering (ALL, PENDING, APPROVED, REJECTED)
- ✅ Approve/Reject actions
- ✅ Review details display
- ✅ Status badges

### Navigation
- ✅ Reviews link in Admin sidebar
- ✅ Complaints link in Admin sidebar
- ✅ Complaints link in user header

---

## Database Schema

### reviews Table
```sql
CREATE TABLE reviews (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  vehicle_id INT NOT NULL,
  rating INT NOT NULL,
  comment TEXT,
  status ENUM('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
);
```

---

## Summary

The Review Module provides:
- ✅ User review submission
- ✅ Admin approval/rejection workflow
- ✅ Public display of approved reviews
- ✅ One review per user per vehicle
- ✅ Rating system (1-5 stars)
- ✅ Comment support
- ✅ Status management
- ✅ Frontend-backend integration
- ✅ Admin review management page

**Status: ✅ FULLY IMPLEMENTED**

The review module is production-ready and fully integrated with the frontend.
