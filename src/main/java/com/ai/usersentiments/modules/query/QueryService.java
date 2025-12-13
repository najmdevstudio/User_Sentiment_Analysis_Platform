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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class QueryService {
    private final TicketJpaRepository ticketRepository;
    private final LlmProviderRouter router;
    private final ScoringService scoringService;

    private static final Pattern TICKET_ID_PATTERN =
            Pattern.compile("\\b([0-9a-fA-F]{8})\\b");

    public QueryResult handleQuery(String message) {

        // 1. Try regex extraction
        String ticketId = extractTicketId(message);

        // 2. Optional fallback: use LLM to extract ticket if regex fails
        if (ticketId == null) {
            router.setStrategy(RoutingStrategy.SINGLE);
            String extractPrompt = """
                    Extract ONLY the ticket ID from the following user query.
                    A valid ticket ID is exactly 8 characters, hexadecimal (0-9, a-f).
                    If no valid ID exists, respond with 'NONE'.

                    User query:
                    "%s"
                    """.formatted(message);

            String llmResult = router.route(extractPrompt).trim();
            if (!"NONE".equalsIgnoreCase(llmResult) &&
                    llmResult.matches("^[0-9a-fA-F]{8}$")) {
                ticketId = llmResult;
            }
        }

        if (ticketId == null) {
            String reply = """
                    I couldn't detect a valid ticket ID in your message.
                    Please provide your 8-character ticket ID so I can help you.
                    """;
            return new QueryResult(null, reply);
        }

        // 3. Lookup ticket in DB
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);

        if (ticketOpt.isEmpty()) {
            String reply = """
                    I couldn't find any ticket with ID %s.
                    Please check the ID and try again.
                    """.formatted(ticketId);
            return new QueryResult(ticketId, reply);
        }

        Ticket ticket = ticketOpt.get();

        // 4. Apply simple business logic: close if user says 'close'
        if (message.toLowerCase().contains("close")) {
            ticket.setStatus("CLOSED");
            ticketRepository.save(ticket);
        }

        // 5. Build nice response
        String reply = """
                Dear User,

                I found your ticket with ID: %s.
                Current status: %s.

                %s

                Best regards,
                Support Agent
                """.formatted(ticketId, ticket.getStatus(),
                message.toLowerCase().contains("close")
                        ? "Your ticket has been marked as CLOSED."
                        : "If you want to close or update this ticket, just let me know.");

        Score score = scoringService.evaluate(message, reply);

        System.out.println("Query Response Score = " + score);

        return new QueryResult(ticketId, reply);
    }

    private String extractTicketId(String message) {
        Matcher matcher = TICKET_ID_PATTERN.matcher(message);
        return matcher.find() ? matcher.group(1) : null;
    }

}



