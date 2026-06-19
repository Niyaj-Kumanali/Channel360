package com.channel360.auth.repository;

import com.channel360.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUserId(Long userId);

    @Procedure("sp_refresh_tokens_save")
    Long spSave(Long id, String token, Long userId, LocalDateTime expiryDate, Boolean revoked);

    @Procedure("sp_refresh_tokens_revoke")
    void spRevoke(String token);

    @Procedure("sp_refresh_tokens_delete_by_user_id")
    void spDeleteByUserId(Long userId);
}
