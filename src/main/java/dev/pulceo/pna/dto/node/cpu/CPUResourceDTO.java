package dev.pulceo.pna.dto.node.cpu;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CPUResourceDTO {

    private CPUDTO cpuCapacity;
    private CPUDTO cpuAllocatable;

}
