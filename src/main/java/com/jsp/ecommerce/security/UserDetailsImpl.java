package com.jsp.ecommerce.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.jsp.ecommerce.entity.User;

import lombok.AllArgsConstructor;

@SuppressWarnings("serial")
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
	
	private final User user;
  
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(user.getUserRole().name()));
		
	}
	@Override
	public String getUsername() {
		return user.getUsername();
	}
	@Override
	public String getPassword() {
		return user.getPassword();
	}

	



}
