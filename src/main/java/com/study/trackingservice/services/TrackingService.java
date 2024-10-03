package com.study.trackingservice.services;

import com.study.dispatchservice.messages.DispatchPreparingEvent;
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

    public void process(DispatchPreparingEvent dispatchPreparingEvent) throws Exception{
        log.info("Processing dispatch preparing event: {}", dispatchPreparingEvent);

        var trackingEvent = new TrackingStatusUpdatedEvent(dispatchPreparingEvent.getOrderId(), TrackingStatus.PREPARING);

        kafkaProducer.send(TRACKING_STATUS_TOPIC, trackingEvent).get();
        log.info("Tracking status updated: {}", trackingEvent);
    }
}
