package com.study.trackingservice.utils;

import com.study.dispatchservice.messages.DispatchPreparingEvent;
import com.study.dispatchservice.messages.TrackingStatusUpdatedEvent;
import com.study.trackingservice.statuses.TrackingStatus;

import java.util.UUID;

public class EventUtils {

    public static TrackingStatusUpdatedEvent randomTrackingStatusUpdatedEvent() {
        return TrackingStatusUpdatedEvent.builder()
                .orderId(UUID.randomUUID())
                .status(TrackingStatus.PREPARING)
                .build();
    }

    public static DispatchPreparingEvent randomDispatchPreparingEvent() {
        return DispatchPreparingEvent.builder()
                .orderId(UUID.randomUUID())
                .build();
    }
}
