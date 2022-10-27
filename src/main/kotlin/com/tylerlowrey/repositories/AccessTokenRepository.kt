package com.tylerlowrey.repositories

import com.tylerlowrey.models.AccessToken
import org.springframework.data.repository.CrudRepository

interface AccessTokenRepository: CrudRepository<AccessToken, Long> {
    fun getAccessTokensByUserId(userId: Long): List<AccessToken>
}