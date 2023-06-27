package com.buglachuyennho.securityapp.api;

import com.buglachuyennho.securityapp.domain.Save;
import com.buglachuyennho.securityapp.repo.SaveRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/save")
@RequiredArgsConstructor
@Slf4j
public class SaveController {

    private final SaveRepo saveRepo;
    @Autowired
    private MongoTemplate mongoTemplate;
    @GetMapping("/{pinId}")
    ResponseEntity<?>getUserWhoSaveByPinId(@PathVariable String pinId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("pinId").is(pinId));
        List<Save> saveList = mongoTemplate.find(query, Save.class);
        return ResponseEntity.ok().body(saveList);
    }

    @PostMapping("/")
    ResponseEntity<?>savePin(@RequestBody Save save) {
        Save result = saveRepo.save(save);
        return ResponseEntity.ok().body(result);
    }
}
