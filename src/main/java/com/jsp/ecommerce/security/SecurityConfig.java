package com.jsp.ecommerce.security;
import com.jsp.ecommerce.repo.AccessTokenRepo;
import com.jsp.ecommerce.repo.RefreshTokenRepo;
import com.jsp.ecommerce.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jsp.ecommerce.securityfilters.JwtAuthFilter;
import com.jsp.ecommerce.securityfilters.LoginFilter;
import com.jsp.ecommerce.securityfilters.RefreshFilter;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {
	
	// /api/v1/logout

	private final JwtService jwtservice;
	private final RefreshTokenRepo refreshTokenRepo;
	private final AccessTokenRepo accessTokenRepo;
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);// uses bycript algorithem to incript the password
	}

	
	@Order(3)
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		// csrf is security pattern which will provide security
		return httpSecurity.csrf(AbstractHttpConfigurer::disable)
				.securityMatchers(match-> match.requestMatchers("/api/v1/**"))
				.authorizeHttpRequests(authorize -> authorize
						                            .anyRequest()
						                           .authenticated())
				.sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(new JwtAuthFilter(jwtservice,accessTokenRepo), UsernamePasswordAuthenticationFilter.class)
				.build();
	}
	
	
	@Order(1)
	@Bean
	SecurityFilterChain loginFilterChain(HttpSecurity httpSecurity) throws Exception {	
	return	httpSecurity.csrf(csrf-> csrf.disable())
				.securityMatchers(match-> match.requestMatchers(
						"/api/v1/login/**",
						"/api/v1/customers/register/**",
						"/api/v1/seller/register/**",
						"/api/v1/users/otpCerification/**"))
				.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
				.sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(new LoginFilter(), UsernamePasswordAuthenticationFilter.class)
				.build();
	}
	
	@Order(2)
	@Bean
	SecurityFilterChain refreshFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity.securityMatchers(match-> match.requestMatchers("/api/v1/refreshlogin/**"))
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
				.sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(new RefreshFilter(refreshTokenRepo, jwtservice), UsernamePasswordAuthenticationFilter.class)
				.build();
	}
	
	@Bean
	AuthenticationManager getAuthenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	

}
