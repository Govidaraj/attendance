package com.hhgs.Attendances.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.hhgs.Attendances.jwt.JwtUtil;
import com.hhgs.Attendances.model.Employee;
import com.hhgs.Attendances.repository.EmployeeRepository;
import com.hhgs.Attendances.request.ForgotPasswordRequest;
import com.hhgs.Attendances.request.LoginResponse;

import java.util.List;
import java.util.Random;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;


    public Employee register(Employee emp) {
        if (repository.existsByEmail(emp.getEmail())) {
            throw new RuntimeException("Email already registered.");
        }

        // Generate password
        String namePrefix = emp.getName().length() >= 4 ? emp.getName().substring(0, 4) : emp.getName();
        String randomDigits = String.format("%04d", new Random().nextInt(10000));
        String rawPassword = namePrefix + randomDigits;

        // Encrypt password
        System.out.println("password: "+rawPassword);
        String encryptedPassword = passwordEncoder.encode(rawPassword);
        emp.setPassword(encryptedPassword);

        // Save to DB
        Employee saved = repository.save(emp);

        // Send confirmation mail
        sendMail(emp.getEmail(), emp.getName(), rawPassword);

        return saved;
    }

    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }

    private void sendMail(String toEmail, String name, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Registration Successful");
        message.setText("Hello " + name + ",\n\nYour registration was successful.\nYour password: " + password);
        mailSender.send(message);
    }

    public String updatePassword(ForgotPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        Employee employee = repository.findAll().stream()
                .filter(e -> e.getEmail().equalsIgnoreCase(request.getEmail()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Email not found"));

        String encrypted = passwordEncoder.encode(request.getNewPassword());
        employee.setPassword(encrypted);
        repository.save(employee);

        return "Password updated successfully";
    }

}
