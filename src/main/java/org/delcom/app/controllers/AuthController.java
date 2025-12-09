package org.delcom.app.controllers;
import org.delcom.app.dto.*;
import org.delcom.app.entities.*;
import org.delcom.app.services.*;
import org.delcom.app.utils.JwtUtil;
import org.delcom.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;

@Controller @RequestMapping("/auth")
public class AuthController {
    @Autowired private UserService userService;
    @Autowired private AuthTokenService authTokenService;
    @Autowired private UserRepository userRepository;

    @GetMapping("/login") public String login(Model model) { model.addAttribute("loginForm", new LoginForm()); return "login"; }
    
    @PostMapping("/login/post")
    public String loginPost(@Valid @ModelAttribute LoginForm form, BindingResult res, HttpServletResponse resp) {
        if (res.hasErrors()) return "login";
        User user = userService.login(form.getEmail(), form.getPassword());
        if (user == null) return "login"; // Tambah error message sendiri ya

        String token = JwtUtil.generateToken(user.getId());
        authTokenService.save(new AuthToken(user.getId(), token));
        
        Cookie cookie = new Cookie("AUTH_TOKEN", token);
        cookie.setPath("/"); cookie.setHttpOnly(true);
        resp.addCookie(cookie);

        return "redirect:/shop"; // SEMUA MASUK KE TOKO DULU
    }

    @GetMapping("/register") public String register(Model model) { model.addAttribute("registerForm", new RegisterForm()); return "register"; }
    
    @PostMapping("/register/save")
    public String regSave(@Valid @ModelAttribute RegisterForm form, BindingResult res) {
        if(res.hasErrors()) return "register";
        User u = new User(); u.setName(form.getName()); u.setEmail(form.getEmail()); u.setPassword(form.getPassword()); u.setRole("BUYER");
        userService.register(u);
        return "redirect:/auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse resp) {
        Cookie c = new Cookie("AUTH_TOKEN", null); c.setPath("/"); c.setMaxAge(0); resp.addCookie(c);
        return "redirect:/auth/login";
    }

    // FITUR SHOPEE: BUKA TOKO
    @PostMapping("/become-seller")
    public String becomeSeller() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            User u = (User) auth.getPrincipal();
            u.setRole("ADMIN");
            userRepository.save(u);
            return "redirect:/cookies"; // Langsung ke Dashboard
        }
        return "redirect:/shop";
    }
}