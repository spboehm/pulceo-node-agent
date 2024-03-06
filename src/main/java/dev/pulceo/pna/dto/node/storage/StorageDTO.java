package dev.pulceo.pna.dto.node.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StorageDTO {
    private float size;
    private int slots;
}
