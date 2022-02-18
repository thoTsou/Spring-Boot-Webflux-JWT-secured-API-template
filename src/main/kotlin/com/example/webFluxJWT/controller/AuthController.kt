package com.example.webFluxJWT.controller

import com.example.webFluxJWT.configuration.JWTUtil
import com.example.webFluxJWT.model.AuthRequest
import com.example.webFluxJWT.model.AuthResponse
import com.example.webFluxJWT.model.User
import com.example.webFluxJWT.service.UserService
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@RestController
class AuthController(
    private val jwtUtil: JWTUtil,
    private val passwordEncoder: PasswordEncoder,
    private val userService: UserService
) {

    @PostMapping("/login")
    suspend fun login(@RequestBody authRequest: AuthRequest): ResponseEntity<AuthResponse> {

        val user = userService.findUserByUsername(authRequest.username).awaitSingle()

        val doubleCheckPassword = passwordEncoder.matches(authRequest.password,user.password)

        if(!doubleCheckPassword)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        else
            return ResponseEntity.ok(AuthResponse(jwtUtil.generateToken(user)))
    }

}