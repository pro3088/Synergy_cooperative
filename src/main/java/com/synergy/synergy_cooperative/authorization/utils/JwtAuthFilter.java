package com.synergy.synergy_cooperative.authorization.utils;

import com.synergy.synergy_cooperative.authorization.JwtService;
import com.synergy.synergy_cooperative.user.UserService;
import com.synergy.synergy_cooperative.util.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    protected static Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Value("${authentication.auth.accessTokenCookieName}")
    private String accessTokenCookieName;

    @Value("${authentication.auth.refreshTokenCookieName}")
    private String refreshTokenCookieName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        Cookie[] cookies = request.getCookies();
        String token = null;
        String refreshToken = null;
        String username = null;
        String refreshUsername = null;
        String warning = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            log.info("Getting details from token");
            token = authHeader.substring(7);
            username = jwtService.extractUsername(token);
            log.info("username has been extracted: {}", username);
        }
        else if (cookies != null) {
            for (Cookie cookie : cookies) {
                try {
                    if (accessTokenCookieName.equals(cookie.getName())) {
                        token = cookie.getValue();
                        token = new CookiesUtil().decodeCookieValue(token);
                        username = jwtService.extractUsername(token);
                        log.info("username has been extracted: {}", username);
                    } else if (refreshTokenCookieName.equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                        refreshToken = new CookiesUtil().decodeCookieValue(refreshToken);
                        refreshUsername = jwtService.extractUsername(refreshToken);
                        log.info("Refresh username has been extracted: {}", refreshUsername);
                    }
                } catch (Exception e) {
                    warning = e.getMessage();
                    log.warn("Error Occurred: {}", warning);
                }
            }
        }


        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            if (username != null ) {
                log.info("getting user details from username {}", username);
                UserDetails userDetails = userService.loadUserByUsername(username);
                if(jwtService.validateToken(token, userDetails)) {
                    if (jwtService.isTokenAboutToExpire(token) && refreshToken != null && jwtService.validateToken(refreshToken, userDetails)) {
                        token = jwtService.generateToken(username);
                        response.addHeader(HttpHeaders.SET_COOKIE, new CookiesUtil(accessTokenCookieName, token).getCookie());
                    }
                    setAuthContext(request, userDetails);
                }
            }
            else if(refreshUsername != null){
                log.info("getting user details from username {}", refreshUsername);
                UserDetails userDetails = userService.loadUserByUsername(refreshUsername);
                if (jwtService.validateToken(refreshToken, userDetails)) {
                    token = jwtService.generateToken(refreshUsername);
                    response.addHeader(HttpHeaders.SET_COOKIE, new CookiesUtil(accessTokenCookieName, token).getCookie());
                    setAuthContext(request, userDetails);
                }
            }
            else{
                SecurityContextHolder.clearContext();
                response.addHeader(HttpHeaders.SET_COOKIE, new CookiesUtil(refreshTokenCookieName,"").clear());
                response.addHeader(HttpHeaders.SET_COOKIE, new CookiesUtil(accessTokenCookieName,"").clear());
                if (warning != null)
                    log.info("Warning regarding authentication with error: {}", warning);
            }
        }
        filterChain.doFilter(request, response);
    }

    private static void setAuthContext(HttpServletRequest request, UserDetails userDetails) {
        log.info("adding token to security context");
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
