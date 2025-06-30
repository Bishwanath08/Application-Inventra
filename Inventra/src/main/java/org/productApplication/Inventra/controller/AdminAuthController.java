package org.productApplication.Inventra.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.productApplication.Inventra.DTO.LoginRequest;
import org.productApplication.Inventra.DTO.OTPVerificationRequest;
import org.productApplication.Inventra.jwt.JwtTokenUtil;
import org.productApplication.Inventra.models.TblUsers;
import org.productApplication.Inventra.service.GroupService;
import org.productApplication.Inventra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class AdminAuthController {

        @Autowired
        private UserService userService;

        @Autowired
        private JwtTokenUtil jwtTokenUtil;

        @Autowired
        private BCryptPasswordEncoder PasswordEncoder;
        @Autowired
        private GroupService groupService;

    @GetMapping("/")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@ModelAttribute("loginRequest") LoginRequest loginRequest, Model model, HttpServletResponse response) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

       String otp = userService.login(email, password);
            if (otp != null) {
                model.addAttribute("email", email);
                model.addAttribute("generatedOtp", otp);
                model.addAttribute("otpVerificationRequest", new OTPVerificationRequest());
                return "verifyOtp";
            } else {
                model.addAttribute("error", "Invalid credentials");
                return "login";
            }
    }

    @PostMapping("/verifyOtp")
    public String verifyOtp(HttpServletRequest request,
                            @ModelAttribute("otpVerificationRequest") OTPVerificationRequest otpVerificationRequest,
                            @RequestParam("email") String email,
                            Model model,
                            HttpServletResponse response) {
        String jwt = userService.verifyOtp(email, otpVerificationRequest.getOtp());

        if (jwt != null) {

            Cookie jwtCookie = new Cookie("jwtToken", jwt);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            response.addCookie(jwtCookie);

            request.getSession().setAttribute("Authorization", "Bearer " + jwt);
            response.setHeader("Authorization", "Bearer " + jwt);

            TblUsers users = userService.getUsersDetails(email);

            List<String> permissions = groupService.getPermissionsForGroup(users.getGroupId());
            model.addAttribute("allPermissions", permissions);

            return "redirect:/dashboard";

        } else {

            model.addAttribute("email", email);
            model.addAttribute("otpVerificationRequest", new OTPVerificationRequest());
            model.addAttribute("error", "Invalid OTP");
            return "verifyOtp";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model, @CookieValue(value = "jwtToken",
            defaultValue = "Guest") String jwtToken) {
        try {
            String email = jwtTokenUtil.extractEmail(jwtToken);
            TblUsers users = userService.getUsersDetails(email);
            List<String> permissions = groupService.getPermissionsForGroup(users.getGroupId());
            model.addAttribute("allPermissions", permissions);
            model.addAttribute("username", users.getUserName());
            model.addAttribute("email", users.getEmail());
            model.addAttribute("avatarUrl", "/images/avatar.jpeg");
            model.addAttribute("role", users.getRole());
            return "dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred: " + e.getMessage());
            return "error_page";
        }
    }

    @GetMapping("/header")
    public String showHeader(Model model) {
        model.addAttribute("username", "JohnDoe");
        return "header";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request,  @CookieValue(value = "jwtToken",
            defaultValue = "Guest") String jwtToken) {
        try {
            String email = jwtTokenUtil.extractEmail(jwtToken);
            TblUsers users = userService.getUsersDetails(email);
            HttpSession session = request.getSession(false);

            if (session != null) {
                session.invalidate();
            }

            return "redirect:/login";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
