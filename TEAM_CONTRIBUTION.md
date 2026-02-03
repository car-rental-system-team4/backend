# Team Contribution Guide

This guide outlines how to distribute the "Car Rental System" project into individual contributions for 4 team members. Each member will push their specific module to the organization repositories.

## Team Member Roles & Modules

### 👤 Member 1: Authentication & Core Setup (Project Lead)
*Responsible for the initial project skeleton, security, and public-facing base pages.*
*   **Backend**:
    *   **Setup**: `pom.xml`, `application.properties`, `SecurityConfig`, `JwtUtil`, `JwtFilter`, `GlobalExceptionHandler`.
    *   **Entities**: `User`, `Role` (Enums).
    *   **Controllers**: `AuthController`, `UserController`.
    *   **Services**: `AuthService`, `UserService`, `CustomUserDetailsService`.
*   **Frontend**:
    *   **Setup**: `vite`, `App.jsx`, `AuthContext`, `api.js`, `Header.jsx`, `Footer.jsx`.
    *   **Pages**: `LoginPage`, `RegisterPage`, `UserProfilePage`, `HomePage`, `AboutPage`.

### 👤 Member 2: Vehicle & Vendor Management
*Responsible for car inventory and vendor operations.*
*   **Backend**:
    *   **Entities**: `Vehicle`, `Image` (if separate).
    *   **Controllers**: `VehicleController`.
    *   **Services**: `VehicleService`, `FileStorageService` (S3).
    *   **Repository**: `VehicleRepository`.
*   **Frontend**:
    *   **Pages**: `VendorDashboard`, `AddCarPage`, `MyCarsPage`, `CarDetailsPage`, `CarsPage` (Public Listing).
    *   **Components**: `CarCard`, `ImageUpload`, `CarsSection`.

### 👤 Member 3: Booking & Payment System
*Responsible for the reservation logic and transaction handling.*
*   **Backend**:
    *   **Entities**: `Booking`, `Payment`.
    *   **Controllers**: `BookingController`, `PaymentController`.
    *   **Services**: `BookingService`, `PaymentService`.
    *   **Repository**: `BookingRepository`, `PaymentRepository`.
*   **Frontend**:
    *   **Pages**: `BookingsPage` (User view), `PaymentPage`, `BookingSuccessPage`.
    *   **Components**: `BookingForm`, `PaymentStatus`.

### 👤 Member 4: Admin, Reports & Feedback
*Responsible for system administration, analytics, and user feedback.*
*   **Backend**:
    *   **Entities**: `Complaint`, `Review`, `ContactMessage`.
    *   **Controllers**: `AdminController`, `ComplaintController`, `ReviewController`, `ContactController`.
    *   **Services**: `AdminService`, `ComplaintService`, `ReviewService`, `ReportService`, `ContactService`.
*   **Frontend**:
    *   **Pages**: `AdminDashboard` (Users, Cars, Bookings overview), `ReportsPage`, `ComplaintsPage`, `ContactPage`.
    *   **Components**: `Charts`, `ReviewList`, `ComplaintForm`, `ContactForm`.

---

## 🚀 Step-by-Step Push Strategy

Since this is a shared codebase, you must push in a specific order to avoid conflicts.

### 1. Member 1 (The Starter)
1.  **Clone** the empty organization repos:
    *   `git clone https://github.com/car-rental-system-team4/backend.git`
    *   `git clone https://github.com/car-rental-system-team4/frontend.git`
2.  **Add** the skeleton code (pom.xml, configs) and your Auth/User modules.
3.  **Push** to `main`.
    *   `git add .`
    *   `git commit -m "feat: Initialize project with Auth and Core Security"`
    *   `git push origin main`

### 2. Member 2, 3, & 4 (The Contributors)
*Do this one by one or communicate when you are pushing.*
1.  **Clone** the repo (OR `git pull origin main` if you already cloned).
2.  **Create a Branch** (Start from fresh main):
    *   `git checkout -b feature/my-module`
3.  **Copy** your specific files (Controllers, Services, Pages) into the project folders.
    *   *Be careful not to overwrite `pom.xml` or `App.jsx` unless necessary. Append your changes.*
4.  **Push & Merge**:
    *   `git add .`
    *   `git commit -m "feat: Add [Your Module Name] functionality"`
    *   `git push origin feature/my-module`
    *   (Then Create a Pull Request on GitHub and Merge it)

## 📂 Folder Breakdown for "Copy-Pasting"

If you are copying files from the `CDAC-Project` monorepo or your laptop:

*   **Backend**: `src/main/java/com/carrental/...` (Copy your Controller, Service, Impl, Repo files).
*   **Frontend**: `src/pages/...` (Copy your specific Page folders).

> [!TIP]
> **Member 1** should share the `CDAC-Project` code with everyone first. Each member then "picks" their files and pushes them to the Team Organization repo following the order above.
