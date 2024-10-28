package com.keycloak.controller;

import com.keycloak.dto.UserDTO.UserRequestDTO;
import com.keycloak.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody UserRequestDTO userRequestDTO){

        log.info("UserReqDto {}", userRequestDTO.toString());

        int status = userService.createUser(userRequestDTO);

        if(status == 201){
            return ResponseEntity.status(HttpStatus.CREATED).body("User created.!!");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not created.!!");
    }

    @PutMapping("/{userId}/send-verification-email")
    public ResponseEntity<?> sendVerificationEmail(@PathVariable String userId) {
        try {
            userService.sendVerificationEmail(userId);
            return ResponseEntity.ok("Verification email sent successfully");
        } catch (RuntimeException e) {
            log.error("Failed to send verification email for user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send verification email: " + e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Failed to delete user: " + e.getMessage());
        }
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String username) {
        try {
            userService.forgotPassword(username);
            return ResponseEntity.ok("Forgot password mail sent.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Failed to reset password: " + e.getMessage());
        }
    }
}
