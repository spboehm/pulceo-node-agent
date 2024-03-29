package dev.pulceo.pna.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PulceoController {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok().build();
    }

}
