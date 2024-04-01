package com.sa.clothingstore.service.authentication;

import com.sa.clothingstore.dto.request.authentication.AuthenticationRequest;
import com.sa.clothingstore.dto.request.authentication.RegisterRequest;
import com.sa.clothingstore.dto.response.authentication.AuthenticationResponse;
import com.sa.clothingstore.dto.response.authentication.CookieResponse;
import com.sa.clothingstore.exception.ObjectAlreadyExistsException;
import com.sa.clothingstore.exception.ObjectNotFoundException;
import com.sa.clothingstore.model.user.RefreshToken;
import com.sa.clothingstore.model.user.Role;
import com.sa.clothingstore.model.user.User;
import com.sa.clothingstore.repository.user.UserRepository;
import com.sa.clothingstore.service.token.JwtService;
import com.sa.clothingstore.service.token.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImp implements AuthenticationService{
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    private final AuthenticationManager authenticationManager;


    @Override
    public User signup(RegisterRequest registerRequest) {
        userRepository.findByEmail(registerRequest.getEmail()).ifPresent(user -> {
            throw new ObjectAlreadyExistsException("Email already existed");
        });
        userRepository.findByPhone(registerRequest.getPhone()).ifPresent(user -> {
            throw new ObjectAlreadyExistsException("Phone already existed");
        });
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setFullName(registerRequest.getFullname());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.CUSTOMER);
        return userRepository.save(user);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
       authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );
       var user = userRepository.findByEmail(authenticationRequest.getUsername())
               .orElseThrow(() -> new ObjectNotFoundException("User not found")
               );
       return this.generateToken(user);
    }
    @Override
    public CookieResponse signout(){
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principle.toString() != "anonymousUser") {
            UUID userId = ((User) principle).getId();
            refreshTokenService.deleteByUserId(userId);
        }
        return CookieResponse.builder()
                .accessCookie(jwtService.getCleanJwtCookie())
                .refreshCookie(jwtService.getCleanJwtRefreshCookie())
                .build();
    }

    @Override
    public Optional<ResponseCookie> refreshToken(HttpServletRequest request) {
        String refreshToken = jwtService.getJwtRefreshFromCookies(request);
        System.out.println(refreshToken);
        if ((refreshToken != null) && (refreshToken.length() > 0)) {
            return refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        ResponseCookie jwtCookie = jwtService.generateJwtCookie(user);
                        return jwtCookie;
                    });
        }
        return null;
    }

    private AuthenticationResponse generateToken(User user){
        ResponseCookie jwtCookie = jwtService.generateJwtCookie(user);
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(user.getId());
        ResponseCookie jwtRefreshCookie = jwtService.generateRefreshJwtCookie(refreshToken.getToken());
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(jwtCookie, jwtRefreshCookie, user.getRole().toString());
        return authenticationResponse;
    }

}