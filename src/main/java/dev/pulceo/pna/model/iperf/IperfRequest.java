package dev.pulceo.pna.model.iperf;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class IperfRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sourceHost;
    private String destinationHost;
    private int port;
    // default is set to unlimited bandwidth (0), unit in Mbit
    private int bitrate = 0;
    private int time;
    // default format is Mbit ("m")
    private final String format = "m";
    private IperfClientProtocol iperfClientProtocol;
    private String bind;

    public IperfRequest(String sourceHost, String destinationHost, int port, int bitrate, int time, IperfClientProtocol iperfClientProtocol, String bind) {
        this.sourceHost = sourceHost;
        this.destinationHost = destinationHost;
        this.port = port;
        this.bitrate = bitrate;
        this.time = time;
        this.iperfClientProtocol = iperfClientProtocol;
        this.bind = bind;
    }

    public String getCmd() {
        if (isUDPSender()) {
            return String.format("/usr/bin/iperf3 -c %s -u -p %s -b %sM -t %s -f %s", destinationHost, port, bitrate, time, format);
        } else {
            return String.format("/usr/bin/iperf3 -c %s -p %s -b %sM -t %s -f %s", destinationHost, port, bitrate, time, format);
        }
    }

    public boolean isUDPSender() {
        if (this.iperfClientProtocol == IperfClientProtocol.UDP) {
            return true;
        }
        return false;
    }

}
