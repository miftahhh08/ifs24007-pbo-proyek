package org.delcom.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterForm {

    @NotBlank(message = "Nama wajib diisi") // Pakai NotBlank agar spasi dianggap error
    private String name;

    @NotBlank(message = "Email wajib diisi")
    @Email(message = "Format email tidak valid")
    private String email;

    @NotBlank(message = "Password wajib diisi")
    @Size(min = 6, message = "Password minimal 6 karakter")
    private String password;

    // --- 1. CONSTRUCTOR KOSONG (Wajib buat Spring Boot) ---
    public RegisterForm() {
    }

    // --- 2. CONSTRUCTOR LENGKAP (Wajib buat Test agar tidak error "undefined") ---
    public RegisterForm(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // --- 3. GETTER & SETTER ---
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}