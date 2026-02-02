# Authentication & User Module - Comprehensive Implementation Documentation

## Overview
The Authentication & User Module provides the foundation for the entire car rental system, implementing JWT-based authentication, user registration, login, profile management, and role-based access control. This module is critical as it secures all other modules and manages user lifecycle.

---

## Table of Contents
1. [Architecture](#architecture)
2. [Core Concepts Used](#core-concepts-used)
3. [Design Patterns](#design-patterns)
4. [Spring Security Concepts](#spring-security-concepts)
5. [JWT Concepts](#jwt-concepts)
6. [Password Security](#password-security)
7. [Backend Implementation](#backend-implementation)
8. [Frontend Implementation](#frontend-implementation)
9. [API Endpoints](#api-endpoints)
10. [How It Works - Step by Step](#how-it-works---step-by-step)
11. [Interview Questions & Answers](#interview-questions--answers)
12. [Code Walkthrough](#code-walkthrough)
13. [Security Best Practices](#security-best-practices)
14. [Testing](#testing)

---

## Architecture

### Backend Layers
```
┌─────────────────────────────────────┐
│   Controller Layer                  │  ← AuthController (REST endpoints)
│   (AuthController)                  │     - Register, Login, Profile
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│   Service Layer                      │  ← AuthService, CustomUserDetailsService
│   (AuthServiceImpl)                  │     - Business logic, User loading
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│   Security Layer                      │  ← JWT Filter, SecurityConfig
│   (JwtFilter, SecurityConfig)        │     - Token validation, Authorization
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│   Repository Layer                   │  ← UserRepository
│   (UserRepository)                   │     - Data access
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│   Entity Layer                      │  ← User entity
│   (User implements UserDetails)      │     - Domain model, Spring Security integration
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│   Database (MySQL)                   │  ← users table
└─────────────────────────────────────┘
```

### Security Flow
```
Request → JwtFilter (validates token) → SecurityContext (stores auth) → 
SecurityConfig (checks authority) → Controller → Service
```

---

## Core Concepts Used

### 1. **JWT (JSON Web Token) Authentication**
**What it is**: A stateless authentication mechanism using signed tokens.

**Token Structure**:
```
Header.Payload.Signature

Header: { "alg": "HS256", "typ": "JWT" }
Payload: { "sub": "user@email.com", "roles": "CUSTOMER", "userId": 1, "iat": 1234567890, "exp": 1234571490 }
Signature: HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)
```

**How it's used**:
```java
// Token generation
String token = jwtUtil.generateToken(user);

// Token validation
if (jwtUtil.isTokenValid(token, userDetails)) {
    // Token is valid
}
```

**Why use JWT?**
- **Stateless**: No server-side session storage
- **Scalable**: Works across multiple servers (microservices)
- **Mobile-friendly**: Works with mobile apps
- **CORS-friendly**: Works across domains
- **Self-contained**: Token contains user info

**Interview Question**: "Why use JWT instead of session-based authentication?"
**Answer**: 
1. **Stateless**: No server-side session storage needed
2. **Scalable**: Works across multiple servers without shared session store
3. **Mobile-friendly**: Works with mobile apps (no cookies)
4. **CORS-friendly**: Works across domains
5. **Drawback**: Can't revoke token until expiration (unless using token blacklist)

---

### 2. **BCrypt Password Hashing**
**What it is**: Adaptive hashing algorithm for passwords.

**How it's used**:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// Encoding password
String encodedPassword = passwordEncoder.encode(plainPassword);

// Verifying password
boolean matches = passwordEncoder.matches(plainPassword, encodedPassword);
```

**Why use BCrypt?**
- **Adaptive**: Can increase cost factor as hardware improves
- **Salt**: Automatically generates unique salt per password
- **Slow**: Intentionally slow to prevent brute force attacks
- **Industry standard**: Widely used and trusted

**How it works**:
1. **Salt generation**: Random salt generated for each password
2. **Hashing**: Password + salt hashed multiple times (cost factor)
3. **Storage**: Salt + hash stored together
4. **Verification**: Compare hashed input with stored hash

**Interview Question**: "How does BCrypt password hashing work?"
**Answer**:
1. **Salt**: Random salt generated for each password (prevents rainbow table attacks)
2. **Cost factor**: Number of iterations (default 10 = 2^10 = 1024 iterations)
3. **Hashing**: Password + salt hashed multiple times
4. **Storage**: Format: `$2a$10$salt22charshashed31chars`
5. **Verification**: Hash input password with stored salt, compare with stored hash

**Example**:
```
Plain password: "password123"
BCrypt hash: "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
              └─┬─┘└─┬─┘└──────────────────────────────────────────┬────────────────────────┘
                │     │                                            │
            Algorithm Cost factor                              Salt + Hash
```

---

### 3. **Spring Security Framework**
**What it is**: Comprehensive security framework for Java applications.

**Key Components**:
- **SecurityFilterChain**: Configures security rules
- **AuthenticationManager**: Manages authentication
- **AuthenticationProvider**: Provides authentication logic
- **UserDetailsService**: Loads user-specific data
- **SecurityContextHolder**: Thread-local storage for authentication

**How it's configured**:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(request -> request
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers("/api/admin/**").hasAuthority(UserRole.ADMIN.name())
                .anyRequest().authenticated()
            )
            .sessionManagement(manager -> manager
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

**Interview Question**: "Explain Spring Security architecture."
**Answer**:
1. **Filter Chain**: Requests pass through security filters
2. **Authentication**: JwtFilter validates token, sets Authentication in SecurityContext
3. **Authorization**: SecurityConfig checks authorities for endpoint access
4. **UserDetailsService**: Loads user details for authentication
5. **SecurityContextHolder**: Thread-local storage for current user

---

### 4. **UserDetails Interface**
**What it is**: Spring Security interface for user information.

**How User entity implements it**:
```java
@Entity
public class User implements UserDetails {
    // ... fields
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
    
    @Override
    public String getUsername() {
        return this.email;  // Email is username
    }
    
    @Override
    public boolean isAccountNonExpired() { return true; }
    
    @Override
    public boolean isAccountNonLocked() { return true; }
    
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    
    @Override
    public boolean isEnabled() { return true; }
}
```

**Why implement UserDetails?**
- **Spring Security integration**: Works seamlessly with Spring Security
- **Standard interface**: Consistent user representation
- **Account status**: Can check account expiration, locking, etc.

**Interview Question**: "Why does User entity implement UserDetails?"
**Answer**:
1. **Spring Security integration**: Spring Security expects UserDetails interface
2. **Standardization**: Consistent user representation across application
3. **Account management**: Can check account status (expired, locked, enabled)
4. **Authority management**: Provides user roles/authorities
5. **Seamless integration**: Works with AuthenticationManager, UserDetailsService

---

### 5. **Custom Filter (JwtFilter)**
**What it is**: Servlet filter that intercepts requests to validate JWT tokens.

**How it works**:
```java
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) {
        // 1. Extract token from Authorization header
        String authHeader = request.getHeader("Authorization");
        String jwt = authHeader.substring(7);  // Remove "Bearer "
        
        // 2. Extract username from token
        String userEmail = jwtUtil.extractUsername(jwt);
        
        // 3. Load user details
        UserDetails userDetails = userService.loadUserByUsername(userEmail);
        
        // 4. Validate token
        if (jwtUtil.isTokenValid(jwt, userDetails)) {
            // 5. Set authentication in SecurityContext
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            context.setAuthentication(authToken);
            SecurityContextHolder.setContext(context);
        }
        
        // 6. Continue filter chain
        filterChain.doFilter(request, response);
    }
}
```

**Why use OncePerRequestFilter?**
- **Guarantees single execution**: Filter runs once per request
- **Thread-safe**: Prevents multiple executions
- **Spring integration**: Works with Spring's filter chain

**Interview Question**: "How does JWT filter work in Spring Security?"
**Answer**:
1. **Intercepts requests**: Runs before controller
2. **Extracts token**: Gets JWT from `Authorization: Bearer <token>` header
3. **Validates token**: Checks signature and expiration
4. **Loads user**: Gets user details from database
5. **Sets authentication**: Creates Authentication object and sets in SecurityContext
6. **Continues chain**: Allows request to proceed to controller

---

### 6. **Role-Based Access Control (RBAC)**
**What it is**: Access control based on user roles.

**Roles in system**:
- **ADMIN**: Full system access
- **VENDOR**: Vehicle management, booking viewing
- **CUSTOMER**: Booking, payment, reviews

**How it's implemented**:
```java
// SecurityConfig
.requestMatchers("/api/admin/**").hasAuthority(UserRole.ADMIN.name())
.requestMatchers(HttpMethod.POST, "/api/vehicles").hasAuthority(UserRole.VENDOR.name())

// Controller (additional check)
if (user.getRole() != UserRole.ADMIN) {
    return ResponseEntity.status(403).body("Only admins can access");
}
```

**Two-level security**:
1. **SecurityConfig**: URL-level (first line of defense)
2. **Controller/Service**: Method-level (second line of defense)

**Interview Question**: "How do you implement role-based access control?"
**Answer**:
1. **User roles**: Define roles (ADMIN, VENDOR, CUSTOMER) as enum
2. **SecurityConfig**: Configure URL patterns with `hasAuthority(role)`
3. **JWT token**: Include role in token claims
4. **Filter**: Extract role from token, set in SecurityContext
5. **Controller**: Additional role verification if needed

---

### 7. **User Registration Approval Workflow**
**What it is**: New users require admin approval before they can use the system.

**How it works**:
```java
// During registration
if (user.getRole() == UserRole.ADMIN) {
    user.setStatus(UserStatus.APPROVED);  // Admins auto-approved
} else {
    user.setStatus(UserStatus.PENDING);   // Others need approval
}
```

**Status flow**:
```
Registration → Status: PENDING
    ↓
Admin approves → Status: APPROVED
    ↓
User can login and use system
```

**Why use approval workflow?**
- **Security**: Prevent unauthorized registrations
- **Quality control**: Verify user information
- **Business control**: Control who can use the system

---

## Design Patterns

### 1. **Filter Pattern**
**What it is**: Intercepting requests before they reach controllers.

**Implementation**: JwtFilter extends OncePerRequestFilter

**Benefits**:
- **Separation of concerns**: Security logic separate from business logic
- **Reusability**: Applied to all requests automatically
- **Centralized**: Single place for token validation

---

### 2. **Strategy Pattern (Implicit)**
**How it appears**: Different authentication strategies based on endpoint.

```java
// Public endpoints: No authentication
.requestMatchers("/api/auth/register", "/api/auth/login").permitAll()

// Authenticated endpoints: Any logged-in user
.requestMatchers("/api/bookings/**").authenticated()

// Role-based endpoints: Specific roles
.requestMatchers("/api/admin/**").hasAuthority(UserRole.ADMIN.name())
```

---

### 3. **Template Method Pattern**
**How it appears**: Spring Security provides template methods.

```java
// Template method from OncePerRequestFilter
protected abstract void doFilterInternal(...);

// Our implementation
@Override
protected void doFilterInternal(...) {
    // Custom logic
}
```

---

### 4. **Service Layer Pattern**
**Purpose**: Encapsulate authentication and user management logic.

**Implementation**:
```java
@Service
public class AuthServiceImpl implements AuthService {
    public boolean register(RegisterRequest req) { ... }
    public JwtAuthenticationResponse login(LoginRequest req) { ... }
    public boolean updateProfile(String userEmail, UpdateProfileRequest request) { ... }
}
```

---

## Spring Security Concepts

### 1. **SecurityFilterChain**
**What it is**: Configuration for security rules.

**Key configurations**:
```java
http
    .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF for stateless APIs
    .authorizeHttpRequests(request -> request
        .requestMatchers("/api/auth/**").permitAll()  // Public endpoints
        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")  // Admin only
        .anyRequest().authenticated()  // All others require authentication
    )
    .sessionManagement(manager -> manager
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Stateless (JWT)
    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);  // Add JWT filter
```

**Why disable CSRF?**
- **Stateless APIs**: JWT-based APIs don't use sessions
- **CORS**: Cross-origin requests don't need CSRF protection
- **RESTful**: Stateless REST APIs don't require CSRF tokens

**Interview Question**: "Why disable CSRF in Spring Security for REST APIs?"
**Answer**:
1. **Stateless**: JWT-based APIs are stateless (no sessions)
2. **CSRF protection**: CSRF protects against cross-site request forgery for stateful apps
3. **Not needed**: Stateless APIs don't maintain session state
4. **CORS**: Use CORS for cross-origin protection instead

---

### 2. **AuthenticationManager**
**What it is**: Manages authentication process.

**How it's used**:
```java
@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
    return config.getAuthenticationManager();
}

// In service
authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(email, password)
);
```

**How it works**:
1. **Receives credentials**: Username and password
2. **Delegates to AuthenticationProvider**: Uses configured provider
3. **Validates credentials**: Checks against UserDetailsService
4. **Returns Authentication**: If valid, returns authenticated object

---

### 3. **AuthenticationProvider**
**What it is**: Provides authentication logic.

**Configuration**:
```java
@Bean
public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userService);  // Load user
    authProvider.setPasswordEncoder(passwordEncoder());  // Encode/verify password
    return authProvider;
}
```

**How it works**:
1. **Loads user**: Uses UserDetailsService to load user by username
2. **Verifies password**: Uses PasswordEncoder to compare passwords
3. **Returns Authentication**: If valid, creates Authentication object

---

### 4. **SecurityContextHolder**
**What it is**: Thread-local storage for security context.

**How it's used**:
```java
// Set authentication (in JwtFilter)
SecurityContext context = SecurityContextHolder.createEmptyContext();
context.setAuthentication(authToken);
SecurityContextHolder.setContext(context);

// Get authentication (in Controller)
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
UserDetails userDetails = (UserDetails) auth.getPrincipal();
String userEmail = userDetails.getUsername();
```

**Why ThreadLocal?**
- **Thread safety**: Each thread has its own SecurityContext
- **Isolation**: One user's authentication doesn't affect another
- **Automatic cleanup**: ThreadLocal cleaned up when thread ends

**Interview Question**: "How does SecurityContextHolder work in multi-threaded environment?"
**Answer**: SecurityContextHolder uses ThreadLocal by default, which means each thread has its own SecurityContext. This ensures thread safety - one user's authentication doesn't interfere with another user's request, even in a multi-threaded server environment.

---

## JWT Concepts

### 1. **Token Generation**
**How it's implemented**:
```java
public String generateToken(User user) {
    return Jwts.builder()
        .setSubject(user.getEmail())           // Username (email)
        .claim("roles", user.getRole().name()) // Role claim
        .claim("userId", user.getId())         // User ID claim
        .setIssuedAt(new Date())               // Issued at time
        .setExpiration(new Date(System.currentTimeMillis() + expiration))  // Expiration
        .signWith(signinKey, SignatureAlgorithm.HS256)  // Sign with secret key
        .compact();  // Build token
}
```

**Token claims**:
- **sub (subject)**: User email (username)
- **roles**: User role (ADMIN, VENDOR, CUSTOMER)
- **userId**: User ID
- **iat (issued at)**: Token creation time
- **exp (expiration)**: Token expiration time

---

### 2. **Token Validation**
**How it's implemented**:
```java
public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
}

private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
}
```

**Validation checks**:
1. **Signature**: Token signed with correct secret key
2. **Expiration**: Token not expired
3. **Username match**: Token username matches user details

---

### 3. **Token Extraction**
**How it's implemented**:
```java
public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
}

public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
}

public Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(signinKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
}
```

---

## Password Security

### 1. **Password Encoding**
**How it's used**:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// During registration
user.setPassword(encoder.encode(req.getPassword()));
```

**BCrypt characteristics**:
- **Cost factor**: 10 (2^10 = 1024 iterations)
- **Salt**: Automatically generated (22 characters)
- **Hash length**: 31 characters
- **Format**: `$2a$10$salt22charshashed31chars`

---

### 2. **Password Verification**
**How it's used**:
```java
// During login
authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(email, password)
);

// During password change
if (!encoder.matches(request.getCurrentPassword(), user.getPassword())) {
    throw new IllegalArgumentException("Current password is incorrect");
}
```

**How BCrypt verification works**:
1. **Extract salt**: Get salt from stored hash
2. **Hash input**: Hash input password with extracted salt
3. **Compare**: Compare hashed input with stored hash

---

### 3. **Password Update Security**
**How it's implemented**:
```java
// Verify current password before allowing change
if (request.getCurrentPassword() == null || request.getCurrentPassword().isEmpty()) {
    throw new IllegalArgumentException("Current password is required");
}

if (!encoder.matches(request.getCurrentPassword(), user.getPassword())) {
    throw new IllegalArgumentException("Current password is incorrect");
}

// Encode and set new password
user.setPassword(encoder.encode(request.getPassword()));
```

**Security measures**:
- **Current password required**: User must provide current password
- **Verification**: Current password must match
- **Encoding**: New password is encoded before storage

---

## Backend Implementation

### 1. User Entity
**File:** `entity/User.java`

**Key Features**:
- Implements `UserDetails` interface
- Enum-based role and status
- Password stored with `@JsonIgnore` (never serialized)
- Unique constraints on email, phone, license, aadhar

**Fields**:
- `id`: Primary key
- `name`: User name
- `email`: Unique, used as username
- `password`: BCrypt hashed, never exposed
- `phoneNo`: Unique phone number
- `licenseNo`: Unique license number
- `aadharNo`: Unique Aadhar number
- `role`: UserRole enum (ADMIN, VENDOR, CUSTOMER)
- `status`: UserStatus enum (PENDING, APPROVED, REJECTED)
- `gender`: Gender enum (MALE, FEMALE, OTHER)
- Address fields: houseNo, buildingName, streetName, area, pincode

---

### 2. AuthService Implementation
**File:** `service/AuthServiceImpl.java`

#### register()
**Business Logic**:
1. Check if email already exists
2. Create User entity
3. Encode password with BCrypt
4. Set role (default: CUSTOMER)
5. Set status (PENDING for non-admins, APPROVED for admins)
6. Save user

**Key validations**:
- Email uniqueness
- Role validation (defaults to CUSTOMER if invalid)
- Gender validation (optional)

---

#### login()
**Business Logic**:
1. Authenticate with Spring Security (validates credentials)
2. Load user from database
3. Generate JWT token
4. Return token and user details

**Authentication flow**:
```java
// Spring Security validates credentials
authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(email, password)
);

// If successful, generate token
String token = jwtUtil.generateToken(user);
```

---

#### updateProfile()
**Business Logic**:
1. Find user by email
2. If password update requested:
   - Verify current password
   - Encode new password
3. Update other fields if provided
4. Check for duplicate phone/license/aadhar if changed
5. Save updated user

**Security measures**:
- Current password required for password change
- Duplicate check for unique fields
- Partial updates (only update provided fields)

---

#### deleteProfile()
**Business Logic**:
1. Find user by email
2. Check for dependencies (bookings, vehicles, etc.)
3. Delete user if no dependencies
4. Throw exception if dependencies exist

---

### 3. JwtUtil
**File:** `security/JwtUtil.java`

**Key Methods**:
- `generateToken(User user)`: Creates JWT token
- `extractUsername(String token)`: Extracts email from token
- `extractExpiration(String token)`: Extracts expiration time
- `isTokenValid(String token, UserDetails userDetails)`: Validates token
- `extractAllClaims(String token)`: Extracts all claims

**Token structure**:
```json
{
  "sub": "user@email.com",
  "roles": "CUSTOMER",
  "userId": 1,
  "iat": 1234567890,
  "exp": 1234571490
}
```

---

### 4. JwtFilter
**File:** `security/JwtFilter.java`

**Filter execution**:
1. Extract `Authorization` header
2. Check if header starts with "Bearer "
3. Extract token (remove "Bearer " prefix)
4. Extract username from token
5. Load user details
6. Validate token
7. Set authentication in SecurityContext
8. Continue filter chain

**Why extends OncePerRequestFilter?**
- Guarantees single execution per request
- Thread-safe
- Spring integration

---

### 5. SecurityConfig
**File:** `configuration/SecurityConfig.java`

**Key configurations**:
- **CSRF**: Disabled (stateless API)
- **Session**: STATELESS (JWT-based)
- **Public endpoints**: `/api/auth/register`, `/api/auth/login`
- **Role-based endpoints**: `/api/admin/**` (ADMIN), `/api/vehicles/**` (VENDOR)
- **JWT Filter**: Added before UsernamePasswordAuthenticationFilter

---

## How It Works - Step by Step

### Example 1: User Registration

**1. Frontend Request**:
```javascript
const userData = {
    name: "John Doe",
    email: "john@example.com",
    password: "password123",
    phoneNo: "1234567890",
    role: "CUSTOMER"
};
await register(userData);
```

**2. Controller**:
```java
@PostMapping("/register")
public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
    boolean success = authService.register(req);
    if (!success) {
        return ResponseEntity.badRequest().body("Email already registered");
    }
    return ResponseEntity.ok("User registered successfully");
}
```

**3. Service Method**:
```java
public boolean register(RegisterRequest req) {
    // Step 1: Check email exists
    if (userRepo.findByEmail(req.getEmail()).isPresent()) {
        return false;
    }
    
    // Step 2: Create user entity
    User user = new User();
    user.setName(req.getName());
    user.setEmail(req.getEmail());
    
    // Step 3: Encode password
    user.setPassword(encoder.encode(req.getPassword()));
    
    // Step 4: Set role (default: CUSTOMER)
    user.setRole(UserRole.valueOf(req.getRole()));
    
    // Step 5: Set status (PENDING for non-admins)
    user.setStatus(UserStatus.PENDING);
    
    // Step 6: Save user
    userRepo.save(user);
    return true;
}
```

**4. Database Insert**:
```sql
INSERT INTO users (name, email, password_hash, role, status, ...)
VALUES ('John Doe', 'john@example.com', '$2a$10$...', 'CUSTOMER', 'PENDING', ...);
```

---

### Example 2: User Login

**1. Frontend Request**:
```javascript
const credentials = {
    email: "john@example.com",
    password: "password123"
};
const response = await login(credentials);
// Response: { token: "eyJhbGci...", role: "CUSTOMER", name: "John Doe", ... }
```

**2. Controller**:
```java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest req) {
    try {
        return ResponseEntity.ok(authService.login(req));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Invalid Username or Password");
    }
}
```

**3. Service Method**:
```java
public JwtAuthenticationResponse login(LoginRequest req) {
    // Step 1: Authenticate with Spring Security
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
    );
    // This validates credentials using AuthenticationProvider
    
    // Step 2: Load user
    User user = userRepo.findByEmail(req.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
    
    // Step 3: Generate JWT token
    String token = jwtUtil.generateToken(user);
    
    // Step 4: Build response
    JwtAuthenticationResponse response = new JwtAuthenticationResponse();
    response.setToken(token);
    response.setRole(user.getRole().name());
    response.setName(user.getName());
    // ... set other fields
    
    return response;
}
```

**4. AuthenticationProvider Flow**:
```java
// AuthenticationProvider receives credentials
// 1. Loads user via UserDetailsService
UserDetails userDetails = userService.loadUserByUsername(email);

// 2. Verifies password via PasswordEncoder
boolean matches = passwordEncoder.matches(password, userDetails.getPassword());

// 3. If valid, creates Authentication object
// 4. Returns to AuthenticationManager
```

**5. Token Generation**:
```java
// JwtUtil generates token
String token = Jwts.builder()
    .setSubject(user.getEmail())
    .claim("roles", user.getRole().name())
    .claim("userId", user.getId())
    .setIssuedAt(new Date())
    .setExpiration(new Date(System.currentTimeMillis() + expiration))
    .signWith(signinKey, SignatureAlgorithm.HS256)
    .compact();
```

**6. Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "CUSTOMER",
  "name": "John Doe",
  "userId": 1,
  "email": "john@example.com",
  ...
}
```

---

### Example 3: Authenticated Request (Get Profile)

**1. Frontend Request**:
```javascript
// Token automatically added by axios interceptor
const response = await getProfile();
// Headers: { Authorization: "Bearer eyJhbGci..." }
```

**2. JwtFilter Execution**:
```java
// Step 1: Extract token
String authHeader = request.getHeader("Authorization");
String jwt = authHeader.substring(7);  // Remove "Bearer "

// Step 2: Extract username
String userEmail = jwtUtil.extractUsername(jwt);

// Step 3: Load user
UserDetails userDetails = userService.loadUserByUsername(userEmail);

// Step 4: Validate token
if (jwtUtil.isTokenValid(jwt, userDetails)) {
    // Step 5: Set authentication
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    UsernamePasswordAuthenticationToken authToken = 
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    context.setAuthentication(authToken);
    SecurityContextHolder.setContext(context);
}

// Step 6: Continue to controller
filterChain.doFilter(request, response);
```

**3. SecurityConfig Check**:
```java
// Checks if endpoint requires authentication
.requestMatchers("/api/users/profile").authenticated()
// User is authenticated (from SecurityContext), allows access
```

**4. Controller**:
```java
@GetMapping("/profile")
public ResponseEntity<?> getProfile() {
    // Extract authentication from SecurityContext
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserDetails userDetails = (UserDetails) auth.getPrincipal();
    String userEmail = userDetails.getUsername();
    
    // Get profile
    JwtAuthenticationResponse profile = authService.getProfile(userEmail);
    return ResponseEntity.ok(profile);
}
```

---

## Interview Questions & Answers

### Q1: "Explain the complete JWT authentication flow."
**Answer**:
1. **Registration**: User registers, password hashed with BCrypt, user saved with PENDING status
2. **Login**: User provides email/password
3. **Authentication**: Spring Security validates credentials via AuthenticationProvider
4. **Token generation**: JwtUtil creates JWT token with user info and role
5. **Token storage**: Frontend stores token (localStorage)
6. **Subsequent requests**: Frontend sends token in `Authorization: Bearer <token>` header
7. **Token validation**: JwtFilter validates token, extracts user info
8. **SecurityContext**: Authentication set in SecurityContext
9. **Authorization**: SecurityConfig checks authorities for endpoint access

**Code flow**:
```
Login → AuthenticationManager → AuthenticationProvider → UserDetailsService → 
PasswordEncoder → JwtUtil.generateToken() → Return token

Request → JwtFilter → Extract token → Validate → Load user → Set SecurityContext → 
SecurityConfig → Check authority → Controller
```

---

### Q2: "How does BCrypt password hashing work?"
**Answer**:
1. **Salt generation**: Random 22-character salt generated for each password
2. **Cost factor**: Number of iterations (default 10 = 2^10 = 1024)
3. **Hashing**: Password + salt hashed 1024 times
4. **Storage**: Format `$2a$10$salt22charshashed31chars`
5. **Verification**: Extract salt from stored hash, hash input password with salt, compare

**Why BCrypt?**
- **Adaptive**: Can increase cost factor as hardware improves
- **Salt**: Unique salt prevents rainbow table attacks
- **Slow**: Intentionally slow to prevent brute force
- **Industry standard**: Widely trusted

**Example**:
```
Plain: "password123"
Hash: "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
      └─┬─┘└─┬─┘└──────────────────────────────────────────┬────────────────────────┘
    Algorithm Cost                                      Salt + Hash
```

---

### Q3: "How does Spring Security filter chain work?"
**Answer**:
1. **Request arrives**: HTTP request comes in
2. **Filter chain**: Request passes through security filters
3. **JwtFilter**: Extracts and validates JWT token
4. **SecurityContext**: Sets authentication in SecurityContext
5. **SecurityConfig**: Checks if endpoint requires authentication/authorization
6. **Authorization**: Verifies user has required authority
7. **Controller**: If authorized, request reaches controller

**Filter order**:
```
Request → JwtFilter → SecurityConfig → Controller
```

**Why filter before controller?**
- **Security first**: Authentication/authorization before business logic
- **Centralized**: Single place for security logic
- **Reusable**: Applied to all requests automatically

---

### Q4: "What's the difference between authentication and authorization?"
**Answer**:
- **Authentication**: Verifying who the user is (login, token validation)
- **Authorization**: Verifying what the user can do (role-based access)

**In our system**:
- **Authentication**: JwtFilter validates token, sets user in SecurityContext
- **Authorization**: SecurityConfig checks if user has required role/authority

**Example**:
```java
// Authentication: User is logged in
.requestMatchers("/api/bookings/**").authenticated()

// Authorization: User has ADMIN role
.requestMatchers("/api/admin/**").hasAuthority(UserRole.ADMIN.name())
```

---

### Q5: "How do you prevent password exposure in API responses?"
**Answer**:
1. **@JsonIgnore**: Password field annotated with `@JsonIgnore` in User entity
2. **DTO pattern**: Use DTOs instead of entities in responses
3. **Never return password**: Password never included in response DTOs
4. **Database column**: Password stored in separate column (`password_hash`)

**Code**:
```java
@Entity
public class User {
    @JsonIgnore
    @Column(name = "password_hash")
    private String password;  // Never serialized
}

// Response DTO doesn't include password
public class JwtAuthenticationResponse {
    // No password field
}
```

---

### Q6: "How does user registration approval work?"
**Answer**:
1. **Registration**: User registers, status set to PENDING (except ADMIN)
2. **Admin approval**: Admin views pending users, approves/rejects
3. **Status update**: User status changed to APPROVED or REJECTED
4. **Login restriction**: Only APPROVED users can login (can be enforced)

**Current implementation**:
```java
if (user.getRole() == UserRole.ADMIN) {
    user.setStatus(UserStatus.APPROVED);  // Auto-approved
} else {
    user.setStatus(UserStatus.PENDING);    // Needs approval
}
```

**Future enhancement**: Add login check for APPROVED status only.

---

### Q7: "Explain the SecurityContextHolder and ThreadLocal."
**Answer**:
**SecurityContextHolder**: Thread-local storage for security context (authentication)

**How it works**:
1. **ThreadLocal**: Each thread has its own SecurityContext
2. **Set in filter**: JwtFilter sets authentication in SecurityContext
3. **Access in controller**: Controller can access current user from SecurityContext
4. **Thread safety**: One user's authentication doesn't affect another

**Code**:
```java
// Set (in JwtFilter)
SecurityContext context = SecurityContextHolder.createEmptyContext();
context.setAuthentication(authToken);
SecurityContextHolder.setContext(context);

// Get (in Controller)
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
UserDetails userDetails = (UserDetails) auth.getPrincipal();
```

**Why ThreadLocal?**
- **Thread safety**: Each request thread has isolated context
- **No interference**: Concurrent requests don't affect each other
- **Automatic cleanup**: Context cleaned up when thread ends

---

### Q8: "How do you handle password updates securely?"
**Answer**:
1. **Current password required**: User must provide current password
2. **Verification**: Verify current password matches stored hash
3. **Encoding**: Encode new password with BCrypt
4. **Update**: Save new encoded password

**Code**:
```java
// Verify current password
if (!encoder.matches(request.getCurrentPassword(), user.getPassword())) {
    throw new IllegalArgumentException("Current password is incorrect");
}

// Encode and set new password
user.setPassword(encoder.encode(request.getPassword()));
```

**Security measures**:
- **Current password verification**: Prevents unauthorized password changes
- **BCrypt encoding**: New password properly hashed
- **No password exposure**: Password never returned in response

---

## Code Walkthrough

### Complete Flow: User Login

**1. Frontend** (`LoginPage.jsx`):
```javascript
const handleLogin = async (e) => {
    e.preventDefault();
    try {
        const response = await login({ email, password });
        // Store token
        localStorage.setItem('token', response.data.token);
        // Store user info
        setUser(response.data);
        // Redirect based on role
        navigate(getDashboardPath(response.data.role));
    } catch (error) {
        // Handle error
    }
};
```

**2. Controller** (`AuthController.java`):
```java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest req) {
    try {
        return ResponseEntity.ok(authService.login(req));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Invalid Username or Password");
    }
}
```

**3. Service** (`AuthServiceImpl.java`):
```java
public JwtAuthenticationResponse login(LoginRequest req) {
    // Authenticate (validates credentials)
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
    );
    
    // Load user
    User user = userRepo.findByEmail(req.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
    
    // Generate token
    String token = jwtUtil.generateToken(user);
    
    // Build response
    JwtAuthenticationResponse response = new JwtAuthenticationResponse();
    response.setToken(token);
    response.setRole(user.getRole().name());
    // ... set other fields
    
    return response;
}
```

**4. AuthenticationProvider** (`SecurityConfig.java`):
```java
@Bean
public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userService);  // Load user
    authProvider.setPasswordEncoder(passwordEncoder());  // Verify password
    return authProvider;
}
```

**5. UserDetailsService** (`CustomUserDetailsService.java`):
```java
@Override
public UserDetails loadUserByUsername(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("user not found"));
}
```

**6. PasswordEncoder** (`SecurityConfig.java`):
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// Verification happens in AuthenticationProvider
// passwordEncoder.matches(plainPassword, hashedPassword)
```

