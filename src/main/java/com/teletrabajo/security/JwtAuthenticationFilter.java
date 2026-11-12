package com.teletrabajo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "GET".equalsIgnoreCase(request.getMethod())
                && "/api/v1/time/server".equals(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String header = request.getHeader("Authorization");
        final String prefix = "Bearer ";

        boolean moduleRequest = isModuleRequest(request);
        if (header == null || !header.regionMatches(true, 0, prefix, 0, prefix.length())) {
            if (moduleRequest && !"OPTIONS".equalsIgnoreCase(request.getMethod())) {
                log.warn("JWT_AUTH {} {} reason={}", request.getMethod(), request.getRequestURI(),
                        header == null ? "MISSING_AUTHORIZATION" : "INVALID_AUTHORIZATION_SCHEME");
            }
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(prefix.length()).trim();
        String username;

        try {
            username = jwtService.extractUsername(token);
        } catch (Exception ex) {
            log.warn("JWT_AUTH {} {} reason={}", request.getMethod(), request.getRequestURI(), ex.getClass().getSimpleName());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails user;
            try {
                user = userDetailsService.loadUserByUsername(username);
            } catch (UsernameNotFoundException ex) {
                log.warn("JWT_AUTH {} {} reason=USER_NOT_FOUND", request.getMethod(), request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            if (jwtService.isTokenValid(token, user)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                log.warn("JWT_AUTH {} {} reason=INVALID_SUBJECT_OR_EXPIRATION", request.getMethod(), request.getRequestURI());
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isModuleRequest(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return path.startsWith("/api/v1/acreditacion/")
                || path.startsWith("/api/v1/calificaciones/")
                || path.startsWith("/api/v1/capacitacion/");
    }
}
