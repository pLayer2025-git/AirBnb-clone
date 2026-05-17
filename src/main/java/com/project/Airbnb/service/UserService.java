package com.project.Airbnb.service;

import com.project.Airbnb.dto.ProfileUpdateRequestDto;
import com.project.Airbnb.dto.UserDto;
import com.project.Airbnb.entity.User;

public interface UserService {

    User getUserById(Long id);

    void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto);

    UserDto getMyProfile();
}