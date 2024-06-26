package com.jsp.ecommerce.service;

import org.springframework.http.ResponseEntity;

import com.jsp.ecommerce.enums.UserRole;
import com.jsp.ecommerce.requestdto.OtpVerificationRequest;
import com.jsp.ecommerce.requestdto.UserRequest;
import com.jsp.ecommerce.responsedto.UserResponse;
import com.jsp.ecommerce.utility.ResponseStructure;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

public interface UserService {

	ResponseEntity<ResponseStructure<UserResponse>> addUser(@Valid UserRequest userRequest, UserRole userRole) ;

	ResponseEntity<ResponseStructure<UserResponse>> verifyUser( OtpVerificationRequest otpVerificationRequest);

	

}
