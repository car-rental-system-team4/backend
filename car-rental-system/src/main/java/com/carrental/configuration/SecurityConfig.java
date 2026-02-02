package com.carrental.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.Collections;

import com.carrental.enums.UserRole;
import com.carrental.security.JwtFilter;
import com.carrental.service.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtFilter jwtFilter;
	private final CustomUserDetailsService userService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				// Disable CSRF for stateless APIs
				.csrf(AbstractHttpConfigurer::disable)
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.authorizeHttpRequests(request -> request
						// Allow Public Access to Login and Register only
						.requestMatchers("/api/auth/register", "/api/auth/login", "/api/contact", "/swagger-ui/**",
								"/v3/api-docs/**",
								"/swagger-ui.html")
						.permitAll()
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Permit all OPTIONS requests
						// Profile endpoints require authentication (both /api/auth/profile and
						// /api/users/profile)
						.requestMatchers("/api/auth/profile", "/api/users/profile").authenticated()
						// Vehicle endpoints - GET available to all users (public)
						.requestMatchers(HttpMethod.GET, "/api/vehicles").permitAll() // GET all available vehicles
						.requestMatchers(HttpMethod.GET, "/api/vehicles/{id}").permitAll() // GET vehicle by ID
						// Vehicle endpoints - POST, PUT, DELETE require VENDOR role
						.requestMatchers(HttpMethod.POST, "/api/vehicles").hasAuthority(UserRole.VENDOR.name()) // POST
																												// add
																												// vehicle
						.requestMatchers(HttpMethod.PUT, "/api/vehicles/{id}").hasAuthority(UserRole.VENDOR.name()) // PUT
																													// update
																													// vehicle
						.requestMatchers(HttpMethod.DELETE, "/api/vehicles/{id}")
						.hasAnyAuthority(UserRole.VENDOR.name(), UserRole.ADMIN.name()) // DELETE vehicle
						.requestMatchers(HttpMethod.PUT, "/api/vehicles/{id}/status")
						.hasAuthority(UserRole.VENDOR.name()) // PUT status
						.requestMatchers(HttpMethod.GET, "/api/vehicles/vendor").hasAuthority(UserRole.VENDOR.name()) // GET
																														// vendor
																														// vehicles
						// Booking endpoints - User access
						.requestMatchers(HttpMethod.POST, "/api/bookings").authenticated() // POST create booking
						.requestMatchers(HttpMethod.GET, "/api/bookings/user").authenticated() // GET user bookings
						.requestMatchers(HttpMethod.GET, "/api/bookings/vendor").hasAuthority(UserRole.VENDOR.name()) // GET
																														// vendor
																														// bookings
						.requestMatchers(HttpMethod.GET, "/api/bookings/{id}").authenticated() // GET booking by ID
						.requestMatchers(HttpMethod.PUT, "/api/bookings/{id}/cancel").authenticated() // PUT cancel
																										// booking
						// Payment endpoints - User access
						.requestMatchers(HttpMethod.POST, "/api/payments").authenticated() // POST create payment
						.requestMatchers(HttpMethod.GET, "/api/payments/{bookingId}").authenticated() // GET payment by
																										// booking ID
						.requestMatchers(HttpMethod.PUT, "/api/payments/{id}/status")
						.hasAuthority(UserRole.ADMIN.name()) // PUT update payment status (Admin only)
						// Review endpoints - User access
						.requestMatchers(HttpMethod.POST, "/api/reviews").authenticated() // POST create review
						.requestMatchers(HttpMethod.GET, "/api/reviews/vehicle/{id}").authenticated() // GET reviews by
																										// vehicle
						.requestMatchers(HttpMethod.PUT, "/api/reviews/{id}/approve")
						.hasAuthority(UserRole.ADMIN.name()) // PUT approve review (Admin only)
						.requestMatchers(HttpMethod.PUT, "/api/reviews/{id}/reject").hasAuthority(UserRole.ADMIN.name()) // PUT
																															// reject
																															// review
																															// (Admin
																															// only)
						// Complaint endpoints - User access
						.requestMatchers(HttpMethod.POST, "/api/complaints").authenticated() // POST create complaint
						.requestMatchers(HttpMethod.GET, "/api/complaints/user").authenticated() // GET user complaints
						.requestMatchers(HttpMethod.GET, "/api/complaints").hasAuthority(UserRole.ADMIN.name()) // GET
																												// all
																												// complaints
																												// (Admin
																												// only)
						.requestMatchers(HttpMethod.PUT, "/api/complaints/{id}/resolve")
						.hasAuthority(UserRole.ADMIN.name()) // PUT resolve complaint (Admin only)
						// Admin endpoints - Admin access only
						.requestMatchers("/api/admin/**").hasAuthority(UserRole.ADMIN.name()) // All admin endpoints
						// Protect Vendor Routes (Only VENDOR role)
						.requestMatchers("/api/vendor/**").hasAuthority(UserRole.VENDOR.name()).anyRequest()
						.authenticated())
				.sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;

	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Collections.singletonList("*")); // Or your frontend URL
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		configuration.setAllowedHeaders(
				Arrays.asList("Authorization", "Content-Type", "Accept", "x-requested-with", "Cache-Control"));
		configuration.setExposedHeaders(Collections.singletonList("Authorization"));
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
