package dev.pulceo.pna.model.node;

import dev.pulceo.pna.model.Resource;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class CPU extends Resource {

    // TODO: add further vendor specific information, model number etc.
    private String modelName;
    private int cores;
    private int threads;
    private float bogoMIPS;
    private float MIPS;
    private float GFlop;
    private float minimalFrequency;
    private float averageFrequency;
    private float maximalFrequency;
    private int shares;
    private float slots;

}
