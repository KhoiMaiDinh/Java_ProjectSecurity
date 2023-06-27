package com.buglachuyennho.securityapp.repo;

import com.buglachuyennho.securityapp.domain.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface RoleRepo extends MongoRepository<Role, String> {
    Optional<Role> findRoleByName(String name);
}
