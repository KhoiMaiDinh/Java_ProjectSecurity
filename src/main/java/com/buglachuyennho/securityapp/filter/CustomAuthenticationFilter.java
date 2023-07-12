package com.buglachuyennho.securityapp.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@CrossOrigin(maxAge = 3600)
@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private String SECRET_KEY = "472B4B6250655367566B5970337336763979244226452948404D635166546A57"; // should be secure in .env file
    private final AuthenticationManager authenticationManager;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        log.info("username is: {}", username);
        log.info("password is: {}", password);

        // generate UsernamePasswordAuthenticationToken from username & password
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    // authentication fail case
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.error("Error logging in {}", failed.getMessage());
        response.setHeader("error", failed.getMessage());
        response.setStatus(UNAUTHORIZED.value());
        Map<String, String> errors = new HashMap<>();
        errors.put("error_message", "Invalid username or password");
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), errors);
        log.error(failed.getMessage());
    }

    // authentication success case
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {
        User user = (User)authentication.getPrincipal();
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());

        // generate access_token
        String access_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 5* 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        // Get the current date
        Calendar currentDate = Calendar.getInstance();

        // Add 1 month to the current date
        currentDate.add(Calendar.MONTH, 1);

        // Get the updated date
        Date updatedDate = currentDate.getTime();

        // generate refresh_token
        String refresh_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(updatedDate)
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("access_token", access_token);
        responseMap.put("user", user);

        // Add a refresh_token cookie to response
        Cookie refreshTokenCookie = new Cookie( "java_jwt", refresh_token);
        refreshTokenCookie.setMaxAge(30 * 24 * 60 * 60); // 1 month
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);

        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);
        // add return values to response's body
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), responseMap);
    }
}
