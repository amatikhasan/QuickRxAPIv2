package com.example.quickrx.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Date;

@Data
@Schema(description = "Request object for user registration")
public class UserRegistrationRequest {

    @Schema(description = "Full name of the user.", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Schema(description = "Phone number of the user.", example = "+11234567890", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format")
    private String phone;

    @Schema(description = "Email address of the user.", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Password for the user account.", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @Schema(description = "Date of birth of the user (YYYY-MM-DD).", example = "1990-01-15", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Date of birth cannot be null")
    @Past(message = "Date of birth must be in the past")
    private Date dob; // java.util.Date

    @Schema(description = "Registration number, if applicable.", example = "REG12345")
    private String regNumber; // Optional
    // firebaseUid, uniqueId, accountStatus could be set internally or via different flows
}
