package dev.pulceo.pna.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.UUID;

public record CloudRegistrationRequestDto(

        @NotBlank
        @UUID
        String prmUUID,

        @NotBlank
        @URL
        String prmEndpoint,

        @Length(min = 76, max = 76)
        @NotBlank
        String pnaInitToken) { }



