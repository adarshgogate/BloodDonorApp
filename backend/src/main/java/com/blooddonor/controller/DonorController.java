package com.blooddonor.controller;

import com.blooddonor.model.Donor;
import com.blooddonor.repository.DonorRepository;
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
@RequestMapping("/api/donors")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DonorController {

    @Autowired
    private DonorRepository donorRepository;

    // ✅ Create donor
    @PostMapping
    public ResponseEntity<?> createDonor(@Valid @RequestBody Donor donor) {
        try {
            // Validate required fields
            if (donor.getName() == null || donor.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Name is required"));
            }
            if (donor.getBloodGroup() == null || donor.getBloodGroup().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Blood group is required"));
            }
            if (donor.getCity() == null || donor.getCity().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "City is required"));
            }
            if (donor.getContact() == null || donor.getContact().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Contact is required"));
            }

            donor.setName(donor.getName().trim());
            donor.setBloodGroup(donor.getBloodGroup().trim().toUpperCase());
            donor.setCity(donor.getCity().trim());
            donor.setContact(donor.getContact().trim());
            donor.setRegisteredAt(new Date());

            Donor savedDonor = donorRepository.save(donor);
            return ResponseEntity.ok(savedDonor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to create donor: " + e.getMessage()));
        }
    }

    // ✅ Get all donors
    @GetMapping
    public ResponseEntity<?> getAllDonors() {
        try {
            List<Donor> donors = donorRepository.findAll();
            return ResponseEntity.ok(donors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to fetch donors: " + e.getMessage()));
        }
    }

    // ✅ Get donor by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getDonorById(@PathVariable String id) {
        try {
            Optional<Donor> donor = donorRepository.findById(id);
            if (donor.isPresent()) {
                return ResponseEntity.ok(donor.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to fetch donor: " + e.getMessage()));
        }
    }

    // ✅ Search donors by city
    @GetMapping("/search/city")
    public ResponseEntity<?> getDonorsByCity(@RequestParam String city) {
        try {
            if (city == null || city.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "City parameter is required"));
            }
            List<Donor> donors = donorRepository.findByCityIgnoreCase(city.trim());
            return ResponseEntity.ok(donors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Search failed: " + e.getMessage()));
        }
    }

    // ✅ Search donors by blood group
    @GetMapping("/search/bloodgroup")
    public ResponseEntity<?> getDonorsByBloodGroup(@RequestParam String bloodGroup) {
        try {
            if (bloodGroup == null || bloodGroup.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Blood group parameter is required"));
            }
            List<Donor> donors = donorRepository.findByBloodGroupIgnoreCase(bloodGroup.trim());
            return ResponseEntity.ok(donors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Search failed: " + e.getMessage()));
        }
    }

    // ✅ Search donors by city & blood group
    @GetMapping("/search")
    public ResponseEntity<?> searchDonors(
            @RequestParam String city, 
            @RequestParam String bloodGroup) {
        try {
            if (city == null || city.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "City parameter is required"));
            }
            if (bloodGroup == null || bloodGroup.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Blood group parameter is required"));
            }
            
            List<Donor> donors = donorRepository.findByCityIgnoreCaseAndBloodGroupIgnoreCase(
                    city.trim(), bloodGroup.trim());
            return ResponseEntity.ok(donors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Search failed: " + e.getMessage()));
        }
    }

    // ✅ Update donor
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDonor(@PathVariable String id, @Valid @RequestBody Donor donor) {
        try {
            Optional<Donor> existingDonor = donorRepository.findById(id);
            if (existingDonor.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Donor donorToUpdate = existingDonor.get();
            if (donor.getName() != null && !donor.getName().trim().isEmpty()) {
                donorToUpdate.setName(donor.getName().trim());
            }
            if (donor.getBloodGroup() != null && !donor.getBloodGroup().trim().isEmpty()) {
                donorToUpdate.setBloodGroup(donor.getBloodGroup().trim().toUpperCase());
            }
            if (donor.getCity() != null && !donor.getCity().trim().isEmpty()) {
                donorToUpdate.setCity(donor.getCity().trim());
            }
            if (donor.getContact() != null && !donor.getContact().trim().isEmpty()) {
                donorToUpdate.setContact(donor.getContact().trim());
            }

            Donor updatedDonor = donorRepository.save(donorToUpdate);
            return ResponseEntity.ok(updatedDonor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to update donor: " + e.getMessage()));
        }
    }

    // ✅ Delete donor
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDonor(@PathVariable String id) {
        try {
            if (!donorRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            donorRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Donor deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to delete donor: " + e.getMessage()));
        }
    }
}