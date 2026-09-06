package com.jpbazaar.service;

import com.jpbazaar.dto.request.ChangePasswordRequest;
import com.jpbazaar.dto.request.UpdateProfileRequest;
import com.jpbazaar.dto.response.UserResponse;

public interface UserService {

    UserResponse getCurrentProfile(String email);

    UserResponse updateProfile(String email, UpdateProfileRequest request);

    void changePassword(String email, ChangePasswordRequest request);
}
