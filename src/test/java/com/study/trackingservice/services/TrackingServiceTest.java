package com.study.trackingservice.services;

import com.study.dispatchservice.messages.TrackingStatusUpdatedEvent;
import com.study.trackingservice.utils.EventUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TrackingServiceTest {

    private TrackingService trackingService;
    private KafkaTemplate<String, Object> kafkaTemplateMock;

    @BeforeEach
    void setUp() {
        kafkaTemplateMock = mock(KafkaTemplate.class);
        trackingService = new TrackingService(kafkaTemplateMock);
    }

    @Test
    void process_success() throws Exception {
        when(kafkaTemplateMock.send(anyString(), any(TrackingStatusUpdatedEvent.class)))
                .thenReturn(mock(CompletableFuture.class));

        var testEvent = EventUtils.randomDispatchPreparingEvent();
        trackingService.process(testEvent);

        verify(kafkaTemplateMock).send(eq("tracking.status"), any(TrackingStatusUpdatedEvent.class));
    }

    @Test
    void process_throwsException() {
        when(kafkaTemplateMock.send(anyString(), any(TrackingStatusUpdatedEvent.class)))
                .thenReturn(mock(CompletableFuture.class));
        doThrow(new RuntimeException()).when(kafkaTemplateMock).send(anyString(), any(TrackingStatusUpdatedEvent.class));

        var testEvent = EventUtils.randomDispatchPreparingEvent();

        assertThrows(RuntimeException.class, () -> trackingService.process(testEvent));
        verify(kafkaTemplateMock).send(eq("tracking.status"), any(TrackingStatusUpdatedEvent.class));
    }
}