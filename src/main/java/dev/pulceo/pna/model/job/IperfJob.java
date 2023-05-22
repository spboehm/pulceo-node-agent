package dev.pulceo.pna.model.job;

import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.iperf3.IperfBandwidthMeasurement;
import dev.pulceo.pna.model.iperf3.IperfClientProtocol;
import dev.pulceo.pna.model.iperf3.IperfResult;
import dev.pulceo.pna.model.iperf3.IperfRole;
import dev.pulceo.pna.util.Iperf3Utils;
import dev.pulceo.pna.util.ProcessUtils;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class IperfJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sourceHost;
    private String destinationHost;
    private int port;
    private IperfClientProtocol iperfClientProtocol;
    private int recurrence;
    private boolean active = false;

    public IperfJob(String sourceHost, String destinationHost, int port, IperfClientProtocol iperfClientProtocol, int recurrence) {
        this.sourceHost = sourceHost;
        this.destinationHost = destinationHost;
        this.port = port;
        this.iperfClientProtocol = iperfClientProtocol;
        this.recurrence = recurrence;
    }

    public IperfResult measureBandwidth() {
        try {
            String start = Instant.now().toString();
            Process p;
            if (this.getIperfClientProtocol() == IperfClientProtocol.TCP) {
                p = new ProcessBuilder("/bin/iperf3", "-c", this.getDestinationHost(), "-p", String.valueOf(this.getPort()), "-f", "m").start();
            } else {
                p = new ProcessBuilder("/bin/iperf3", "-u", "-c", this.getDestinationHost(), "-p", String.valueOf(this.getPort()), "-f m").start();
            }
            p.waitFor();
            String end = Instant.now().toString();
            List<String> iperf3Output = ProcessUtils.readProcessOutput(p.getInputStream());
            IperfBandwidthMeasurement iperfBandwidthMeasurementSender = Iperf3Utils.extractIperf3BandwidthMeasurement(this.getIperfClientProtocol(), iperf3Output, IperfRole.SENDER);
            IperfBandwidthMeasurement iperfBandwidthMeasurementReceiver = Iperf3Utils.extractIperf3BandwidthMeasurement(this.getIperfClientProtocol(), iperf3Output, IperfRole.RECEIVER);
            return new IperfResult(this.getSourceHost(), this.getDestinationHost(), start, end, iperfBandwidthMeasurementSender, iperfBandwidthMeasurementReceiver);
        } catch (InterruptedException | IOException | ProcessException e) {
            throw new RuntimeException("Could not measure bandwidth!", e);
        }
    }

    public Runnable getIperfJobRunnable() {
        return this::measureBandwidth;
    }

}
