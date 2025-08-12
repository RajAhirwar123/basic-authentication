package com.lanos_platform.security.Request;

import lombok.Data;

@Data
public class LoginRequest {
	
    private String email;
   // private String userName;
    private String password;
}
