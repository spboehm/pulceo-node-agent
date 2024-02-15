package dev.pulceo.pna.model.resources;

import dev.pulceo.pna.model.Resource;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class  NetworkUtilizationMeasurement extends Resource {

    private String time;
    private String iface;
    private long rxBytes;
    private long txBytes;

}
