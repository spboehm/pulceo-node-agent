package dev.pulceo.pna.model.registration;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.UUID;

public record CloudRegistrationRequest (

        @NotBlank @UUID String prmUUID,
        @NotBlank @URL String prmEndpoint,
        @NotBlank @Length(min = 76, max = 76) String pnaInitToken) {}
