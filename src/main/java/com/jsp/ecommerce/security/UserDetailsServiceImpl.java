package com.jsp.ecommerce.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jsp.ecommerce.repo.UserRepo;

import lombok.AllArgsConstructor;
@Service
@AllArgsConstructor
public class UserDetailsServiceImpl  implements UserDetailsService{

	private final UserRepo userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return  userRepo.findByUsername(username)
				.map(UserDetailsImpl::new)
				.orElseThrow(()->new UsernameNotFoundException("username not found"));
	}



}
