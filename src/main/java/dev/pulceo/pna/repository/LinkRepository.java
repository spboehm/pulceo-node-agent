package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.link.Link;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface LinkRepository extends CrudRepository<Link, Long> {

    @Override
    List<Link> findAll();

    Optional<Link> findLinkByDestId(long id);


}
