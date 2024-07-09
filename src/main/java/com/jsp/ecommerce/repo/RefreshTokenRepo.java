package com.jsp.ecommerce.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.ecommerce.entity.RefreshToken;
import com.jsp.ecommerce.entity.User;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Integer> {

public Optional<RefreshToken> findByToken(String refreshToken);

public List<RefreshToken> findByUserAndIsBlockedAndTokenNot(User user, boolean b, String refreshToken);

public  List<RefreshToken> findByUserAndIsBlocked(User user, boolean b);

public List<RefreshToken> findByExpirationBefore(LocalDateTime now);
}
