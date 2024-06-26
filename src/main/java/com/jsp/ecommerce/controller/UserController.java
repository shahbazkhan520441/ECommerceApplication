package com.jsp.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jsp.ecommerce.enums.UserRole;
import com.jsp.ecommerce.requestdto.OtpVerificationRequest;
import com.jsp.ecommerce.requestdto.UserRequest;
import com.jsp.ecommerce.responsedto.UserResponse;
import com.jsp.ecommerce.service.UserService;
import com.jsp.ecommerce.utility.ResponseStructure;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class UserController {
	private UserService userService;
	
    @PostMapping("/sellers/register")
    public ResponseEntity<ResponseStructure<UserResponse>> addSeller(@Valid @RequestBody UserRequest userRequest) {
        return userService.addUser(userRequest, UserRole.SELLER);
    }

    @PostMapping("/customers/register")
    public ResponseEntity<ResponseStructure<UserResponse>> addCustomer(@Valid @RequestBody UserRequest userRequest)  {
        return userService.addUser(userRequest, UserRole.CUSTOMER);
    }
    @PostMapping("/user/otp")
    public ResponseEntity<ResponseStructure<UserResponse>>  verifyUser(@RequestBody OtpVerificationRequest otpVerificationRequest){
    	return userService.verifyUser(otpVerificationRequest);
    }

}
