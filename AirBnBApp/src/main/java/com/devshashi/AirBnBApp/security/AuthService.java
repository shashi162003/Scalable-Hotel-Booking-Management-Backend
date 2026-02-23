package com.devshashi.AirBnBApp.security;

import com.devshashi.AirBnBApp.dto.LoginDTO;
import com.devshashi.AirBnBApp.dto.SignUpRequestDTO;
import com.devshashi.AirBnBApp.dto.UserDTO;
import com.devshashi.AirBnBApp.entity.User;
import com.devshashi.AirBnBApp.entity.enums.Role;
import com.devshashi.AirBnBApp.exception.ResourceNotFoundException;
import com.devshashi.AirBnBApp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public AuthService(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public UserDTO signUp(SignUpRequestDTO signUpRequestDTO){
        Optional<User> user = userRepository.findByEmail(signUpRequestDTO.getEmail());
        if(user.isPresent()){
            throw new BadCredentialsException("User with email already exists " + signUpRequestDTO.getEmail());
        }
        User toBeCreatedUser = modelMapper.map(signUpRequestDTO, User.class);
        toBeCreatedUser.setPassword(passwordEncoder.encode(signUpRequestDTO.getPassword()));
        toBeCreatedUser.setRoles(Set.of(Role.GUEST));
        User savedUser = userRepository.save(toBeCreatedUser);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    public String[] login(LoginDTO loginDTO){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );
        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new String[]{accessToken, refreshToken};
    }

    public String refreshToken(String refreshToken){

        Long userId = jwtService.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        String newAccessToken = jwtService.generateAccessToken(user);
        return newAccessToken;
    }
}
