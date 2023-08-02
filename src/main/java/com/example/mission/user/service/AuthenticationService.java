package com.example.mission.user.service;

import com.example.mission.config.JwtService;
import com.example.mission.user.Request.PartnerRequest;
import com.example.mission.user.Request.RegisterRequest;
import com.example.mission.user.Request.AuthenticationRequest;
import com.example.mission.user.response.AuthenticationResponse;
import com.example.mission.user.enumType.Role;
import com.example.mission.user.entity.UserEntity;
import com.example.mission.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final LocalDateTime currentTime = LocalDateTime.now();

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;
    public AuthenticationResponse register(RegisterRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        String nickname = request.getNickname();

        // 이메일 유효성 확인
        if (!UserEntity.isValidEmail(email)) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
        }

        // 비밀번호 유효성 확인
        if (!UserEntity.isValidPassword(password)) {
            throw new IllegalArgumentException("비밀번호는 특수문자를 포함한 8글자 이상이어야 합니다.");
        }

        // 이미 존재하는 이메일인지 확인
        Optional<UserEntity> existingUser = repository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        Optional<UserEntity> existingNickname = repository.findByNickname(nickname);
        if (existingNickname.isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        var user = UserEntity.builder()
                .name(request.getName())
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .role(Role.USER)
                .registerDt(currentTime)
                .build();

        repository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse partnerRegister(PartnerRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        String nickname = request.getNickname();

        System.out.println("currenttime = " + currentTime);

        // 이메일 유효성 확인
        if (!UserEntity.isValidEmail(email)) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
        }

        // 비밀번호 유효성 확인
        if (!UserEntity.isValidPassword(password)) {
            throw new IllegalArgumentException("비밀번호는 특수문자를 포함한 8글자 이상이어야 합니다.");
        }

        // 이미 존재하는 이메일인지 확인
        Optional<UserEntity> existingUser = repository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        Optional<UserEntity> existingNickname = repository.findByNickname(nickname);
        if (existingNickname.isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        var user = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(nickname)
                .role(Role.PARTNER_USER)
                .registerDt(currentTime)
                .build();
        repository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
           new UsernamePasswordAuthenticationToken(
                   request.getEmail(),
                   request.getPassword()
           )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
