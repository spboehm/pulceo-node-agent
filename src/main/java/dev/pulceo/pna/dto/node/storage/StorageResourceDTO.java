package dev.pulceo.pna.dto.node.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StorageResourceDTO {
    private StorageDTO storageCapacity;
    private StorageDTO storageAllocatable;
}
