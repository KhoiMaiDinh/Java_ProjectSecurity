package com.buglachuyennho.securityapp.api;

import com.buglachuyennho.securityapp.domain.Comment;
import com.buglachuyennho.securityapp.domain.User;
import com.buglachuyennho.securityapp.repo.CommentRepo;
import com.buglachuyennho.securityapp.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
@Slf4j

public class CommentController {
    private final CommentRepo commentRepo;
    @Autowired
    private MongoTemplate mongoTemplate;
    private final UserRepo userRepo;
    @PostMapping
    @ResponseBody
    public ResponseEntity<Comment> comment( String postedBy, String comment, String pinId) {
        Comment comment1 = new Comment(postedBy,comment,pinId);
        commentRepo.save(comment1);
        log.info(pinId);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/comment/").toUriString());
        return ResponseEntity.created(uri).body(comment1);
    }

    @GetMapping ("/get/{pinId}")
    @ResponseBody
    public ResponseEntity<?> getComment(@PathVariable String pinId) throws IOException {
        log.info(pinId);
        Query query = new Query();
        query.addCriteria(Criteria.where("pinId").is(pinId));
        List<Comment> comments = mongoTemplate.find(query, Comment.class);

        List<Object> response = new ArrayList<>();
        for (Comment comment : comments) {
            User user = userRepo.findById(comment.getPostedBy()).get();

            Map<String, Object> pinUserMap = new HashMap<>();
            pinUserMap.put("comment", comment);
            pinUserMap.put("user", user);
            response.add(pinUserMap);
        }
        System.out.println(response);
        return ResponseEntity.ok().body(response);

    }
}