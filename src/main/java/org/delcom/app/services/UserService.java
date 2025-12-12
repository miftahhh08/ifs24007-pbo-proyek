package org.delcom.app.services;

import org.delcom.app.dto.RegisterForm;
import org.delcom.app.entities.User;
import org.delcom.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // --- FUNGSI LOGIN ---
    public User login(String email, String password) {
        String cleanEmail = email.trim().toLowerCase();
        String cleanPass = password.trim();

        User user = userRepository.findByEmail(cleanEmail);
        if (user == null) return null;

        if (user.getPassword().equals(cleanPass)) {
            return user;
        }
        return null;
    }

    // --- FUNGSI REGISTER ---
    public void register(RegisterForm form) {
        User newUser = new User();
        newUser.setEmail(form.getEmail().trim().toLowerCase());
        newUser.setPassword(form.getPassword().trim());
        newUser.setName(form.getName());
        userRepository.save(newUser);
    }

    // --- [BARU] FUNGSI CARI USER BERDASARKAN ID (Untuk Home) ---
    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null); // Jika ketemu kembalikan user, jika tidak kembalikan null
    }
}