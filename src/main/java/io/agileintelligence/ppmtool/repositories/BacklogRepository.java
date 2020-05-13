package io.agileintelligence.ppmtool.repositories;

import io.agileintelligence.ppmtool.domain.Backlog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BacklogRepository extends CrudRepository<Backlog, Long> {
    Optional<Backlog> findByProjectIdentifier(String identifier);
}
