package com.lanos_platform.security.Request;

import lombok.Data;

@Data
public class SignupRequest {
	
	private String userName;
    private String email;
    private String password;
	
}
