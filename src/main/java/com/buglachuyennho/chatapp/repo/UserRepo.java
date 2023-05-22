package com.buglachuyennho.chatapp.repo;

import com.buglachuyennho.chatapp.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends MongoRepository<User, String> {
    Optional<User> findUserByUsername(String username);
}
