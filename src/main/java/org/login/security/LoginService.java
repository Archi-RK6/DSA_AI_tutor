package org.login.security;

import org.login.common.repository.ProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final ProfileRepository repo;

    public LoginService(ProfileRepository repo) {
        this.repo = repo;
    }

    public boolean validateUser(String name, String password) {
        return repo.findByName(name)
                .map(profile -> profile.getPassword().equals(password))
                .orElse(false);
    }
}
