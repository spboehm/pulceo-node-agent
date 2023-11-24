package dev.pulceo.pna.model;


import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class PNAMetaData extends Resource {

    private String pnaUUID;
    private String pnaInitToken;
    boolean valid = true;

}
