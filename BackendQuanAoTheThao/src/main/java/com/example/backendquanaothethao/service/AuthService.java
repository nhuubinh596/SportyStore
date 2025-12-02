// src/main/java/com/example/backendquanaothethao/service/AuthService.java
package com.example.backendquanaothethao.service;

import com.example.backendquanaothethao.model.UserAccount;

public interface AuthService {
    /**
     * Return UserAccount if credentials valid; otherwise return null.
     */
    UserAccount authenticate(String username, String password);
}
