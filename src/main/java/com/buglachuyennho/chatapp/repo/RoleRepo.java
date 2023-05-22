package com.buglachuyennho.chatapp.repo;

import com.buglachuyennho.chatapp.domain.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface RoleRepo extends MongoRepository<Role, String> {
    Optional<Role> findRoleByName(String name);
}
