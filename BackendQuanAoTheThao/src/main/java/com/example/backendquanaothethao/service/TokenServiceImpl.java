package com.example.backendquanaothethao.service;

import com.example.backendquanaothethao.model.UserAccount;
import com.example.backendquanaothethao.service.TokenService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {
    @Override
    public String createTokenFor(UserAccount user) {
        return UUID.randomUUID().toString();
    }
}
