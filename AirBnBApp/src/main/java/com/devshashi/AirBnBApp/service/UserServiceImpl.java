package com.devshashi.AirBnBApp.service;

import com.devshashi.AirBnBApp.dto.ProfileUpdateRequestDTO;
import com.devshashi.AirBnBApp.dto.UserDTO;
import com.devshashi.AirBnBApp.entity.User;
import com.devshashi.AirBnBApp.exception.ResourceNotFoundException;
import com.devshashi.AirBnBApp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.devshashi.AirBnBApp.util.AppUtils.getCurrentUser;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + id));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new BadCredentialsException("User not found with username " + username));
    }

    @Override
    public void updateProfile(ProfileUpdateRequestDTO profileUpdateRequestDTO) {
        User user = getCurrentUser();
        if(profileUpdateRequestDTO.getDateOfBirth() != null){
            user.setDateOfBirth(profileUpdateRequestDTO.getDateOfBirth());
        }
        if(profileUpdateRequestDTO.getGender() != null){
            user.setGender(profileUpdateRequestDTO.getGender());
        }
        if(profileUpdateRequestDTO.getName() != null) {
            user.setName(profileUpdateRequestDTO.getName());
        }
        userRepository.save(user);
    }

    @Override
    public UserDTO getMyProfile() {
        User user = getCurrentUser();
        return modelMapper.map(user, UserDTO.class);
    }
}
