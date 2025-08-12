package com.lanos_platform.security.Repository;

import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;

import com.lanos_platform.security.Modal.User;


public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	boolean existsByUserName(String string);

	
	

}
