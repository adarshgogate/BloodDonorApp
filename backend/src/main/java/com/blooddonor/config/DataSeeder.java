// 1. DataSeeder.java - Fixed character encoding issues
package com.blooddonor.config;

import com.blooddonor.model.User;
import com.blooddonor.model.Donor;
import com.blooddonor.model.BloodRequest;
import com.blooddonor.repository.UserRepository;
import com.blooddonor.repository.DonorRepository;
import com.blooddonor.repository.BloodRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private BloodRequestRepository bloodRequestRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedAdminUser();
        seedSampleData();
    }

    private void seedAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@blooddonor.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ROLE_ADMIN");
            admin.setCreatedAt(new Date());
            userRepository.save(admin);
            System.out.println("✅ Admin user created - Username: admin, Password: admin123");
        }

        if (!userRepository.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@blooddonor.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole("ROLE_USER");
            user.setCreatedAt(new Date());
            userRepository.save(user);
            System.out.println("✅ Test user created - Username: user, Password: user123");
        }
    }

    private void seedSampleData() {
        // Seed sample donors if none exist
        if (donorRepository.count() == 0) {
            Donor[] sampleDonors = {
                new Donor("John Doe", "A+", "Mumbai", "+91 9876543210"),
                new Donor("Jane Smith", "O-", "Delhi", "+91 9876543211"),
                new Donor("Rajesh Kumar", "B+", "Bangalore", "+91 9876543212"),
                new Donor("Priya Sharma", "AB+", "Chennai", "+91 9876543213"),
                new Donor("Mohammed Ali", "O+", "Hyderabad", "+91 9876543214"),
                new Donor("Sunita Patel", "A-", "Pune", "+91 9876543215")
            };

            for (Donor donor : sampleDonors) {
                donorRepository.save(donor);
            }
            System.out.println("✅ Sample donors created");
        }

        // Seed sample blood requests if none exist
        if (bloodRequestRepository.count() == 0) {
            BloodRequest[] sampleRequests = {
                new BloodRequest("Emergency Patient 1", "A+", "Mumbai", "+91 8765432109"),
                new BloodRequest("Critical Care Unit", "O-", "Delhi", "+91 8765432108"),
                new BloodRequest("Surgery Department", "B+", "Bangalore", "+91 8765432107"),
                new BloodRequest("Accident Victim", "AB+", "Chennai", "+91 8765432106"),
                new BloodRequest("Maternity Ward", "O+", "Hyderabad", "+91 8765432105")
            };

            for (BloodRequest request : sampleRequests) {
                bloodRequestRepository.save(request);
            }
            System.out.println("✅ Sample blood requests created");
        }
    }
}