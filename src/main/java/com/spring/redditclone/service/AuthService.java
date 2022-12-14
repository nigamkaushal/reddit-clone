package com.spring.redditclone.service;

import com.spring.redditclone.dto.AuthenticationResponse;
import com.spring.redditclone.dto.LoginRequest;
import com.spring.redditclone.dto.RegisterRequest;
import com.spring.redditclone.exceptions.RedditCloneException;
import com.spring.redditclone.model.NotificationEmail;
import com.spring.redditclone.model.User;
import com.spring.redditclone.model.VerificationToken;
import com.spring.redditclone.repository.UserRepository;
import com.spring.redditclone.repository.VerificationTokenRepository;
import com.spring.redditclone.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    @Value("${accountVerificationUrl}")
    private String verificationUrl;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public void signUp(RegisterRequest registerRequest) throws RedditCloneException {
        if (userRepository.existsByUsername(registerRequest.getUsername()))
            throw new RedditCloneException("Username already exists!");
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);
        userRepository.save(user);

        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Please Activate your Account",
                user.getEmail(),
                "Thank you for signing up to Reddit Clone, " +
                        "Please click on the below url to activate your account : " +
                        verificationUrl + token
        ));
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    @Transactional
    public void verifyAccount(String token) {
        Optional<VerificationToken> tokenOptional = verificationTokenRepository.findByToken(token);
        VerificationToken verificationToken = tokenOptional.orElseThrow(() -> new RedditCloneException("Invalid Token"));
        fetchUserAndEnable(verificationToken);
    }

    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RedditCloneException("User not found with name - " + username));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(authenticate);
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationTime()))
                .username(loginRequest.getUsername())
                .build();
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        String name = SecurityContextHolder.
                getContext().getAuthentication().getName();
        return userRepository.findByUsername(name)
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + name));
    }
}
