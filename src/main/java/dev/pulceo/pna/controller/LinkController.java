package dev.pulceo.pna.controller;

import dev.pulceo.pna.dto.link.CreateNewLinkDTO;
import dev.pulceo.pna.dto.link.LinkDTO;
import dev.pulceo.pna.dto.metricrequests.*;
import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.exception.LinkServiceException;
import dev.pulceo.pna.model.iperf.IperfClientProtocol;
import dev.pulceo.pna.model.iperf.IperfRequest;
import dev.pulceo.pna.model.jobs.IperfJob;
import dev.pulceo.pna.model.jobs.LinkJob;
import dev.pulceo.pna.model.jobs.NpingJob;
import dev.pulceo.pna.model.jobs.PingJob;
import dev.pulceo.pna.model.link.Link;
import dev.pulceo.pna.model.node.Node;
import dev.pulceo.pna.model.nping.NpingClientProtocol;
import dev.pulceo.pna.model.nping.NpingRequest;
import dev.pulceo.pna.model.ping.PingRequest;
import dev.pulceo.pna.service.JobService;
import dev.pulceo.pna.service.LinkService;
import dev.pulceo.pna.service.NodeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/links")
public class LinkController {

    private final NodeService nodeService;
    private final LinkService linkService;
    private final JobService jobService;
    private final ModelMapper modelMapper;

    @Value("${pna.host:localhost}")
    private String sourceHost;

    @Value("${pna.delay.udp.port:4001}")
    private int npingDelayUDPPort;

    @Value("${pna.delay.tcp.port:4002}")
    private int npingDelayTCPPort;

    @Value("${pna.delay.rounds:10}")
    private int rounds;

    @Value("${pna.delay.interface:eth0}")
    private String iface;

    @Value("${pna.delay.udp.data.length}")
    private int dataLength;

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
            // get dest node
            Optional<Node> destNode = this.nodeService.readNodeByUUID(createNewLinkDTO.getDestNodeUUID());
            // TODO: do by request not found
            if (destNode.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            link.setDestNode(this.nodeService.readNodeByUUID(destNode.get().getUuid()).orElseThrow());
            // TODO: duplicate check
            long linkId = this.linkService.createLink(link);
            Link createdLink = this.linkService.readLink(linkId).orElseThrow();
            // TODO: revise link DTO creation and replace with RestFullDTO
            LinkDTO linkDTO = LinkDTO.builder()
                    .linkUUID(createdLink.getUuid())
                    .name(createdLink.getName())
                    .resourceType(createdLink.getResourceType())
                    .linkDirectionType(createdLink.getLinkDirectionType())
                    .srcNode(createdLink.getSrcNode().getUuid())
                    .destNode(createdLink.getDestNode().getUuid())
                    .linkJobs(createdLink.getLinkJobs())
                    .build();
            return new ResponseEntity<>(linkDTO, HttpStatus.CREATED);
    }

