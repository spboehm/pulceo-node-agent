package dev.pulceo.pna.controller;

import dev.pulceo.pna.dto.link.CreateNewLinkDTO;
import dev.pulceo.pna.dto.link.LinkDTO;
import dev.pulceo.pna.dto.metricrequests.CreateNewMetricRequestDTO;
import dev.pulceo.pna.dto.metricrequests.MetricDTO;
import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.exception.LinkServiceException;
import dev.pulceo.pna.model.jobs.PingJob;
import dev.pulceo.pna.model.link.Link;
import dev.pulceo.pna.model.ping.IPVersion;
import dev.pulceo.pna.model.ping.PingRequest;
import dev.pulceo.pna.service.JobService;
import dev.pulceo.pna.service.LinkService;
import dev.pulceo.pna.service.NodeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/links")
public class LinkController {

    private final NodeService nodeService;
    private final LinkService linkService;
    private final JobService jobService;
    private final ModelMapper modelMapper;

    @Autowired
    public LinkController(NodeService nodeService, LinkService linkService, JobService jobService, ModelMapper modelMapper) {
        this.nodeService = nodeService;
        this.linkService = linkService;
        this.jobService = jobService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("")
    public ResponseEntity<LinkDTO> createLink(@Valid @NotNull @RequestBody CreateNewLinkDTO createNewLinkDTO) throws JobServiceException, LinkServiceException {
            Link link = this.modelMapper.map(createNewLinkDTO, Link.class);
            link.setSrcNode(this.nodeService.readLocalNode().orElseThrow());
            link.setDestNode(this.nodeService.readNodeByPnaUUID(String.valueOf(createNewLinkDTO.getDestNodeUUID())).orElseThrow());
            // TODO: duplicate check
            Long linkId = this.linkService.createLink(link);
            Link createdLink = this.linkService.readLink(linkId).orElseThrow();
            LinkDTO linkDTO = this.modelMapper.map(createdLink, LinkDTO.class);
            return new ResponseEntity<>(linkDTO, HttpStatus.CREATED);
    }


    @PostMapping("/links/{linkId}/metric-requests")
    public ResponseEntity<MetricDTO> newMetricRequestForLink(@PathVariable UUID linkId, @Valid @NotNull @RequestBody CreateNewMetricRequestDTO createNewMetricRequestDTO) throws JobServiceException {
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
        MetricDTO createdMetricRequestDTO = new MetricDTO(createdPingJob.getUuid(), "icmp-rtt", "15s", true, new HashMap<>(), new HashMap<>());
        return new ResponseEntity<>(createdMetricRequestDTO, HttpStatus.OK);
    }





}
