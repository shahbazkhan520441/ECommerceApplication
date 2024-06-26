package com.jsp.ecommerce.mapper;



import org.springframework.stereotype.Component;

import com.jsp.ecommerce.entity.User;
import com.jsp.ecommerce.requestdto.UserRequest;
import com.jsp.ecommerce.responsedto.UserResponse;
@Component
public class UserMapper{
	
	
		public User mapToUser(UserRequest userRequest, User user) {
			user.setUsername(userRequest.getUsername());
			user.setEmail(userRequest.getEmail());
			user.setPassword((userRequest.getPassword()));
			return user;
			
			
		}
		
		public UserResponse mapToUserResponse(User user) {
			return UserResponse.builder()
			.userId(user.getUserId())
			.username(user.getUsername())
			.email(user.getEmail())
			.userRole(user.getUserRole())
			
			.build();

}
}
