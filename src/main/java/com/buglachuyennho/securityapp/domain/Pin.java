package com.buglachuyennho.securityapp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.net.URL;

@Document("Pin")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pin {
    @Id
    private String id;
    private String title;
    private String category;
    private URL destination;
    private String userId;
    private String image;
    private int save;

    public Pin(String title, String category, URL destination, String userId, String image, int save) {
        this.title = title;
        this.category = category;
        this.destination = destination;
        this.userId = userId;
        this.image = image;
        this.save = save;
    }
}
