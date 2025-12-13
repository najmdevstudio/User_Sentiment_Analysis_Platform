package com.ai.usersentiments.modules.feedback;

import com.ai.usersentiments.domain.Ticket;
import com.ai.usersentiments.infrastructure.persistence.TicketJpaRepository;
import com.ai.usersentiments.modules.providers.LlmProviderRouter;
import com.ai.usersentiments.modules.providers.RoutingStrategy;
import com.ai.usersentiments.modules.scoring.Score;
import com.ai.usersentiments.modules.scoring.ScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final TicketJpaRepository ticketRepository;
    private final LlmProviderRouter router;
    private final ScoringService scoringService;

    public FeedBackResult handleComplaint(String message) {

        // ──────────────────────────────────────────────
        // 1. Create ticket
        // ──────────────────────────────────────────────
        String ticketId = UUID.randomUUID().toString().substring(0, 8);

        Ticket ticket = new Ticket(ticketId, message, "OPEN");
        ticketRepository.save(ticket);

        // ──────────────────────────────────────────────
        // 2. Build LLM prompt for empathetic reply
        // ──────────────────────────────────────────────
        String prompt = """
            The user is reporting a complaint.

            User message:
            "%s"

            Craft a highly empathetic, concise and professional response.
            End your response with:
            "Your ticket ID is: %s"
            """.formatted(message, ticketId);

        // ──────────────────────────────────────────────
        // 3. Use weighted routing (best for empathy)
        // ──────────────────────────────────────────────
        router.setStrategy(RoutingStrategy.SINGLE);

        String llmResponse = router.route(prompt);

        // ──────────────────────────────────────────────
        // 4. Score the quality of the response
        // ──────────────────────────────────────────────
        Score score = scoringService.evaluate(message, llmResponse);

        if(!llmResponse.contains(ticketId)){
            llmResponse = llmResponse+"\n\nYour ticket ID is: " + ticketId;
        }

        // Your AOP metrics already capture these via Prometheus
        // If needed, store locally or log
        System.out.println("Feedback Response Score = " + score);

        return new FeedBackResult(llmResponse,ticketId);
    }
}
