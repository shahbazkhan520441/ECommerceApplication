package com.jsp.ecommerce.serviceimpl;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseCookie.ResponseCookieBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.jsp.ecommerce.entity.AccessToken;
import com.jsp.ecommerce.entity.Customer;
import com.jsp.ecommerce.entity.RefreshToken;
import com.jsp.ecommerce.entity.Seller;
import com.jsp.ecommerce.entity.User;
import com.jsp.ecommerce.enums.UserRole;
import com.jsp.ecommerce.exception.InvalidRefreshToken;
import com.jsp.ecommerce.mailservice.MailService;
import com.jsp.ecommerce.mapper.UserMapper;
import com.jsp.ecommerce.repo.AccessTokenRepo;
import com.jsp.ecommerce.repo.RefreshTokenRepo;
import com.jsp.ecommerce.repo.UserRepo;
import com.jsp.ecommerce.requestdto.AuthRequest;
import com.jsp.ecommerce.requestdto.OtpVerificationRequest;
import com.jsp.ecommerce.requestdto.UserRequest;
import com.jsp.ecommerce.responsedto.AuthResponse;
import com.jsp.ecommerce.responsedto.LogoutResponse;
import com.jsp.ecommerce.responsedto.UserResponse;
import com.jsp.ecommerce.security.JwtService;
import com.jsp.ecommerce.service.UserService;
import com.jsp.ecommerce.utility.MessageData;
import com.jsp.ecommerce.utility.ResponseStructure;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepo userRepo;
	private final UserMapper userMapper;
	private final Cache<String, User> userCache;
	private final Cache<String, String> otpCache;
	private final Random random;
	private final MailService mailService;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final AccessTokenRepo accessTokenRepo;
	private final RefreshTokenRepo refreshTokenRepo;
//-------------------------------------------------------------------------------------------------------------------------------
	@Value("${application.jwt.access_expiry_seconds}")
	private long accessExpirySeconds;

	@Value("${application.jwt.refresh_expiry_seconds}")
	private long refreshExpirySeconds;

	@Value("${application.cookie.domain}")
	private String domain;

	@Value("${application.cookie.same-site}")
	private String sameSite;

	@Value("${application.cookie.secure}")
	private boolean secure;
//-------------------------------------------------------------------------------------------------------------------------------
	public UserServiceImpl(UserRepo userRepo, UserMapper userMapper, Cache<String, User> userCache,
			Cache<String, String> otpCache, Random random, MailService mailService,
			AuthenticationManager authenticationManager, JwtService jwtService, AccessTokenRepo accessTokenRepo,
			RefreshTokenRepo refreshTokenRepo) {
		super();
		this.userRepo = userRepo;
		this.userMapper = userMapper;
		this.userCache = userCache;
		this.otpCache = otpCache;
		this.random = random;
		this.mailService = mailService;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.accessTokenRepo = accessTokenRepo;
		this.refreshTokenRepo = refreshTokenRepo;
	}
	
