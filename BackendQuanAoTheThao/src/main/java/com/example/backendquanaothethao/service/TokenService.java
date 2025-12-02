// src/main/java/com/example/backendquanaothethao/service/TokenService.java
package com.example.backendquanaothethao.service;

import com.example.backendquanaothethao.model.UserAccount;

public interface TokenService {
    String createTokenFor(UserAccount user);
}
