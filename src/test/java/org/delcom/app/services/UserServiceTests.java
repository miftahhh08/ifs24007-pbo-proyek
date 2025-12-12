package org.delcom.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Optional;

import org.delcom.app.dto.RegisterForm;
import org.delcom.app.entities.User;
import org.delcom.app.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class UserServiceTests {

    @Test
    @DisplayName("Pengujian UserService (100% Coverage)")
    public void testVariousUserService() throws Exception {

        // 1. Setup Mock Repository
        UserRepository userRepository = mock(UserRepository.class);

        // 2. Setup Service Manual
        UserService userService = new UserService();

        // 3. Inject Mock via Reflection (Karena Field Injection @Autowired)
        injectField(userService, "userRepository", userRepository);

        // ==========================================
        // 1. METHOD: login
        // ==========================================
        {
            // Skenario A: User Tidak Ditemukan (Return Null)
            {
                String email = " unknown@email.com "; // Ada spasi
                String password = "pass";
                
                // Mock behavior: cari email yang sudah di-trim & lowercase -> return null
                when(userRepository.findByEmail("unknown@email.com")).thenReturn(null);

                User result = userService.login(email, password);
                
                assertNull(result);
            }

            // Skenario B: User Ditemukan, Tapi Password Salah
            {
                String email = "exist@email.com";
                String password = " wrongPass "; // Input user
                
                User dbUser = new User();
                dbUser.setEmail("exist@email.com");
                dbUser.setPassword("correctPass"); // Password di DB

                when(userRepository.findByEmail("exist@email.com")).thenReturn(dbUser);

                User result = userService.login(email, password);

                // Harusnya null karena "wrongPass" != "correctPass"
                assertNull(result);
            }

            // Skenario C: Login Sukses (User Ada & Password Cocok)
            {
                // Input "kotor" (ada spasi & huruf besar)
                String emailInput = "  MyUser@Gmail.Com  "; 
                String passInput = "  secret123  ";

                // Ekspektasi data "bersih"
                String cleanEmail = "myuser@gmail.com";
                String cleanPass = "secret123";

                User dbUser = new User();
                dbUser.setEmail(cleanEmail);
                dbUser.setPassword(cleanPass);

                when(userRepository.findByEmail(cleanEmail)).thenReturn(dbUser);

                User result = userService.login(emailInput, passInput);

                assertNotNull(result);
                assertEquals(cleanEmail, result.getEmail());
            }
        }

        // ==========================================
        // 2. METHOD: register
        // ==========================================
        {
            RegisterForm form = new RegisterForm();
            form.setName("Budi ");
            form.setEmail(" Budi@Email.Com "); // Ada spasi & Caps
            form.setPassword(" rahasia ");     // Ada spasi

            // Execute
            userService.register(form);

            // Verify dengan ArgumentCaptor untuk memastikan data di-trim & lowercase sebelum save
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository, times(1)).save(userCaptor.capture());

            User savedUser = userCaptor.getValue();
            
            assertEquals("budi@email.com", savedUser.getEmail()); // Harus lowercase & trim
            assertEquals("rahasia", savedUser.getPassword());     // Harus trim
            assertEquals("Budi ", savedUser.getName());           // Name tidak diubah logicnya di code Anda
        }

        // ==========================================
        // 3. METHOD: getUserById
        // ==========================================
        {
            // Skenario A: User Ditemukan
            {
                Long id = 1L;
                User user = new User();
                when(userRepository.findById(id)).thenReturn(Optional.of(user));

                User result = userService.getUserById(id);
                assertNotNull(result);
                assertEquals(user, result);
            }

            // Skenario B: User Tidak Ditemukan
            {
                Long id = 99L;
                when(userRepository.findById(id)).thenReturn(Optional.empty());

                User result = userService.getUserById(id);
                assertNull(result); // .orElse(null)
            }
        }
    }

    /**
     * Helper method untuk inject private field (Reflection)
     */
    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}