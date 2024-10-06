package com.study.trackingservice.services;

import com.study.dispatchservice.messages.DispatchCompletedEvent;
import com.study.dispatchservice.messages.DispatchPreparingEvent;
import com.study.dispatchservice.messages.TrackingEvent;
import com.study.dispatchservice.messages.TrackingStatusUpdatedEvent;
import com.study.trackingservice.statuses.TrackingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingService {

    public static final String TRACKING_STATUS_TOPIC = "tracking.status";

    private final KafkaTemplate<String, Object> kafkaProducer;

    public void process(TrackingEvent trackingEvent) throws Exception {
        log.info("Processing tracking event: {}", trackingEvent);
        var trackingStatusEvent = TrackingStatusUpdatedEvent.builder().orderId(trackingEvent.getOrderId()).build();

        switch (trackingEvent) {
            case DispatchPreparingEvent ignored -> trackingStatusEvent.setStatus(TrackingStatus.PREPARING);
            case DispatchCompletedEvent ignored -> trackingStatusEvent.setStatus(TrackingStatus.DISPATCHED);
            default -> throw new IllegalStateException("Unexpected value: " + trackingEvent);
        }

        kafkaProducer.send(TRACKING_STATUS_TOPIC, trackingEvent).get();
        log.info("Tracking status updated: {}", trackingEvent);
    }
}
