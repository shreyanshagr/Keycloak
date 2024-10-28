package com.keycloak.service.impl;

import com.keycloak.dto.UserDTO;
import com.keycloak.service.IUserService;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final Keycloak keycloak;

    @Value("${app.keycloak.realm}")
    private String realm;

    @Override
    public int createUser(UserDTO.UserRequestDTO userRequestDTO) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setFirstName(userRequestDTO.getFirstName());
        userRepresentation.setLastName(userRequestDTO.getLastName());
        userRepresentation.setUsername(userRequestDTO.getEmail());
        userRepresentation.setEmail(userRequestDTO.getEmail());
        userRepresentation.setEmailVerified(false);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(userRequestDTO.getPassword());
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        userRepresentation.setCredentials(List.of(credentialRepresentation));

        log.info("User Representation {}", userRepresentation);
        UsersResource usersResources = getUsersResources();
        Response response = usersResources.create(userRepresentation);

        log.info("Response status {}",response.getStatus());

        if(Objects.equals(201,response.getStatus())){
            log.info("New user has been created");
        }

       List<UserRepresentation> userRepresentations = usersResources.searchByUsername(userRequestDTO.getEmail(),true);
        log.info("List of userRespresentations -> {}",userRepresentations);
       UserRepresentation userRepresentation1 =  userRepresentations.getFirst();
        log.info("First of userRespresentations List and id-> {}<-->{}",userRepresentation1,userRepresentation1.getId());
       sendVerificationEmail(userRepresentation1.getId());
        return response.getStatus();

    }

    public void sendVerificationEmail(String userId) {
        try {
            UsersResource usersResource = getUsersResources();
            UserResource userResource = usersResource.get(userId);

            // Check if user exists
            try {
                userResource.toRepresentation();
            } catch (NotFoundException e) {
                log.error("User not found with ID: {}", userId);
                throw new RuntimeException("User not found", e);
            }

            userResource.sendVerifyEmail();
            log.info("Verification email sent successfully for user ID: {}", userId);
        } catch (WebApplicationException e) {
            log.error("Keycloak server error while sending verification email for user ID: {}. Status: {}, Response: {}",
                    userId, e.getResponse().getStatus(), e.getResponse().readEntity(String.class), e);
            throw new RuntimeException("Failed to send verification email due to Keycloak server error", e);
        } catch (Exception e) {
            log.error("Unexpected error sending verification email for user ID: {}", userId, e);
            throw new RuntimeException("Unexpected error while sending verification email", e);
        }
    }

    private UsersResource getUsersResources(){
        return keycloak.realm(realm).users();
    }
}
