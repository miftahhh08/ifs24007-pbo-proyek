package org.delcom.app.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.delcom.app.dto.LoginForm;
import org.delcom.app.dto.RegisterForm;
import org.delcom.app.entities.AuthToken;
import org.delcom.app.entities.User;
import org.delcom.app.services.AuthTokenService;
import org.delcom.app.services.UserService;
import org.delcom.app.utils.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class AuthControllerTests {

    @Test
    @DisplayName("Pengujian AuthController dengan berbagai skenario (100% Coverage)")
    public void testVariousAuthController() throws Exception {

        // 1. Setup Mocks
        UserService userService = mock(UserService.class);
        AuthTokenService tokenService = mock(AuthTokenService.class);

        // 2. Setup Controller Manual
        AuthController authController = new AuthController();

        // 3. Inject Mocks via Reflection
        injectField(authController, "userService", userService);
        injectField(authController, "tokenService", tokenService);

        // 4. Mock Static JwtUtil untuk seluruh skenario
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {

            // ==========================================
            // BLOCK: REGISTER (GET)
            // ==========================================
            {
                Model model = mock(Model.class);
                String viewName = authController.registerForm(model);

                assertEquals("pages/auth/register", viewName);
                verify(model, times(1)).addAttribute(anyString(), any(RegisterForm.class));
            }

            // ==========================================
            // BLOCK: REGISTER (POST)
            // ==========================================
            {
                // Skenario 1: Validasi Error
                {
                    RegisterForm form = new RegisterForm();
                    BindingResult res = mock(BindingResult.class);
                    when(res.hasErrors()).thenReturn(true);

                    String viewName = authController.registerSave(form, res);
                    
                    assertEquals("pages/auth/register", viewName);
                    verify(userService, never()).register(any());
                }

                // Skenario 2: Exception saat Register
                {
                    reset(userService); // Reset mock state
                    RegisterForm form = new RegisterForm();
                    BindingResult res = mock(BindingResult.class);
                    when(res.hasErrors()).thenReturn(false);

                    // Mock throw exception
                    doThrow(new RuntimeException("Email Duplicate")).when(userService).register(any());

                    String viewName = authController.registerSave(form, res);
                    
                    // Controller menangkap error dan redirect dengan pesan error
                    assertEquals("redirect:/auth/register?error=Email Duplicate", viewName);
                }

                // Skenario 3: Sukses
                {
                    reset(userService);
                    RegisterForm form = new RegisterForm();
                    BindingResult res = mock(BindingResult.class);
                    when(res.hasErrors()).thenReturn(false);

                    doNothing().when(userService).register(any());

                    String viewName = authController.registerSave(form, res);

                    assertEquals("redirect:/auth/login?success_register", viewName);
                }
            }

            // ==========================================
            // BLOCK: LOGIN (GET)
            // ==========================================
            {
                Model model = mock(Model.class);
                String viewName = authController.loginForm(model);

                assertEquals("pages/auth/login", viewName);
                verify(model, times(1)).addAttribute(anyString(), any(LoginForm.class));
            }

            // ==========================================
            // BLOCK: LOGIN (POST)
            // ==========================================
            {
                // Skenario 1: Validasi Error
                {
                    LoginForm form = new LoginForm();
                    BindingResult res = mock(BindingResult.class);
                    HttpServletResponse response = mock(HttpServletResponse.class);
                    
                    when(res.hasErrors()).thenReturn(true);

                    String viewName = authController.loginPost(form, res, response);
                    
                    assertEquals("pages/auth/login", viewName);
                }

                // Skenario 2: User Tidak Ditemukan / Password Salah
                {
                    reset(userService);
                    LoginForm form = new LoginForm();
                    form.setEmail("wrong@test.com");
                    form.setPassword("pass");
                    
                    BindingResult res = mock(BindingResult.class);
                    HttpServletResponse response = mock(HttpServletResponse.class);
                    when(res.hasErrors()).thenReturn(false);

                    // User null
                    when(userService.login(anyString(), anyString())).thenReturn(null);

                    String viewName = authController.loginPost(form, res, response);
                    
                    assertEquals("redirect:/auth/login?error", viewName);
                }

                // Skenario 3: Exception (Server Error)
                {
                    reset(userService);
                    LoginForm form = new LoginForm();
                    form.setEmail("error@test.com");
                    form.setPassword("pass");
                    
                    BindingResult res = mock(BindingResult.class);
                    HttpServletResponse response = mock(HttpServletResponse.class);
                    when(res.hasErrors()).thenReturn(false);

                    // Throw Exception
                    when(userService.login(anyString(), anyString())).thenThrow(new RuntimeException("DB Down"));

                    String viewName = authController.loginPost(form, res, response);
                    
                    // Controller menangkap exception
                    assertEquals("redirect:/auth/login?error=server_error", viewName);
                }

                // Skenario 4: Login Sukses
                {
                    reset(userService);
                    reset(tokenService);

                    LoginForm form = new LoginForm();
                    form.setEmail("user@test.com");
                    form.setPassword("pass");

                    User user = new User();
                    user.setId(100L);

                    BindingResult res = mock(BindingResult.class);
                    HttpServletResponse response = mock(HttpServletResponse.class);
                    when(res.hasErrors()).thenReturn(false);

                    // Mock Login User Sukses
                    when(userService.login(anyString(), anyString())).thenReturn(user);
                    
                    // Mock Generate Token
                    jwtUtilMock.when(() -> JwtUtil.generateToken(user.getId())).thenReturn("MOCK_JWT");

                    // Capture Cookie
                    ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
                    doNothing().when(response).addCookie(cookieCaptor.capture());

                    String viewName = authController.loginPost(form, res, response);

                    // Assertions
                    assertEquals("redirect:/", viewName);
                    
                    // Verify Token Saved
                    verify(tokenService, times(1)).saveToken(any(AuthToken.class));

                    // Verify Cookie
                    Cookie cookie = cookieCaptor.getValue();
                    assertNotNull(cookie);
                    assertEquals("AUTH_TOKEN", cookie.getName());
                    assertEquals("MOCK_JWT", cookie.getValue());
                    assertEquals(-1, cookie.getMaxAge());
                    assertEquals("/", cookie.getPath());
                }
            }

            // ==========================================
            // BLOCK: LOGOUT (GET)
            // ==========================================
            {
                HttpServletResponse response = mock(HttpServletResponse.class);
                
                // Capture Cookie untuk memastikan penghapusan
                ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
                doNothing().when(response).addCookie(cookieCaptor.capture());

                String viewName = authController.logout(response);

                assertEquals("redirect:/auth/login", viewName);

                Cookie cookie = cookieCaptor.getValue();
                assertEquals("AUTH_TOKEN", cookie.getName());
                assertEquals(0, cookie.getMaxAge()); // MaxAge 0 berarti dihapus
            }
        }
    }

    /**
     * Helper method untuk inject private field via Reflection
     */
    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}