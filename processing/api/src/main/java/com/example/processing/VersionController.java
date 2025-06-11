package com.example.processing;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
public class VersionController {
    @GetMapping("/version")
    public ResponseEntity<String> getVersion() {
        return ResponseEntity.ok("1.0.0");
    }
}
