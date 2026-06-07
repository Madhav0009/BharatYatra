package com.Madhav.bharatYatra.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // ✅ Skip if no Bearer token present
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            // ✅ Validate first, then extract
            if (jwtUtils.validateToken(token)) {
                String email = jwtUtils.extractEmail(token);

                // ✅ Only set auth if not already authenticated
                if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                    UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );

                    authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                            .buildDetails(request)
                    );

                    // ✅ THIS sets the user as authenticated
                    SecurityContextHolder.getContext()
                        .setAuthentication(authToken);

                    log.debug("Authenticated user: {}", email);
                }
            } else {
                // ✅ Now you'll see exactly why validation failed
                log.warn("Token validation failed for request: {}",
                    request.getRequestURI());
            }

        } catch (Exception e) {
            // ✅ Never silently swallow — always log the real cause
            log.error("JWT Filter error [{}]: {}",
                e.getClass().getSimpleName(), e.getMessage());
        }

        chain.doFilter(request, response);
    }
}