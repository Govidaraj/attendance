package com.hhgs.Attendances.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hhgs.Attendances.jwt.JwtUtil;
import com.hhgs.Attendances.model.User;
import com.hhgs.Attendances.repository.UserRepository;
import com.hhgs.Attendances.request.LoginRequest;
import com.hhgs.Attendances.request.LoginResponse;
import com.hhgs.Attendances.request.LogoutResponse;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil; 

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
    	System.out.println("login");
        Optional<User> userOp = userRepository.findByUsername(request.getUsername());
        User user = userOp.get();
        System.out.println("username: "+request.getUsername());
        if (user != null && user.getPassword().equals(request.getPassword())) {
            String token = jwtUtil.generateToken(user.getUsername(), user.getEmail(), user.getRole());
            LoginResponse response = new LoginResponse();
            response.setUsername(user.getUsername());
            response.setEmpId(user.getEmpId());
            response.setRole(user.getRole());
            response.setToken(token);
            return response;
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("Authorization") String token) {
        // Optionally: Add this token to a blacklist for invalidation
        System.out.println("User logged out. Token: " + token);

        return ResponseEntity.ok(new LogoutResponse("Logout successful"));
    }
}
