package com.jsp.ecommerce.responsedto;

import com.jsp.ecommerce.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
@Builder
public class UserResponse {
	
	private int userId;
	private String username;
	private String email;
	private UserRole userRole;
	private boolean isEmailVerified;
	private boolean isDeleted;

}
