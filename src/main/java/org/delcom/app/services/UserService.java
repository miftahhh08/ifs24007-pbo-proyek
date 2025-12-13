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

    // 1. REGISTER
    public void register(RegisterForm form) throws Exception {
        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            throw new Exception("Email sudah terdaftar");
        }
        
        User user = new User(form.getName(), form.getEmail(), form.getPassword());
        userRepository.save(user);
        System.out.println(">>> USER BARU TERDAFTAR: " + user.getEmail() + " (ID: " + user.getId() + ")");
    }

    // 2. LOGIN
    public User login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) {
                System.out.println(">>> LOGIN VALID: " + email + " (ID: " + user.getId() + ")");
                return user;
            } else {
                System.out.println(">>> LOGIN GAGAL: Password salah untuk " + email);
            }
        } else {
            System.out.println(">>> LOGIN GAGAL: Email tidak ditemukan - " + email);
        }
        return null;
    }

    // 3. GET USER BY ID
    public User getUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println(">>> USER DITEMUKAN: ID=" + id + ", Email=" + user.getEmail());
            return user;
        } else {
            System.out.println(">>> USER TIDAK DITEMUKAN: ID=" + id);
            return null;
        }
    }

    // 4. SAVE USER (Update Profil Toko)
    public void saveUser(User user) {
        userRepository.save(user);
        System.out.println(">>> USER UPDATED: " + user.getEmail() + " (ID: " + user.getId() + ")");
        System.out.println(">>> Shop Name: " + user.getShopName());
    }
}