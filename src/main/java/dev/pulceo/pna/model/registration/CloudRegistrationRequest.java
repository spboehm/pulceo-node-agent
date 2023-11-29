package dev.pulceo.pna.model.registration;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudRegistrationRequest {
        
        private String prmUUID;
        private String prmEndpoint;
        private String pnaInitToken;

}
