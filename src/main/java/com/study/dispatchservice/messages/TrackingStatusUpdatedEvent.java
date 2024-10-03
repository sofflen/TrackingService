package com.study.dispatchservice.messages;

import com.study.trackingservice.statuses.TrackingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrackingStatusUpdatedEvent {
    private UUID orderId;
    private TrackingStatus status;
}
