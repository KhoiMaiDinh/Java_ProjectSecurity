package com.buglachuyennho.securityapp.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.buglachuyennho.securityapp.domain.User;
import com.buglachuyennho.securityapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;
    @GetMapping("/user")
    public ResponseEntity<?>getUserInfo(HttpServletRequest request, HttpServletResponse response) {
        String SECRET_KEY = "472B4B6250655367566B5970337336763979244226452948404D635166546A57";
        String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String access_token = authHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(access_token);
                String username = decodedJWT.getSubject();
                User user = userService.getUser(username);

                return ResponseEntity.ok().body(user);
            } catch (Exception exception) {
//                response.setHeader("error", exception.getMessage());
//                response.setStatus(FORBIDDEN.value());
//                Map<String, String> errors = new HashMap<>();
//                errors.put("error_message", exception.getMessage());
//                response.setContentType(APPLICATION_JSON_VALUE);
//                new ObjectMapper().writeValue(response.getOutputStream(), errors);
                return ResponseEntity.badRequest().body(exception);
            }
        } else {
            return ResponseEntity.badRequest().body(new RuntimeException("Token is missing"));
        }
    }
    @GetMapping("/users")
    public ResponseEntity<List<User>>getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }
    @PostMapping("/user/save")
    public ResponseEntity<User>saveUser(@RequestBody User user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }
}

@Data
class RoleToUserForm {
    private String username;
    private String roleName;
}
