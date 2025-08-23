package com.blooddonor.repository;

import com.blooddonor.model.Donor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DonorRepository extends MongoRepository<Donor, String> {
    
    // Search by city (case-insensitive)
    List<Donor> findByCityIgnoreCase(String city);
    
    // Search by blood group (case-insensitive)
    List<Donor> findByBloodGroupIgnoreCase(String bloodGroup);
    
    // Search by both city and blood group (case-insensitive)
    List<Donor> findByCityIgnoreCaseAndBloodGroupIgnoreCase(String city, String bloodGroup);
    
    // Search by name (case-insensitive)
    List<Donor> findByNameIgnoreCase(String name);
    
    // Check if donor exists by contact
    boolean existsByContact(String contact);
}