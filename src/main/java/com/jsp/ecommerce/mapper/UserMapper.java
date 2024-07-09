package com.jsp.ecommerce.mapper;



import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.jsp.ecommerce.entity.User;
import com.jsp.ecommerce.requestdto.UserRequest;
import com.jsp.ecommerce.responsedto.UserResponse;

import lombok.AllArgsConstructor;
@Component
@AllArgsConstructor
public class UserMapper{
 
	private final PasswordEncoder passwordEncoder;
		public User mapToUser(UserRequest userRequest, User user) {
			user.setEmail(userRequest.getEmail());
			user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
			return user;
			
			
		}
		
		public UserResponse mapToUserResponse(User user) {
			return UserResponse.builder()
			.userId(user.getUserId())
			.username(user.getUsername())
			.email(user.getEmail())
			.isEmailVerified(user.isEmailVerified())
			.userRole(user.getUserRole())
			
			.build();

}
}
