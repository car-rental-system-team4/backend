# Car Rental System - Backend

A comprehensive Spring Boot-based backend application for managing a car rental system with support for multiple user roles (Admin, Vendor, Customer), vehicle management, bookings, payments, reviews, and complaints.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Setup & Installation](#setup--installation)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Modules](#modules)
- [Security](#security)
- [Database Schema](#database-schema)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [Documentation](#documentation)

## 🎯 Overview

This backend application provides RESTful APIs for a car rental management system. It supports three main user roles:
- **Admin**: Full system management, user approval, reviews/complaints handling, reports & analytics
- **Vendor**: Vehicle management, booking tracking, revenue management
- **Customer**: Browse vehicles, make bookings, payments, reviews, complaints

## ✨ Features

### Authentication & Authorization
- JWT-based authentication
- Role-based access control (RBAC)
- Password encryption using BCrypt
- User registration with approval workflow
- Profile management

### Vehicle Management
- CRUD operations for vehicles
- Vehicle status management (Available, Rented, Under Maintenance, Unavailable)
- Vendor-specific vehicle listing
- Image upload support

### Booking Management
- Create, view, and cancel bookings
- Booking status tracking
- Date validation
- Total amount calculation
- Vendor booking views

### Payment Processing
- Payment creation linked to bookings
- Payment status management (Paid, Pending, Failed, Refund, Cancelled)
- Admin payment status updates
- UPI payment support

### Reviews & Complaints
- User reviews for vehicles
- Admin approval/rejection workflow
- Complaint submission and resolution
- Status tracking

### Admin Dashboard
- System statistics
- User management (approve/reject/delete)
- Booking management
- Vehicle management
- Reports & Analytics
  - Revenue reports (monthly/yearly)
  - Booking analytics
  - Vehicle performance metrics
  - User analytics

## 🛠 Technology Stack

- **Framework**: Spring Boot 3.2.2
- **Language**: Java 21
- **Database**: MySQL
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security with JWT
- **Build Tool**: Maven
- **Libraries**:
  - Lombok (boilerplate reduction)
  - JWT (jjwt 0.11.5)
  - SpringDoc OpenAPI (API documentation)
  - Spring Boot Actuator (monitoring)

## 📁 Project Structure

```
src/main/java/com/carrental/
├── configuration/
│   └── SecurityConfig.java          # Security configuration
├── controller/
│   ├── AdminController.java         # Admin endpoints
│   ├── AuthController.java          # Authentication endpoints
│   ├── BookingController.java       # Booking endpoints
│   ├── ComplaintController.java     # Complaint endpoints
│   ├── PaymentController.java      # Payment endpoints
│   ├── ReviewController.java       # Review endpoints
│   ├── UserController.java         # User profile endpoints
│   └── VehicleController.java      # Vehicle endpoints
├── dto/
│   ├── AdminStatsResponse.java
│   ├── BookingRequest.java
│   ├── BookingResponse.java
│   ├── ComplaintRequest.java
│   ├── PaymentRequest.java
│   ├── ReviewRequest.java
│   ├── VehicleRequest.java
│   └── ... (other DTOs)
├── entity/
│   ├── Booking.java
│   ├── Complaint.java
│   ├── Payment.java
│   ├── Review.java
│   ├── User.java
│   └── Vehicle.java
├── enums/
│   ├── BookingStatus.java
│   ├── ComplaintStatus.java
│   ├── PaymentStatus.java
│   ├── ReviewStatus.java
│   ├── UserRole.java
│   ├── UserStatus.java
│   └── VehicleStatus.java
├── repository/
│   ├── BookingRepository.java
│   ├── ComplaintRepository.java
│   ├── PaymentRepository.java
│   ├── ReviewRepository.java
│   ├── UserRepository.java
│   └── VehicleRepository.java
├── security/
│   ├── JwtFilter.java              # JWT authentication filter
│   └── JwtUtil.java                # JWT utility class
└── service/
    ├── AdminService.java
    ├── AdminServiceImpl.java
    ├── AuthService.java
    ├── AuthServiceImpl.java
    ├── BookingService.java
    ├── BookingServiceImpl.java
    ├── ComplaintService.java
    ├── ComplaintServiceImpl.java
    ├── CustomUserDetailsService.java
    ├── PaymentService.java
    ├── PaymentServiceImpl.java
    ├── ReviewService.java
    ├── ReviewServiceImpl.java
    ├── VehicleService.java
    └── VehicleServiceImpl.java
```

## 📋 Prerequisites

- **Java**: JDK 21 or higher
- **Maven**: 3.6+ 
- **MySQL**: 8.0+
- **IDE**: IntelliJ IDEA / Eclipse / VS Code (recommended)

## 🚀 Setup & Installation

### 1. Clone the Repository
```bash
git clone <repository-url>
cd car-rental-system/backend/car-rental-system
```

### 2. Database Setup

Create a MySQL database:
```sql
CREATE DATABASE car_rental_db;
```

### 3. Configure Application Properties

Update `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/car_rental_db
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD

# JWT Configuration
jwt.token.secret=YOUR_SECRET_KEY
jwt.token.expiration.millis=3600000
```

### 4. Build the Project

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

Or run the main class:
```bash
java -jar target/car-rental-system-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## ⚙️ Configuration

### Application Properties

Key configuration options in `application.properties`:

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/car_rental_db
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# JWT
jwt.token.secret=YOUR_SECRET_KEY
jwt.token.expiration.millis=3600000  # 1 hour
```

## 📚 API Documentation

### Swagger UI

Once the application is running, access the API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### Base URL
```
http://localhost:8080/api
```

### Authentication

Most endpoints require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

### Main API Endpoints

#### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

#### User Profile
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update user profile
- `PUT /api/auth/profile` - Update profile (alternative)
- `DELETE /api/auth/profile` - Delete account

#### Vehicles
- `GET /api/vehicles` - Get all available vehicles
- `GET /api/vehicles/{id}` - Get vehicle by ID
- `GET /api/vehicles/vendor` - Get vendor's vehicles (Vendor only)
- `POST /api/vehicles` - Add vehicle (Vendor only)
- `PUT /api/vehicles/{id}` - Update vehicle (Vendor only)
- `DELETE /api/vehicles/{id}` - Delete vehicle (Vendor only)
- `PUT /api/vehicles/{id}/status` - Update vehicle status (Vendor only)

#### Bookings
- `POST /api/bookings` - Create booking (User)
- `GET /api/bookings/{id}` - Get booking by ID (User)
- `GET /api/bookings/user` - Get user's bookings (User)
- `PUT /api/bookings/{id}/cancel` - Cancel booking (User)
- `GET /api/bookings/vendor` - Get vendor's bookings (Vendor)

#### Payments
- `POST /api/payments` - Create payment (User)
- `GET /api/payments/{bookingId}` - Get payment by booking ID (User)
- `PUT /api/payments/{id}/status` - Update payment status (Admin)

#### Reviews
- `POST /api/reviews` - Create review (User)
- `GET /api/reviews/vehicle/{id}` - Get reviews for vehicle (User)
- `PUT /api/reviews/{id}/approve` - Approve review (Admin)
- `PUT /api/reviews/{id}/reject` - Reject review (Admin)

#### Complaints
- `POST /api/complaints` - Create complaint (User)
- `GET /api/complaints/user` - Get user's complaints (User)
- `GET /api/complaints` - Get all complaints (Admin)
- `PUT /api/complaints/{id}/resolve` - Resolve complaint (Admin)

#### Admin
- `GET /api/admin/stats` - Get admin statistics
- `GET /api/admin/users` - Get all users
- `GET /api/admin/bookings` - Get all bookings
- `GET /api/admin/payments` - Get all payments
- `GET /api/admin/vehicles` - Get all vehicles
- `GET /api/admin/users/pending` - Get pending users
- `PUT /api/admin/users/{id}/approve` - Approve user
- `PUT /api/admin/users/{id}/reject` - Reject user
- `DELETE /api/admin/users/{id}` - Delete user
- `GET /api/admin/reports/revenue` - Revenue report
- `GET /api/admin/reports/bookings` - Booking analytics
- `GET /api/admin/reports/vehicles` - Vehicle performance
- `GET /api/admin/reports/users` - User analytics

## 📦 Modules

The application is organized into the following modules:

1. **Authentication Module** (`MEMBER1_REVIEW.md`)
   - User registration and login
   - JWT token generation
   - Role-based access control

2. **Vehicle & Vendor Module** (`VEHICLE_VENDOR_MODULE.md`)
   - Vehicle CRUD operations
   - Vendor-vehicle relationships
   - Status management

3. **Booking Module** (`BOOKING_MODULE.md`)
   - Booking creation and management
   - Date validation
   - Amount calculation

4. **Payment Module** (`PAYMENT_MODULE.md`)
   - Payment processing
   - Status management
   - UPI support

5. **Review Module** (`REVIEW_MODULE.md`)
   - Review submission
   - Admin approval workflow

6. **Complaint Module** (`COMPLAINT_MODULE.md`)
   - Complaint submission
   - Admin resolution workflow

7. **Admin Module** (`ADMIN_MODULE.md`)
   - User management
   - System statistics
   - Reports & analytics

8. **Reports Module** (`REPORTS_MODULE.md`)
   - Revenue reports
   - Booking analytics
   - Vehicle performance
   - User analytics

## 🔒 Security

### JWT Authentication
- Tokens expire after 1 hour (configurable)
- Tokens include user ID, email, and role
- Stateless authentication

### Role-Based Access Control
- **ADMIN**: Full system access
- **VENDOR**: Vehicle and booking management
- **CUSTOMER**: Booking, payment, review, complaint operations

### Security Configuration
- CSRF disabled for stateless API
- Session management: STATELESS
- Password encryption: BCrypt
- Endpoint-level security via `SecurityConfig`

## 🗄️ Database Schema

### Main Entities

- **users**: User accounts (Admin, Vendor, Customer)
- **vehicles**: Vehicle listings
- **bookings**: Rental bookings
- **payments**: Payment transactions
- **reviews**: Vehicle reviews
- **complaints**: User complaints

### Relationships

- User (Vendor) → Vehicles (One-to-Many)
- User (Customer) → Bookings (One-to-Many)
- Vehicle → Bookings (One-to-Many)
- Booking → Payment (One-to-One)
- Booking → Reviews (One-to-Many)
- Booking → Complaints (One-to-Many)

## ▶️ Running the Application

### Development Mode
```bash
mvn spring-boot:run
```

### Production Mode
```bash
mvn clean package
java -jar target/car-rental-system-0.0.1-SNAPSHOT.jar
```

### Using Maven Wrapper
```bash
./mvnw spring-boot:run
```

## 🧪 Testing

### Manual Testing

Use tools like:
- **Postman**: API testing
- **Swagger UI**: Interactive API documentation
- **curl**: Command-line testing

### Example API Test

```bash
# Register a user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "password123",
    "phoneNo": "1234567890",
    "role": "CUSTOMER"
  }'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

## 📖 Documentation

Detailed module documentation is available:

- `ADMIN_MODULE.md` - Admin functionality
- `BOOKING_MODULE.md` - Booking system
- `PAYMENT_MODULE.md` - Payment processing
- `REVIEW_MODULE.md` - Review system
- `COMPLAINT_MODULE.md` - Complaint handling
- `VEHICLE_VENDOR_MODULE.md` - Vehicle management
- `REPORTS_MODULE.md` - Reports & analytics
- `MEMBER1_REVIEW.md` - Authentication review

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Verify MySQL is running
   - Check database credentials in `application.properties`
   - Ensure database exists

2. **Port Already in Use**
   - Change `server.port` in `application.properties`
   - Or stop the process using port 8080

3. **JWT Token Expired**
   - Tokens expire after 1 hour
   - Re-login to get a new token

4. **Foreign Key Constraint Errors**
   - Check for existing relationships before deletion
   - Use cascade operations where appropriate

## 📝 License

This project is part of an academic/educational car rental system.

## 👥 Contributors

- Member 1: Authentication & Core Setup
- Member 2: Vehicle & Vendor Module
- Member 3: Booking & Payment Module
- Member 4: Admin, Reviews & Complaints Module

## 📞 Support

For issues or questions, please refer to the module-specific documentation files or contact the development team.

---

**Last Updated**: January 2026
