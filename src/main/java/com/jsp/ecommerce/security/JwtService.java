package com.jsp.ecommerce.security;

import java.security.Key;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jsp.ecommerce.enums.UserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	@Value("${application.jwt.seceret}")
	private String seceret;
	private static final String ROLE="role";


	public String createJwtToken(String username, Long experationTimeInMillis,UserRole role) {
		return Jwts.builder()
				.setClaims(Map.of(ROLE,role))
				.setSubject(username)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + experationTimeInMillis))
				.signWith(getsignInKey(), SignatureAlgorithm.HS512).compact();

	}
	public UserRole extractUserRole(String token) {
	String role= ParsejwtToken(token).get(ROLE,String.class );
	return UserRole.valueOf(role);
	}

	private Key getsignInKey() {
		
		byte[] key = Decoders.BASE64.decode(seceret);
      return Keys.hmacShaKeyFor(key);
	}
	//	for authenticating user token 
	private  Claims ParsejwtToken(String token){
		return	Jwts.parserBuilder()              //<builder>
				.setSigningKey(getsignInKey())
				.build()                              //<JWTPARSER> THIS METHOD RETURNS JWTPARSER CLASS TYPE OBJECT
				.parseClaimsJws(token).getBody();
	}

	public String extractUsername(String token) {
		return	ParsejwtToken(token).getSubject();
	}
	public Date getExpirationDate(String token) {
		return ParsejwtToken(token).getExpiration();
	}
	public Date extractIssueDate(String token) {
		return ParsejwtToken(token).getIssuedAt();
	}
}
