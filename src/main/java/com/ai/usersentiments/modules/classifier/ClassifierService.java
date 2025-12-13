package com.ai.usersentiments.modules.classifier;

import com.ai.usersentiments.langgraph.model.SentimentType;
import com.ai.usersentiments.modules.providers.LlmProviderRouter;
import com.ai.usersentiments.modules.providers.RoutingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class ClassifierService {

    private final LlmProviderRouter router;


    public SentimentType classify(String message){
        Prompt prompt = new Prompt("""
                You are a sentiment and intent classifier for a support system.

                Possible labels:
                - POSITIVE   : Happy / thankful / positive feedback
                - NEGATIVE   : Unhappy / angry / negative but WITHOUT asking to create/update/close a ticket
                - COMPLAINT  : User is reporting a new issue or asking to raise a NEW complaint
                - QUERY      : User is asking about an EXISTING ticket or its status, OR wants to change/close an existing ticket

                Very important rules:
                - If the user mentions a specific ticket ID (e.g. 'ticket 8a2a3bae', 'my ticket id is 1234abcd'),
                  classify as QUERY, NOT COMPLAINT.
                - Examples of QUERY:
                  - "What's the status of ticket 8a2a3bae?"
                  - "Close my ticket: 8a2a3bae"
                  - "Can you update ticket 1234abcd?"
                - Examples of COMPLAINT:
                  - "I want to raise a complaint"
                  - "I had the worst experience, please open a ticket for me"
                  - "I want to file a complaint about service"

                Reply with ONLY one of:
                POSITIVE, NEGATIVE, QUERY, COMPLAINT

                Message: "%s"
                """.formatted(message));

        // Optional: Use majority vote routing for better accuracy
        router.setStrategy(RoutingStrategy.SINGLE);
        router.setPrimary("openAI");

        // Get classification result (provider routing handles it)
        String raw = router.route(String.valueOf(prompt)).trim().toUpperCase();

        return Arrays.stream(SentimentType.values())
                .filter(s -> s.name().equals(raw))
                .findFirst()
                .orElse(SentimentType.UNKNOWN);
    }


}
