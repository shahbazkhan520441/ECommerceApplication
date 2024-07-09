package com.jsp.ecommerce.repo;

import java.util.List;
import java.util.Optional;

import org.apache.catalina.authenticator.SpnegoAuthenticator.AcceptAction;
import org.checkerframework.checker.units.qual.Acceleration;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.ecommerce.entity.AccessToken;
import com.jsp.ecommerce.entity.User;

public interface AccessTokenRepo extends JpaRepository<AccessToken, Integer> {

	Optional<AccessToken> findByToken(String at);

	List<AccessToken> findByUserAndIsBlocked(User user,boolean isBlocked);
	List<AccessToken> findByUserAndIsBlockedAndTokenNot(User user,boolean isBlocked,String accesstoken);
}
