package org.delcom.app.interceptors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.delcom.app.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthInterceptorTests {

    @Test
    @DisplayName("Pengujian AuthInterceptor (100% Coverage)")
    public void testAuthInterceptor() throws Exception {

        // 1. Setup Interceptor
        AuthInterceptor interceptor = new AuthInterceptor();

        // 2. Setup Mock Request & Response
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // 3. Mock Static SecurityContextHolder
        try (MockedStatic<SecurityContextHolder> mockedContextHolder = mockStatic(SecurityContextHolder.class)) {

            // Mock SecurityContext (Container authentication)
            SecurityContext securityContext = mock(SecurityContext.class);
            
            // Mengarahkan SecurityContextHolder.getContext() agar mengembalikan mock kita
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // ==========================================
            // SKENARIO 1: Authentication NULL (Belum Login)
            // ==========================================
            {
                // set getAuthentication() return null
                when(securityContext.getAuthentication()).thenReturn(null);

                // Execute
                boolean result = interceptor.preHandle(request, response, new Object());

                // Verify
                assertTrue(result); // Harus tetap true agar request lanjut (sesuai kode)
                
                // Pastikan logic setAttribute TIDAK dijalankan
                verify(request, never()).setAttribute(any(), any());
            }

            // ==========================================
            // SKENARIO 2: Authentication ADA, Tapi Principal BUKAN User (Misal: String "anonymousUser")
            // ==========================================
            {
                Authentication auth = mock(Authentication.class);
                
                // set getAuthentication() return auth object
                when(securityContext.getAuthentication()).thenReturn(auth);
                // set Principal return String (Bukan org.delcom.app.entities.User)
                when(auth.getPrincipal()).thenReturn("anonymousUser");

                // Execute
                boolean result = interceptor.preHandle(request, response, new Object());

                // Verify
                assertTrue(result);
                
                // Pastikan logic setAttribute TIDAK dijalankan karena instanceof gagal
                verify(request, never()).setAttribute(any(), any());
            }

            // ==========================================
            // SKENARIO 3: SUKSES (Principal ADALAH User Entity)
            // ==========================================
            {
                Authentication auth = mock(Authentication.class);
                User user = new User(); // User entity asli/mock
                user.setName("Test User");

                // Setup Mock return chain
                when(securityContext.getAuthentication()).thenReturn(auth);
                when(auth.getPrincipal()).thenReturn(user);

                // Execute
                boolean result = interceptor.preHandle(request, response, new Object());

                // Verify
                assertTrue(result);

                // Pastikan user disimpan ke request attribute
                verify(request, times(1)).setAttribute("user", user);
            }
        }
    }
}