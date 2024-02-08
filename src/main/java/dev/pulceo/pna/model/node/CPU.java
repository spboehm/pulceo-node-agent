package dev.pulceo.pna.model.node;

import dev.pulceo.pna.model.Resource;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CPU extends Resource {

    // TODO: add further vendor specific information
    private String modelName;
    private int cores;
    private int threads;
    private float bogoMIPS;
    private float MIPS;
    private float GFlop;
    private float minimalFrequency;
    private float averageFrequency;
    private float maximalFrequency;
    private float slots;

}
