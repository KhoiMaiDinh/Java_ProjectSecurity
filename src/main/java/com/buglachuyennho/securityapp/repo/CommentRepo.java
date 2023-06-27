package com.buglachuyennho.securityapp.repo;

import com.buglachuyennho.securityapp.domain.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CommentRepo extends MongoRepository<Comment, String> {
    Optional<Comment> findCommentByPinId(String pinId) ;
}
