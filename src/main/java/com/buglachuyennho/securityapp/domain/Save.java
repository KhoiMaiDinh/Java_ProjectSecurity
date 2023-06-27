package com.buglachuyennho.securityapp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Save")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Save {
    @Id
    private String id;
    private String pinId;
    private String userId;

    public Save(String pinId, String userId) {
        this.pinId = pinId;
        this.userId = userId;
    }
}
