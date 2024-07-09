package com.jsp.ecommerce.service;

import org.springframework.http.ResponseEntity;

import com.jsp.ecommerce.enums.UserRole;
import com.jsp.ecommerce.requestdto.AuthRequest;
import com.jsp.ecommerce.requestdto.OtpVerificationRequest;
import com.jsp.ecommerce.requestdto.UserRequest;
import com.jsp.ecommerce.responsedto.AuthResponse;
import com.jsp.ecommerce.responsedto.LogoutResponse;
import com.jsp.ecommerce.responsedto.UserResponse;
import com.jsp.ecommerce.utility.ResponseStructure;

import jakarta.mail.AuthenticationFailedException;
import jakarta.validation.Valid;

public interface UserService {

	ResponseEntity<ResponseStructure<UserResponse>> addUser(@Valid UserRequest userRequest, UserRole userRole);

	ResponseEntity<ResponseStructure<UserResponse>> verifyUser(OtpVerificationRequest otpVerificationRequest);

	public ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest authRequest);

	ResponseEntity<ResponseStructure<AuthResponse>> refreshLogin(String refreshToken);

	ResponseEntity<ResponseStructure<LogoutResponse>> logout(String refreshToken, String accessToken);

	ResponseEntity<ResponseStructure<LogoutResponse>> logoutFromAllDevices(String refreshToken, String accessToken);

	ResponseEntity<LogoutResponse> logoutFromOtherDevices(String refreshToken, String accessToken);

}
