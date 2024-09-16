package com.pn.career.repositories;

import com.pn.career.models.Token;
import com.pn.career.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    List<Token> findByUser(User user);
    Token findByRefreshToken(String token);
    Optional<Token> findByTokenAndTokenType(String token, String tokenType);
    @Modifying
    @Transactional
    @Query("UPDATE Token t SET t.revoked = true, t.expired = true WHERE t.user.userId = :userId AND t.tokenType = :tokenType AND (t.expired = false OR t.revoked = false)")
    void revokeAllUserTokens(int userId, String tokenType);
}
