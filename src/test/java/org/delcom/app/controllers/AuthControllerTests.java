package org.delcom.app.controllers;

import org.delcom.app.dto.LoginForm;
import org.delcom.app.dto.RegisterForm;
import org.delcom.app.entities.AuthToken;
import org.delcom.app.entities.User;
import org.delcom.app.services.AuthTokenService;
import org.delcom.app.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private AuthTokenService tokenService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthController authController;

    private User testUser;
    private LoginForm loginForm;
    private RegisterForm registerForm;

    @BeforeEach
    void setUp() {
        testUser = new User("Test User", "test@example.com", "password123");
        testUser.setId(1L);

        loginForm = new LoginForm();
        loginForm.setEmail("test@example.com");
        loginForm.setPassword("password123");

        registerForm = new RegisterForm();
        registerForm.setName("New User");
        registerForm.setEmail("new@example.com");
        registerForm.setPassword("newpass123");
    }

    // --- REGISTER TESTS ---

    @Test
    void testRegisterForm_ReturnsRegisterPage() {
        String viewName = authController.registerForm(model);
        assertEquals("pages/auth/register", viewName);
        verify(model, times(1)).addAttribute(eq("registerForm"), any(RegisterForm.class));
    }

    @Test
    void testRegisterSave_Success() {
        when(bindingResult.hasErrors()).thenReturn(false);
        try {
            doNothing().when(userService).register(any(RegisterForm.class));
        } catch (Exception e) {
            fail("Mock setup should not throw exception");
        }

        String result = authController.registerSave(registerForm, bindingResult);

        assertEquals("redirect:/auth/login?success_register", result);
        try {
            verify(userService, times(1)).register(registerForm);
        } catch (Exception e) {
            fail("Verification should not throw exception");
        }
    }

    @Test
    void testRegisterSave_WithValidationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = authController.registerSave(registerForm, bindingResult);

        assertEquals("pages/auth/register", result);
        try {
            verify(userService, never()).register(any());
        } catch (Exception e) {
            fail("Verification should not throw exception");
        }
    }

    @Test
    void testRegisterSave_WithException() {
        when(bindingResult.hasErrors()).thenReturn(false);
        try {
            doThrow(new Exception("Email already exists")).when(userService).register(any(RegisterForm.class));
        } catch (Exception e) {
            fail("Mock setup should not throw exception");
        }

        String result = authController.registerSave(registerForm, bindingResult);

        assertTrue(result.contains("redirect:/auth/register?error="));
    }

    // --- LOGIN FORM TESTS ---

    @Test
    void testLoginForm_ReturnsLoginPage() {
        doNothing().when(response).addCookie(any(Cookie.class));

        String viewName = authController.loginForm(model, response);

        assertEquals("pages/auth/login", viewName);
        verify(model, times(1)).addAttribute(eq("loginForm"), any(LoginForm.class));
        verify(response, times(2)).addCookie(any(Cookie.class));
    }

    // --- LOGIN POST TESTS ---

    @Test
    void testLoginPost_Success() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.login(anyString(), anyString())).thenReturn(testUser);
        when(request.getCookies()).thenReturn(null);
        doNothing().when(tokenService).saveToken(any(AuthToken.class));
        doNothing().when(response).addCookie(any(Cookie.class));

        String result = authController.loginPost(loginForm, bindingResult, request, response);

        assertEquals("redirect:/", result);
        verify(userService, times(1)).login("test@example.com", "password123");
        verify(tokenService, times(1)).saveToken(any(AuthToken.class));
        
        // 2 calls (clear) + 1 call (new token) = 3 calls
        verify(response, atLeast(3)).addCookie(any(Cookie.class));
    }

    @Test
    void testLoginPost_InvalidCredentials() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.login(anyString(), anyString())).thenReturn(null);
        when(request.getCookies()).thenReturn(null);
        doNothing().when(response).addCookie(any(Cookie.class));

        String result = authController.loginPost(loginForm, bindingResult, request, response);

        assertEquals("redirect:/auth/login?error", result);
        verify(userService, times(1)).login("test@example.com", "password123");
        verify(tokenService, never()).saveToken(any());
    }

    @Test
    void testLoginPost_WithValidationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = authController.loginPost(loginForm, bindingResult, request, response);

        assertEquals("pages/auth/login", result);
        verify(userService, never()).login(anyString(), anyString());
    }

    @Test
    void testLoginPost_WithOldCookie() {
        Cookie oldCookie = new Cookie("AUTH_TOKEN", "old-token-123");
        Cookie[] cookies = {oldCookie};
        
        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getCookies()).thenReturn(cookies);
        when(userService.login(anyString(), anyString())).thenReturn(testUser);
        doNothing().when(tokenService).deleteToken(anyString());
        doNothing().when(tokenService).saveToken(any(AuthToken.class));
        doNothing().when(response).addCookie(any(Cookie.class));

        String result = authController.loginPost(loginForm, bindingResult, request, response);

        assertEquals("redirect:/", result);
        verify(tokenService, times(1)).deleteToken("old-token-123");
        verify(tokenService, times(1)).saveToken(any(AuthToken.class));
    }

    @Test
    void testLoginPost_WithException() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getCookies()).thenReturn(null);
        when(userService.login(anyString(), anyString())).thenThrow(new RuntimeException("Database error"));
        doNothing().when(response).addCookie(any(Cookie.class));

        String result = authController.loginPost(loginForm, bindingResult, request, response);

        assertEquals("redirect:/auth/login?error=server_error", result);
    }

    // --- LOGOUT TESTS ---

    @Test
    void testLogout_WithToken() {
        Cookie cookie = new Cookie("AUTH_TOKEN", "test-token-123");
        Cookie[] cookies = {cookie};
        
        when(request.getCookies()).thenReturn(cookies);
        doNothing().when(tokenService).deleteToken(anyString());
        doNothing().when(response).addCookie(any(Cookie.class));

        String result = authController.logout(request, response);

        assertEquals("redirect:/auth/login", result);
        verify(tokenService, times(1)).deleteToken("test-token-123");
        verify(response, times(2)).addCookie(any(Cookie.class));
    }

    @Test
    void testLogout_WithoutToken() {
        when(request.getCookies()).thenReturn(null);
        doNothing().when(response).addCookie(any(Cookie.class));

        String result = authController.logout(request, response);

        assertEquals("redirect:/auth/login", result);
        verify(tokenService, never()).deleteToken(anyString());
        verify(response, times(2)).addCookie(any(Cookie.class));
    }

    @Test
    void testLogout_WithEmptyCookie() {
        Cookie cookie = new Cookie("AUTH_TOKEN", "");
        Cookie[] cookies = {cookie};
        
        when(request.getCookies()).thenReturn(cookies);
        doNothing().when(response).addCookie(any(Cookie.class));

        String result = authController.logout(request, response);

        assertEquals("redirect:/auth/login", result);
        verify(tokenService, never()).deleteToken(anyString());
        verify(response, times(2)).addCookie(any(Cookie.class));
    }
    
    @Test
    void testLogout_WithException() {
        // Arrange
        Cookie cookie = new Cookie("AUTH_TOKEN", "token-to-fail");
        Cookie[] cookies = {cookie};
        
        // Gunakan lenient() agar Mockito tidak komplain jika stub ini dianggap "tidak terpakai" karena error
        lenient().when(request.getCookies()).thenReturn(cookies);
        
        // Simulasi error saat deleteToken
        doThrow(new RuntimeException("DB Error")).when(tokenService).deleteToken(anyString());
        
        // PENTING: Jangan stub response.addCookie di sini!
        // Karena saat Exception terjadi, kode akan masuk catch dan SKIP pemanggilan response.addCookie.
        // Jika kita stub tapi tidak dipanggil, akan muncul UnnecessaryStubbingException.

        // Act
        String result = authController.logout(request, response);

        // Assert
        assertEquals("redirect:/auth/login", result);
        verify(tokenService, times(1)).deleteToken("token-to-fail");
        
        // Pastikan response.addCookie TIDAK dipanggil karena error terjadi sebelumnya
        verify(response, never()).addCookie(any(Cookie.class));
    }
}