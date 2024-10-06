package com.study.trackingservice.services;

import com.study.dispatchservice.messages.TrackingStatusUpdatedEvent;
import com.study.trackingservice.statuses.TrackingStatus;
import com.study.trackingservice.utils.EventUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
    private ArgumentCaptor<TrackingStatusUpdatedEvent> trackingArgumentCaptor;

    @BeforeEach
    void setUp() {
        kafkaTemplateMock = mock(KafkaTemplate.class);
        trackingService = new TrackingService(kafkaTemplateMock);
        trackingArgumentCaptor = ArgumentCaptor.forClass(TrackingStatusUpdatedEvent.class);
    }

    @Test
    void process_DispatchPreparingEventSuccess() throws Exception {
        when(kafkaTemplateMock.send(anyString(), any(TrackingStatusUpdatedEvent.class)))
                .thenReturn(mock(CompletableFuture.class));

        var testEvent = EventUtils.randomDispatchPreparingEvent();
        trackingService.process(testEvent);

        verify(kafkaTemplateMock).send(eq("tracking.status"), trackingArgumentCaptor.capture());
        assertThat(trackingArgumentCaptor.getValue().getStatus(), equalTo(TrackingStatus.PREPARING));
    }

    @Test
    void process_DispatchCompletedEventSuccess() throws Exception {
        when(kafkaTemplateMock.send(anyString(), any(TrackingStatusUpdatedEvent.class)))
                .thenReturn(mock(CompletableFuture.class));

        var testEvent = EventUtils.randomDispatchCompletedEvent();
        trackingService.process(testEvent);

        verify(kafkaTemplateMock).send(eq("tracking.status"),trackingArgumentCaptor.capture());
        assertThat(trackingArgumentCaptor.getValue().getStatus(), equalTo(TrackingStatus.DISPATCHED));
    }

    @Test
    void process_DispatchPreparingEventThrowsException() {
        when(kafkaTemplateMock.send(anyString(), any(TrackingStatusUpdatedEvent.class)))
                .thenReturn(mock(CompletableFuture.class));
        doThrow(new RuntimeException()).when(kafkaTemplateMock).send(anyString(), any(TrackingStatusUpdatedEvent.class));

        var testEvent = EventUtils.randomDispatchPreparingEvent();

        assertThrows(RuntimeException.class, () -> trackingService.process(testEvent));
        verify(kafkaTemplateMock).send(eq("tracking.status"), any(TrackingStatusUpdatedEvent.class));
    }

    @Test
    void process_DispatchCompletedEventThrowsException() {
        when(kafkaTemplateMock.send(anyString(), any(TrackingStatusUpdatedEvent.class)))
                .thenReturn(mock(CompletableFuture.class));
        doThrow(new RuntimeException()).when(kafkaTemplateMock).send(anyString(), any(TrackingStatusUpdatedEvent.class));

        var testEvent = EventUtils.randomDispatchCompletedEvent();

        assertThrows(RuntimeException.class, () -> trackingService.process(testEvent));
        verify(kafkaTemplateMock).send(eq("tracking.status"), any(TrackingStatusUpdatedEvent.class));
    }
}