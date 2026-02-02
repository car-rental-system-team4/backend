# Reports & Analytics Module - Implementation Documentation

## Overview
The Reports & Analytics Module provides comprehensive insights into the car rental system's performance, including revenue trends, booking analytics, vehicle performance, and user statistics. This module is exclusively available to administrators.

---

## Table of Contents
1. [Architecture](#architecture)
2. [Core Concepts Used](#core-concepts-used)
3. [Data Aggregation Concepts](#data-aggregation-concepts)
4. [Time-Series Data Handling](#time-series-data-handling)
5. [Backend Implementation](#backend-implementation)
6. [Frontend Implementation](#frontend-implementation)
7. [API Endpoints](#api-endpoints)
8. [How It Works - Step by Step](#how-it-works---step-by-step)
9. [Interview Questions & Answers](#interview-questions--answers)
10. [Charts & Visualizations](#charts--visualizations)
11. [Security](#security)
12. [Business Rules](#business-rules)

---

## Architecture

### Backend Layers
```
Controller (AdminController - Reports Endpoints)
    ↓
Service (AdminService → AdminServiceImpl)
    ↓
Repository (PaymentRepository, BookingRepository, VehicleRepository, UserRepository, ReviewRepository)
    ↓
Entity (Payment, Booking, Vehicle, User, Review)
    ↓
Database (payments, bookings, vehicles, users, reviews tables)
```

### Frontend Components
```
Pages:
  - AdminReportsPage.jsx (Main reports dashboard with charts)

Libraries:
  - Recharts (Chart visualization library)

Services:
  - api.js (Reports API functions)
```

---

## Core Concepts Used

### 1. **Data Aggregation**
**What it is**: Combining and summarizing data from multiple sources.

**How it's used**:
```java
// Revenue aggregation
Double totalRevenue = completedPayments.stream()
    .mapToDouble(Payment::getAmount)
    .sum();

// Booking count by status
Map<BookingStatus, Long> bookingsByStatus = allBookings.stream()
    .collect(Collectors.groupingBy(
        Booking::getStatus,
        Collectors.counting()
    ));
```

**Aggregation operations**:
- **Sum**: Total revenue, total bookings
- **Count**: Number of items by category
- **Average**: Average booking value, average duration
- **Grouping**: Group by status, month, role, etc.

**Interview Question**: "How do you aggregate revenue from payments?"
**Answer**:
1. **Filter**: Get only completed payments
2. **Map**: Extract amount from each payment
3. **Sum**: Add all amounts together

**Code**:
```java
List<Payment> completed = payments.stream()
    .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
    .collect(Collectors.toList());

Double total = completed.stream()
    .mapToDouble(Payment::getAmount)
    .sum();
```

---

### 2. **Time-Series Data Grouping**
**What it is**: Grouping data by time periods (monthly, yearly).

**How it's used**:
```java
// Group payments by month
Map<YearMonth, List<Payment>> paymentsByMonth = completedPayments.stream()
    .collect(Collectors.groupingBy(payment -> 
        YearMonth.from(payment.getPaymentDate())
    ));

// Calculate monthly revenue
List<MonthlyRevenue> monthlyBreakdown = paymentsByMonth.entrySet().stream()
    .map(entry -> {
        MonthlyRevenue monthly = new MonthlyRevenue();
        monthly.setMonth(entry.getKey().getMonthValue());
        monthly.setYear(entry.getKey().getYear());
        monthly.setRevenue(entry.getValue().stream()
            .mapToDouble(Payment::getAmount)
            .sum());
        return monthly;
    })
    .collect(Collectors.toList());
```

**YearMonth class**:
- **Purpose**: Represents year and month (e.g., 2025-01)
- **Benefits**: Easy comparison, sorting, formatting
- **Usage**: Group data by month for time-series analysis

**Interview Question**: "How do you generate monthly revenue reports?"
**Answer**:
1. **Group by month**: Use `YearMonth.from(date)` to group payments
2. **Sum amounts**: Calculate total for each month
3. **Sort by date**: Order months chronologically
4. **Return DTO**: Create MonthlyRevenue objects

---

### 3. **Stream API Advanced Operations**
**Complex data transformations**:

**Grouping**:
```java
Map<BookingStatus, Long> counts = bookings.stream()
    .collect(Collectors.groupingBy(
        Booking::getStatus,
        Collectors.counting()
    ));
```

**Filtering and Mapping**:
```java
List<TopVehicle> topVehicles = vehicles.stream()
    .filter(v -> v.getBookings().size() > 0)
    .map(vehicle -> {
        TopVehicle top = new TopVehicle();
        top.setVehicleId(vehicle.getId());
        top.setBookingCount(vehicle.getBookings().size());
        top.setTotalRevenue(calculateRevenue(vehicle));
        return top;
    })
    .sorted((a, b) -> b.getTotalRevenue().compareTo(a.getTotalRevenue()))
    .limit(10)
    .collect(Collectors.toList());
```

**Interview Question**: "Explain the Stream API operations used in reports."
**Answer**:
1. **filter()**: Filter data (e.g., completed payments only)
2. **map()**: Transform data (e.g., extract amounts)
3. **groupingBy()**: Group by key (e.g., by status, by month)
4. **collectingAndThen()**: Additional transformation after collection
5. **sorted()**: Sort results
6. **limit()**: Get top N items

---

### 4. **Nested DTOs for Complex Responses**
**What it is**: DTOs containing other DTOs for structured responses.

**Example**:
```java
public class RevenueReportResponse {
    private Double totalRevenue;
    private List<MonthlyRevenue> monthlyBreakdown;  // Nested DTO
    
    @Data
    public static class MonthlyRevenue {
        private Integer month;
        private Integer year;
        private Double revenue;
        private Integer bookingCount;
    }
}
```

**Why use nested DTOs?**
- **Structure**: Organized, hierarchical data
- **Type safety**: Compile-time checking
- **Reusability**: Can reuse nested DTOs

---

## Data Aggregation Concepts

### 1. **Revenue Calculation**
**Total Revenue**:
```java
Double totalRevenue = completedPayments.stream()
    .mapToDouble(Payment::getAmount)
    .sum();
```

**Monthly Revenue**:
```java
Double monthlyRevenue = completedPayments.stream()
    .filter(p -> YearMonth.from(p.getPaymentDate()).equals(YearMonth.now()))
    .mapToDouble(Payment::getAmount)
    .sum();
```

**Yearly Revenue**:
```java
Double yearlyRevenue = completedPayments.stream()
    .filter(p -> p.getPaymentDate().getYear() == LocalDate.now().getYear())
    .mapToDouble(Payment::getAmount)
    .sum();
```

---

### 2. **Booking Analytics**
**Count by Status**:
```java
Map<BookingStatus, Long> bookingsByStatus = allBookings.stream()
    .collect(Collectors.groupingBy(
        Booking::getStatus,
        Collectors.counting()
    ));
```

**Average Duration**:
```java
double averageDuration = allBookings.stream()
    .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
    .mapToLong(b -> ChronoUnit.DAYS.between(b.getPickupDate(), b.getReturnDate()))
    .average()
    .orElse(0.0);
```

**Cancellation Rate**:
```java
long totalBookings = allBookings.size();
long cancelledBookings = allBookings.stream()
    .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
    .count();
double cancellationRate = (cancelledBookings * 100.0) / totalBookings;
```

---

### 3. **Top N Queries**
**Top Vehicles by Revenue**:
```java
List<TopVehicle> topVehicles = vehicles.stream()
    .map(vehicle -> {
        TopVehicle top = new TopVehicle();
        top.setVehicleId(vehicle.getId());
        top.setTotalRevenue(calculateVehicleRevenue(vehicle));
        return top;
    })
    .sorted((a, b) -> b.getTotalRevenue().compareTo(a.getTotalRevenue()))
    .limit(10)  // Top 10
    .collect(Collectors.toList());
```

**Top Customers by Spending**:
```java
List<TopCustomer> topCustomers = users.stream()
    .filter(user -> user.getRole() == UserRole.CUSTOMER)
    .map(user -> {
        TopCustomer top = new TopCustomer();
        top.setUserId(user.getId());
        top.setTotalSpent(calculateUserSpending(user));
        return top;
    })
    .sorted((a, b) -> b.getTotalSpent().compareTo(a.getTotalSpent()))
    .limit(10)
    .collect(Collectors.toList());
```

---

## Time-Series Data Handling

### 1. **YearMonth for Monthly Grouping**
**What it is**: Java 8+ class representing year and month.

**How it's used**:
```java
YearMonth currentMonth = YearMonth.now();  // 2025-01
YearMonth paymentMonth = YearMonth.from(payment.getPaymentDate());

if (paymentMonth.equals(currentMonth)) {
    // Payment is in current month
}
```

**Benefits**:
- **Comparison**: Easy to compare months
- **Sorting**: Natural chronological order
- **Formatting**: Easy to format as "January 2025"

---

### 2. **Monthly Breakdown Generation**
**Last 12 months**:
```java
YearMonth current = YearMonth.now();
List<YearMonth> last12Months = new ArrayList<>();
for (int i = 11; i >= 0; i--) {
    last12Months.add(current.minusMonths(i));
}

// Group payments by month
Map<YearMonth, List<Payment>> byMonth = payments.stream()
    .collect(Collectors.groupingBy(p -> YearMonth.from(p.getPaymentDate())));

// Create monthly breakdown
List<MonthlyRevenue> breakdown = last12Months.stream()
    .map(month -> {
        MonthlyRevenue monthly = new MonthlyRevenue();
        monthly.setMonth(month.getMonthValue());
        monthly.setYear(month.getYear());
        
        List<Payment> monthPayments = byMonth.getOrDefault(month, Collections.emptyList());
        monthly.setRevenue(monthPayments.stream()
            .mapToDouble(Payment::getAmount)
            .sum());
        
        return monthly;
    })
    .collect(Collectors.toList());
```

---

## How It Works - Step by Step

### Example: Generate Revenue Report

**1. Frontend Request**:
```javascript
const response = await getRevenueReport('year');
```

**2. Service Method**:
```java
public RevenueReportResponse getRevenueReport(String period) {
    RevenueReportResponse response = new RevenueReportResponse();
    
    // Step 1: Get all payments
    List<Payment> allPayments = paymentRepository.findAll();
    
    // Step 2: Filter completed payments
    List<Payment> completedPayments = allPayments.stream()
        .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
        .collect(Collectors.toList());
    
    // Step 3: Calculate total revenue
    Double totalRevenue = completedPayments.stream()
        .mapToDouble(Payment::getAmount)
        .sum();
    response.setTotalRevenue(totalRevenue);
    
    // Step 4: Calculate monthly revenue
    YearMonth currentMonth = YearMonth.now();
    Double monthlyRevenue = completedPayments.stream()
        .filter(p -> YearMonth.from(p.getPaymentDate()).equals(currentMonth))
        .mapToDouble(Payment::getAmount)
        .sum();
    response.setMonthlyRevenue(monthlyRevenue);
    
    // Step 5: Generate monthly breakdown (last 12 months)
    Map<YearMonth, List<Payment>> byMonth = completedPayments.stream()
        .collect(Collectors.groupingBy(p -> YearMonth.from(p.getPaymentDate())));
    
    List<MonthlyRevenue> monthlyBreakdown = generateMonthlyBreakdown(byMonth);
    response.setMonthlyBreakdown(monthlyBreakdown);
    
    return response;
}
```

**3. Frontend Visualization**:
```javascript
// Use Recharts to visualize
<LineChart data={monthlyBreakdown}>
    <Line dataKey="revenue" />
    <XAxis dataKey="month" />
    <YAxis />
</LineChart>
```

---

## Interview Questions & Answers

### Q1: "How do you generate monthly revenue reports?"
**Answer**:
1. **Get all payments**: Fetch from repository
2. **Filter completed**: Only count completed payments
3. **Group by month**: Use `YearMonth.from(paymentDate)` to group
4. **Sum amounts**: Calculate total for each month
5. **Sort chronologically**: Order by year and month
6. **Return DTO**: Create MonthlyRevenue objects

**Code**:
```java
Map<YearMonth, List<Payment>> byMonth = payments.stream()
    .collect(Collectors.groupingBy(p -> YearMonth.from(p.getPaymentDate())));

List<MonthlyRevenue> breakdown = byMonth.entrySet().stream()
    .map(entry -> {
        MonthlyRevenue monthly = new MonthlyRevenue();
        monthly.setRevenue(entry.getValue().stream()
            .mapToDouble(Payment::getAmount)
            .sum());
        return monthly;
    })
    .collect(Collectors.toList());
```

---

### Q2: "How do you calculate top performing vehicles?"
**Answer**:
1. **Get all vehicles**: Fetch from repository
2. **Calculate metrics**: For each vehicle, calculate booking count and revenue
3. **Sort by revenue**: Sort vehicles by total revenue (descending)
4. **Limit to top 10**: Use `limit(10)`
5. **Return DTO**: Create TopVehicle objects

**Code**:
```java
List<TopVehicle> topVehicles = vehicles.stream()
    .map(vehicle -> {
        TopVehicle top = new TopVehicle();
        top.setVehicleId(vehicle.getId());
        top.setBookingCount(vehicle.getBookings().size());
        top.setTotalRevenue(calculateRevenue(vehicle));
        return top;
    })
    .sorted((a, b) -> b.getTotalRevenue().compareTo(a.getTotalRevenue()))
    .limit(10)
    .collect(Collectors.toList());
```

---

### Q3: "Explain the Stream API operations in report generation."
**Answer**:
1. **filter()**: Filter data (e.g., completed payments, active bookings)
2. **map()**: Transform entities to DTOs or extract values
3. **groupingBy()**: Group by key (status, month, role)
4. **collectingAndThen()**: Additional transformation
5. **sorted()**: Sort results
6. **limit()**: Get top N items
7. **sum()**, **average()**, **count()**: Aggregation operations

**Example**:
```java
Map<BookingStatus, Long> counts = bookings.stream()
    .collect(Collectors.groupingBy(
        Booking::getStatus,
        Collectors.counting()  // Count items in each group
    ));
```

---

### Q4: "How do you handle time-series data for reports?"
**Answer**:
1. **YearMonth class**: Use for monthly grouping
2. **Group by month**: `groupingBy(p -> YearMonth.from(p.getDate()))`
3. **Generate last N months**: Loop to create list of months
4. **Fill missing months**: Ensure all months in range are included (even with 0 revenue)
5. **Sort chronologically**: Order by year and month

**Benefits**:
- **Consistent**: All months included in report
- **Visualization**: Easy to plot on charts
- **Comparison**: Can compare month-over-month

---

## Backend Implementation

### 1. Report DTOs

#### RevenueReportResponse
**File:** `dto/RevenueReportResponse.java`

**Fields:**
- `totalRevenue` - Total revenue from all completed payments
- `monthlyRevenue` - Revenue for current month
- `yearlyRevenue` - Revenue for current year
- `monthlyBreakdown` - List of monthly revenue data (last 12 months)
- `revenueByStatus` - Revenue grouped by payment status
- `averageBookingValue` - Average amount per booking
- `totalTransactions` - Total number of completed transactions

**Nested Class:**
- `MonthlyRevenue` - Contains month, year, revenue, and booking count

---

#### BookingAnalyticsResponse
**File:** `dto/BookingAnalyticsResponse.java`

**Fields:**
- `totalBookings` - Total number of bookings
- `pendingBookings` - Count of pending bookings
- `confirmedBookings` - Count of confirmed bookings
- `completedBookings` - Count of completed bookings
- `cancelledBookings` - Count of cancelled bookings
- `bookingsByStatus` - Map of status to count
- `monthlyBookings` - List of monthly booking counts (last 12 months)
- `averageBookingDuration` - Average rental duration in days
- `cancellationRate` - Percentage of cancelled bookings

**Nested Class:**
- `MonthlyBookings` - Contains month, year, and count

---

#### VehiclePerformanceResponse
**File:** `dto/VehiclePerformanceResponse.java`

**Fields:**
- `topVehicles` - List of top 10 performing vehicles
- `totalVehicles` - Total number of vehicles
- `availableVehicles` - Count of available vehicles
- `bookedVehicles` - Count of booked vehicles
- `maintenanceVehicles` - Count of vehicles in maintenance
- `averageUtilizationRate` - Average vehicle utilization percentage

**Nested Class:**
- `TopVehicle` - Contains vehicle ID, make, model, booking count, total revenue, and average rating

---

#### UserAnalyticsResponse
**File:** `dto/UserAnalyticsResponse.java`

**Fields:**
- `totalUsers` - Total number of users
- `activeUsers` - Users with at least one booking
- `newUsersThisMonth` - New users registered this month
- `usersByRole` - Map of role to user count
- `topCustomers` - List of top 10 customers by spending
- `averageBookingsPerUser` - Average number of bookings per user

**Nested Class:**
- `TopCustomer` - Contains user ID, name, email, booking count, and total spent

---

### 2. AdminService Methods

#### getRevenueReport(String period)
**File:** `service/AdminServiceImpl.java`

**Business Logic:**
1. Fetches all payments from database
2. Filters completed payments for revenue calculations
3. Calculates total, monthly, and yearly revenue
4. Generates monthly breakdown for last 12 months
5. Groups revenue by payment status
6. Calculates average booking value
7. Returns comprehensive revenue report

**Key Calculations:**
- Total revenue: Sum of all completed payment amounts
- Monthly revenue: Sum of completed payments in current month
- Yearly revenue: Sum of completed payments in current year
- Monthly breakdown: Revenue and booking count per month (last 12 months)

---

#### getBookingAnalytics()
**File:** `service/AdminServiceImpl.java`

**Business Logic:**
1. Fetches all bookings from database
2. Counts bookings by status (PENDING, CONFIRMED, COMPLETED, CANCELLED)
3. Generates monthly booking trends (last 12 months)
4. Calculates average booking duration
5. Calculates cancellation rate
6. Returns comprehensive booking analytics

**Key Calculations:**
- Booking counts by status
- Monthly booking trends
- Average duration: Average days between pickup and return dates
- Cancellation rate: (Cancelled bookings / Total bookings) * 100

---

#### getVehiclePerformance()
**File:** `service/AdminServiceImpl.java`

**Business Logic:**
1. Fetches all vehicles, bookings, payments, and reviews
2. Counts vehicles by status (AVAILABLE, BOOKED, MAINTENANCE)
3. Calculates booking count per vehicle
4. Calculates revenue per vehicle from completed payments
5. Calculates average rating per vehicle from approved reviews
6. Sorts vehicles by revenue and returns top 10
7. Calculates average utilization rate
8. Returns vehicle performance report

**Key Calculations:**
- Top vehicles: Sorted by total revenue, limited to top 10
- Vehicle utilization: (Total booking days / (Total vehicles * 365)) * 100
- Average rating: Sum of ratings / Count of reviews per vehicle

---

#### getUserAnalytics()
**File:** `service/AdminServiceImpl.java`

**Business Logic:**
1. Fetches all users, bookings, and payments
2. Counts total users and active users (users with bookings)
3. Counts new users registered this month
4. Groups users by role (CUSTOMER, VENDOR, ADMIN)
5. Calculates booking count and total spending per customer
6. Sorts customers by spending and returns top 10
7. Calculates average bookings per user
8. Returns user analytics report

**Key Calculations:**
- Active users: Distinct users who have made at least one booking
- Top customers: Sorted by total spent, limited to top 10
- Average bookings per user: Total bookings / Total users

---

### 3. AdminController Endpoints

**File:** `controller/AdminController.java`

#### GET /api/admin/reports/revenue
**Purpose:** Get revenue report

**Query Parameters:**
- `period` (optional, default: "year") - Period for report (month/year)

**Response:** RevenueReportResponse

**Security:** Requires ADMIN role

---

#### GET /api/admin/reports/bookings
**Purpose:** Get booking analytics

**Response:** BookingAnalyticsResponse

**Security:** Requires ADMIN role

---

#### GET /api/admin/reports/vehicles
**Purpose:** Get vehicle performance report

**Response:** VehiclePerformanceResponse

**Security:** Requires ADMIN role

---

#### GET /api/admin/reports/users
**Purpose:** Get user analytics

**Response:** UserAnalyticsResponse

**Security:** Requires ADMIN role

---

## Frontend Implementation

### 1. API Service
**File:** `services/api.js`

**Functions:**
```javascript
export const getRevenueReport = (period = 'year') => api.get(`/admin/reports/revenue?period=${period}`);
export const getBookingAnalytics = () => api.get("/admin/reports/bookings");
export const getVehiclePerformance = () => api.get("/admin/reports/vehicles");
export const getUserAnalytics = () => api.get("/admin/reports/users");
```

---

### 2. AdminReportsPage
**File:** `pages/Admin/AdminReportsPage.jsx`

**Features:**
- **Key Metrics Cards:** Total Revenue, Total Bookings, Total Vehicles, Total Users
- **Revenue Overview:**
  - Line chart showing monthly revenue trends (last 12 months)
  - Revenue by status breakdown
  - Average booking value and total transactions
- **Booking Analytics:**
  - Bar chart showing monthly booking trends
  - Pie chart showing booking status distribution
  - Booking status cards (Pending, Confirmed, Completed, Cancelled)
- **Vehicle Performance:**
  - Top 5 performing vehicles with revenue and ratings
  - Progress bars showing booking counts
- **User Analytics:**
  - Pie chart showing user distribution by role
  - User statistics (Active users, New users, Avg bookings/user)
  - Top 5 customers by spending

**Charts Used:**
- **LineChart** (Recharts) - Revenue trends
- **BarChart** (Recharts) - Booking trends
- **PieChart** (Recharts) - Status and role distributions

**Period Toggle:**
- Month/Year toggle for revenue report
- Updates all reports when period changes

---

### 3. Chart Library
**Library:** Recharts

**Installation:**
```bash
npm install recharts
```

**Components Used:**
- `LineChart`, `Line` - Revenue trends
- `BarChart`, `Bar` - Booking trends
- `PieChart`, `Pie`, `Cell` - Status and role distributions
- `XAxis`, `YAxis` - Chart axes
- `CartesianGrid` - Grid lines
- `Tooltip` - Hover information
- `Legend` - Chart legends
- `ResponsiveContainer` - Responsive chart container

---

## API Endpoints

### GET /api/admin/reports/revenue
**Purpose:** Get comprehensive revenue report

**Query Parameters:**
- `period` (optional) - "month" or "year" (default: "year")

**Response Example:**
```json
{
  "totalRevenue": 1245000.0,
  "monthlyRevenue": 125000.0,
  "yearlyRevenue": 1245000.0,
  "monthlyBreakdown": [
    {
      "month": "JANUARY",
      "year": 2025,
      "revenue": 95000.0,
      "bookingCount": 45
    },
    ...
  ],
  "revenueByStatus": {
    "COMPLETED": 1245000.0,
    "PENDING": 5000.0,
    "FAILED": 0.0
  },
  "averageBookingValue": 2500.0,
  "totalTransactions": 498
}
```

**Security:** Requires ADMIN role

---

### GET /api/admin/reports/bookings
**Purpose:** Get booking analytics

**Response Example:**
```json
{
  "totalBookings": 500,
  "pendingBookings": 25,
  "confirmedBookings": 150,
  "completedBookings": 300,
  "cancelledBookings": 25,
  "bookingsByStatus": {
    "PENDING": 25,
    "CONFIRMED": 150,
    "COMPLETED": 300,
    "CANCELLED": 25
  },
  "monthlyBookings": [
    {
      "month": "JANUARY",
      "year": 2025,
      "count": 45
    },
    ...
  ],
  "averageBookingDuration": 5.5,
  "cancellationRate": 5.0
}
```

**Security:** Requires ADMIN role

---

### GET /api/admin/reports/vehicles
**Purpose:** Get vehicle performance report

**Response Example:**
```json
{
  "topVehicles": [
    {
      "vehicleId": 1,
      "make": "Maruti",
      "model": "Swift",
      "bookingCount": 45,
      "totalRevenue": 120000.0,
      "averageRating": 4.5
    },
    ...
  ],
  "totalVehicles": 50,
  "availableVehicles": 35,
  "bookedVehicles": 10,
  "maintenanceVehicles": 5,
  "averageUtilizationRate": 65.5
}
```

**Security:** Requires ADMIN role

---

### GET /api/admin/reports/users
**Purpose:** Get user analytics

**Response Example:**
```json
{
  "totalUsers": 200,
  "activeUsers": 150,
  "newUsersThisMonth": 25,
  "usersByRole": {
    "CUSTOMER": 180,
    "VENDOR": 15,
    "ADMIN": 5
  },
  "topCustomers": [
    {
      "userId": 10,
      "userName": "Rajesh Kumar",
      "email": "rajesh@example.com",
      "bookingCount": 15,
      "totalSpent": 45000.0
    },
    ...
  ],
  "averageBookingsPerUser": 2.5
}
```

**Security:** Requires ADMIN role

---

## Charts & Visualizations

### Revenue Overview
- **Line Chart:** Monthly revenue trends (last 12 months)
- **Revenue Cards:** Total, Monthly, Yearly revenue
- **Status Breakdown:** Revenue by payment status
- **Metrics:** Average booking value, Total transactions

### Booking Analytics
- **Bar Chart:** Monthly booking trends (last 12 months)
- **Pie Chart:** Booking status distribution
- **Status Cards:** Pending, Confirmed, Completed, Cancelled counts
- **Metrics:** Average booking duration, Cancellation rate

### Vehicle Performance
- **Top Vehicles List:** Top 5 vehicles with:
  - Booking count
  - Total revenue
  - Average rating
  - Progress bars
- **Vehicle Status:** Available, Booked, Maintenance counts
- **Metrics:** Total vehicles, Utilization rate

### User Analytics
- **Pie Chart:** User distribution by role
- **User Statistics:**
  - Active users
  - New users this month
  - Average bookings per user
- **Top Customers:** Top 5 customers by spending

---

## Security

### Role-Based Access Control
- ✅ All report endpoints require ADMIN role
- ✅ Frontend page checks for admin authentication
- ✅ Unauthorized access returns 403 Forbidden

### Data Privacy
- ✅ Only aggregated data is returned
- ✅ No sensitive user information exposed
- ✅ Revenue data based on completed payments only

---

## Business Rules

### Revenue Calculations
1. Only COMPLETED payments are counted in revenue
2. Monthly revenue: Sum of completed payments in current month
3. Yearly revenue: Sum of completed payments in current year
4. Average booking value: Total revenue / Number of completed payments

### Booking Analytics
1. Bookings counted by their current status
2. Monthly trends based on booking creation date
3. Average duration: Days between pickup and return dates
4. Cancellation rate: (Cancelled / Total) * 100

### Vehicle Performance
1. Top vehicles sorted by total revenue
2. Revenue calculated from completed payments linked to bookings
3. Ratings from approved reviews only
4. Utilization rate: (Total booking days / (Vehicles * 365)) * 100

### User Analytics
1. Active users: Users with at least one booking
2. New users: Users registered in current month
3. Top customers: Sorted by total spending
4. Average bookings: Total bookings / Total users

---

## Summary

The Reports & Analytics Module provides:
- ✅ Comprehensive revenue reporting with trends
- ✅ Booking analytics with status breakdown
- ✅ Vehicle performance metrics
- ✅ User analytics and top customers
- ✅ Interactive charts and visualizations
- ✅ Period-based filtering (Month/Year)
- ✅ Real-time data from database
- ✅ Admin-only access
- ✅ Responsive design

**Status: ✅ FULLY IMPLEMENTED**

The reports module is production-ready with comprehensive analytics and beautiful visualizations.
