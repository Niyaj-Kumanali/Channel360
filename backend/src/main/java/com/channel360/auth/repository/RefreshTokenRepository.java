package com.channel360.auth.repository;

import com.channel360.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUserId(Long userId);

    @Procedure("sp_refresh_tokens_save")
    void spSave(@Param("p_id") Long id, @Param("p_token") String token,
                @Param("p_user_id") Long userId, @Param("p_expiry_date") LocalDateTime expiryDate,
                @Param("p_revoked") Boolean revoked);

    @Procedure("sp_refresh_tokens_revoke")
    void spRevoke(@Param("p_token") String token);

    @Procedure("sp_refresh_tokens_delete_by_user_id")
    void spDeleteByUserId(@Param("p_user_id") Long userId);
}
