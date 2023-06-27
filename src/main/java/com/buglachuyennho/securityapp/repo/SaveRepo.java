package com.buglachuyennho.securityapp.repo;

import com.buglachuyennho.securityapp.domain.Save;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SaveRepo extends MongoRepository<Save, String> {
    Optional<Save> findSaveByPinId(String pinId) ;
    List<Save> findSavesByPinId(String pinId);
}
