package com.jsp.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jsp.ecommerce.enums.UserRole;
import com.jsp.ecommerce.requestdto.AuthRequest;
import com.jsp.ecommerce.requestdto.OtpVerificationRequest;
import com.jsp.ecommerce.requestdto.UserRequest;
import com.jsp.ecommerce.responsedto.AuthResponse;
import com.jsp.ecommerce.responsedto.LogoutResponse;
import com.jsp.ecommerce.responsedto.UserResponse;
import com.jsp.ecommerce.security.JwtService;
import com.jsp.ecommerce.service.UserService;
import com.jsp.ecommerce.utility.ResponseStructure;

import jakarta.mail.AuthenticationFailedException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class UserController {
	
	private final UserService userService;

	@PostMapping("/sellers/register")
	public ResponseEntity<ResponseStructure<UserResponse>> addSeller(@Valid @RequestBody UserRequest userRequest) {
		return userService.addUser(userRequest, UserRole.SELLER);
	}

	@PostMapping("/customers/register")
	public ResponseEntity<ResponseStructure<UserResponse>> addCustomer(@Valid @RequestBody UserRequest userRequest)  {
		return userService.addUser(userRequest, UserRole.CUSTOMER);
	}
	@PostMapping("users/otpCerification")
	public ResponseEntity<ResponseStructure<UserResponse>>  verifyUser(@RequestBody OtpVerificationRequest otpVerificationRequest){
		return userService.verifyUser(otpVerificationRequest);
	}
	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<AuthResponse>> login(@RequestBody AuthRequest authRequest)   {
		return	userService.login(authRequest);
	}
    @PostMapping("/test")
    @PreAuthorize("hasAuthority('CUSTOMER')")
	public String test() {
		return  "sucess";
	}
   @PostMapping("/refreshlogin") 
  public ResponseEntity<ResponseStructure<AuthResponse>> refreshLogin(@CookieValue(value="rt",required=false) String refreshToken){
	return userService.refreshLogin(refreshToken);  
  }
   @PostMapping("/logout")
   public ResponseEntity<ResponseStructure<LogoutResponse>> logout(@CookieValue(value="rt",required=false) String refreshToken,@CookieValue(value="at",required=false) String accessToken){
	   return userService.logout(refreshToken,accessToken);
   }
   @PostMapping("/logoutFromOtherDevices")
   public ResponseEntity<LogoutResponse> logoutFromOtherDevices(@CookieValue(value="rt",required = false) String refreshToken,@CookieValue(value="at",required=false) String accessToken){
	   return userService.logoutFromOtherDevices(refreshToken,accessToken);
   }
   
   @PostMapping("/logoutFromAllDevices")
   public ResponseEntity<ResponseStructure<LogoutResponse>> logoutFromAllDevices( @CookieValue(value="rt",required=false) String refreshToken, @CookieValue(value="at",required=false) String accessToken ){
	   return userService.logoutFromAllDevices(refreshToken,accessToken);
   }

}
