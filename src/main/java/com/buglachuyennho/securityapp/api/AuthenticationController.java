package com.buglachuyennho.securityapp.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.buglachuyennho.securityapp.domain.Role;
import com.buglachuyennho.securityapp.domain.User;
import com.buglachuyennho.securityapp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody @Valid User user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
        return ResponseEntity.created(uri).body(userService.register(user));
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("refresh token");
        String SECRET_KEY = "472B4B6250655367566B5970337336763979244226452948404D635166546A57";
        Cookie[] cookies =  request.getCookies();
        String Jwt = "";
        // get jwt_java(refresh_token) if exists
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("java_jwt")) {
                    log.info(cookie.getValue());
                    Jwt = cookie.getValue();
                }
            }
        }
        // generate new access_token if refresh_token exists
        if (Jwt.length() > 0 ) {
            try {
                String refresh_token = Jwt;
                log.info(refresh_token);
                // decode refresh_token
                Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);

                // get and check if refresh_token expire
                Date expireTime = decodedJWT.getExpiresAt();
                Date currentTime = new Date();
                if (currentTime.after(expireTime)) {
                    response.setHeader("error", "token expire");
                    response.setStatus(UNAUTHORIZED.value());
                    Map<String, String> errors = new HashMap<>();
                    errors.put("error_message", "Your working session has finish, please login to continue");
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), errors);
                    return;
                }
                // get user from UserService
                String username = decodedJWT.getSubject();
                User user = userService.getUser(username);

                // generate new token and send to client
                String access_token = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception exception) {
                response.setHeader("error", exception.getMessage());
                response.setStatus(UNAUTHORIZED.value());
                Map<String, String> errors = new HashMap<>();
                errors.put("error_message", exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), errors);
            }
        } else {
            response.setHeader("error", "Refresh token is missing");
            response.setStatus(UNAUTHORIZED.value());
            Map<String, String> errors = new HashMap<>();
            errors.put("error_message", "Refresh token is missing");
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), errors);
            throw new RuntimeException("Refresh token is missing");
        }
    }

    @GetMapping("/token/csrf")
    public @ResponseBody CsrfToken getCsrfToken(HttpServletRequest request) {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return csrf;
    }

    @GetMapping("logout")
    public @ResponseBody void logOut(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie("java_jwt", null);
        cookie.setMaxAge(0);
        cookie.setPath("/"); // Set the path of the cookie you want to delete

        response.addCookie(cookie);
    }
}

