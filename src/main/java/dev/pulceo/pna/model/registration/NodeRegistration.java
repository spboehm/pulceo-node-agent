package dev.pulceo.pna.model.registration;


import dev.pulceo.pna.model.Resource;
import jakarta.persistence.Entity;

@Entity
public class NodeRegistration extends Resource {

    private String pnaUUID;
    private String pnaEndpoint;
    private String pnaToken;

}
