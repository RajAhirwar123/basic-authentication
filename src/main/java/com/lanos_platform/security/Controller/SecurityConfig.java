package com.lanos_platform.security.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.lanos_platform.security.Modal.User;
import com.lanos_platform.security.Repository.UserRepository;
import com.lanos_platform.security.Services.UserDetailsServiceImpl;
import com.lanos_platform.security.jwt.AuthTokenFilter;
import com.lanos_platform.security.jwt.JwtUtils;

@Configuration
@EnableWebSecurity
// @EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	  @Autowired
	    private UserDetailsServiceImpl userDetailsServiceImpl;

	    @Autowired
	    private JwtUtils jwtUtils;
	    
	    @Autowired
	    private UserRepository userRepository;

	    // ✅ `AuthTokenFilter` ka Bean Create Karo (Circular Dependency Fix)
	    @Bean
	    public AuthTokenFilter authTokenFilter() {
	        return new AuthTokenFilter(jwtUtils, userDetailsServiceImpl);
	    }

	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }

	    @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    	http
	        .sessionManagement(session -> session
	            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // ✅ Stateless Session
	        )
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/auth/signup", "/auth/login", "/auth/logout", "/auth/validate").permitAll() // सभी पब्लिक राउट्स
	            .requestMatchers("/h2-console/**").permitAll() // ✅ H2 Console Access
	            .anyRequest().authenticated() // ✅ Baaki sab authenticated users ke liye
	        )
	        
	        .headers(headers -> headers
	            .frameOptions(frameOptions -> frameOptions.sameOrigin()) // ✅ H2 Console ke liye
	        )
	        .csrf(csrf -> csrf.disable()) // ✅ CSRF Disable karo kyunki JWT use ho raha hai
	        .addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class); // ✅ JWT Token Filter Add Karo

	    return http.build();
	}
	    
	    // @Bean
	    // public CommandLineRunner initData(PasswordEncoder passwordEncoder) {
	    //     return args -> {
	            
	    //         // Create admin if not already present
	    //         if (!userRepository.existsByUserName("admin")) {
	            	
	    //             User admin = new User();
	    //             admin.setUserName("admin");
	    //             admin.setEmail("admin@lanos.com");
	    //             admin.setPassword(passwordEncoder.encode("admin@123"));
	          
	    //             userRepository.save(admin);
	    //         }
	    //     };
	    // }


	    @Bean
	    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
	        return authenticationConfiguration.getAuthenticationManager();
	    }
	}
