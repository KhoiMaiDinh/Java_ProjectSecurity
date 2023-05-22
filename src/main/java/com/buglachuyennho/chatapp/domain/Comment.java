package com.buglachuyennho.chatapp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    private String id;
    private String postedBy;
    private String comment;
    private String pinId;

    public Comment(String postedBy, String comment, String pinId) {
        this.postedBy = postedBy;
        this.comment = comment;
        this.pinId= pinId;

    }
}


