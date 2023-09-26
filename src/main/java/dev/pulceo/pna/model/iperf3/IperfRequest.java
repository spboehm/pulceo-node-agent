package dev.pulceo.pna.model.iperf3;

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
    // default is set to unlimited bandwidth (0)
    private int bitrate = 0;
    private int time;
    // default format is Mbit ("m")
    private final String format = "m";
    private IperfClientProtocol iperfClientProtocol;
    private String bindDev;

    public IperfRequest(String sourceHost, String destinationHost, int port, int bitrate, int time, IperfClientProtocol iperfClientProtocol, String bindDev) {
        this.sourceHost = sourceHost;
        this.destinationHost = destinationHost;
        this.port = port;
        this.bitrate = bitrate;
        this.time = time;
        this.iperfClientProtocol = iperfClientProtocol;
        this.bindDev = bindDev;
    }

    public String getCmd() {
        if (isUDPSender()) {
            return String.format("/bin/iperf3 -c %s -u -p %s -b %sM -t %s -f %s --bind-dev %s", destinationHost, port, bitrate, time, format, bindDev);
        } else {
            return String.format("/bin/iperf3 -c %s -p %s -b %sM -t %s -f %s --bind-dev %s", destinationHost, port, bitrate, time, format, bindDev);
        }
    }

    public boolean isUDPSender() {
        if (this.iperfClientProtocol == IperfClientProtocol.UDP) {
            return true;
        }
        return false;
    }

}
