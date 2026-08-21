package dev.mihirpatel.opspilot.incident;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface IncidentRepository extends JpaRepository<Incident, UUID> {
    List<Incident> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
