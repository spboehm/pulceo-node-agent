package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.iperf.IperfServerRequest;
import org.springframework.data.repository.CrudRepository;

public interface IperfServerRequestRepository extends CrudRepository<IperfServerRequest, Long> {
}
