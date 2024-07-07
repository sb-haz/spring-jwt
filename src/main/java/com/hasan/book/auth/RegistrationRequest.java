package com.hasan.book.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {

    @NotEmpty(message = "First name is required")
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotEmpty(message = "Last name is required")
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Email is invalid")
    @NotEmpty(message = "Email is required")
    @NotBlank(message = "Email is required")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    @NotEmpty(message = "Password is required")
    @NotBlank(message = "Password is required")
    private String password;

}
