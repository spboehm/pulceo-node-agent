package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.iperf.IperfResult;
import org.springframework.data.repository.CrudRepository;

public interface IperfResultRepository extends CrudRepository<IperfResult, Long> {

}
