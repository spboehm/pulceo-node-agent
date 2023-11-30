package dev.pulceo.pna.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudRegistrationResponseDto {

    @NotBlank
    @UUID
    String uuid;

    @NotBlank
    @UUID
    String prmUUID;

    @NotBlank
    @URL
    String prmEndpoint;

    @NotBlank
    @Length(min = 76, max = 76)
    String pnaToken;

}
