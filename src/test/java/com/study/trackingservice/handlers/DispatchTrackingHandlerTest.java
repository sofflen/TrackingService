package com.study.trackingservice.handlers;

import com.study.trackingservice.services.TrackingService;
import com.study.trackingservice.utils.EventUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class DispatchTrackingHandlerTest {

    private DispatchTrackingHandler dispatchTrackingHandler;
    private TrackingService trackingServiceMock;

    @BeforeEach
    void setUp() {
        trackingServiceMock = Mockito.mock(TrackingService.class);
        dispatchTrackingHandler = new DispatchTrackingHandler(trackingServiceMock);
    }

    @Test
    void listen_DispatchPreparingEventSuccess() throws Exception {
        var testEvent = EventUtils.randomDispatchPreparingEvent();

        dispatchTrackingHandler.listen(testEvent);

        verify(trackingServiceMock).process(testEvent);
    }

    @Test
    void listen_DispatchCompletedEventSuccess() throws Exception {
        var testEvent = EventUtils.randomDispatchCompletedEvent();

        dispatchTrackingHandler.listen(testEvent);

        verify(trackingServiceMock).process(testEvent);
    }

    @Test
    void listen_DispatchPreparingEventCatchesException() throws Exception {
        var testEvent = EventUtils.randomDispatchPreparingEvent();
        doThrow(new RuntimeException()).when(trackingServiceMock).process(testEvent);

        dispatchTrackingHandler.listen(testEvent);

        verify(trackingServiceMock).process(testEvent);
    }

    @Test
    void listen_DispatchCompletedEventCatchesException() throws Exception {
        var testEvent = EventUtils.randomDispatchCompletedEvent();
        doThrow(new RuntimeException()).when(trackingServiceMock).process(testEvent);

        dispatchTrackingHandler.listen(testEvent);

        verify(trackingServiceMock).process(testEvent);
    }
}