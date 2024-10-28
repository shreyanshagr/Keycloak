package com.keycloak.service;

import com.keycloak.dto.UserDTO.UserRequestDTO;


public interface IUserService {
    public int createUser(UserRequestDTO userRequestDTO);

    public void sendVerificationEmail(String userId);

    public void deleteUser(String userId);

    public void forgotPassword(String username);
}

