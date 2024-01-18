package dev.pulceo.pna.model.registration;

import dev.pulceo.pna.model.Resource;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CloudRegistration extends Resource {

    private String pnaUUID;
    private String prmUUID;
    private String prmEndpoint;
    private String pnaToken;

}
