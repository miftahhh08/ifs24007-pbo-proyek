package org.delcom.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterForm {

    @NotBlank(message = "Nama wajib diisi") // Sesuaikan pesan dengan 'actual' di log error
    private String name;

    @NotBlank(message = "Email wajib diisi") // Sesuaikan pesan
    @Email(message = "Format email tidak valid") // Tambahkan validasi format
    private String email;

    @NotBlank(message = "Password wajib diisi") // Sesuaikan pesan
    @Size(min = 6, message = "Password minimal 6 karakter") // Opsional, best practice
    private String password;

    // --- Constructor Kosong ---
    public RegisterForm() {}

    // --- Constructor Isi (Untuk Test Mudah) ---
    public RegisterForm(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // --- Getter & Setter ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}