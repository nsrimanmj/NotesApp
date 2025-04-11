package com.sriman.notes.security

import com.sriman.notes.database.model.User
import com.sriman.notes.database.repository.UserRepository
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder
){
    data class TokenPair(
        val accessToken: String,
        val refreshToken: String
    )

    fun register(email: String, password: String): User {
        return userRepository.save(
            User(
                email = email,
                hashedPassword = hashEncoder.encode(password)
            )
        )
    }

    fun login(email:String, password:String): TokenPair{
        val user = userRepository.findByEmail(email) ?: throw BadCredentialsException("Bad Credentials")
        if(!hashEncoder.matches(password, user.hashedPassword)){
            throw BadCredentialsException("Bad Credentials")
        }

        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateAccessToken(user.id.toHexString())

        return TokenPair(newAccessToken, newRefreshToken)
    }
}