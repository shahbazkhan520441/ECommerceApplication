package com.jsp.ecommerce.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.jsp.ecommerce.entity.User;

public interface UserRepo extends JpaRepository<User, Integer>{

	boolean existsByEmail(String email);

	Optional<User> findByUsername(String username);


	

}
