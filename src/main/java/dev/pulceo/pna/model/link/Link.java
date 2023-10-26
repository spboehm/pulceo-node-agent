package dev.pulceo.pna.model.link;


import dev.pulceo.pna.model.Resource;
import dev.pulceo.pna.model.ResourceType;
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
    private ResourceType resourceType;
    private LinkDirectionType linkDirectionType = LinkDirectionType.UNDIRECTED;
    private long srcId;
    private long destId;

    public Link(String name, ResourceType resourceType, long srcId, long destId) {
        this.name = name;
        this.resourceType = resourceType;
        this.srcId = srcId;
        this.destId = destId;
    }
}
