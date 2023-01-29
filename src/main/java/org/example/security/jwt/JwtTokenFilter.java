package org.example.security.jwt;

import lombok.RequiredArgsConstructor;
import org.example.exception.InvalidJwtAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest req,
                         ServletResponse resp,
                         FilterChain filterChain) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) req);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication auth;
            try {
                auth = jwtTokenProvider.getAuthentication(token);
            } catch (InvalidJwtAuthenticationException e) {
                throw new RuntimeException("Can`t get authentication from token", e);
            }
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(req, resp);
    }
}