package com.blooddonor.config;

import com.blooddonor.service.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        // Skip JWT processing for public endpoints
        String requestPath = request.getRequestURI();
        if (requestPath.contains("/api/auth/") || requestPath.contains("/api/test/ping")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwtToken);
            } catch (ExpiredJwtException e) {
                logger.warn("⚠️ JWT token has expired: {}", e.getMessage());
                response.setHeader("X-Auth-Error", "Token expired");
            } catch (MalformedJwtException e) {
                logger.warn("⚠️ Malformed JWT token: {}", e.getMessage());
                response.setHeader("X-Auth-Error", "Malformed token");
            } catch (SignatureException e) {
                logger.warn("⚠️ JWT signature verification failed: {}", e.getMessage());
                response.setHeader("X-Auth-Error", "Invalid signature");
            } catch (Exception e) {
                logger.warn("⚠️ Invalid JWT token: {}", e.getMessage());
                response.setHeader("X-Auth-Error", "Invalid token");
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("✅ Authentication set for user: {}", username);
                }
            } catch (Exception e) {
                logger.error("❌ Error setting authentication: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
