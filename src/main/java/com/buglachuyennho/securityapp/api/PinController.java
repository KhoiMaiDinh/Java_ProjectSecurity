package com.buglachuyennho.securityapp.api;

import com.buglachuyennho.securityapp.domain.Pin;
import com.buglachuyennho.securityapp.domain.User;
import com.buglachuyennho.securityapp.exception.ResourceNotFoundException;
import com.buglachuyennho.securityapp.repo.PinRepo;
import com.buglachuyennho.securityapp.repo.UserRepo;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.cloudinary.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j

@RestController
@RequestMapping("/api/v1/pin")
@RequiredArgsConstructor
public class PinController {

    private final PinRepo pinRepo;
    private final UserRepo userRepo;
    @Autowired
    private Environment environment;
    @Autowired
    private MongoTemplate mongoTemplate;

    @PostMapping
    public ResponseEntity<?> postNewPin(String title, String category, URL destination, String userId, @RequestParam("File") MultipartFile image) throws IOException {
        // upload image
        log.info(image.getOriginalFilename());
        Cloudinary cloudinary = new Cloudinary(environment.getProperty("CLOUDINARY_URL"));
        cloudinary.config.secure = true;
        log.info(cloudinary.config.cloudName);
        Map params1 = ObjectUtils.asMap(
                "use_filename", true,
                "unique_filename", false,
                "overwrite", true
        );

        // get url
        String fileName = image.getOriginalFilename();
        File tempFile = File.createTempFile(fileName, ".tmp");
        image.transferTo(tempFile);
        String filePath = tempFile.getAbsolutePath();
        Map imageUrl = cloudinary.uploader().upload(filePath, params1);
        Object url = imageUrl.get("url");

        // create new pin
        Pin pin = new Pin(title,category,destination,userId, url.toString(),0);
        Pin rusult = pinRepo.save(pin);
        System.out.println(imageUrl.get("url"));
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/pin").toUriString());
        return ResponseEntity.created(uri).body(pin);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<Pin> updatePin(@PathVariable String id,String title, String category, URL destination) throws IOException {

        Pin updatePin = pinRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pin not exist with id: "+ id));
        updatePin.setTitle(title);
        updatePin.setCategory(category);
        updatePin.setDestination(destination);
        pinRepo.save(updatePin);
        return ResponseEntity.ok(updatePin);
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllPin() throws IOException {
        List<Pin> pinList = pinRepo.findAll();
        List<Object> response = new ArrayList<>();
        for (Pin pin : pinList) {
            User user = userRepo.findById(pin.getUserId()).get();

            Map<String, Object> pinUserMap = new HashMap<>();
            pinUserMap.put("pin", pin);
            pinUserMap.put("user", user);
            response.add(pinUserMap);
        }
        System.out.println(response);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getPinsByUserId(@PathVariable String userId) throws IOException {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        List<Pin> pinList = mongoTemplate.find(query, Pin.class);
        return ResponseEntity.ok().body(pinList);
    }
}
