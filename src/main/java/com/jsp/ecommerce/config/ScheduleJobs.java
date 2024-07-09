package com.jsp.ecommerce.config;

import com.jsp.ecommerce.repo.AccessTokenRepo;
import com.jsp.ecommerce.repo.RefreshTokenRepo;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Configuration
@EnableScheduling
@AllArgsConstructor
public class ScheduleJobs {
	
    private final AccessTokenRepo accessTokenRepository;
    private final RefreshTokenRepo refreshTokenRepository;

    @Scheduled(fixedDelay = 300000L) // after 5 minutes again and again start
    public void cleanExpiredAccessToken() {
        accessTokenRepository.findByExpirationBefore(LocalDateTime.now())
                .forEach(accessTokenRepository::delete);
    }

    @Scheduled(fixedDelay = 300000L) // after 5 minutes again and again start
    public void cleanExpiredRefreshToken() {
        refreshTokenRepository.findByExpirationBefore(LocalDateTime.now())
                .forEach(refreshTokenRepository::delete);
    }
}

