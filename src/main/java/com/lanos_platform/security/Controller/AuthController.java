package com.lanos_platform.security.Controller;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lanos_platform.security.Payload.UserDTO;
import com.lanos_platform.security.Request.LoginRequest;
import com.lanos_platform.security.Request.SignupRequest;
import com.lanos_platform.security.Response.ApiResponse;
import com.lanos_platform.security.Response.UserResponse;
//import com.lanos_platform.security.Response.AuthResponse;
import com.lanos_platform.security.Services.AuthService;
import com.lanos_platform.security.jwt.JwtUtils;
//import com.lanos_platform.security.jwt.JwtUtils;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtUtils jwtUtils;
 
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> register(@RequestBody SignupRequest request) {
        String message = authService.registerUser(
                request.getUserName(), 
                request.getEmail(), 
                request.getPassword() 
            
            );
        UserResponse response = new UserResponse(
            message.contains("successfully"), 
            message
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        try {
            UserDTO userDTO = authService.loginUser(request.getEmail(), request.getPassword());
            ApiResponse response = new ApiResponse(true, "Login successful", userDTO); // UserDTO data mein add kiya
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error occurred";
            ApiResponse response = new ApiResponse(false, errorMessage, null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<UserResponse> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logoutUser(token);
            UserResponse response = new UserResponse(true, "Logout successful");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        UserResponse response = new UserResponse(false, "Invalid or missing token");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @GetMapping("/validate")
public ResponseEntity<Boolean> validate(@RequestHeader("Authorization") String token) {
    try {
        String jwt = token.substring(7);
        jwtUtils.validateJwtToken(jwt);
        return ResponseEntity.ok(true);
    } catch (Exception e) {
        return ResponseEntity.ok(false);
    }
}
  
}

//    @PostMapping("/logout")
//    public ResponseEntity<ApiResponse> logout() {
//        ApiResponse response = new ApiResponse(true, "Logout successful", null);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