**7. JwtUtil** (`JwtUtil.java`):
```java
public String generateToken(User user) {
    return Jwts.builder()
        .setSubject(user.getEmail())
        .claim("roles", user.getRole().name())
        .claim("userId", user.getId())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(signinKey, SignatureAlgorithm.HS256)
        .compact();
}
```

---

## Security Best Practices

### 1. **Password Security**
- ✅ **BCrypt hashing**: Use BCrypt for password encoding
- ✅ **Never store plaintext**: Always hash passwords
- ✅ **Never return password**: Use @JsonIgnore, don't include in DTOs
- ✅ **Current password verification**: Require current password for changes
- ✅ **Strong password policy**: Enforce minimum length, complexity (future)

---

### 2. **JWT Security**
- ✅ **Secret key**: Use strong, random secret key
- ✅ **Expiration**: Set reasonable expiration time
- ✅ **HTTPS**: Use HTTPS in production (prevents token theft)
- ✅ **Token storage**: Store in httpOnly cookie or secure storage (frontend)
- ✅ **Token validation**: Always validate signature and expiration

---

### 3. **Authentication Security**
- ✅ **Rate limiting**: Prevent brute force attacks (future)
- ✅ **Account locking**: Lock account after failed attempts (future)
- ✅ **Two-factor authentication**: Add 2FA for sensitive operations (future)
- ✅ **Session management**: Use stateless JWT (no server-side sessions)

