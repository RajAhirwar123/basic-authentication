package com.lanos_platform.security.Response;

import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
	
    private boolean success;
    private String message;
    private Object data;
}
