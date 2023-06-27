package com.buglachuyennho.securityapp.service;

import com.buglachuyennho.securityapp.domain.AuthProvider;
import com.buglachuyennho.securityapp.domain.Role;
import com.buglachuyennho.securityapp.domain.User;
import com.buglachuyennho.securityapp.repo.RoleRepo;
import com.buglachuyennho.securityapp.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service @RequiredArgsConstructor @Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private final UserRepo userRepo;
    @Autowired
    private final RoleRepo roleRepo;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Override
    public User saveUser(User user) {
        log.info("Saving new user {} to the db", user.getRoles());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findUserByUsername(username).orElse(null);
        if(user == null ) {
            log.error("User not found");
            throw new UsernameNotFoundException("User not found in database");
        } else {
          log.info("User found in database: {}", username);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(
                    new SimpleGrantedAuthority(role.getName())
            );
        });
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role {} to the db", role.getName());
        return roleRepo.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role {} to user {}", roleName, username);
        User user = userRepo.findUserByUsername(username).orElse(null);
        Role role = roleRepo.findRoleByName(roleName).orElse(null);
        log.info("fetch role {} to user {}",user, role);
        user.getRoles().add(role);
        userRepo.save(user);
    }

    @Override
    public User getUser(String username) {
        log.info("Fetching user {}", username);
        return userRepo.findUserByUsername(username).orElse(null);
    }

    @Override
    public List<User> getUsers() {
        log.info("Fetching all user ");
        return userRepo.findAll();
    }

    @Override
    public User register(User user) {
        log.info("Registering user {}", user.getUsername());
        User oldUser = userRepo.findUserByUsername(user.getUsername()).orElseGet(()-> {
            return null;
        });
        if (oldUser != null)
            throw new DuplicateKeyException("User with the same name has already exists");

        // check password valid
        // Define the regular expression
        String regex = "^(?=.*[0-9])(?=.*[a-z]).{8,20}$";

        // Define the input string to test the regex on
        String input = user.getPassword();

        // Create a Pattern object with the regex
        Pattern pattern = Pattern.compile(regex);

        // Create a Matcher object with the input string and the Pattern
        Matcher matcher = pattern.matcher(input);

        // Check if the input matches the regex
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid password");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = roleRepo.findRoleByName("ROLE_USER").orElse(null);
        log.info(role.getName());
        user.getRoles().add(role);
        user.setAvatarUrl("https://res.cloudinary.com/dux8aqzzz/image/upload/v1685547037/xd0gen7b4z5wgwuqfvpz.png");
        user.setProvider(AuthProvider.local);
        return userRepo.save(user);
    }
}
