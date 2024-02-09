package dev.pulceo.pna.dto.node.memory;

import dev.pulceo.pna.model.node.Memory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemoryResourceDTO {

    private Memory memoryCapacity;
    private Memory memoryAllocatable;

}
