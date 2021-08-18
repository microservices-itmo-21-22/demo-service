package com.itmo.microservices.demo.auth.impl.service

import com.itmo.microservices.demo.auth.impl.config.SecurityProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.util.*

@Component
class JwtTokenManager(private val properties: SecurityProperties) {

    /**
     * Validate received token & check if it matches with user details
     * @param token Token data
     * @return User info retrieved from token
     */
    fun readAccessToken(token: String): UserDetails {
        val username = getUsernameFromToken(token)
        val type = getClaimFromToken(token) { it["type"] }
        if (TokenType.ACCESS.name.lowercase(Locale.getDefault()) != type)
            throw IllegalArgumentException("Token is not of ACCESS type")
        return User(username, token, mutableListOf(SimpleGrantedAuthority("ACCESS")))
    }

    /**
     * Validate received refresh token & check if it matches with user details
     * @param token Token data
     * @return User info retrieved from token
     */
    fun readRefreshToken(token: String): UserDetails {
        val username = getUsernameFromToken(token)
        val type = getClaimFromToken(token) { it["type"] }
        if (TokenType.REFRESH.name.lowercase(Locale.getDefault()) != type)
            throw IllegalArgumentException("Token is not of REFRESH type")
        return User(username, token, mutableListOf(SimpleGrantedAuthority("REFRESH")))
    }

    fun <T> getClaimFromToken(token: String, claimsResolver: (Claims) -> T): T {
        return claimsResolver(getAllClaimsFromToken(token))
    }

    //retrieve username from jwt token
    fun getUsernameFromToken(token: String): String {
        return getClaimFromToken(token) { it.subject }
    }

    //retrieve roles from jwt token
    fun getRolesFromToken(token: String): List<String> {
        return getClaimFromToken(token) { claims ->
            ((claims.get("roles", List::class.java) ?: emptyList<String>()) as List<*>)
                    .filterIsInstance(String::class.java)
        }
    }

    //retrieve expiration date from jwt token
    fun getExpirationDateFromToken(token: String): Date {
        return getClaimFromToken(token) { it.expiration }
    }

    //check if the token has expired
    private fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    /**
     * Generate token for user specified by UserDetails
     * @param userDetails User information
     * @return Generated token
     */
    fun generateToken(userDetails: UserDetails): String {
        return doGenerateToken(userDetails, TokenType.ACCESS,
                properties.tokenLifetime)
    }

    /**
     * Generate refresh token for user specified by UserDetails
     * @param userDetails User information
     * @return Generated token
     */
    fun generateRefreshToken(userDetails: UserDetails): String {
        return doGenerateToken(userDetails, TokenType.REFRESH,
                properties.refreshTokenLifetime)
    }

    //for retrieving any information from token we will need the secret key
    private fun getAllClaimsFromToken(token: String): Claims = Jwts.parser()
            .setSigningKey(properties.secret)
            .parseClaimsJws(token)
            .body

    private fun doGenerateToken(userDetails: UserDetails,
                                type: TokenType,
                                tokenTTL: Duration): String =
            Jwts.builder()
                    .claim("type", type.name.lowercase(Locale.getDefault()))
                    .setSubject(userDetails.username)
                    .setIssuedAt(Date())
                    .setExpiration(Date.from(Instant.now().plus(tokenTTL)))
                    .claim("roles", userDetails.authorities.map { it.authority })
                    .signWith(SignatureAlgorithm.HS512, properties.secret)
                    .compact()

    private enum class TokenType {
        ACCESS, REFRESH
    }
}