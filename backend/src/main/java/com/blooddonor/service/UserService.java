package com.blooddonor.service;

public interface UserService {
    void registerUser(String username, String password);
    String authenticateAndGenerateToken(String username, String password);
}
