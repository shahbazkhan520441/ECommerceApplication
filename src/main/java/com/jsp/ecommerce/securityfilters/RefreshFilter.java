package com.jsp.ecommerce.securityfilters;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.jsp.ecommerce.entity.RefreshToken;
import com.jsp.ecommerce.entity.User;
import com.jsp.ecommerce.enums.UserRole;
import com.jsp.ecommerce.repo.RefreshTokenRepo;
import com.jsp.ecommerce.security.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@Component
public class RefreshFilter extends OncePerRequestFilter {

	private final RefreshTokenRepo refreshTokenRepo;
	private final JwtService jwtService;


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String rt=null;

		if(request.getCookies()!=null) {
			for(Cookie cookie:request.getCookies()) {
				if(cookie.getName().equals("rt")) {
					rt=cookie.getValue();
					break;
				}

			}
		}
		if(rt != null) {
			System.out.println("in refresh filter");
			RefreshToken	exrefreshToken = refreshTokenRepo.findByToken(rt).get();
			if( exrefreshToken.isBlocked()) {
				TokenExceptionHandler.tokenExceptionHandler(HttpStatus.UNAUTHORIZED.value(), "invalid token", response); 
			}
			else {
				String username = jwtService.extractUsername(rt);
				UserRole userRole = jwtService.extractUserRole(rt);

				if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(username,null,
							List.of(new SimpleGrantedAuthority(userRole.name().toString())));
					authenticationToken.setDetails(new WebAuthenticationDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				}
			}

		}
		filterChain.doFilter(request, response);	
	}

}
