package com.hhgs.Attendances.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.hhgs.Attendances.model.User;
import com.hhgs.Attendances.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    // ✅ CREATE USER with duplicate check
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (userRepo.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        if (userRepo.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }
        if (userRepo.existsByEmpId(user.getEmpId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Emp ID already exists");
        }

        return ResponseEntity.ok(userRepo.save(user));
    }

    // ✅ GET ALL USERS (only employees)
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userRepo.findByRole("employee");
    }

    // ✅ UPDATE USER with duplicate check (ignoring self)
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody User user) {
    	System.out.println("update: "+id);
        Optional<User> existing = userRepo.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Check if username/email/empId already used by another user
        Optional<User> byUsername = userRepo.findByUsername(user.getUsername());
        if (byUsername.isPresent() && !byUsername.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken");
        }

        Optional<User> byEmail = userRepo.findByEmail(user.getEmail());
        if (byEmail.isPresent() && !byEmail.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already taken");
        }

        Optional<User> byEmpId = userRepo.findByEmpId(user.getEmpId());
        if (byEmpId.isPresent() && !byEmpId.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Emp ID already taken");
        }

        user.setId(id); // ensure ID is set
        return ResponseEntity.ok(userRepo.save(user));
    }

    // ✅ DELETE USER
    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable String id) {
        userRepo.deleteById(id);
    }
}
