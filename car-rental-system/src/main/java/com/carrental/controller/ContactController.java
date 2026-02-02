package com.carrental.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrental.dto.ContactRequest;
import com.carrental.entity.ContactMessage;
import com.carrental.repository.ContactMessageRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ContactController {

    private final ContactMessageRepository contactMessageRepository;

    @PostMapping
    public ResponseEntity<?> submitContactForm(@Valid @RequestBody ContactRequest request) {
        try {
            ContactMessage message = new ContactMessage();
            message.setName(request.getName());
            message.setEmail(request.getEmail());
            message.setMessage(request.getMessage());

            contactMessageRepository.save(message);

            return ResponseEntity.ok("Message sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send message: " + e.getMessage());
        }
    }
}
