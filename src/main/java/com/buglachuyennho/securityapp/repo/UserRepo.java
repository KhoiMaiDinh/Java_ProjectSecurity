package com.buglachuyennho.securityapp.repo;

import com.buglachuyennho.securityapp.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends MongoRepository<User, String> {
    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByUsernameAndProvider(String username, String provider);

}
