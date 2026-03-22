package com.cosume.cosumeRest.Controller;

import com.cosume.cosumeRest.entities.User;
import com.cosume.cosumeRest.entities.UserRecord;
import com.cosume.cosumeRest.service.UserRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/user-records")
public class UserRecordController {

    @Autowired
    private UserRecordService service;

    @PostMapping
    public ResponseEntity<UserRecord> create(@RequestBody UserRecord record) {
        UserRecord saved = service.save(record);
        return ResponseEntity.created(URI.create("/user-records")).body(saved);
    }

    @GetMapping
    public ResponseEntity<UserRecord> get(@RequestParam int id, @RequestParam String name) {
        User user = new User(id, name);
        Optional<UserRecord> maybe = service.findById(user);
        return maybe.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}