    @PatchMapping("/{linkUUID}/metric-requests/{metricRequestUUID}")
    public ResponseEntity<ShortMetricRequestDTO> updateMetricRequest(@PathVariable UUID linkUUID, @PathVariable UUID metricRequestUUID, @Valid @NotNull @RequestBody DisableMetricRequestDto disableMetricRequestDto) throws JobServiceException {
        // find metric request by uuid
        Optional<Link> retrievedLink = linkService.readLinkByUUID(linkUUID);
        if (retrievedLink.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Link link = retrievedLink.get();

        List<LinkJob> linkJobs = link.getLinkJobs();
//        linkJobs.stream().filter(linkJob -> linkJob.getUuid().equals(metricRequestUUID)).findFirst().ifPresent(linkJob -> {
//            if (disableMetricRequestDto.isEnabled()) {
//                this.jobService.enableJob(linkJob.getId());
//            } else {
//                this.jobService.disableJob(linkJob.getId());
//            }
//        });

        // cancel job
        return null;
    }


    @PostMapping("{linkUUID}/metric-requests/icmp-rtt-requests")
    public ResponseEntity<ShortMetricRequestDTO> newIcmpRttMetricRequest(@PathVariable UUID linkUUID, @Valid @NotNull @RequestBody CreateNewMetricRequestIcmpRttDTO createNewMetricRequestIcmpRttDTO) throws JobServiceException {
        Optional<Link> retrievedLink = linkService.readLinkByUUID(linkUUID);
        if (retrievedLink.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Link link = retrievedLink.get();

        // create PingRequest
        PingRequest pingRequest = new PingRequest(link.getSrcNode().getHost(), link.getSrcNode().getHost(), createNewMetricRequestIcmpRttDTO.getIpVersion(), createNewMetricRequestIcmpRttDTO.getCount(), createNewMetricRequestIcmpRttDTO.getDataLength(), createNewMetricRequestIcmpRttDTO.getIface());
        // Encapsulate PingRequest in PingJob
        PingJob pingJob = new PingJob(pingRequest, Integer.parseInt(createNewMetricRequestIcmpRttDTO.getRecurrence()));
        long id = this.jobService.createPingJob(pingJob);

        // if enabled
        if (createNewMetricRequestIcmpRttDTO.isEnabled()) {
            this.jobService.enablePingJob(id);
        }

        this.jobService.schedulePingJob(id);
        PingJob createdPingJob = this.jobService.readPingJob(pingJob.getId());
        ShortMetricRequestDTO createdShortMetricRequestDTO = new ShortMetricRequestDTO(createdPingJob.getUuid(), createNewMetricRequestIcmpRttDTO.getType(), createNewMetricRequestIcmpRttDTO.getRecurrence(), createNewMetricRequestIcmpRttDTO.isEnabled());
        return new ResponseEntity<>(createdShortMetricRequestDTO, HttpStatus.OK);
    }

    @PostMapping("{linkUUID}/metric-requests/udp-rtt-requests")
    public ResponseEntity<ShortMetricRequestDTO> newUdpRttMetricRequest(@PathVariable UUID linkUUID, @Valid @NotNull @RequestBody CreateNewMetricRequestUdpRttDto createNewMetricRequestUdpRttDto) throws JobServiceException {
        Optional<Link> retrievedLink = linkService.readLinkByUUID(linkUUID);
        if (retrievedLink.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Link link = retrievedLink.get();

        // create NpingRequest
        NpingRequest npingRequest = new NpingRequest(link.getSrcNode().getHost(), link.getSrcNode().getHost(), this.npingDelayUDPPort, NpingClientProtocol.UDP, this.rounds, this.iface);
        NpingJob npingJob = new NpingJob(npingRequest, Integer.parseInt(createNewMetricRequestUdpRttDto.getRecurrence()));
        long id = this.jobService.createNpingJob(npingJob);

        // if enabled
        if (createNewMetricRequestUdpRttDto.isEnabled()) {
            this.jobService.enableNpingJob(id);
        }

        this.jobService.scheduleNpingJob(id);
        NpingJob createdNpingJob = this.jobService.readNpingJob(npingJob.getId());
        ShortMetricRequestDTO createdShortMetricRequestDTO = new ShortMetricRequestDTO(createdNpingJob.getUuid(), createNewMetricRequestUdpRttDto.getType(), createNewMetricRequestUdpRttDto.getRecurrence(), createNewMetricRequestUdpRttDto.isEnabled());
        return new ResponseEntity<>(createdShortMetricRequestDTO, HttpStatus.OK);
    }

    @PostMapping("{linkUUID}/metric-requests/tcp-rtt-requests")
    public ResponseEntity<ShortMetricRequestDTO> newUdpRttMetricRequest(@PathVariable UUID linkUUID, @Valid @NotNull @RequestBody CreateNewMetricRequestTcpRttDto createNewMetricRequestTcpRttDto) throws JobServiceException {
        Optional<Link> retrievedLink = linkService.readLinkByUUID(linkUUID);
        if (retrievedLink.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Link link = retrievedLink.get();

        // create NpingRequest
        NpingRequest npingRequest = new NpingRequest(link.getSrcNode().getHost(), link.getSrcNode().getHost(), this.npingDelayUDPPort, NpingClientProtocol.TCP, this.rounds, this.iface);
        NpingJob npingJob = new NpingJob(npingRequest, Integer.parseInt(createNewMetricRequestTcpRttDto.getRecurrence()));
        long id = this.jobService.createNpingJob(npingJob);

        // if enabled
        if (createNewMetricRequestTcpRttDto.isEnabled()) {
            this.jobService.enableNpingJob(id);
        }

        this.jobService.scheduleNpingJob(id);
        NpingJob createdNpingJob = this.jobService.readNpingJob(npingJob.getId());
        ShortMetricRequestDTO createdShortMetricRequestDTO = new ShortMetricRequestDTO(createdNpingJob.getUuid(), createNewMetricRequestTcpRttDto.getType(), createNewMetricRequestTcpRttDto.getRecurrence(), createNewMetricRequestTcpRttDto.isEnabled());
        return new ResponseEntity<>(createdShortMetricRequestDTO, HttpStatus.OK);
    }

    // TODO: udp-bw
    @PostMapping("{linkUUID}/metric-requests/udp-bw-requests")
    public ResponseEntity<ShortMetricRequestDTO> newUdpBwMetricRequest(@PathVariable UUID linkUUID, @Valid @NotNull @RequestBody CreateNewMetricRequestUdpBwDto createNewMetricRequestUdpBwDto) throws JobServiceException {
        Optional<Link> retrievedLink = linkService.readLinkByUUID(linkUUID);
        if (retrievedLink.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Link link = retrievedLink.get();

        // create
        // ask for the next free port on the other node, now statically mocked
        // TODO: do this dynamically
        IperfRequest iperfRequest = new IperfRequest(link.getSrcNode().getHost(), link.getSrcNode().getHost(), 5000, createNewMetricRequestUdpBwDto.getBitrate(), createNewMetricRequestUdpBwDto.getTime(), IperfClientProtocol.UDP, "localhost");
        // Encapsulate PingRequest in PingJob
        IperfJob iperfJob = new IperfJob(iperfRequest, Integer.parseInt(createNewMetricRequestUdpBwDto.getRecurrence()));
        long id = this.jobService.createIperfJob(iperfJob);

        // if enabled
        if (createNewMetricRequestUdpBwDto.isEnabled()) {
            this.jobService.enableIperfJob(id);
        }

        this.jobService.scheduleIperfJob(id);

        IperfJob createdIperfJob = this.jobService.readIperfJob(iperfJob.getId());
        ShortMetricRequestDTO createdShortMetricRequestDTO = new ShortMetricRequestDTO(createdIperfJob.getUuid(), createNewMetricRequestUdpBwDto.getType(), createNewMetricRequestUdpBwDto.getRecurrence(), createNewMetricRequestUdpBwDto.isEnabled());
        return new ResponseEntity<>(createdShortMetricRequestDTO, HttpStatus.OK);
    }

    // TODO: tcp-bw
    //@PostMapping("{linkUUID}/metric-requests/udp-tcp-requests")




    // TODO: icmp-e2e

    // TODO: udp-e2e

    // TODO: tcp-e2e


}
