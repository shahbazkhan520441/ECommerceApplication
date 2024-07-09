package com.jsp.ecommerce.responsedto;

import com.jsp.ecommerce.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class AuthResponse {
private int userId;
private String username;
private String role; 
private long accesstokenExpiration;
private long refreshtokenExpiration;

}
