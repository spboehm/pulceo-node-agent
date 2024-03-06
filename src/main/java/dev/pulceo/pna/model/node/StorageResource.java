package dev.pulceo.pna.model.node;

import dev.pulceo.pna.model.Resource;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Entity
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class StorageResource extends Resource {

    @Builder.Default
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Storage storageCapacity = Storage.builder().build();

    @Builder.Default
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Storage storageAllocatable = Storage.builder().build();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        StorageResource that = (StorageResource) o;

        if (!Objects.equals(storageCapacity, that.storageCapacity))
            return false;
        return Objects.equals(storageAllocatable, that.storageAllocatable);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (storageCapacity != null ? storageCapacity.hashCode() : 0);
        result = 31 * result + (storageAllocatable != null ? storageAllocatable.hashCode() : 0);
        return result;
    }
}
