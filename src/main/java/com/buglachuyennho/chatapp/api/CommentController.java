package com.buglachuyennho.chatapp.api;

import com.buglachuyennho.chatapp.domain.Comment;
import com.buglachuyennho.chatapp.repo.CommentRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
@Slf4j

public class CommentController {
    private final CommentRepo commentRepo;
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
    public ResponseEntity<Comment> getComment(@PathVariable String pinId) {
        log.info(pinId);
        Comment comment = commentRepo.findCommentByPinId(pinId).orElse(null);
        //log.info(pinId);
        return ResponseEntity.ok().body(comment);


    }
}