package dev.pulceo.pna.controller;

import dev.pulceo.pna.dto.metricrequests.MetricRequestDTO;
import dev.pulceo.pna.dto.metricrequests.MetricResponseDTO;
import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.model.jobs.PingJob;
import dev.pulceo.pna.model.link.Link;
import dev.pulceo.pna.model.ping.IPVersion;
import dev.pulceo.pna.model.ping.PingRequest;
import dev.pulceo.pna.service.JobService;
import dev.pulceo.pna.service.LinkService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/nodes")
public class LinkController {

    private final LinkService linkService;
    private final JobService jobService;

    @Autowired
    public LinkController(LinkService linkService, JobService jobService) {
        this.linkService = linkService;
        this.jobService = jobService;
    }

    @PostMapping("/links/{linkId}/metric-requests")
    public ResponseEntity<MetricResponseDTO> newMetricRequestForLink(@PathVariable UUID linkId, @Valid @NotNull @RequestBody MetricRequestDTO metricRequestDTO) throws JobServiceException {
        // first, get the link
        Optional<Link> retrievedLink = linkService.readLinkByUUID(linkId);
        if (retrievedLink.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // retrieve link
        Link link = retrievedLink.get();

        // create PingRequest
        PingRequest pingRequest = new PingRequest(link.getSrcNode().getPnaEndpoint(),link.getSrcNode().getPnaEndpoint(), IPVersion.IPv4, 5, 66, "lo");
        // Encapsulate PingRequest in PingJob
        PingJob pingJob = new PingJob(pingRequest, 15);
        this.jobService.createPingJob(pingJob);
        PingJob createdPingJob = this.jobService.readPingJob(pingJob.getId());
        MetricResponseDTO createdMetricRequestDTO = new MetricResponseDTO(createdPingJob.getUuid(), "icmp-rtt", "15s", true, new HashMap<>(), new HashMap<>());
        return new ResponseEntity<>(createdMetricRequestDTO, HttpStatus.OK);
    }





}
