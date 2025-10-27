package ine5417.records;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateChannelRequest(
        @NotBlank(message = "Name cannot be blank") String name,
        String description,
        @NotBlank(message = "Email cannot be blank") @Email(message = "Email should be valid") String email
) {}