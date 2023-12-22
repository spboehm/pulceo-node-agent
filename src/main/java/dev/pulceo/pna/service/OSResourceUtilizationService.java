package dev.pulceo.pna.service;

import org.springframework.stereotype.Service;

@Service
public class OSResourceUtilizationService {
    /* Dynamic
    /* Important mount /proc and /sys of host to container
     * CPU
     */
    // TS, value, unit

    /* Memory  */
    // /proc/meminfo
    //

    /* Disk */
    // free capacity
    // free -h

    /* Net */
    // tx rx
    // ip -s -j -p link show eth0
    // /sys/class/net/eth0/statistics
}
