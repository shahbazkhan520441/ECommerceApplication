package com.jsp.ecommerce.requestdto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UserRequest {
	@NotNull(message = "username not be null")
	@NotBlank(message="username not be blank")
	private String username;
	@Email(regexp =  "[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}", message = "invalid email ")
	private String email;
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must"
+ " contain at least one letter, one number, one special character")
	private String password;

}
