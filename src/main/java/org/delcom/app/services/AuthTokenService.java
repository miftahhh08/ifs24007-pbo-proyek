package org.delcom.app.services;

import org.delcom.app.entities.AuthToken;
import org.delcom.app.repositories.AuthTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthTokenService {

    private final AuthTokenRepository repository;

    public AuthTokenService(AuthTokenRepository repository) {
        this.repository = repository;
    }

    public void saveToken(AuthToken token) {
        repository.save(token);
    }
}