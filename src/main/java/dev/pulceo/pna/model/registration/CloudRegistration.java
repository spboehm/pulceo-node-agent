package dev.pulceo.pna.model.registration;

import dev.pulceo.pna.model.Resource;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class CloudRegistration extends Resource {

    private String prmUUID;
    private String prmEndpoint;
    private String pnaToken;

}
