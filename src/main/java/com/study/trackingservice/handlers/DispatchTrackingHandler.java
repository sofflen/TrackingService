package com.study.trackingservice.handlers;

import com.study.dispatchservice.messages.DispatchCompletedEvent;
import com.study.dispatchservice.messages.DispatchPreparingEvent;
import com.study.trackingservice.services.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(
        id = "dispatchTrackingConsumerClient",
        topics = DispatchTrackingHandler.DISPATCH_TRACKING_TOPIC,
        groupId = "tracking.dispatch.tracking.consumer",
        containerFactory = "kafkaListenerContainerFactory"
)
public class DispatchTrackingHandler {

    public static final String DISPATCH_TRACKING_TOPIC = "dispatch.tracking";

    private final TrackingService trackingService;

    @KafkaHandler
    public void listen(DispatchPreparingEvent payload) {
        log.info("DispatchTrackingHandler received payload: {}", payload);

        try {
            trackingService.process(payload);
        } catch (Exception e) {
            log.error("DispatchPreparingEvent Processing failure: ", e);
            if (e.getCause() instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @KafkaHandler
    public void listen(DispatchCompletedEvent payload) {
        log.info("DispatchTrackingHandler received payload: {}", payload);

        try {
            trackingService.process(payload);
        } catch (Exception e) {
            log.error("DispatchCompletedEvent Processing failure: ", e);
            if (e.getCause() instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