//-------------------------------------------------------------------------------------------------------------------------------
	
	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> addUser(UserRequest userRequest, UserRole userRole) {
		
		boolean existsByEmail = userRepo.existsByEmail(userRequest.getEmail());
		if (existsByEmail == true) {
			throw new IllegalStateException("email id is alreday used use differnet email to register");
		} else {
			User user = null;
			MessageData messageData = new MessageData();
	
			switch (userRole) {
	
			case SELLER -> user = new Seller();
			case CUSTOMER -> user = new Customer();
	
			}
	
			if (user != null) {
				user = userMapper.mapToUser(userRequest, user);
				String username = user.getEmail().split("@gmail.com")[0];
				user.setUsername(username);
				user.setUserRole(userRole);
			}
	
			int num = random.nextInt(100000, 999999);
			String otpValue = String.valueOf(num);
			userCache.put(user.getEmail(), user);
			otpCache.put(user.getEmail(), otpValue);
	
			messageData.setTo(user.getEmail());
			messageData.setSubject("Verify your email using otp");
			messageData.setSentDate(new Date());
			messageData.setText("otp: " + otpValue);
	
			try {
				mailService.sendMail(messageData);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
	
			return ResponseEntity
					.status(HttpStatus.ACCEPTED)
					.body(new ResponseStructure<UserResponse>()
				    .setStatus(HttpStatus.ACCEPTED.value())
					.setMessage("User created")
					.setData(userMapper.mapToUserResponse(user)));

		}
	}

//-------------------------------------------------------------------------------------------------------------------------------
	
	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> verifyUser(OtpVerificationRequest otpVerificationRequest) {
		MessageData messageData = new MessageData();
		System.out.println("in verfiy method");
		User user = userCache.getIfPresent(otpVerificationRequest.getEmail());
		String otp = otpCache.getIfPresent(otpVerificationRequest.getEmail());
		String email = user.getEmail();
		if (email == null) {
			throw new IllegalArgumentException("invalid email address");
		} else if (otp == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseStructure<UserResponse>()
					.setStatus(HttpStatus.NOT_FOUND.value()).setMessage("otp expiered"));
		
		}
		
		if (otpVerificationRequest.getOtp().equals(otp)) {
			user.setEmailVerified(true);
		
			userRepo.save(user);
			messageData.setTo(user.getEmail());
			messageData.setSubject("Verification sucessfull");
			messageData.setSentDate(new Date());
			messageData.setText("ecommerce:user as" + " " + user.getUsername() + " regitered sucessfully ");
		
			try {
				mailService.sendMail(messageData);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(new ResponseStructure<UserResponse>().setStatus(HttpStatus.CREATED.value())
							.setMessage(user.getUserRole() + " saved sucessfully")
							.setData(userMapper.mapToUserResponse(user)));
		} else
			throw new RuntimeException();

	}
	//-------------------------------------------------------------------------------------------------------------------------------
	
	public ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest authRequest) {
		
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
			if (authentication.isAuthenticated()) {
				return userRepo.findByUsername(authRequest.getUsername()).map(user -> {
					HttpHeaders httpHeaders = new HttpHeaders();
					granatAccessToken(httpHeaders, user);
					granatRefreshToken(httpHeaders, user);
	
					return	 ResponseEntity.ok()
							.headers(httpHeaders)
							.body(new ResponseStructure<AuthResponse>().setStatus(HttpStatus.OK.value())
									.setMessage("login sucessfull")
									.setData(AuthResponse.builder().username(user.getUsername())
											.userId(user.getUserId())
											.role(user.getUserRole().toString())
											.accesstokenExpiration(accessExpirySeconds)
											.refreshtokenExpiration(refreshExpirySeconds).build()));
				}).orElseThrow(() -> new UsernameNotFoundException("user not found"));
	
			} else
				throw new BadCredentialsException("invalid credinteals");
		} catch (AuthenticationException ex) {
			throw new BadCredentialsException("invalid credentilas");
		}
	}
	
//-------------------------------------------------------------------------------------------------------------------------------

	private void granatAccessToken(HttpHeaders httpheaders, User user) {
		String jwtToken = jwtService.createJwtToken(user.getUsername(), accessExpirySeconds * 1000, user.getUserRole());// 1 hr in milliseconds because we are
		// using date object from java.util
		// as return type
		AccessToken accessToken = new AccessToken();
		accessToken.setToken(jwtToken);
		accessToken.setBlocked(false);
		accessToken.setExpiration(LocalDateTime.now().plusSeconds(accessExpirySeconds)); // converting milliseconds to sec
		accessToken.setUser(user);
		accessTokenRepo.save(accessToken);

		httpheaders.add(HttpHeaders.SET_COOKIE, genrateCookie("at" , jwtToken, accessExpirySeconds));

	}
	
//-------------------------------------------------------------------------------------------------------------------------------

	private void granatRefreshToken(HttpHeaders httpheaders, User user) {
		String jwtToken = jwtService.createJwtToken(user.getUsername(), refreshExpirySeconds * 1000,user.getUserRole());// 1 hr in milliseconds

		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setToken(jwtToken);
		refreshToken.setBlocked(false);
		refreshToken.setExpiration(LocalDateTime.now().plusSeconds(refreshExpirySeconds )); // converting milliseconds to sec

		refreshToken.setUser(user);
		refreshTokenRepo.save(refreshToken);

		httpheaders.add(HttpHeaders.SET_COOKIE, genrateCookie("rt" , jwtToken, refreshExpirySeconds));
	}
	
//-------------------------------------------------------------------------------------------------------------------------------

	private String genrateCookie(String name, String value, long maxAge) {
		return ResponseCookie.from(name, value)
				.domain(domain)
				.path("/")
				.maxAge(maxAge)
				.sameSite(sameSite)// how can issue  cookie to particular browser 
				.httpOnly(true)
				.secure(secure).build().toString();
	}
	
