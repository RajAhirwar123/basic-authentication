package com.lanos_platform.security.Modal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class BlacklistedToken {
    @Id
    private String token;
}