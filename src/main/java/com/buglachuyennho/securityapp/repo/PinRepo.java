package com.buglachuyennho.securityapp.repo;

import com.buglachuyennho.securityapp.domain.Pin;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PinRepo extends MongoRepository<Pin, String> {

}
