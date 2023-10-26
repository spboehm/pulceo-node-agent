package dev.pulceo.pna.model.link;


import dev.pulceo.pna.model.Resource;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Link extends Resource  {

    private String name;

    private long srcNodeId;
    private long destNodeId;

}
