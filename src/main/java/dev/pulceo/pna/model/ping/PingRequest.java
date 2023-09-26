package dev.pulceo.pna.model.ping;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class PingRequest {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sourceHost;
    private String destinationHost;
    private IPVersion ipVersion;
    private int dataLength;
    private String iface;

    public PingRequest(String sourceHost, String destinationHost, IPVersion ipVersion, int dataLength, String iface) {
        this.sourceHost = sourceHost;
        this.destinationHost = destinationHost;
        this.ipVersion = ipVersion;
        this.dataLength = dataLength;
        this.iface = iface;
    }

    public String getCmd() {
        return String.format("ping -%s -s %s -I %s", this.ipVersion.label, this.dataLength, this.iface);
    }

}
