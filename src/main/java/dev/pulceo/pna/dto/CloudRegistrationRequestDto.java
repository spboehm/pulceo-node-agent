package dev.pulceo.pna.dto;

import jakarta.validation.constraints.NotBlank;

public record CloudRegistrationRequestDto(
        @NotBlank String prmUUID,
        @NotBlank String prmEndpoint,
        @NotBlank String token) { }