//-------------------------------------------------------------------------------------------------------------------------------

	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> refreshLogin(String refreshToken) {
		Date expirationdate=jwtService.getExpirationDate(refreshToken);
	
		if(expirationdate.getTime() < new Date().getTime() || refreshToken == null || refreshToken.isEmpty()) {
			throw new InvalidRefreshToken("invalid token refresh token has expired please mae a new signin");
		}

		String username = jwtService.extractUsername(refreshToken);
		UserRole userRole = jwtService.extractUserRole(refreshToken);
		return  userRepo.findByUsername(username).map(exuser->{
			HttpHeaders httpHeaders=new HttpHeaders();
			granatAccessToken(httpHeaders, exuser);
         

//            List<AccessToken> allAT = accessTokenRepo.findAll();
//            for (AccessToken at : allAT) {
//                if (at.getExpiration().getSecond() * 1000 < new Date().getTime()) {
//                    accessTokenRepo.delete(at);
//                }
//            }
			return	 ResponseEntity.ok()
					.headers(httpHeaders)
					.body(new ResponseStructure<AuthResponse>().setStatus(HttpStatus.OK.value())
							.setMessage("refrsh login sucessfull")
							.setData(AuthResponse.builder().username(exuser.getUsername())
									.userId(exuser.getUserId())
									.role(exuser.getUserRole().toString())
									.accesstokenExpiration(accessExpirySeconds)
									.refreshtokenExpiration((expirationdate.getTime()-new Date().getTime())/1000).build()));
		}).orElseThrow(() -> new UsernameNotFoundException("user not found"));

	}
//-------------------------------------------------------------------------------------------------------------------------------

	@Override
	public ResponseEntity<ResponseStructure<LogoutResponse>> logout(String refreshToken, String accessToken) {
		AccessToken exaccesstoken=null;
	
		Optional<AccessToken> byToken = accessTokenRepo.findByToken(accessToken);
		if(byToken.isPresent()) {
			 exaccesstoken = byToken.get();
			exaccesstoken.setBlocked(true);
			accessTokenRepo.save(exaccesstoken);
			
		}
		User user=exaccesstoken.getUser();
		Optional<RefreshToken> byToken2 = refreshTokenRepo.findByToken(refreshToken);
		if(byToken2.isPresent()) {
			RefreshToken exrefreshtoken = byToken2.get();
			exrefreshtoken.setBlocked(true);
			refreshTokenRepo.save(exrefreshtoken);
		}
		HttpHeaders httpHeaders=new HttpHeaders();
		httpHeaders.add(HttpHeaders.SET_COOKIE, genrateCookie("rt" , null, 0));
		httpHeaders.add(HttpHeaders.SET_COOKIE, genrateCookie("at" , null, 0));
		
		return ResponseEntity.ok()
				.headers(httpHeaders)
				.body(new ResponseStructure<LogoutResponse>().setStatus(HttpStatus.OK.value())
						.setMessage("logout sucessfully"));
						
		
	}

	@Override
	public ResponseEntity<ResponseStructure<LogoutResponse>> logoutFromAllDevices(String refreshToken,
			String accessToken) {
		AccessToken exaccessToken = accessTokenRepo.findByToken(accessToken).get();
		User user = exaccessToken.getUser();
		accessTokenRepo.findByUserAndIsBlocked(user,false).forEach(exacesstoken->{
		exaccessToken.setBlocked(true);
		accessTokenRepo.save(exaccessToken);
		});
		
		refreshTokenRepo.findByUserAndIsBlocked(user,false).forEach(exrefrestoken->{
			exrefrestoken.setBlocked(true);
			refreshTokenRepo.save(exrefrestoken);
			});
		HttpHeaders httpHeaders=new HttpHeaders();
		httpHeaders.add(HttpHeaders.SET_COOKIE, genrateCookie("rt" , null, 0));
		httpHeaders.add(HttpHeaders.SET_COOKIE, genrateCookie("at" , null, 0));
		return ResponseEntity.ok()
				.headers(httpHeaders)
				.body(new ResponseStructure<LogoutResponse>()
						.setMessage("logout from all devices  sucessfully")
						.setStatus(HttpStatus.OK.value()));
		
	}

	@Override
	public ResponseEntity<LogoutResponse> logoutFromOtherDevices(String refreshToken,
			String accessToken) {
		   String username = jwtService.extractUsername(refreshToken);
           User user = userRepo.findByUsername(username).get();

           List<RefreshToken> listRT = refreshTokenRepo.findByUserAndIsBlockedAndTokenNot(user, false, refreshToken);
           List<AccessToken> listAT = accessTokenRepo.findByUserAndIsBlockedAndTokenNot(user, false, accessToken);
           listRT.forEach(rt->{
               rt.setBlocked(true);
               refreshTokenRepo.save(rt);
           });
           listAT.forEach(at->{
               at.setBlocked(true);
               accessTokenRepo.save(at);
           });
           return ResponseEntity.status(HttpStatus.OK).body(LogoutResponse.builder()
                   .status(HttpStatus.OK.value())
                   .message("Other Devices Logout done")
                   .build());
		

	}
	
	
}
