package com.ai.usersentiments.infrastructure.persistence;

import com.ai.usersentiments.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketJpaRepository extends JpaRepository<Ticket, String> {
}
