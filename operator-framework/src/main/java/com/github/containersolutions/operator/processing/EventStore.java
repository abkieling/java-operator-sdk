package com.github.containersolutions.operator.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public class EventStore {

    private final static Logger log = LoggerFactory.getLogger(EventScheduler.class);

    private final Map<String, CustomResourceEvent> eventsNotScheduledYet = new HashMap<>();
    private final Map<String, ResourceScheduleHolder> eventsScheduledForProcessing = new HashMap<>();
    private final Map<String, CustomResourceEvent> eventsUnderProcessing = new HashMap<>();

    public boolean containsOlderVersionOfNotScheduledEvent(CustomResourceEvent newEvent) {
        return eventsNotScheduledYet.containsKey(newEvent.resourceUid()) &&
                newEvent.isSameResourceAndNewerVersion(eventsNotScheduledYet.get(newEvent.resourceUid()));
    }

    public CustomResourceEvent removeEventNotScheduledYet(String uid) {
        return eventsNotScheduledYet.remove(uid);
    }

    public void addOrReplaceEventAsNotScheduledYet(CustomResourceEvent newEvent) {
        eventsNotScheduledYet.put(newEvent.resourceUid(), newEvent);
    }

    public boolean containsOlderVersionOfEventUnderProcessing(CustomResourceEvent newEvent) {
        return eventsUnderProcessing.containsKey(newEvent.resourceUid()) &&
                newEvent.isSameResourceAndNewerVersion(eventsUnderProcessing.get(newEvent.resourceUid()));
    }

    public boolean containsEventScheduledForProcessing(String uid) {
        return eventsScheduledForProcessing.containsKey(uid);
    }

    public ResourceScheduleHolder getEventScheduledForProcessing(String uid) {
        return eventsScheduledForProcessing.get(uid);
    }

    public ResourceScheduleHolder removeEventScheduledForProcessing(String uid) {
        return eventsScheduledForProcessing.remove(uid);
    }

    public void addEventScheduledForProcessing(ResourceScheduleHolder resourceScheduleHolder) {
        eventsScheduledForProcessing.put(resourceScheduleHolder.getCustomResourceEvent().resourceUid(), resourceScheduleHolder);
    }

    public void addEventUnderProcessing(CustomResourceEvent event) {
        eventsUnderProcessing.put(event.resourceUid(), event);
    }

    public CustomResourceEvent removeEventUnderProcessing(String uid) {
        return eventsUnderProcessing.remove(uid);
    }

    public static class ResourceScheduleHolder {
        private CustomResourceEvent customResourceEvent;
        private ScheduledFuture<?> scheduledFuture;

        public ResourceScheduleHolder(CustomResourceEvent customResourceEvent, ScheduledFuture<?> scheduledFuture) {
            this.customResourceEvent = customResourceEvent;
            this.scheduledFuture = scheduledFuture;
        }

        public CustomResourceEvent getCustomResourceEvent() {
            return customResourceEvent;
        }

        public ScheduledFuture<?> getScheduledFuture() {
            return scheduledFuture;
        }
    }
}
