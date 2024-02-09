package dev.pulceo.pna.dto.node.memory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemoryResourceDTO {

    private MemoryDTO memoryCapacity;
    private MemoryDTO memoryAllocatable;

}
