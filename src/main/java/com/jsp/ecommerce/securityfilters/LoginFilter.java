package com.jsp.ecommerce.securityfilters;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		System.out.println("in login filter");
		boolean loggedIn=false;
		if(request.getCookies()!= null)
		{
			for(Cookie cookie: request.getCookies())
			{
				if ("at".equals(cookie.getName()) || "rt".equals(cookie.getName())) {
					loggedIn=true;
                }
			}
		}
		if (loggedIn) {
			
		TokenExceptionHandler.tokenExceptionHandler(HttpStatus.BAD_REQUEST.value(), "User is already logged in. Token is already present", response);
		
		}
		
		else {
		// If no token is found, proceed with the filter chain
        filterChain.doFilter(request, response);
		}
	}
	}


