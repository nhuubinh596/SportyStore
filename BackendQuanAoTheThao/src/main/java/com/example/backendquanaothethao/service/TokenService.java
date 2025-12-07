package com.example.backendquanaothethao.service;

import com.example.backendquanaothethao.model.UserAccount;

public interface TokenService {
    String createTokenFor(UserAccount user);
}
