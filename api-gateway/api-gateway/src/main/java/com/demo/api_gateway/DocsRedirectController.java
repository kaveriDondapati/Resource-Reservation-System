package com.demo.api_gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class DocsRedirectController {

    @GetMapping({"/api/docs", "/api/docs/"})
    public ResponseEntity<Void> docs() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/swagger-ui.html"))
                .build();
    }
}
