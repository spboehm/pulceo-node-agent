package dev.pulceo.pna.controller;

import dev.pulceo.pna.exception.NodeServiceException;
import dev.pulceo.pna.service.IperfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/iperf3-servers")
public class IperfServerController {

    private final IperfService iperfService;

    @Autowired
    public IperfServerController(IperfService iperfService) {
        this.iperfService = iperfService;
    }

    @PostMapping("")
    public ResponseEntity<Long> createNewIperf3Server() throws NodeServiceException {
        try {
            return ResponseEntity.ok().body(this.iperfService.startIperf3ServerAndReturnPort());
        } catch (Exception e) {
            throw new NodeServiceException("Could not start Iperf3 server process!", e);
        }
    }
}
