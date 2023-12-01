package dev.pulceo.pna.model.registration;

import dev.pulceo.pna.model.Resource;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PnaNodeInitToken extends Resource {

    private String pnaUUID;
    private String token;
    boolean valid = true;

    public PnaNodeInitToken(String pnaUUID, String token) {
        this.pnaUUID = pnaUUID;
        this.token = token;
    }

}
