package com.buglachuyennho.chatapp.service;

import com.buglachuyennho.chatapp.domain.Role;
import com.buglachuyennho.chatapp.domain.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);
    User getUser(String username);
    List<User> getUsers();
    User register(String firstname, String lastname, String username, String password);
}
