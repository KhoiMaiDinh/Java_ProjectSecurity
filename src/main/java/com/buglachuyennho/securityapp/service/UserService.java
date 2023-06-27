package com.buglachuyennho.securityapp.service;

import com.buglachuyennho.securityapp.domain.Role;
import com.buglachuyennho.securityapp.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserService {
    User saveUser(User user);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);
    User getUser(String username);
    List<User> getUsers();
    User register(User user);
}
