package edu.cit.alicaba.stockwise.controller;

import edu.cit.alicaba.stockwise.entity.User;
import edu.cit.alicaba.stockwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody Map<String, String> payload) {
        Map<String, Object> response = new HashMap<>();

        String name = payload.get("name");
        String email = payload.get("email");
        String password = payload.get("password");

        // 1. Prevent duplicate emails
        if (userRepository.existsByEmail(email)) {
            response.put("success", false);
            response.put("error", Map.of("message", "Email is already registered."));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // 2. Hash password securely and save
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));

        userRepository.save(newUser);

        response.put("success", true);
        response.put("data", Map.of("message", "User registered successfully"));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> payload) {
        Map<String, Object> response = new HashMap<>();

        // Grab the input from the frontend (even if the React input is named 'email',
        // the user might have typed their name into it)
        String identifier = payload.get("email");
        String password = payload.get("password");

        // Call the repository to search BOTH columns using that single identifier
        Optional<User> userOptional = userRepository.findByEmailOrName(identifier, identifier);

        // 1. Validate credentials against the hashed password
        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {

            // Note: For Phase 1, we generate a mock token. Real JWT will be added in Phase 2.
            String mockToken = UUID.randomUUID().toString();

            User user = userOptional.get();
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", user.getName());
            userData.put("email", user.getEmail());

            response.put("success", true);
            response.put("data", Map.of(
                    "user", userData,
                    "accessToken", mockToken
            ));

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("error", Map.of("message", "Invalid email/name or password"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}