package com.jsp.ecommerce.securityfilters;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.catalina.authenticator.SpnegoAuthenticator.AcceptAction;
import org.checkerframework.checker.units.qual.Acceleration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jsp.ecommerce.entity.AccessToken;
import com.jsp.ecommerce.entity.User;
import com.jsp.ecommerce.enums.UserRole;
import com.jsp.ecommerce.exception.InvalidJwtException;
import com.jsp.ecommerce.exception.JwtExpiredException;
import com.jsp.ecommerce.repo.AccessTokenRepo;
import com.jsp.ecommerce.security.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {// jwt token authentication filter to authenticate the token
															// which we get from user

	private final JwtService jwtservice;
    private final AccessTokenRepo accessTokenRepo;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		System.out.println("in filter ");
//		String token = request.getHeader("Authorization");// get token from postman or user for authetiacation of user
//	second way for cookie based authetication 	
	
		String at=null;
		if(request.getCookies()!=null) {
	    for (Cookie cookie : request.getCookies()) {
	    	if(cookie.getName().equals("at")) 
	    		 at=cookie.getValue();
	    	     break;    	
	    }
	    if(at==null) {
	    	TokenExceptionHandler.tokenExceptionHandler(HttpStatus.FORBIDDEN.value(),"the token is already expired" ,response );	
	    }
	    
		}
		AccessToken accessToken=null;
		if (at != null) {
			System.out.println(" inside first if");
			 Optional<AccessToken> exat = accessTokenRepo.findByToken(at);
				if(exat.isPresent()){
					 accessToken = exat.get();
					
				}
				if(accessToken.isBlocked()) {
					TokenExceptionHandler.tokenExceptionHandler(HttpStatus.FORBIDDEN.value(),"the token is already expired" ,response );
				}
			
				
			
			try {
				System.out.println("inside try");
				Date expiryDate=jwtservice.getExpirationDate(at);
				System.out.println("after expiry date");
				String username = jwtservice.extractUsername(at);
				System.out.println("after extracting user name");
				System.out.println("after username");
				UserRole userRole=jwtservice.extractUserRole(at);
				System.out.println("out side last of try");
				if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					System.out.println("before authentication in if");
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
							username, null, List.of(new SimpleGrantedAuthority(userRole.name().toString()))); // username,password,authoraity
					System.out.println("after authentication");
					authenticationToken.setDetails(new WebAuthenticationDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				}

			} catch (ExpiredJwtException ex) {

	                TokenExceptionHandler.tokenExceptionHandler(HttpStatus.UNAUTHORIZED.value(),"the token is already expired" ,response );
	                
			} catch (JwtException ex) {

				   TokenExceptionHandler.tokenExceptionHandler(HttpStatus.UNAUTHORIZED.value(),"Invalid JWT token exception" ,response );
				   
			}
		}
		System.out.println("out jwt filter chain");
		filterChain.doFilter(request, response);

	}

}

