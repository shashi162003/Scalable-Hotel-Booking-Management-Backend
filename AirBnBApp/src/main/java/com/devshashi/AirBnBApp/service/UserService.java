package com.devshashi.AirBnBApp.service;

import com.devshashi.AirBnBApp.dto.ProfileUpdateRequestDTO;
import com.devshashi.AirBnBApp.dto.UserDTO;
import com.devshashi.AirBnBApp.entity.User;

public interface UserService {
    User getUserById(Long id);

    void updateProfile(ProfileUpdateRequestDTO profileUpdateRequestDTO);

    UserDTO getMyProfile();
}
