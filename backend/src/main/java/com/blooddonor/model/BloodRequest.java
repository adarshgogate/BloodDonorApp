package com.blooddonor.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "blood_requests")
public class BloodRequest {

    @Id
    private String id;
    private String name;
    private String bloodGroup;
    private String city;  // ✅ Added city field
    private String contact;
    private Date requestDate;

    public BloodRequest() {
        this.requestDate = new Date();
    }

    public BloodRequest(String name, String bloodGroup, String city, String contact) {
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.city = city;
        this.contact = contact;
        this.requestDate = new Date();
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public Date getRequestDate() { return requestDate; }
    public void setRequestDate(Date requestDate) { this.requestDate = requestDate; }
}
