package com.ai.usersentiments.modules.classifier;

import com.ai.usersentiments.langgraph.model.SentimentType;
import com.ai.usersentiments.modules.providers.LlmProviderRouter;
import com.ai.usersentiments.modules.providers.RoutingStrategy;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ClassifierServiceTest {
    @Test
    void classify_usesRouterAndMapsLabel() {
        LlmProviderRouter router = mock(LlmProviderRouter.class);
        ClassifierService service = new ClassifierService(router);

        when(router.route(anyString())).thenReturn("COMPLAINT");

        SentimentType result = service.classify("I want to file a complaint");

        // verify routing strategy set
        verify(router).setStrategy(RoutingStrategy.SINGLE);

        // verify prompt was built and routed
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(router).route(captor.capture());

        String usedPrompt = captor.getValue();
        assertThat(usedPrompt).contains("Classify the user message");

        // assert mapping
        assertThat(result).isEqualTo(SentimentType.COMPLAINT);
    }
}
