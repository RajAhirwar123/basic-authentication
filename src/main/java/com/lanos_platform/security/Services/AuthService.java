package com.lanos_platform.security.Services;


import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lanos_platform.security.Modal.BlacklistedToken;
import com.lanos_platform.security.Modal.User;
import com.lanos_platform.security.Payload.UserDTO;
import com.lanos_platform.security.Repository.BlacklistedTokenRepository;
import com.lanos_platform.security.Repository.UserRepository;
import com.lanos_platform.security.jwt.JwtUtils;

import java.util.Optional;

@Service
public class AuthService {

	@Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;  // ✅ JWT Utility

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;
    
    public String registerUser(String userName, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            return "Email already registered!" + email;
        }

        User user = new User();
        user.setUserName(userName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        
        userRepository.save(user);
        return "User registered successfully!" + email;
    }

    public UserDTO loginUser(String email, String password ) {
    	System.out.println("Login attempt with email: " + email);
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Login failed: User not found!");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Login failed: Incorrect password!");
        }

        String token = jwtUtils.generateTokenFromUsername(email);
        return new UserDTO(user.getUserName(), user.getEmail(), token);
    }

    public void logoutUser(String token) {
        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(token);
        blacklistedTokenRepository.save(blacklistedToken);
        System.out.println("Blacklisted Token: " + token);
    }
    
    
    
    }