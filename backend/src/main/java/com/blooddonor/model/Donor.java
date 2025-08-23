package com.blooddonor.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "donors")
public class Donor {

    @Id
    private String id;
    private String name;
    private String bloodGroup;
    private String city;
    private String contact;
    private Date registeredAt;

    public Donor() {
        this.registeredAt = new Date();
    }

    public Donor(String name, String bloodGroup, String city, String contact) {
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.city = city;
        this.contact = contact;
        this.registeredAt = new Date();
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

    public Date getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(Date registeredAt) { this.registeredAt = registeredAt; }
}
