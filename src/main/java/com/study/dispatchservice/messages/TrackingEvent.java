package com.study.dispatchservice.messages;

import java.util.UUID;

public interface TrackingEvent {

    UUID getOrderId();
}