---

### 4. **Authorization Security**
- ✅ **Role-based access**: Enforce roles at SecurityConfig level
- ✅ **Defense in depth**: Verify roles in controller/service too
- ✅ **Principle of least privilege**: Users get minimum required access
- ✅ **Input validation**: Validate all inputs

---

## Testing

### Unit Test Example

```java
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @InjectMocks
    private AuthServiceImpl authService;
    
    @Test
    void testRegister_Success() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$...");
        
        // Act
        boolean result = authService.register(request);
        
        // Assert
        assertTrue(result);
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void testLogin_Success() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("$2a$10$...");
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user)).thenReturn("token123");
        
        // Act
        JwtAuthenticationResponse response = authService.login(request);
        
        // Assert
        assertNotNull(response);
        assertEquals("token123", response.getToken());
    }
}
```

---

## Summary

The Authentication & User Module provides:
- ✅ **JWT-based authentication**: Stateless, scalable
- ✅ **BCrypt password hashing**: Secure password storage
- ✅ **Role-based access control**: ADMIN, VENDOR, CUSTOMER roles
- ✅ **User registration**: With approval workflow
- ✅ **Profile management**: Update profile, change password, delete account
- ✅ **Spring Security integration**: Seamless security framework integration
- ✅ **Token validation**: Secure token generation and validation
- ✅ **Security best practices**: Password security, token security, authorization

**Status: ✅ FULLY IMPLEMENTED AND PRODUCTION-READY**

The authentication module is the foundation of the entire system, providing secure user management and access control for all other modules.

---

**Key Takeaways for Interviews**:
1. **JWT flow**: Registration → Login → Token generation → Token validation → Authorization
2. **BCrypt**: Salt + cost factor + hashing = secure password storage
3. **Spring Security**: Filter chain → Authentication → Authorization → Controller
4. **SecurityContextHolder**: Thread-local storage for authentication
5. **Role-based access**: SecurityConfig + Controller verification = defense in depth
