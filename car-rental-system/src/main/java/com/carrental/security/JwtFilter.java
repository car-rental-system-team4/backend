package com.carrental.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.carrental.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final CustomUserDetailsService userService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// Get Authorization Header
		final String authHeader = request.getHeader("Authorization");
		final String jwt;
		final String userEmail;

		// Check if Header is empty or doesn't start with "Bearer "
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		// Extract Token and Email
		jwt = authHeader.substring(7);// Remove "Bearer"
		try {
			userEmail = jwtUtil.extractUsername(jwt);
			log.info("Processing JWT for user: {}", userEmail);
		} catch (Exception e) {
			log.error("Failed to extract username from JWT: {}", e.getMessage());
			filterChain.doFilter(request, response);
			return;
		}

		// Validate Token
		if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = userService.loadUserByUsername(userEmail);

			if (jwtUtil.isTokenValid(jwt, userDetails)) {
				log.info("JWT valid for user: {}. Authorities: {}", userEmail, userDetails.getAuthorities());
				// Create Authentication Token
				SecurityContext context = SecurityContextHolder.createEmptyContext();

				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());

				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				// Set Context
				context.setAuthentication(authToken);
				SecurityContextHolder.setContext(context);
				log.info("SecurityContext populated for user: {}", userEmail);
			} else {
				log.warn("JWT invalid or expired for user: {}", userEmail);
			}
		}
		filterChain.doFilter(request, response);

	}

}
