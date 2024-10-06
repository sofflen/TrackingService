package com.study.trackingservice.utils;

import com.study.dispatchservice.messages.DispatchCompletedEvent;
import com.study.dispatchservice.messages.DispatchPreparingEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public class EventUtils {

    public static DispatchPreparingEvent randomDispatchPreparingEvent() {
        return DispatchPreparingEvent.builder()
                .orderId(UUID.randomUUID())
                .build();
    }

    public static DispatchCompletedEvent randomDispatchCompletedEvent() {
        return DispatchCompletedEvent.builder()
                .orderId(UUID.randomUUID())
                .date(LocalDateTime.now().toString())
                .build();
    }
}
