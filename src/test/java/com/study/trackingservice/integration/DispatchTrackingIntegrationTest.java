package com.study.trackingservice.integration;

import com.study.dispatchservice.messages.TrackingStatusUpdatedEvent;
import com.study.trackingservice.utils.EventUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.study.trackingservice.handlers.DispatchTrackingHandler.DISPATCH_TRACKING_TOPIC;
import static com.study.trackingservice.services.TrackingService.TRACKING_STATUS_TOPIC;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
@EmbeddedKafka(controlledShutdown = true)
@Slf4j
class DispatchTrackingIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private KafkaTestListener testListener;
    @Autowired
    private EmbeddedKafkaBroker kafkaBroker;
    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @BeforeEach
    void setUp() {
        testListener.trackingUpdateCounter.set(0);

        registry.getListenerContainers().forEach(container ->
                ContainerTestUtils.waitForAssignment(container, kafkaBroker.getPartitionsPerTopic()));
    }

    @Test
    void testDispatchPreparingUpdate() throws Exception {
        var dispatchPreparingEvent = EventUtils.randomDispatchPreparingEvent();

        kafkaTemplate.send(DISPATCH_TRACKING_TOPIC, dispatchPreparingEvent).get();

        await().atMost(3, TimeUnit.SECONDS).pollDelay(100, TimeUnit.MILLISECONDS)
                .until(testListener.trackingUpdateCounter::get, equalTo(1));
    }

    @Test
    void testDispatchCompletedUpdate() throws Exception {
        var dispatchCompletedEvent = EventUtils.randomDispatchCompletedEvent();

        kafkaTemplate.send(DISPATCH_TRACKING_TOPIC, dispatchCompletedEvent).get();

        await().atMost(3, TimeUnit.SECONDS).pollDelay(100, TimeUnit.MILLISECONDS)
                .until(testListener.trackingUpdateCounter::get, equalTo(1));
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public KafkaTestListener kafkaTestListener() {
            return new KafkaTestListener();
        }
    }

    static class KafkaTestListener {
        AtomicInteger trackingUpdateCounter = new AtomicInteger();

        @KafkaListener(groupId = "KafkaIntegrationTest", topics = TRACKING_STATUS_TOPIC)
        void receiveTrackingStatusUpdatedEvent(@Payload TrackingStatusUpdatedEvent payload) {
            log.debug("Received tracking status updated event: {}", payload);
            trackingUpdateCounter.incrementAndGet();
        }
    }
}
