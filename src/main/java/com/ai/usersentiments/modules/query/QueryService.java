package com.ai.usersentiments.modules.query;

import com.ai.usersentiments.domain.Ticket;
import com.ai.usersentiments.infrastructure.persistence.TicketJpaRepository;
import com.ai.usersentiments.modules.providers.LlmProviderRouter;
import com.ai.usersentiments.modules.providers.RoutingStrategy;
import com.ai.usersentiments.modules.scoring.Score;
import com.ai.usersentiments.modules.scoring.ScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QueryService {
    private final TicketJpaRepository repo;
    private final LlmProviderRouter router;
    private final ScoringService scoringService;

    public String handleQuery(String message) {

        // ────────────────────────────────────────────────
        // 1. Extract Ticket ID using LLM
        // ────────────────────────────────────────────────
        router.setStrategy(RoutingStrategy.SINGLE); // Best for deterministic extraction

        String extractPrompt = """
            Extract ONLY the Ticket ID from the following user query.
            If no valid ID exists, respond with "NONE".

            User Query:
            "%s"
            """.formatted(message);

        String ticketId = router.route(extractPrompt).trim();

        if (ticketId.equalsIgnoreCase("NONE")) {
            return "I couldn't identify a valid ticket ID in your message. Please provide the ticket number.";
        }

        // ────────────────────────────────────────────────
        // 2. Query database for ticket
        // ────────────────────────────────────────────────
        Optional<Ticket> opt = repo.findById(ticketId);

        if (opt.isEmpty()) {
            return "I could not find a ticket with ID: " + ticketId;
        }

        Ticket ticket = opt.get();

        // ────────────────────────────────────────────────
        // 3. Build final status message
        // ────────────────────────────────────────────────
        String reply = """
            Status for ticket %s:
            Current status: %s
            """.formatted(ticketId, ticket.getStatus());

        // ────────────────────────────────────────────────
        // 4. Score accuracy and clarity of this response
        // ────────────────────────────────────────────────
        Score score = scoringService.evaluate(message, reply);

        System.out.println("Query Response Score = " + score);

        return reply;
    }
}
