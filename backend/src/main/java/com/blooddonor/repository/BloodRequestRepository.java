package com.blooddonor.repository;

import com.blooddonor.model.BloodRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloodRequestRepository extends MongoRepository<BloodRequest, String> {

    // 🔍 Search by city (case-insensitive)
    List<BloodRequest> findByCityIgnoreCase(String city);

    // 🔍 Search by blood group (case-insensitive)
    List<BloodRequest> findByBloodGroupIgnoreCase(String bloodGroup);

    // 🔍 Search by both city and blood group (case-insensitive)
    List<BloodRequest> findByCityIgnoreCaseAndBloodGroupIgnoreCase(String city, String bloodGroup);
}
