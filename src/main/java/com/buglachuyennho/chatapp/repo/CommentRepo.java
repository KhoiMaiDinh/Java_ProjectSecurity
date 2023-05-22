package com.buglachuyennho.chatapp.repo;

import com.buglachuyennho.chatapp.domain.Comment;
import com.buglachuyennho.chatapp.domain.Role;
import com.buglachuyennho.chatapp.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CommentRepo extends MongoRepository<Comment, String> {
    Optional<Comment> findCommentByPinId(String pinId) ;
}
