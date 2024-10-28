package com.keycloak.dto;

import lombok.Builder;
import lombok.Data;

public class UserDTO {

    public UserDTO(){
    }

    @Builder
    @Data
    public static class UserRequestDTO {

        public String firstName;

        public String lastName;

        public String email;

        public String password;
    }

}
