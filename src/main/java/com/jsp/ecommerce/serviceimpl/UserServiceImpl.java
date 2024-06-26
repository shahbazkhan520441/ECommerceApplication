package com.jsp.ecommerce.serviceimpl;

import java.util.Date;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.jsp.ecommerce.entity.Customer;
import com.jsp.ecommerce.entity.Seller;
import com.jsp.ecommerce.entity.User;
import com.jsp.ecommerce.enums.UserRole;
import com.jsp.ecommerce.mailservice.MailService;
import com.jsp.ecommerce.mapper.UserMapper;
import com.jsp.ecommerce.repo.CustomerRepo;
import com.jsp.ecommerce.repo.SellerRepo;
import com.jsp.ecommerce.repo.UserRepo;
import com.jsp.ecommerce.requestdto.OtpVerificationRequest;
import com.jsp.ecommerce.requestdto.UserRequest;
import com.jsp.ecommerce.responsedto.UserResponse;
import com.jsp.ecommerce.service.UserService;
import com.jsp.ecommerce.utility.MessageData;
import com.jsp.ecommerce.utility.ResponseStructure;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepo userRepo;

	private final UserMapper userMapper;

	private final CustomerRepo customerRepo;

	private final SellerRepo sellerRepo;
	
	private final Cache<String, User> userCache;
	
	private final Cache<String, String> otpCache;
	
	private final Random random;
	
	private final MailService mailService;
	
	
	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> addUser(UserRequest userRequest, UserRole userRole) {
		User user = null;
		MessageData messageData = new MessageData();

		switch (userRole) {

		case SELLER -> user = new Seller();
		case CUSTOMER -> user = new Customer();
		
		}
		
		if(user != null) {
			user = userMapper.mapToUser(userRequest, user);
		}
		
		int num = random.nextInt(100000, 999999);
		String otpValue = String.valueOf(num);
		userCache.put(user.getEmail(), user);
		otpCache.put(user.getEmail(), otpValue);
		
		messageData.setTo(user.getEmail());
		messageData.setSubject("Verify your email using otp");
		messageData.setSentDate(new Date());
		messageData.setText("Your otp "+otpValue);
		
		try {
			mailService.sendMail(messageData);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.status(HttpStatus.ACCEPTED)
				.body(new ResponseStructure<UserResponse>().setStatus(HttpStatus.ACCEPTED.value())
						.setMessage("User created").setData(userMapper.mapToUserResponse(user)));

	}
		

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> verifyUser(OtpVerificationRequest otpVerificationRequest) {
		boolean existsByEmail = userRepo.existsByEmail(otpVerificationRequest.getEmail());
//		if(existsByEmail) {
//			throw new IllegalStateException("email id is alreday used use differnet email to register");
//		}
   User user=userCache.getIfPresent(otpVerificationRequest.getEmail());
   String otp=otpCache.getIfPresent(otpVerificationRequest.getEmail());
   String email=user.getEmail();
   if(email==null) {
	   throw new IllegalArgumentException("invalid email address");
   }
   else if(otp==null) {
	   return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseStructure<UserResponse>()
			                                                         .setStatus(HttpStatus.NOT_FOUND.value())
			                                                         .setMessage("otp expiered"));
			                                                         
   }
   int atIndex=email.indexOf("@");
   String name=email.substring(0, atIndex);
   
   if(otpVerificationRequest.getOtp().equals(otp)) {
	   
	   user.setUsername(name);
	   user.setEmailVerified(true);
	   userRepo.save(user);
	   return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseStructure<UserResponse>()
               .setStatus(HttpStatus.CREATED.value())
               .setMessage(user.getUserRole()+" saved sucessfully")
               .setData(userMapper.mapToUserResponse(user)));
   }
   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseStructure<UserResponse>()
           .setStatus(HttpStatus.BAD_REQUEST.value())
           .setMessage(user.getUserRole()+" invalid otp"));
   
	}


	}
