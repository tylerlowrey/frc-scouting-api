package com.tylerlowrey.repositories

import com.tylerlowrey.models.AccessToken
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface AccessTokenRepository: CrudRepository<AccessToken, Long> {
    fun getAccessTokensByUserId(userId: Long): List<AccessToken>
    @Query("UPDATE AccessToken token SET token.valid = false WHERE token.id = :tokenId")
    fun updateAccessTokenAsInvalid(tokenId: String)
}