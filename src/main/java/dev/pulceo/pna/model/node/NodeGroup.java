package dev.pulceo.pna.model.node;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class NodeGroup {

    @Id
    private Long id;
    public String uuid;
    public String groupName;

}
