package dev.pulceo.pna.model.task;

import dev.pulceo.pna.dto.task.CreateNewTaskOnPnaDTO;
import dev.pulceo.pna.model.Resource;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Task extends Resource {

    @Builder.Default
    private long taskSequenceNumber = 0; // sequence number of the task
    @Builder.Default
    private String globalTaskUUID = ""; // based on the one from PSM
    @Builder.Default
    private String remoteNodeUUID = ""; // local UUID of device (remote from psm), hence, the local uuid
    @Builder.Default
    private String applicationUUID = ""; // local application UUID on device (remote from psm)
    @Builder.Default
    private String applicationComponentId = ""; // local application component id on device (remote from psm)
    @Builder.Default
    private Timestamp arrived = Timestamp.valueOf(LocalDateTime.now()); // timestamp where task has arrived at the servers
    @Builder.Default
    private String arrivedAt = "pna"; // user who has arrived the task at the servers
    @Builder.Default
    @Lob
    private byte[] payload = new byte[0]; // payload of the task
    @Builder.Default
    private String callbackProtocol = ""; // statically generated, never changed
    @Builder.Default
    private String callbackEndpoint = ""; // statically generated, never changed
    @Builder.Default
    private String destinationApplicationComponentProtocol = ""; // statically generated, never changed, e.g., http
    @Builder.Default
    private String destinationApplicationComponentEndpoint = ""; // statically generated, never changed, e.g., /api/test
    @Builder.Default
    private TaskStatus status = TaskStatus.NEW; // task status
    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "property_key")
    @Column(name = "property_value")
    @CollectionTable(name = "task_properties", joinColumns = @JoinColumn(name = "task_property_id"))
    private Map<String, String> properties = new HashMap<>(); // properties of the task

    public static Task fromCreateNewTaskOnPnaDTO(@Valid CreateNewTaskOnPnaDTO createNewTaskOnPnaDTO) {
        return Task.builder()
                .taskSequenceNumber(createNewTaskOnPnaDTO.getTaskSequenceNumber())
                .globalTaskUUID(createNewTaskOnPnaDTO.getGlobalTaskUUID())
                .applicationUUID(createNewTaskOnPnaDTO.getApplicationId())
                .applicationComponentId(createNewTaskOnPnaDTO.getApplicationComponentId())
                .payload(createNewTaskOnPnaDTO.getPayload())
                .callbackProtocol(createNewTaskOnPnaDTO.getCallbackProtocol())
                .callbackEndpoint(createNewTaskOnPnaDTO.getCallbackEndpoint())
                .destinationApplicationComponentProtocol(createNewTaskOnPnaDTO.getDestinationApplicationComponentProtocol())
                .destinationApplicationComponentEndpoint(createNewTaskOnPnaDTO.getDestinationApplicationComponentEndpoint())
                .properties(createNewTaskOnPnaDTO.getProperties())
                .build();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Task task = (Task) o;
        return getId() != null && Objects.equals(getId(), task.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
