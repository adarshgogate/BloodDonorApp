package com.blooddonor.controller;

import com.blooddonor.model.BloodRequest;
import com.blooddonor.repository.BloodRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/blood-requests")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BloodRequestController {

    @Autowired
    private BloodRequestRepository bloodRequestRepository;

    // 🆕 Add new blood request
    @PostMapping
    public ResponseEntity<?> createBloodRequest(@Valid @RequestBody BloodRequest bloodRequest) {
        try {
            // Validate required fields
            if (bloodRequest.getName() == null || bloodRequest.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Name is required"));
            }
            if (bloodRequest.getBloodGroup() == null || bloodRequest.getBloodGroup().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Blood group is required"));
            }
            if (bloodRequest.getCity() == null || bloodRequest.getCity().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "City is required"));
            }
            if (bloodRequest.getContact() == null || bloodRequest.getContact().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Contact is required"));
            }

            bloodRequest.setName(bloodRequest.getName().trim());
            bloodRequest.setBloodGroup(bloodRequest.getBloodGroup().trim().toUpperCase());
            bloodRequest.setCity(bloodRequest.getCity().trim());
            bloodRequest.setContact(bloodRequest.getContact().trim());
            bloodRequest.setRequestDate(new Date());

            BloodRequest savedRequest = bloodRequestRepository.save(bloodRequest);
            return ResponseEntity.ok(savedRequest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to create blood request: " + e.getMessage()));
        }
    }

    // 📄 Get all blood requests
    @GetMapping
    public ResponseEntity<?> getAllRequests() {
        try {
            List<BloodRequest> requests = bloodRequestRepository.findAll();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to fetch blood requests: " + e.getMessage()));
        }
    }

    // ✅ Get blood request by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getRequestById(@PathVariable String id) {
        try {
            Optional<BloodRequest> request = bloodRequestRepository.findById(id);
            if (request.isPresent()) {
                return ResponseEntity.ok(request.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Blood request not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to fetch blood request: " + e.getMessage()));
        }
    }
}
