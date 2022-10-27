package com.tylerlowrey.repositories

import com.tylerlowrey.models.RefreshToken
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface RefreshTokenRepository: CrudRepository<RefreshToken, Long> {
    fun getRefreshTokensByUserId(userId: Long): List<RefreshToken>
    @Query("UPDATE RefreshToken token SET token.valid = false WHERE token.token = :tokenValue")
    fun updatedRefreshTokenAsInvalid(@Param("tokenValue") token: String)
}
